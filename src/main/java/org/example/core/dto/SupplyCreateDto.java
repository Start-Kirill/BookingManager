package org.example.core.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class SupplyCreateDto {

    private String name;

    private BigDecimal price;

    /*
        minutes
     */
    private Integer duration;

    public SupplyCreateDto() {
    }

    public SupplyCreateDto(String name, BigDecimal price, Integer duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyCreateDto supplyCreateDto = (SupplyCreateDto) o;
        return Objects.equals(name, supplyCreateDto.name) && Objects.equals(price, supplyCreateDto.price) && Objects.equals(duration, supplyCreateDto.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, duration);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SupplyDto{");
        sb.append("name='").append(name).append('\'');
        sb.append(", price=").append(price);
        sb.append(", duration=").append(duration);
        sb.append('}');
        return sb.toString();
    }
}
