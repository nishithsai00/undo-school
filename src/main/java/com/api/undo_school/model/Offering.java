package com.api.undo_school.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offeringId;
    private String batchType;
    @ManyToOne
    @JoinColumn(name="CourseId")
    private Course course;
    private Instant offeringStartDate;
    private Instant offeringEndDate;
    private Status status;
    @OneToMany(mappedBy = "offering")
    private List<Bookings> bookings;
    @ManyToOne
    @JoinColumn(name="teacher")
    private Users teacherId;


    public int getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getOfferingStartDate() {
        return offeringStartDate;
    }

    public void setOfferingStartDate(Instant offeringStartDate) {
        this.offeringStartDate = offeringStartDate;
    }

    public Instant getOfferingEndDate() {
        return offeringEndDate;
    }

    public void setOfferingEndDate(Instant offeringEndDate) {
        this.offeringEndDate = offeringEndDate;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public void setBookings(List<Bookings> bookings) {
        this.bookings = bookings;
    }

    public Users getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Users teacherId) {
        this.teacherId = teacherId;
    }
}
