package org.scouts105bentaya.core.configuration;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class ErrorViewResolverConfiguration {

    @Bean
    public ErrorViewResolver errorViewResolverToRedirectToAngularApp() {
        final ModelAndView redirectToIndexHtml = new ModelAndView("forward:/index.html", HttpStatus.OK);
        return ((request, status, model) ->  status == HttpStatus.NOT_FOUND ? redirectToIndexHtml: null);
    }
}