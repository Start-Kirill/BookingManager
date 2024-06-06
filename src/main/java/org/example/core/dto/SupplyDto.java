package org.example.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SupplyDto {

    private UUID uuid;

    private String name;

    private BigDecimal price;

    /*
        minutes
     */
    private Integer duration;

    private List<UUID> masters;

    @JsonProperty("dt_create")
    private LocalDateTime dtCreate;

    @JsonProperty("dt_update")
    private LocalDateTime dtUpdate;

    public SupplyDto() {
    }

    public SupplyDto(UUID uuid, String name, BigDecimal price, Integer duration, List<UUID> masters, LocalDateTime dtCreate, LocalDateTime dtUpdate) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.masters = masters;
        this.dtCreate = dtCreate;
        this.dtUpdate = dtUpdate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public LocalDateTime getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(LocalDateTime dtCreate) {
        this.dtCreate = dtCreate;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalDateTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyDto supplyDto = (SupplyDto) o;
        return Objects.equals(uuid, supplyDto.uuid) && Objects.equals(name, supplyDto.name) && Objects.equals(price, supplyDto.price) && Objects.equals(duration, supplyDto.duration) && Objects.equals(masters, supplyDto.masters) && Objects.equals(dtCreate, supplyDto.dtCreate) && Objects.equals(dtUpdate, supplyDto.dtUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, price, duration, masters, dtCreate, dtUpdate);
    }
}
