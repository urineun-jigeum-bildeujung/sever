package com.golajugaenyang.review.domain.entity.enums;

public enum ReviewAnswer {
    NEGATIVE(1),
    NEUTRAL(2),
    POSITIVE(3);

    private final int score;

    ReviewAnswer(int score){
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
