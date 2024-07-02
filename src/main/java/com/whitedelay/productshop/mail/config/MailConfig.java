package com.whitedelay.productshop.mail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${MAIL_HOST}")
    private String mailHost;

    @Value("${MAIL_PORT}")
    private int mailPort;

    @Value("${MAIL_USERNAME}")
    private String mailUsername;

    @Value("${MAIL_PASSWORD}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailHost);
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);
        javaMailSender.setPort(mailPort);
        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", mailHost); // SSL을 위한 호스트 신뢰 설정
        props.put("mail.debug", "true"); // 디버그 출력 활성화
        return props;
    }
}