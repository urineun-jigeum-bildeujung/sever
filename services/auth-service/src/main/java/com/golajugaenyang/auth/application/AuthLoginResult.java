package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.domain.entity.Auth;

public record AuthLoginResult(Auth auth, boolean isNewUser) {
}
