package org.example.core.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Schedule {

    private UUID uuid;

    private User master;

    private LocalDateTime dtStart;

    private LocalDateTime dtEnd;

    private LocalDateTime dtCreate;

    private LocalDateTime dtUpdate;

    public Schedule() {
    }

    public Schedule(UUID uuid, User master, LocalDateTime dtStart, LocalDateTime dtEnd, LocalDateTime dtCreate, LocalDateTime dtUpdate) {
        this.uuid = uuid;
        this.master = master;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.dtCreate = dtCreate;
        this.dtUpdate = dtUpdate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getMaster() {
        return master;
    }

    public void setMaster(User master) {
        this.master = master;
    }

    public LocalDateTime getDtStart() {
        return dtStart;
    }

    public void setDtStart(LocalDateTime dtStart) {
        this.dtStart = dtStart;
    }

    public LocalDateTime getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(LocalDateTime dtEnd) {
        this.dtEnd = dtEnd;
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
        Schedule schedule = (Schedule) o;
        dtStart = dtStart == null ? null : dtStart.truncatedTo(ChronoUnit.MILLIS);
        dtEnd = dtEnd == null ? null : dtEnd.truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime dtStartTarget = schedule.getDtStart() == null ? null : schedule.getDtStart().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime dtEndTarget = schedule.getDtEnd() == null ? null : schedule.getDtEnd().truncatedTo(ChronoUnit.MILLIS);
        return Objects.equals(uuid, schedule.uuid)
                && Objects.equals(master, schedule.master)
                && Objects.equals(dtStart, dtStartTarget)
                && Objects.equals(dtEnd, dtEndTarget)
                && Objects.equals(dtCreate.truncatedTo(ChronoUnit.MILLIS), schedule.dtCreate.truncatedTo(ChronoUnit.MILLIS))
                && Objects.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS), schedule.dtUpdate.truncatedTo(ChronoUnit.MILLIS));
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, master, dtStart, dtEnd, dtCreate, dtUpdate);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Schedule{");
        sb.append("uuid=").append(uuid);
        sb.append(", master=").append(master);
        sb.append(", dtStart=").append(dtStart);
        sb.append(", dtEnd=").append(dtEnd);
        sb.append(", dtCreate=").append(dtCreate);
        sb.append(", dtUpdate=").append(dtUpdate);
        sb.append('}');
        return sb.toString();
    }
}
