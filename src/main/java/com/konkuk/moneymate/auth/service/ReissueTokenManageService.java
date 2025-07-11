package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.api.response.AuthTokensResponse;
import com.konkuk.moneymate.auth.application.RefreshTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReissueTokenManageService {

    private final JwtService jwtService;
    private final RefreshTokenValidator refreshTokenValidator;

    public AuthTokensResponse reissueToken(final String refreshToken, HttpServletRequest request) {

        // 검증 부분
        // refreshTokenValidator.validateToken(refreshToken);
        // refreshTokenValidator.validateLogoutToken(refreshToken);

        // Access Token에서 uid parse 하는 부분

        String userId = jwtService.getUserIdFromRefreshToken(refreshToken);
        // String uid = jwtService.getUserUid(request);

        String newAccessToken = jwtService.getAccessToken(userId);
        String newRefreshToken = jwtService.getRefreshToken(userId);

        return AuthTokensResponse.of(newAccessToken, newRefreshToken, "Bearer");
    }

}
