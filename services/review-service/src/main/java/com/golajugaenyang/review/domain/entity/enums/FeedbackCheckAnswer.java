package com.golajugaenyang.review.domain.entity.enums;

public enum FeedbackCheckAnswer {
    GOOD(3), //잘 맞았어요
    NEUTRAL(2), //그냥 그랬어요
    BAD(1); //안 맞았어요

    private final int score;

    FeedbackCheckAnswer(int score){
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
