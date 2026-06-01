package com.api.undo_school.model;

import jakarta.persistence.*;

import java.time.Instant;


@Entity
public class Sessions {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int sessionId;
private Instant startTime;
private Instant endTime;
@ManyToOne
@JoinColumn(name="offeringId")
private Offering offering;
@JoinColumn(name="teacherId")
@ManyToOne
private Users teacherId;


    public Users getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Users teacherId) {
        this.teacherId = teacherId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }
}
