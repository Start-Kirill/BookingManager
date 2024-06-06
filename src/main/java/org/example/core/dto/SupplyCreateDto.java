package org.example.core.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SupplyCreateDto {

    private String name;

    private BigDecimal price;

    /*
        minutes
     */
    private Integer duration;

    private List<UUID> masters;

    public SupplyCreateDto() {
    }

    public SupplyCreateDto(String name, BigDecimal price, Integer duration, List<UUID> masters) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.masters = masters;
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

    public List<UUID> getMasters() {
        return masters;
    }

    public void setMasters(List<UUID> masters) {
        this.masters = masters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyCreateDto that = (SupplyCreateDto) o;
        return Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(duration, that.duration) && Objects.equals(masters, that.masters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, duration, masters);
    }
}
