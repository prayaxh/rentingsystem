package com.rentingsystem.dao;

import com.rentingsystem.model.Booking;
import com.rentingsystem.model.Property;
import com.rentingsystem.utility.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (property_id, booker_user_id, owner_user_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        int bookingId = -1;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getPropertyId());
            stmt.setInt(2, booking.getBookerUserId());
            stmt.setInt(3, booking.getOwnerUserId());
            stmt.setDate(4, booking.getStartDate());
            stmt.setDate(5, booking.getEndDate());
            stmt.setString(6, booking.getStatus());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bookingId = generatedKeys.getInt(1);
                        booking.setBookingId(bookingId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingId;
    }

    public List<Booking> getBookingsByBookerUserId(int bookerUserId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_id, property_id, booker_user_id, owner_user_id, start_date, end_date, booking_date, status FROM bookings WHERE booker_user_id = ? ORDER BY booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookerUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setPropertyId(rs.getInt("property_id"));
                    booking.setBookerUserId(rs.getInt("booker_user_id"));
                    booking.setOwnerUserId(rs.getInt("owner_user_id"));
                    booking.setStartDate(rs.getDate("start_date"));
                    booking.setEndDate(rs.getDate("end_date"));
                    booking.setBookingDate(rs.getTimestamp("booking_date"));
                    booking.setStatus(rs.getString("status"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByOwnerUserId(int ownerUserId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_id, property_id, booker_user_id, owner_user_id, start_date, end_date, booking_date, status FROM bookings WHERE owner_user_id = ? ORDER BY booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setPropertyId(rs.getInt("property_id"));
                    booking.setBookerUserId(rs.getInt("booker_user_id"));
                    booking.setOwnerUserId(rs.getInt("owner_user_id"));
                    booking.setStartDate(rs.getDate("start_date"));
                    booking.setEndDate(rs.getDate("end_date"));
                    booking.setBookingDate(rs.getTimestamp("booking_date"));
                    booking.setStatus(rs.getString("status"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT booking_id, property_id, booker_user_id, owner_user_id, start_date, end_date, booking_date, status FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setPropertyId(rs.getInt("property_id"));
                    booking.setBookerUserId(rs.getInt("booker_user_id"));
                    booking.setOwnerUserId(rs.getInt("owner_user_id"));
                    booking.setStartDate(rs.getDate("start_date"));
                    booking.setEndDate(rs.getDate("end_date"));
                    booking.setBookingDate(rs.getTimestamp("booking_date"));
                    booking.setStatus(rs.getString("status"));
                    return booking;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasConfirmedBookings(int propertyId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE property_id = ? AND status = 'confirmed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
