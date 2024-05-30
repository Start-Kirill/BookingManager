package org.example.core.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ScheduleCreateDto {

    private UUID master;

    private LocalDateTime dtStart;

    private LocalDateTime dtEnd;

    public ScheduleCreateDto() {
    }

    public ScheduleCreateDto(UUID master, LocalDateTime dtStart, LocalDateTime dtEnd) {
        this.master = master;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
    }

    public UUID getMaster() {
        return master;
    }

    public void setMaster(UUID master) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleCreateDto that = (ScheduleCreateDto) o;
        return Objects.equals(master, that.master) && Objects.equals(dtStart, that.dtStart) && Objects.equals(dtEnd, that.dtEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(master, dtStart, dtEnd);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ScheduleCreateDto{");
        sb.append("master=").append(master);
        sb.append(", dtStart=").append(dtStart);
        sb.append(", dtEnd=").append(dtEnd);
        sb.append('}');
        return sb.toString();
    }
}
