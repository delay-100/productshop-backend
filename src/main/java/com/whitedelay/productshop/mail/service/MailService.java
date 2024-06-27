package com.whitedelay.productshop.mail.service;

import com.whitedelay.productshop.member.repository.MemberRepository;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification-code:";
    private static final String VERIFICATION_CODE_KEY_CHECK = "_checked";

    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private String authNum;

    @Value("${spring.mail.username}")
    private String mailUsername;


    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);	//보내는 대상
        message.setSubject("회원가입 이메일 인증");		//제목
        System.out.println("MailService="+mailUsername);

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authNum + "</strong>";	//메일 인증번호
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress(mailUsername, "백지연"));

        return message;
    }

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
        return authNum = key.toString();
    }

    public String sendSimpleMessage(String sendEmail) throws Exception {
        if (memberRepository.findByEmail(sendEmail).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        authNum = createCode();
        MimeMessage message = createMessage(sendEmail);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("이메일 전송에 실패했습니다.");
        }
        return authNum;
    }


    public boolean saveVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES);
        return true;
    }

    public boolean verifyEmail(String email, String verificationCode) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        System.out.println("key = " + key);
        System.out.println("savedCode = " + savedCode);
        if (savedCode != null && savedCode.equals(verificationCode)) {
            redisTemplate.opsForValue().set(key, verificationCode + VERIFICATION_CODE_KEY_CHECK, 10, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }
    public boolean isEmailVerified(String email) {
        String key = VERIFICATION_CODE_KEY_PREFIX + email;
        String value = redisTemplate.opsForValue().get(key);
        return value != null && value.endsWith(VERIFICATION_CODE_KEY_CHECK);
    }
}
