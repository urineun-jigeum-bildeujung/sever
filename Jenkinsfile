// 여러 스테이지에 걸쳐 공유해야 해서 pipeline 블록 바깥, 파일 최상단에 선언
def detectedServices = '[]'
def imageTag = ''
// PR 검증 빌드(env.CHANGE_ID 존재)나 dev 아닌 브랜치 push는 "빌드+테스트+스캔까지만"
// 하고 실제 ECR push/배포는 안 함 — dev로 실제 merge된 빌드만 진짜 배포로 취급.
def isRealDeploy = false

// 감지 결과를 셸 경로나 Gradle task에 직접 보간하지 않고, 고정된 허용 목록에서만
// 프로젝트/Dockerfile/Values 경로를 꺼낸다. api-gateway는 services/*가 아닌
// platform/* 모듈이므로 이 매핑이 모든 CI 단계의 단일 기준이다.
def imageTargets = [
    'auth-service': [
        name: 'auth-service', gradleProject: ':services:auth-service',
        dockerfile: 'services/auth-service/Dockerfile',
        valuesPath: 'values/dev/services/auth-service/values.yaml',
    ],
    'member-service': [
        name: 'member-service', gradleProject: ':services:member-service',
        dockerfile: 'services/member-service/Dockerfile',
        valuesPath: 'values/dev/services/member-service/values.yaml',
    ],
    'product-service': [
        name: 'product-service', gradleProject: ':services:product-service',
        dockerfile: 'services/product-service/Dockerfile',
        valuesPath: 'values/dev/services/product-service/values.yaml',
    ],
    'order-service': [
        name: 'order-service', gradleProject: ':services:order-service',
        dockerfile: 'services/order-service/Dockerfile',
        valuesPath: 'values/dev/services/order-service/values.yaml',
    ],
    'payment-service': [
        name: 'payment-service', gradleProject: ':services:payment-service',
        dockerfile: 'services/payment-service/Dockerfile',
        valuesPath: 'values/dev/services/payment-service/values.yaml',
    ],
    'review-service': [
        name: 'review-service', gradleProject: ':services:review-service',
        dockerfile: 'services/review-service/Dockerfile',
        valuesPath: 'values/dev/services/review-service/values.yaml',
    ],
    'notification-service': [
        name: 'notification-service', gradleProject: ':services:notification-service',
        dockerfile: 'services/notification-service/Dockerfile',
        valuesPath: 'values/dev/services/notification-service/values.yaml',
    ],
    'api-gateway': [
        name: 'api-gateway', gradleProject: ':platform:api-gateway',
        dockerfile: 'platform/api-gateway/Dockerfile',
        valuesPath: 'values/dev/services/api-gateway/values.yaml',
    ],
]

