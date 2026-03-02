package com.rentingsystem.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Booking implements Serializable {
    private int bookingId;
    private int propertyId;
    private int bookerUserId;
    private int ownerUserId;
    private Date startDate;
    private Date endDate;
    private Timestamp bookingDate;
    private String status;

    // Constructors
    public Booking() {
    }

    public Booking(int bookingId, int propertyId, int bookerUserId, int ownerUserId, Date startDate, Date endDate, Timestamp bookingDate, String status) {
        this.bookingId = bookingId;
        this.propertyId = propertyId;
        this.bookerUserId = bookerUserId;
        this.ownerUserId = ownerUserId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Booking(int propertyId, int bookerUserId, int ownerUserId, Date startDate, Date endDate) {
        this.propertyId = propertyId;
        this.bookerUserId = bookerUserId;
        this.ownerUserId = ownerUserId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "pending"; // Default status
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getBookerUserId() {
        return bookerUserId;
    }

    public void setBookerUserId(int bookerUserId) {
        this.bookerUserId = bookerUserId;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
