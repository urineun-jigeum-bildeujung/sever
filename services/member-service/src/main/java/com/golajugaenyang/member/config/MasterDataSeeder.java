package com.golajugaenyang.member.config;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.out.persistence.entity.BreedMasterJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.entity.ConcernMasterJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.repository.BreedMasterJpaRepository;
import com.golajugaenyang.member.adapter.out.persistence.repository.ConcernMasterJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MasterDataSeeder implements CommandLineRunner {

    private static final List<String> DOG_BREEDS = List.of(
        "말티즈", "말티푸", "토이 푸들", "스탠다드 푸들", "미니어처 푸들", "비숑 프리제",
        "포메라니안", "스피츠", "폼피츠", "이탈리안 그레이하운드", "치와와", "시츄",
        "페키니즈", "파피용", "베드리턴 테리어", "요크셔테리어", "미니어처 슈나우저",
        "닥스훈트", "웰시코기", "프렌치 불도그", "퍼그", "비글", "코커 스패니얼",
        "시바견", "진돗개", "골든 리트리버", "래브라도 리트리버", "보더콜리", "사모예드",
        "허스키", "셰틀랜드 쉽독", "보스턴테리어", "불독", "믹스견", "기타"
    );

    private static final List<String> CAT_BREEDS = List.of(
        "코리안 숏헤어", "페르시안", "러시안 블루", "샴", "아메리칸 숏헤어", "브리티시 숏헤어",
        "스코티시 폴드", "스코티시 스트레이트", "스코티시 킬트", "먼치킨", "노르웨이 숲",
        "데본렉스", "미누엣 (나폴레옹)", "메인쿤", "랙돌", "벵갈", "아비시니안",
        "터키시 앙고라", "스핑크스", "엑조틱 숏헤어", "셀커크 렉스", "믹스묘", "기타"
    );

    private static final Map<String, List<String>> DOG_CONCERNS = Map.ofEntries(
        Map.entry("관절·뼈", List.of("슬개골 탈구", "관절염", "고관절 이형성증", "십자인대 질환", "척추·디스크")),
        Map.entry("피부·귀 알러지", List.of("아토피·피부염", "식이 알러지", "피부 민감", "잦은 가려움", "털·피모 관리", "외이도염", "귀 냄새")),
        Map.entry("체중·대사", List.of("과체중·비만", "체중 관리", "당뇨", "갑상선·호르몬 질환", "쿠싱 증후군")),
        Map.entry("소화기·장 건강", List.of("소화불량", "설사·묽은 변", "변비", "민감한 장", "췌장염")),
        Map.entry("구강 관리", List.of("치석·플라그", "잇몸 건강", "구취", "치주질환")),
        Map.entry("심장", List.of("심장질환", "심장판막질환", "심혈관 관리")),
        Map.entry("신장·비뇨기", List.of("신장 건강", "요로·방광 건강", "요로결석", "음수량 부족")),
        Map.entry("눈", List.of("눈물·눈물자국", "안구 건조", "눈 건강", "백내장", "유전성 안질환")),
        Map.entry("간", List.of("간 건강", "간 수치 관리", "피로·기력 저하")),
        Map.entry("노화", List.of("노령견 건강", "인지기능 저하", "면역력 관리", "치매", "기력 저하")),
        Map.entry("호흡기", List.of("기관지 협착증", "잦은 기침", "호흡 곤란"))
    );

    private static final Map<String, List<String>> CAT_CONCERNS = Map.ofEntries(
        Map.entry("신장", List.of("신장 건강", "만성 신장질환")),
        Map.entry("비뇨기", List.of("방광·요로 건강", "방광염", "요로결석", "요로폐색 경험")),
        Map.entry("체중·대사", List.of("과체중·비만", "체중 관리", "당뇨")),
        Map.entry("장 건강", List.of("민감한 장", "설사·묽은 변", "변비", "소화불량", "헤어볼")),
        Map.entry("피부·알러지", List.of("피부염", "식이 알러지", "피부 민감", "털·피모 관리", "오버그루밍(탈모)")),
        Map.entry("구강 관리", List.of("치석·플라그", "잇몸 건강", "구내염", "치아흡수병변")),
        Map.entry("관절", List.of("관절염", "관절·활동성 관리")),
        Map.entry("심장", List.of("심장 건강", "심근질환")),
        Map.entry("간", List.of("간 건강", "지방간")),
        Map.entry("노화", List.of("노령묘 건강", "인지·활동성 관리")),
        Map.entry("눈·호흡기", List.of("허피스", "결막염", "눈곱·눈물", "콧물·재채기")),
        Map.entry("스트레스 행동", List.of("영역 불안", "오버그루밍", "스트레스 완화"))
    );

    private final BreedMasterJpaRepository breedMasterJpaRepo;
    private final ConcernMasterJpaRepository concernMasterJpaRepo;

    @Override
    public void run(String... args) {
        seedBreeds();
        seedConcerns();
    }

    private void seedBreeds() {
        if (breedMasterJpaRepo.count() > 0) {
            return;
        }

        List<BreedMasterJpaEntity> breeds = new ArrayList<>();
        DOG_BREEDS.forEach(name -> breeds.add(toBreedEntity(Species.DOG, name)));
        CAT_BREEDS.forEach(name -> breeds.add(toBreedEntity(Species.CAT, name)));

        breedMasterJpaRepo.saveAll(breeds);
    }

    private void seedConcerns() {
        if (concernMasterJpaRepo.count() > 0) {
            return;
        }

        List<ConcernMasterJpaEntity> concerns = new ArrayList<>();
        addConcerns(concerns, Species.DOG, DOG_CONCERNS);
        addConcerns(concerns, Species.CAT, CAT_CONCERNS);

        concernMasterJpaRepo.saveAll(concerns);
    }

    private void addConcerns(List<ConcernMasterJpaEntity> target, Species species, Map<String, List<String>> concernsByCategory) {
        concernsByCategory.forEach((category, codes) ->
            codes.forEach(code -> target.add(toConcernEntity(species, category, code)))
        );
    }

    private BreedMasterJpaEntity toBreedEntity(Species species, String breedName) {
        return BreedMasterJpaEntity.builder()
            .species(species)
            .breedName(breedName)
            .build();
    }

    private ConcernMasterJpaEntity toConcernEntity(Species species, String category, String code) {
        return ConcernMasterJpaEntity.builder()
            .species(species)
            .concernCategory(category)
            .concernCode(code)
            .build();
    }
}
