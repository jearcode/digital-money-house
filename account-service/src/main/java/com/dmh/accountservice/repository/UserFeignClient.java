package com.dmh.accountservice.repository;

import com.dmh.accountservice.dto.response.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/users/me")
    UserDto getMyProfile (@RequestHeader("Authorization") String token);

    @GetMapping("/users/{id}")
    UserDto getUserById (@RequestHeader("Authorization") String token, @PathVariable Long id);

}
