package com.golajugaenyang.member.application;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class NicknameGenerator {

    private static final String[] ADJECTIVES = {
            "졸린", "신나는", "행복한", "포근한", "말랑한", "귀여운", "사랑스러운", "활발한",
            "씩씩한", "용감한", "호기심많은", "장난꾸러기", "느긋한", "다정한", "순한", "반짝이는",
            "따뜻한", "폭신한", "똑똑한", "재빠른", "새침한", "해맑은", "꼬물꼬물한", "통통한",
            "쪼꼬만", "하찮은", "동그란", "순수한", "배고픈", "배부른", "토실토실한", "오물거리는"
    };

    private static final String[] ANIMALS = {
            "강아지", "고양이", "토끼", "햄스터", "기니피그", "고슴도치", "앵무새", "카나리아",
            "거북이", "페럿", "친칠라", "도마뱀", "사막여우", "오리", "꼬꼬닭", "금붕어",
            "카멜레온", "우파루파", "사슴벌레", "병아리", "슈가글라이더", "다람쥐", "사슴", "펭귄",
            "곰돌이", "유니콘", "너구리"
    };

    public String generate(){
        String adjective = pickRandom(ADJECTIVES);
        String animal = pickRandom(ANIMALS);
        int randomInt = ThreadLocalRandom.current().nextInt(1, 1000);

        return adjective + animal + String.valueOf(randomInt);
    }

    private String pickRandom(String[] arr){
        int randomIndex = ThreadLocalRandom.current().nextInt(0, arr.length);
        return arr[randomIndex];
    }

}