pipeline {
    // dev 브랜치에 짧은 시간 안에 push가 몰리면 두 빌드가 동시에 같은 gitops-value
    // HEAD를 기준으로 clone해서, 먼저 push한 쪽 다음 push가 non-fast-forward로
    // 실패할 수 있음(2026-09-13 CodeRabbit 리뷰로 발견). 같은 파이프라인의 빌드를
    // 한 번에 하나씩만 돌게 줄 세워서 막는다.
    options {
        disableConcurrentBuilds()
    }

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
          ephemeral-storage: 3Gi
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
          # kaniko는 이미지 빌드마다 전체 파일시스템을 스냅샷 떠서 레이어 diff를
          # 계산하는 구조라, jar만 COPY하는 지금도 순차로 7번 반복하면 예상보다
          # 훨씬 디스크를 많이 씀 — 3Gi로도 trivy가 Evicted됐음(2026-09-14).
          # 노드 여유가 대당 47GB라 넉넉하게 올려도 안전함.
          ephemeral-storage: 5Gi
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
          # workspace-volume(emptyDir)을 파드 내 모든 컨테이너가 공유하는데, 로컬에서
          # 실측한 workspace 자체 크기는 7개 서비스 jar까지 다 만들어도 180MB 수준이라
          # 이것만으론 3Gi도 안 채움 — 그런데도 3Gi에서 Evicted가 재현됨(2026-09-14).
          # kaniko의 스냅샷 오버헤드 등 정확한 원인은 더 봐야 하지만, 노드 여유가 대당
          # 47GB라 일단 여유 있게 올려서 계속 막히지 않게 한다.
          ephemeral-storage: 6Gi
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
    - name: awscli
      # kaniko는 AWS ECR 인증이 내장돼 있어서 --destination push가 바로 되지만,
      # crane(go-containerregistry)은 그런 클라우드 자동인증이 없어서 crane push가
      # 401 Unauthorized로 실패함(2026-09-14 실제 dev 빌드에서 재현). aws-cli로
      # ECR 토큰을 직접 받아서 crane auth login에 넘겨주기 위한 컨테이너.
      image: amazon/aws-cli:2.29.0
      command:
        - sleep
      args:
        - 99d
      resources:
        requests:
          cpu: 20m
          memory: 64Mi
          ephemeral-storage: 128Mi
        limits:
          cpu: 500m
          memory: 256Mi
          ephemeral-storage: 256Mi
      volumeMounts:
      - mountPath: "/home/jenkins/agent"
        name: "workspace-volume"
        readOnly: false
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
        OTEL_AGENT_VERSION = '2.31.1'
        OTEL_AGENT_SHA256 = 'bbf83c151b6400709e2f225bdd07a04f839d9d13b8b93464241333fd25d3e3ba'
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
                            //
                            // Declarative Checkout SCM은 PR 빌드에서 refs/pull/<N>/head만 fetch하고
                            // 대상 브랜치(origin/${CHANGE_TARGET})는 로컬에 안 받아와서, 바로 merge-base를
                            // 돌리면 "Not a valid object name"으로 실패함(2026-09-14 실제로 겪음).
                            // merge-base 전에 대상 브랜치를 먼저 fetch해서 origin/${CHANGE_TARGET}이
                            // 로컬에 존재하게 만든다.
                            sh "git fetch --no-tags origin ${env.CHANGE_TARGET}:refs/remotes/origin/${env.CHANGE_TARGET}"
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

                    // requestedImage는 사람이 입력하는 Jenkins 빌드 파라미터라 신뢰 못 함 —
                    // 예전엔 이 값을 Groovy 문자열 보간으로 셸 스크립트 소스에 직접 끼워넣어서,
                    // 세미콜론/백틱 등을 넣으면 임의 명령 실행이 가능했음(2026-09-13 CodeRabbit
                    // 리뷰로 발견). withEnv로 진짜 프로세스 환경변수로 넘기면 셸이 그 값을
                    // "명령의 일부"가 아니라 "그냥 문자열 데이터"로만 다루므로 안전함.
                    withEnv([
                        "EVENT_NAME=${eventName}",
                        "BASE_SHA=${baseSha}",
                        "REQUESTED_IMAGE=${requestedImage}",
                    ]) {
                        detectedServices = sh(
                            script: 'bash scripts/detect-services.sh',
                            returnStdout: true
                        ).trim()
                    }

                    imageTag = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()

                    // dev 브랜치로 실제 merge된 push 빌드만 배포로 취급 (PR 검증 빌드, 다른
                    // 브랜치 push는 빌드+테스트+스캔까지만 하고 ECR push/values 갱신은 안 함).
                    // 사람이 "Build Now"로 수동 실행한 빌드는(IMAGE 파라미터를 안 채웠어도)
                    // CHANGE_ID==null && BRANCH_NAME=='dev' 조건을 그대로 만족해버려서, 자동
                    // push와 구분이 안 됨 — UserIdCause가 있으면 사람이 직접 누른 것이므로
                    // 실배포에서 제외한다(2026-09-13 CodeRabbit 리뷰로 발견 — "그냥 재실행"이
                    // 실제 ECR push/GitOps 갱신으로 이어지는 사고를 막기 위함).
                    def isManualTrigger = !currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').isEmpty()
                    isRealDeploy = (env.CHANGE_ID == null) && (env.BRANCH_NAME == 'dev') && !isManualTrigger

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

                    def targets = services.collect { svc ->
                        def target = imageTargets[svc]
                        if (target == null) { error("허용되지 않은 이미지명: ${svc}") }
                        return target
                    }
                    def testTasks = targets.collect { "${it.gradleProject}:test" }.join(' ')
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

                    // Dockerfile 안에서 각 서비스마다 kaniko가 ./gradlew bootJar를 처음부터
                    // 새로 돌리면, 서비스 7개가 전부 모노레포 전체를 COPY + 풀 JDK 이미지
                    // 언패킹 + 의존성 재해석을 반복하게 됨 — 이게 "Timeout waiting to lock
                    // journal cache" 락 경합(gradle 캐시를 여러 프로세스가 동시에 잡으려 함)과
                    // ephemeral-storage 초과(파드 Evicted, 1Gi/3Gi 둘 다 부족)의 진짜 원인이었음
                    // (2026-09-14 실제 Jenkins 빌드에서 재현). Test 스테이지처럼 gradle
                    // 컨테이너에서 jar를 한 번만 미리 빌드해두고, kaniko는 그 jar를 COPY만
                    // 하도록 Dockerfile을 단순화해서 이 문제를 구조적으로 없앤다.
                    def targets = services.collect { svc ->
                        def target = imageTargets[svc]
                        if (target == null) { error("허용되지 않은 이미지명: ${svc}") }
                        return target
                    }
                    def bootJarTasks = targets.collect { "${it.gradleProject}:bootJar" }.join(' ')
                    container('gradle') {
                        sh """
                            chmod +x gradlew
                            ./gradlew ${bootJarTasks} -x test --no-daemon
                        """
                    }

                    // crane은 kaniko와 달리 ECR 자동인증이 없어서 crane push가 401
                    // Unauthorized로 실패함(2026-09-14 실제 dev 빌드에서 재현). 레지스트리
                    // 하나당 로그인 한 번이면 되므로(서비스마다 반복할 필요 없음) 서비스
                    // 루프 밖에서 딱 한 번만 로그인한다.
                    if (isRealDeploy) {
                        container('awscli') {
                            sh "aws ecr get-login-password --region ap-northeast-2 > ecr-token.txt"
                        }
                        container('crane') {
                            sh "crane auth login ${env.IMAGE_REGISTRY.split('/')[0]} --username AWS --password-stdin < ecr-token.txt"
                        }
                        sh "rm -f ecr-token.txt"
                    }

                    // 각 서비스: kaniko로 로컬 tar 빌드(push 안 함) -> Gateway는 최종 tar의
                    // Agent를 실제 appuser 권한으로 로드 -> Trivy로 CRITICAL 스캔(걸리면 실패)
                    // -> 실배포일 때만 crane으로 검증한 그 tar를 그대로 ECR에 push.
                    // kaniko는 빌드만, crane은 push만 담당 — 스캔 통과 못 한 이미지는
                    // 애초에 push 코드 경로를 안 타서 물리적으로 못 올라감.
                    //
                    // jar가 이미 만들어져 있어서 kaniko는 COPY만 하면 되므로 캐시 경합이
                    // 구조적으로 불가능함 — 그래도 디스크 여유를 위해 tar는 순차로 지우며 진행.
                    targets.each { target ->
                        def tarFile = "${target.name}.tar"
                        def imageRef = "${env.IMAGE_REGISTRY}/${target.name}:${imageTag}"

                        container('kaniko') {
                            sh """
                                /kaniko/executor \\
                                  --context=`pwd` \\
                                  --dockerfile=${target.dockerfile} \\
                                  --destination=${imageRef} \\
                                  --no-push \\
                                  --tarPath=${tarFile}
                            """
                        }

                        if (target.name == 'api-gateway') {
                            // Docker 소켓/privileged Pod 없이 Kaniko가 만든 실제 Docker archive를
                            // 풀어 최종 filesystem과 이미지 config를 재구성한다. 이미지 기본
                            // 사용자인 appuser로 chroot 실행해 Agent 읽기·SHA256·JVM 로딩까지
                            // 성공해야 다음 Trivy/Crane 단계로 진행한다.
                            container('gradle') {
                                sh """
                                    chmod +x scripts/verify-api-gateway-image.sh
                                    scripts/verify-api-gateway-image.sh \\
                                      ${tarFile} \\
                                      ${imageRef} \\
                                      ${env.OTEL_AGENT_VERSION} \\
                                      ${env.OTEL_AGENT_SHA256} \\
                                      appuser
                                """
                            }
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

                        sh "rm -f ${tarFile}"
                    }
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

                    def targets = services.collect { svc ->
                        def target = imageTargets[svc]
                        if (target == null) { error("허용되지 않은 이미지명: ${svc}") }
                        return target
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

                    targets.each { target ->
                        sh """
                            /tmp/yq -i '.image.tag = "${imageTag}"' gitops-value-checkout/${target.valuesPath}
                        """
                    }

                    dir('gitops-value-checkout') {
                        sh """
                            git config user.email 'jenkins@petflow.local'
                            git config user.name 'jenkins-ci'
                            git add values/
                            git diff --cached --quiet && echo '변경 없음, commit 생략' || git commit -m 'chore: deploy ${services.collect { it }.join(", ")} @ ${imageTag}'
                            git push
                        """
                    }
                }
            }
        }
    }
}
