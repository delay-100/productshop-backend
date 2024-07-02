package com.whitedelay.productshop.member.repository;

import com.whitedelay.productshop.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Object> findByEmail(String email);

    boolean existsByMemberId(String memberId);

    Optional<Member> findByMemberId(String id);

}
