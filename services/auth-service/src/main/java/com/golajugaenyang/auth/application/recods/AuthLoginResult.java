package com.golajugaenyang.auth.application.recods;

import com.golajugaenyang.auth.domain.entity.Auth;

public record AuthLoginResult(Auth auth, boolean isNewUser) {
}
