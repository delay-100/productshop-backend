package com.whitedelay.productshop.mail.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.member.repository.MemberRepository;
import com.whitedelay.productshop.security.AES256Encoder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private final AES256Encoder aes256Encoder;

    @Value("${SIGNUP_CODE_KEY_PREFIX}")
    private String SIGNUP_CODE_KEY_PREFIX;

    @Value("${SIGNUP_CODE_KEY_CHECK}")
    private String SIGNUP_CODE_KEY_CHECK;

    @Value("${MAIL_USERNAME}")
    private String mailUsername;

    public boolean postSignupVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<Member> member = memberRepository.findByEmail(aes256Encoder.encodeString(email));
        if (member.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String code = createCode();
        MimeMessage message = createSignupMessage(email, code);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new IllegalArgumentException("이메일 전송에 실패했습니다.");
        }

        String key = SIGNUP_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
        return true;
    }

    public boolean checkSignupEmailCode(String email, String verificationCode) {
        String key = SIGNUP_CODE_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(verificationCode)) {
            redisTemplate.opsForValue().set(key, verificationCode + SIGNUP_CODE_KEY_CHECK, 10, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public MimeMessage createSignupMessage(String to, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("회원가입 이메일 인증");

        String msgText =
                "<div style='margin:100px;'>"
                + "<br>"
                + "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>"
                + "<strong>"
                + code
                + "</strong></div>";

        message.setText(msgText, "utf-8", "html");
        message.setFrom(new InternetAddress(mailUsername, "ProductShop"));

        return message;
    }

    // 인증 코드 만들기
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0; i<8; i++) {
            int index = random.nextInt(3);

            switch(index) {
                case 0 -> key.append((char)((int) random.nextInt(26)+97));
                case 1 -> key.append((char)((int) random.nextInt(26)+65));
                case 2 -> key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    public boolean isSignupEmailVerified(String email) {
        String key = SIGNUP_CODE_KEY_PREFIX + email;
        String value = redisTemplate.opsForValue().get(key);
        return value != null && value.endsWith(SIGNUP_CODE_KEY_CHECK);
    }
}
