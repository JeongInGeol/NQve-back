package com.NQve.backend.domain.member.dto;

// 프론트가 보내는 JSON의 이메일과 비밀번호를 받는 DTO예요.
public record LoginRequest(String email, String password) {
}
