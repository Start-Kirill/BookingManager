package org.example.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    private List<User> masters;

    private LocalDateTime dtCreate;

    private LocalDateTime dtUpdate;


    public Supply() {
    }

    public Supply(UUID uuid, String name, BigDecimal price, Integer duration, List<User> masters, LocalDateTime dtCreate, LocalDateTime dtUpdate) {
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

    public List<User> getMasters() {
        return masters;
    }

    public void setMasters(List<User> masters) {
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
        Supply supply = (Supply) o;
        return uuid.equals(supply.uuid) && name.equals(supply.name) && price.equals(supply.price) && duration.equals(supply.duration) && dtCreate.equals(supply.dtCreate) && dtUpdate.equals(supply.dtUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, price, duration, dtCreate, dtUpdate);
    }
}
