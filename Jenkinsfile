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
    //
    // requests는 일부러 작게 잡음 — 컨테이너 4개가 실제로는 한 번에 하나씩만 일하고
    // 나머진 sleep으로 대기 중이라, 넉넉하게 잡으면 노드에 스케줄링이 안 됨(실측: DEV
    // 노드 2대 c7i-flex.large라 여유가 적음, 2026-09-10). limit은 그대로 둬서 실제
    // 작업할 때는 필요한 만큼 쓸 수 있게 함.
    //
    // 2026-09-11: 노드 1대 추가 전까지 임시로 request를 한 번 더 낮춤 — 기존 값(gradle
    // 200m/512Mi 등, 합계 550m/1088Mi)으로는 지금 두 노드 다 여유가 없어서 에이전트 Pod
    // 자체가 스케줄링이 안 됨. 노드 추가되면 위 주석의 원래 값으로 되돌릴 것.
    //
    // ephemeral-storage도 명시함 — 원래 이게 없어서 노드 디스크 사용량을 스케줄러가
    // 전혀 파악 못 했고, 실제로 노드 하나가 디스크 99% 차서 DiskPressure로 통째로
    // 재기동되는 사고가 남(2026-09-10). kaniko는 이미지 빌드 tar를, gradle은 배포판+
    // 의존성 캐시를 workspace-volume(emptyDir, 노드 디스크)에 쓰기 때문에 둘 다 넉넉히 잡음.
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
          cpu: 50m
          memory: 256Mi
          ephemeral-storage: 512Mi
        limits:
          cpu: "2"
          memory: 2Gi
          ephemeral-storage: 2Gi
    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      command:
        - /busybox/cat
      tty: true
      resources:
        requests:
          cpu: 50m
          memory: 128Mi
          ephemeral-storage: 1Gi
        limits:
          cpu: "2"
          memory: 2Gi
          ephemeral-storage: 3Gi
    - name: trivy
      image: aquasec/trivy:0.74.0 # 2026-09-10 기준 최신 안정 버전
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 30m
          memory: 128Mi
          ephemeral-storage: 512Mi
        limits:
          cpu: "1"
          memory: 1Gi
          ephemeral-storage: 1Gi
    - name: crane
      image: gcr.io/go-containerregistry/crane:debug
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 20m
          memory: 32Mi
          ephemeral-storage: 128Mi
        limits:
          cpu: 500m
          memory: 256Mi
          ephemeral-storage: 512Mi
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
                    //
                    // sed 대신 yq를 쓰는 이유 — sed는 YAML 구조를 모르고 "tag: "로 시작하는
                    // 줄이면 전부 매치해서 바꿔버림. 지금은 파일마다 tag: 줄이 하나뿐이라
                    // 우연히 안전하지만, 나중에 카나리(canary.image.tag) 구조가 추가되면
                    // stable/canary가 같은 값으로 덮어써지는 사고로 이어짐(2026-09-11 도입
                    // 전에 미리 발견). yq는 .image.tag처럼 정확한 경로만 지정해서 바꾸므로
                    // 그런 사고가 구조적으로 불가능함.
                    //
                    // Update GitOps 스테이지는 container()로 안 감싸여 있어서 Jenkins가
                    // 자동으로 붙여주는 jnlp 에이전트 컨테이너에서 도는데(git 내장), 여긴
                    // yq가 없어서 매 빌드마다 고정 버전 바이너리를 내려받아 씀.
                    sh '''
                        curl -sL https://github.com/mikefarah/yq/releases/download/v4.44.3/yq_linux_amd64 -o /tmp/yq
                        chmod +x /tmp/yq
                    '''

                    services.each { svc ->
                        sh """
                            /tmp/yq -i '.image.tag = "${imageTag}"' gitops-value-checkout/values/dev/services/${svc}/values.yaml
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
