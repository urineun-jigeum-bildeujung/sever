// Build stage에서도 써야 해서 pipeline 블록 바깥, 파일 최상단에 선언
def detectedServices = '[]'

pipeline {
    agent any

    parameters {
        // 수동 빌드 시 여기 값 채워서 실행 = GHA의 workflow_dispatch.inputs.image 역할
        string(name: 'IMAGE', defaultValue: '', description: '수동 빌드할 서비스명 (비워두면 자동 감지)')
    }

    environment {
        IMAGE_REGISTRY = 'ci.local' // TODO: infra팀한테 받은 실제 ECR 주소로 교체
        DOCKER_BUILD_PLATFORMS = 'linux/amd64'
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
                            sh """
                                docker buildx build \\
                                  --platform ${env.DOCKER_BUILD_PLATFORMS} \\
                                  -f services/${svc}/Dockerfile \\
                                  -t ${env.IMAGE_REGISTRY}/${svc}:${imageTag} \\
                                  .
                            """
                        }
                    }
                    parallel branches
                }
            }
        }
    }
}
