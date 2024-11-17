package Freeing.user_service.error;


public enum TokenValidationResult {
    VALID,              // 유효한 토큰
    EXPIRED,            // 만료된 토큰
    UNSUPPORTED,        // 지원하지 않는 형식의 토큰
    MALFORMED,          // 잘못된 형식의 토큰
    INVALID_SIGNATURE,  // 서명이 올바르지 않은 토큰
    ILLEGAL_ARGUMENT    // 잘못된 인수
}
