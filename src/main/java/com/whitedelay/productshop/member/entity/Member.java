package com.whitedelay.productshop.member.entity;

import com.whitedelay.productshop.member.dto.MemberRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "member")
public class Member extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String memberId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum role;

    public static Member from(MemberRequestDto member) {
        return Member.builder()
                .memberId(member.getMemberId())
                .password(member.getPassword())
                .address(member.getAddress())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .phone(member.getPhone())
                .role(member.getRole())
                .build();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
