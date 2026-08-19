# common-test 사용법

이 모듈은 test scope 전용입니다. 서비스의 build.gradle에서 아래처럼 의존하세요.

    dependencies {
        testImplementation project(':modules:common-test')
    }

주의: implementation으로 의존하지 마세요 (프로덕션 아티팩트에 테스트 라이브러리가 섞여 들어갑니다).
