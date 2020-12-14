package com.br.dataanalyser.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Messages {

    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource);
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }

    public String get(String code, String... args) {
        return accessor.getMessage(code, args);
    }

}