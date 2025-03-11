package ru.filkin.aopproject.restaop.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class ConfigurationMail {

    private String from;
    private String to;
    private String subject;
    private String text;

}


