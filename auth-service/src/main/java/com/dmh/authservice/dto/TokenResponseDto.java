package com.dmh.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TokenResponseDto {

    @Schema(description = "JWT Access Token used to authenticate requests", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "JWT Refresh Token used to obtain a new Access Token when the current one expires", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "Access Token lifespan in seconds", example = "300")
    @JsonProperty("expires_in")
    private Integer expiresIn;

    @Schema(description = "Refresh Token lifespan in seconds", example = "1800")
    @JsonProperty("refresh_expires_in")
    private Integer refreshExpiresIn;

    @Schema(description = "Type of the token (typically Bearer)", example = "Bearer")
    @JsonProperty("token_type")
    private String tokenType;

}
