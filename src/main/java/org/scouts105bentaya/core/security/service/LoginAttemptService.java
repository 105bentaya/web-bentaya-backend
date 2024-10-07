package org.scouts105bentaya.core.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 100;
    private final LoadingCache<String, Integer> attemptsCache;
    private final RequestService requestService;

    public LoginAttemptService(RequestService requestService) {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS).build(new CacheLoader<>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        });
        this.requestService = requestService;
    }

    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked() {
        try {
            return attemptsCache.get(requestService.getClientIP()) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
