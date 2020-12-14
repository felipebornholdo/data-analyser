package com.br.dataanalyser.dto;

import java.math.BigDecimal;
import java.util.List;

public class Salesman {

    private String name;
    private String cpf;
    private BigDecimal salary;
    private List<Sale> sales;
    private BigDecimal totalSaleValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public BigDecimal getTotalSaleValue() {
        return totalSaleValue;
    }

    public void setTotalSaleValue(BigDecimal totalSaleValue) {
        this.totalSaleValue = totalSaleValue;
    }
}
