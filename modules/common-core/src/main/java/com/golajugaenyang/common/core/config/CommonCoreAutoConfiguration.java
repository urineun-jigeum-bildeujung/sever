package com.golajugaenyang.common.core.config;

import com.golajugaenyang.common.core.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Import;


@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@Import({GlobalExceptionHandler.class})
public class CommonCoreAutoConfiguration {
}
