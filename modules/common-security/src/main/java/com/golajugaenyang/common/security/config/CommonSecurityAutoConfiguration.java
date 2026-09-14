package com.golajugaenyang.common.security.config;

import com.golajugaenyang.common.security.filter.InternalGatewaySecurityFilter;
import com.golajugaenyang.common.security.resolver.AuthIdArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonSecurityAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public AuthIdArgumentResolver authIdArgumentResolver() {
        return new AuthIdArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers){
        resolvers.add(authIdArgumentResolver());
    }

    @Bean
    public FilterRegistrationBean<InternalGatewaySecurityFilter> internalGatewaySecurityFilter(
        @Value("${internal.gateway-secret}") String internalGatewaySecret
    ) {
        FilterRegistrationBean<InternalGatewaySecurityFilter> registration =
            new FilterRegistrationBean<>(new InternalGatewaySecurityFilter(internalGatewaySecret));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
