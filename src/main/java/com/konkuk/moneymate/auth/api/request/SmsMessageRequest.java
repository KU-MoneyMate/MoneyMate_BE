package com.konkuk.moneymate.auth.api.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsMessageRequest {

    private String phoneNumber;
    private Integer verifyCode;

    // /user/verify/reset-pw 에서만 사용
    private String userID;

    // 사용 안 함
    private String message;
}
