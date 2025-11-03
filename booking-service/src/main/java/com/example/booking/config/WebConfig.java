package com.example.booking.config;

import com.example.booking.security.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // proteggi POST/DELETE su /bookings 
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/bookings/**")
                .excludePathPatterns("/bookings/public/**"); //rotte pubbliche
    }
}
