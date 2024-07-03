package com.whitedelay.productshop.member.entity;

import com.whitedelay.productshop.member.dto.SignupRequestDto;
import com.whitedelay.productshop.security.AES256Encoder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Builder(access = AccessLevel.PRIVATE)
@Builder
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
    private int zipCode;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum role;

    public static Member from(SignupRequestDto member, PasswordEncoder passwordEncoder, AES256Encoder aesEncoder) {
        return Member.builder()
                .memberId(member.getMemberId())
                .password(passwordEncoder.encode(member.getPassword()))
                .address(aesEncoder.encodeString(member.getAddress()))
                .zipCode(member.getZipCode())
                .email(aesEncoder.encodeString(member.getEmail()))
                .memberName(aesEncoder.encodeString(member.getMemberName()))
                .phone(aesEncoder.encodeString(member.getPhone()))
                .role(member.getRole())
                .build();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
