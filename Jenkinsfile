// 여러 스테이지에 걸쳐 공유해야 해서 pipeline 블록 바깥, 파일 최상단에 선언
def detectedServices = '[]'
def imageTag = ''
// PR 검증 빌드(env.CHANGE_ID 존재)나 dev 아닌 브랜치 push는 "빌드+테스트+스캔까지만"
// 하고 실제 ECR push/배포는 안 함 — dev로 실제 merge된 빌드만 진짜 배포로 취급.
def isRealDeploy = false

pipeline {
    // Jenkins가 K8s 파드로 떠서 도커 데몬이 없음 — kaniko가 daemon 없이 이미지를 빌드함.
    // Detect Services/Update GitOps 스테이지의 git 명령어는 기본 컨테이너(jnlp, git 내장)에서
    // 실행되고, 나머지(테스트/빌드/스캔/push)는 각자 맞는 컨테이너를 지정해서 씀.
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: jenkins-kaniko # infra팀이 ECR push 권한을 가진 EKS Pod Identity로 생성 완료(infra #17)
  containers:
    - name: gradle
      image: eclipse-temurin:25-jdk # 서비스 Dockerfile 빌드 스테이지와 동일 이미지 (JDK 버전 어긋남 방지)
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 500m
          memory: 1Gi
        limits:
          cpu: "2"
          memory: 2Gi
    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      command:
        - /busybox/cat
      tty: true
      resources:
        requests:
          cpu: 500m
          memory: 512Mi
        limits:
          cpu: "2"
          memory: 2Gi
    - name: trivy
      image: aquasec/trivy:0.74.0 # 2026-09-10 기준 최신 안정 버전
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 250m
          memory: 512Mi
        limits:
          cpu: "1"
          memory: 1Gi
    - name: crane
      image: gcr.io/go-containerregistry/crane:debug
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 100m
          memory: 128Mi
        limits:
          cpu: 500m
          memory: 256Mi
"""
        }
    }

    parameters {
        // 수동 빌드 시 여기 값 채워서 실행 = GHA의 workflow_dispatch.inputs.image 역할
        string(name: 'IMAGE', defaultValue: '', description: '수동 빌드할 서비스명 (비워두면 자동 감지)')
    }

    environment {
        IMAGE_REGISTRY = '297165773875.dkr.ecr.ap-northeast-2.amazonaws.com/petflow'
        GITOPS_VALUE_REPO = 'https://github.com/urineun-jigeum-bildeujung/gitops-value.git'
    }

    stages {
        stage('Detect Services') {
            steps {
                script {
                    def eventName = params.IMAGE?.trim() ? 'workflow_dispatch' : 'push'
                    def requestedImage = params.IMAGE?.trim() ?: 'all'
                    def baseSha = ''

                    if (eventName != 'workflow_dispatch') {
                        if (env.CHANGE_ID) {
                            // PR 빌드: PR 대상 브랜치와의 공통 조상 커밋을 base로 사용
                            // (GHA의 github.event.pull_request.base.sha에 대응, 여긴 자동 제공값이 없어서 직접 계산)
                            baseSha = sh(
                                script: "git merge-base HEAD origin/${env.CHANGE_TARGET}",
                                returnStdout: true
                            ).trim()
                        } else {
                            // 브랜치 push 빌드: 직전 성공 빌드 커밋 기준, 없으면(첫 빌드) 바로 이전 커밋
                            baseSha = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?:
                                sh(script: 'git rev-parse HEAD~1', returnStdout: true).trim()
                        }
                    }

                    detectedServices = sh(
                        script: """
                            EVENT_NAME=${eventName} BASE_SHA=${baseSha} REQUESTED_IMAGE=${requestedImage} \
                            bash scripts/detect-services.sh
                        """,
                        returnStdout: true
                    ).trim()

                    imageTag = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()

                    // dev 브랜치로 실제 merge된 push 빌드만 배포로 취급 (PR 검증 빌드, 다른
                    // 브랜치 push는 빌드+테스트+스캔까지만 하고 ECR push/values 갱신은 안 함)
                    isRealDeploy = (env.CHANGE_ID == null) && (env.BRANCH_NAME == 'dev')

                    echo "감지된 서비스: ${detectedServices}"
                    echo "실배포 여부: ${isRealDeploy}"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def services = readJSON(text: detectedServices)

                    if (services.isEmpty()) {
                        echo '이번 변경에서 테스트할 서비스가 없습니다.'
                        return
                    }

                    def testTasks = services.collect { ":services:${it}:test" }.join(' ')
                    container('gradle') {
                        sh """
                            chmod +x gradlew
                            ./gradlew ${testTasks} --no-daemon
                        """
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def services = readJSON(text: detectedServices)

                    if (services.isEmpty()) {
                        echo '이번 변경에서 빌드할 서비스가 없습니다.'
                        return
                    }

                    // build.yml의 matrix 역할 — 서비스 개수만큼 병렬 브랜치 생성.
                    // 각 브랜치: kaniko로 로컬 tar 빌드(push 안 함) -> Trivy로 CRITICAL 스캔
                    // (걸리면 실패) -> 실배포일 때만 crane으로 그 tar를 그대로 ECR에 push.
                    // kaniko는 빌드만, crane은 push만 담당 — 스캔 통과 못 한 이미지는
                    // 애초에 push 코드 경로를 안 타서 물리적으로 못 올라감.
                    def branches = [:]
                    services.each { svc ->
                        branches[svc] = {
                            def tarFile = "${svc}.tar"
                            def imageRef = "${env.IMAGE_REGISTRY}/${svc}:${imageTag}"

                            container('kaniko') {
                                sh """
                                    /kaniko/executor \\
                                      --context=`pwd` \\
                                      --dockerfile=services/${svc}/Dockerfile \\
                                      --destination=${imageRef} \\
                                      --no-push \\
                                      --tarPath=${tarFile}
                                """
                            }

                            container('trivy') {
                                sh """
                                    trivy image --input ${tarFile} \\
                                      --severity CRITICAL --exit-code 1 --ignore-unfixed
                                """
                            }

                            if (isRealDeploy) {
                                container('crane') {
                                    sh "crane push ${tarFile} ${imageRef}"
                                }
                            }
                        }
                    }
                    parallel branches
                }
            }
        }

        stage('Update GitOps') {
            when {
                expression { return isRealDeploy }
            }
            steps {
                script {
                    def services = readJSON(text: detectedServices)

                    if (services.isEmpty()) {
                        echo '갱신할 서비스가 없습니다.'
                        return
                    }

                    withCredentials([usernamePassword(
                        credentialsId: 'gitops-value-push',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )]) {
                        sh """
                            rm -rf gitops-value-checkout
                            git clone https://\${GIT_USER}:\${GIT_TOKEN}@github.com/urineun-jigeum-bildeujung/gitops-value.git gitops-value-checkout
                        """
                    }

                    // values.yaml의 tag 필드만 이번에 push한 커밋 SHA로 갱신.
                    // gitops-value의 values/dev/services/<svc>/values.yaml 구조에 맞춤
                    // (gitops-value README/appset.yaml과 반드시 일치해야 하는 경로).
                    services.each { svc ->
                        sh """
                            sed -i 's|tag: .*|tag: "${imageTag}"|' gitops-value-checkout/values/dev/services/${svc}/values.yaml
                        """
                    }

                    dir('gitops-value-checkout') {
                        sh """
                            git config user.email 'jenkins@petflow.local'
                            git config user.name 'jenkins-ci'
                            git add values/
                            git diff --cached --quiet && echo '변경 없음, commit 생략' || git commit -m 'chore: deploy ${services.join(", ")} @ ${imageTag}'
                            git push
                        """
                    }
                }
            }
        }
    }
}
