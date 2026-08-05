package com.NQve.backend.domain.member.dto;

public class SignupResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String status;

    public SignupResponse(Long id, String email, String nickname, String status) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getStatus() {
        return status;
    }
}
