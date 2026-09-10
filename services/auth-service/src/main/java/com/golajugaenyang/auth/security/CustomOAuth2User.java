package com.golajugaenyang.auth.security;

import com.golajugaenyang.auth.domain.entity.Auth;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Auth auth;
    private final boolean isNewUser;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
        Auth auth, boolean isNewUser, String nameAttributeKey){
        super(authorities,attributes, nameAttributeKey);
        this.auth = auth;
        this.isNewUser = isNewUser;
    }

}
