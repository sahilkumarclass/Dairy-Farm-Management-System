package com.sahilkumar.api.security;

import com.sahilkumar.api.auth.User;
import com.sahilkumar.api.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public User require() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User u) {
            return u;
        }
        throw new BusinessException("No authenticated user", HttpStatus.UNAUTHORIZED);
    }
}
