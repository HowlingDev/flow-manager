package com.example.services;

import com.example.clients.SubscriptionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final SubscriptionClient subscriptionClient;

    @Cacheable(value = "subscription", key = "#login")
    public String getSubscriptionType(String login) {
        return subscriptionClient.checkSubscription(login);
    }

    @CacheEvict(value = "subscription", key = "#login")
    public void deleteSubscriptionFromCache(String login) {

    }
}
