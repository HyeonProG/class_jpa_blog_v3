package com.tenco.blog_v3.user;

import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter
    @Setter
    public static class DTO {

        private int id;
        private String username;
        private String email;

        //ex) User 엔티티 반환 --> 서비스 계층 DTO 객체로 변환 처리
        public DTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
        // 이메일 정보 포함을 안하고 싶다면
        public DTO(User user, boolean includeEmail) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = includeEmail ? user.getEmail() : null;
        }
    }
}
