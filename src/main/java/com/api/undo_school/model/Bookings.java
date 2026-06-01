package com.api.undo_school.model;

import jakarta.persistence.*;

@Entity
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;
    @JoinColumn(name="parentId")
    @ManyToOne
    private Users parentId;
    @ManyToOne
    @JoinColumn(name="offeringId")
    private Offering offering;
    private Status status;

    public Users getParentId() {
        return parentId;
    }

    public void setParentId(Users parentId) {
        this.parentId = parentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
