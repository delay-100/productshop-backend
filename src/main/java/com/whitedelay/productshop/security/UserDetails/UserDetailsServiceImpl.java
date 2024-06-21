package com.whitedelay.productshop.security.UserDetails;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String memberid) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberid(memberid)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + memberid));
        System.out.println("member.getMemberid() = " + member.getMemberid());
        return new UserDetailsImpl(member);
    }
}