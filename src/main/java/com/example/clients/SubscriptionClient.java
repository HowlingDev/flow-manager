package com.example.clients;

import com.example.dto.SubscriptionTypeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/v1/subscriptions/check/{login}")
    SubscriptionTypeDto checkSubscription(@PathVariable String login);
}
