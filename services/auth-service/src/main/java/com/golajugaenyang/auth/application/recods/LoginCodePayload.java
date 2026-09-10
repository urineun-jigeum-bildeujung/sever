package com.golajugaenyang.auth.application.recods;

public record LoginCodePayload(String accessToken,
                               String refreshToken,
                               boolean isNewUser,
                               String nickname) {

}
