package org.example.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Supply {

    private UUID uuid;

    private String name;

    private BigDecimal price;

    /*
        minutes
     */
    private Integer duration;

    private LocalDateTime dtCreate;

    private LocalDateTime dtUpdate;


    public Supply() {
    }

    public Supply(UUID uuid, String name, BigDecimal price, Integer duration, LocalDateTime dtCreate, LocalDateTime dtUpdate) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.duration = duration;
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
        Supply supply = (Supply) o;
        return Objects.equals(uuid, supply.uuid) && Objects.equals(name, supply.name) && Objects.equals(price, supply.price) && Objects.equals(duration, supply.duration) && Objects.equals(dtCreate, supply.dtCreate) && Objects.equals(dtUpdate, supply.dtUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, price, duration, dtCreate, dtUpdate);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Supply{");
        sb.append("uuid=").append(uuid);
        sb.append(", name='").append(name).append('\'');
        sb.append(", price=").append(price);
        sb.append(", duration=").append(duration);
        sb.append(", dtCreate=").append(dtCreate);
        sb.append(", dtUpdate=").append(dtUpdate);
        sb.append('}');
        return sb.toString();
    }
}
