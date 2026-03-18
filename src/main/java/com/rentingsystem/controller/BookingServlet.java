package com.rentingsystem.controller;

import com.rentingsystem.dao.BookingDAO;
import com.rentingsystem.dao.PropertyDAO;
import com.rentingsystem.dao.UserDAO;
import com.rentingsystem.model.Booking;
import com.rentingsystem.model.Property;
import com.rentingsystem.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/bookProperty", "/myBookings", "/bookerDetails"})
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO;
    private PropertyDAO propertyDAO;
    private UserDAO userDAO; // Added for fetching user details

    public void init() {
        bookingDAO = new BookingDAO();
        propertyDAO = new PropertyDAO();
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        if ("/bookProperty".equals(action)) {
            bookProperty(request, response);
        } else if (request.getServletPath().equals("/myBookings")) {
            String bookingAction = request.getParameter("action");
            if ("acceptBooking".equals(bookingAction)) {
                acceptOrRejectBooking(request, response, "confirmed");
            } else if ("rejectBooking".equals(bookingAction)) {
                acceptOrRejectBooking(request, response, "rejected");
            } else if ("cancelBooking".equals(bookingAction)) { // New action
                acceptOrRejectBooking(request, response, "cancelled");
            }
            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for POST request on /myBookings.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for POST request.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        if ("/myBookings".equals(action)) {
            displayMyBookings(request, response);
        } else if ("/bookerDetails".equals(action)) { // NEW: Handle booker details request
            displayBookerDetails(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for GET request.");
        }
    }

    // NEW: Method to display booker details
    private void displayBookerDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login"); // Redirect to login if not authenticated
            return;
        }

        try {
            int bookerId = Integer.parseInt(request.getParameter("userId"));
            User booker = userDAO.getUserById(bookerId);
            if (booker != null) {
                request.setAttribute("booker", booker);
                request.getRequestDispatcher("/WEB-INF/pages/bookerDetails.jsp").forward(request, response);
            } else {
                session.setAttribute("message", "Booker not found.");
                response.sendRedirect("myBookings"); // Redirect back if booker not found
            }
        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid booker ID.");
            response.sendRedirect("myBookings");
        }
    }

    private void bookProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login"); // Redirect to login if not authenticated
            return;
        }

        User bookerUser = (User) session.getAttribute("user");
        int propertyId = Integer.parseInt(request.getParameter("propertyId"));
        Date startDate = Date.valueOf(request.getParameter("startDate"));
        Date endDate = Date.valueOf(request.getParameter("endDate"));

        Property property = propertyDAO.getPropertyById(propertyId);

        if (property == null || ! "available".equals(property.getStatus())) {
            session.setAttribute("message", "Property is not available for booking.");
            response.sendRedirect("propertyDetail.jsp?id=" + propertyId); // Redirect back to property detail
            return;
        }

        // Prevent booking own property
        if (property.getUserId() == bookerUser.getId()) {
            session.setAttribute("message", "You cannot book your own property.");
            response.sendRedirect("propertyDetail.jsp?id=" + propertyId);
            return;
        }

        Booking booking = new Booking(propertyId, bookerUser.getId(), property.getUserId(), startDate, endDate);
        int bookingId = bookingDAO.addBooking(booking);

        if (bookingId > 0) {
            // Update property status to 'unavailable'
            propertyDAO.updatePropertyStatus(propertyId, "unavailable");
            session.setAttribute("message", "Property booked successfully! Awaiting confirmation from the owner.");
            response.sendRedirect("myBookings"); // Redirect to bookings page
        } else {
            session.setAttribute("message", "Failed to book property.");
            response.sendRedirect("propertyDetail.jsp?id=" + propertyId);
        }
    }

    private void displayMyBookings(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("BookingServlet: Entering displayMyBookings method.");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("BookingServlet: User not logged in, redirecting to /login.");
            response.sendRedirect("login"); // Redirect to login if not authenticated
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        System.out.println("BookingServlet: Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));

        // Get properties booked by the current user
        System.out.println("BookingServlet: Fetching bookings by booker user ID: " + currentUser.getId());
        List<Booking> bookingsByUser = bookingDAO.getBookingsByBookerUserId(currentUser.getId());
        List<Property> propertiesBookedByUser = new ArrayList<>();
        List<User> ownersOfBookedProperties = new ArrayList<>();
        for (Booking booking : bookingsByUser) {
            Property p = propertyDAO.getPropertyById(booking.getPropertyId());
            if (p != null) {
                propertiesBookedByUser.add(p);
                ownersOfBookedProperties.add(userDAO.getUserById(p.getUserId()));
            }
        }
        request.setAttribute("propertiesBookedByUser", propertiesBookedByUser);
        request.setAttribute("bookingsByUser", bookingsByUser);
        request.setAttribute("ownersOfBookedProperties", ownersOfBookedProperties);
        System.out.println("BookingServlet: Found " + propertiesBookedByUser.size() + " properties booked by user.");


        // Get properties owned by the current user that are booked by others
        System.out.println("BookingServlet: Fetching bookings for owner user ID: " + currentUser.getId());
        List<Booking> bookingsForMyProperties = bookingDAO.getBookingsByOwnerUserId(currentUser.getId());
        List<Property> myPropertiesBookedByOthers = new ArrayList<>();
        List<User> bookersOfMyProperties = new ArrayList<>();
        for (Booking booking : bookingsForMyProperties) {
            Property p = propertyDAO.getPropertyById(booking.getPropertyId());
            if (p != null) {
                myPropertiesBookedByOthers.add(p);
                bookersOfMyProperties.add(userDAO.getUserById(booking.getBookerUserId()));
            }
        }
        request.setAttribute("myPropertiesBookedByOthers", myPropertiesBookedByOthers);
        request.setAttribute("bookingsForMyProperties", bookingsForMyProperties);
        request.setAttribute("bookersOfMyProperties", bookersOfMyProperties);
        System.out.println("BookingServlet: Found " + myPropertiesBookedByOthers.size() + " properties owned by user booked by others.");


        System.out.println("BookingServlet: Forwarding to myBookings.jsp.");
        request.getRequestDispatcher("/WEB-INF/pages/myBookings.jsp").forward(request, response);
        System.out.println("BookingServlet: Forwarding completed.");
    }

    private void acceptOrRejectBooking(HttpServletRequest request, HttpServletResponse response, String newStatus) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login"); // Redirect to login if not authenticated
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            session.setAttribute("message", "Booking not found.");
            response.sendRedirect("myBookings");
            return;
        }

        // Only the owner of the property can accept/reject/cancel the booking
        if (booking.getOwnerUserId() != currentUser.getId()) {
            session.setAttribute("message", "You are not authorized to perform this action.");
            response.sendRedirect("myBookings");
            return;
        }

        if (bookingDAO.updateBookingStatus(bookingId, newStatus)) {
            // If accepted, mark the property as unavailable
            if ("confirmed".equals(newStatus)) {
                propertyDAO.updatePropertyStatus(booking.getPropertyId(), "unavailable");
            } else if ("rejected".equals(newStatus) || "cancelled".equals(newStatus)){ // If rejected or cancelled, make the property available again
                 propertyDAO.updatePropertyStatus(booking.getPropertyId(), "available");
            }
            session.setAttribute("message", "Booking " + newStatus + " successfully.");
        } else {
            session.setAttribute("message", "Failed to update booking status.");
        }
        response.sendRedirect("myBookings");
    }
}
