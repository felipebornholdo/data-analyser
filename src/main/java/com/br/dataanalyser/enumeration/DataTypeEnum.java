package com.br.dataanalyser.enumeration;

public enum DataTypeEnum {

    SALESMAN(1L),
    CUSTOMER(2L),
    SALE(3L);

    private final Long typeId;

    DataTypeEnum(Long typeId) {
        this.typeId = typeId;
    }

    public Long getTypeId() {
        return typeId;
    }
}
