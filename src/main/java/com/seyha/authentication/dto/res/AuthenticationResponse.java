package com.seyha.authentication.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Builder
public record AuthenticationResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        long expiresIn
) {}