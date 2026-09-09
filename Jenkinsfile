// Build stage에서도 써야 해서 pipeline 블록 바깥, 파일 최상단에 선언
def detectedServices = '[]'

pipeline {
    // Jenkins가 K8s 파드로 떠서 도커 데몬이 없음 — kaniko가 daemon 없이 이미지를
    // 빌드+push까지 처리함. Detect Services 스테이지의 git 명령어는 여기서 지정 안 한
    // 기본 컨테이너(jnlp, git 내장)에서 실행되고, kaniko 실행만 container('kaniko')로 지정.
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: jenkins-kaniko # TODO(인프라팀): ECR push 권한(ecr:*Layer*, ecr:PutImage, ecr:GetAuthorizationToken)을
                                      # 가진 EKS Pod Identity로 이 이름의 ServiceAccount 생성 필요. 아직 없으면 push 단계에서 실패함.
  containers:
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
"""
        }
    }

    parameters {
        // 수동 빌드 시 여기 값 채워서 실행 = GHA의 workflow_dispatch.inputs.image 역할
        string(name: 'IMAGE', defaultValue: '', description: '수동 빌드할 서비스명 (비워두면 자동 감지)')
    }

    environment {
        IMAGE_REGISTRY = '297165773875.dkr.ecr.ap-northeast-2.amazonaws.com/petflow'
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

                    echo "감지된 서비스: ${detectedServices}"
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

                    def imageTag = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()

                    // build.yml의 matrix 역할 — 서비스 개수만큼 병렬 브랜치 생성
                    def branches = [:]
                    services.each { svc ->
                        branches[svc] = {
                            container('kaniko') {
                                sh """
                                    /kaniko/executor \\
                                      --context=`pwd` \\
                                      --dockerfile=services/${svc}/Dockerfile \\
                                      --destination=${env.IMAGE_REGISTRY}/${svc}:${imageTag}
                                """
                            }
                        }
                    }
                    parallel branches
                }
            }
        }
    }
}
