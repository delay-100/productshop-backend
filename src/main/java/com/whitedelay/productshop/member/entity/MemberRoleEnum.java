package com.whitedelay.productshop.member.entity;

public enum MemberRoleEnum {     // implements GrantedAuthority 하면 SpringSecurity에서 get으로 가져올 수 있음

    USER(Authority.USER),  // 사용자 권한
    ADMIN(Authority.ADMIN);  // 관리자 권한

    private final String authority;

    MemberRoleEnum(String authority) {
        this.authority = authority;
    }

    // @Override하면 됨
    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}