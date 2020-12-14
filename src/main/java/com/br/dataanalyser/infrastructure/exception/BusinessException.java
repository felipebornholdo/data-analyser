package com.br.dataanalyser.infrastructure.exception;

public class BusinessException extends Exception {

    private static final long serialVersionUID = -2434489363987814111L;

    public BusinessException(String defaultMessage) {
        super(defaultMessage);
    }
}
