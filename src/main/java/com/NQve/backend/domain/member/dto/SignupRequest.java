package com.NQve.backend.domain.member.dto;

public class SignupRequest {

    private String email;
    private String password;
    private String nickname;

    public SignupRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
