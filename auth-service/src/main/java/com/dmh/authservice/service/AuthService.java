package com.dmh.authservice.service;

import com.dmh.authservice.dto.LoginRequestDto;
import com.dmh.authservice.dto.TokenResponseDto;
import com.dmh.authservice.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AuthService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    public AuthService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public TokenResponseDto login (LoginRequestDto loginRequest){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "password");
        map.add("username", loginRequest.getEmail());
        map.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenResponseDto> response = restTemplateBuilder.build().postForEntity(
                    tokenUrl,
                    request,
                    TokenResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new InvalidCredentialsException("Invalid username or password");

        } catch (HttpClientErrorException.BadRequest e) {
            throw new InvalidCredentialsException("Account is disabled or credentials are invalid");

        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable. Please try again later.");
        }


    }

}
