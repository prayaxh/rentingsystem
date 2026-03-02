package com.rentingsystem.controller;

import com.rentingsystem.dao.BookingDAO;
import com.rentingsystem.dao.PropertyDAO;
import com.rentingsystem.model.Property;
import com.rentingsystem.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/deleteProperty")
public class DeletePropertyServlet extends HttpServlet {
    private PropertyDAO propertyDAO;
    private BookingDAO bookingDAO; // Declare BookingDAO

    @Override
    public void init() throws ServletException {
        super.init();
        propertyDAO = new PropertyDAO();
        bookingDAO = new BookingDAO(); // Initialize BookingDAO
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"User not logged in.\"}");
            return;
        }

        try {
            int propertyId = Integer.parseInt(request.getParameter("propertyId"));
            User currentUser = (User) session.getAttribute("user");
            int userId = currentUser.getId();

            Property property = propertyDAO.getPropertyById(propertyId);
            if (property != null && property.getUserId() == userId) {
                if (bookingDAO.hasConfirmedBookings(propertyId)) {
                    response.getWriter().write("{\"success\": false, \"message\": \"Cannot delete property with active confirmed bookings.\"}");
                    return;
                }

                boolean deleted = propertyDAO.deleteProperty(propertyId);
                if (deleted) {
                    response.getWriter().write("{\"success\": true, \"message\": \"Property deleted successfully.\"}");
                } else {
                    response.getWriter().write("{\"success\": false, \"message\": \"Failed to delete property.\"}");
                }
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Property not found or you don't have permission to delete it.\"}");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid property ID.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
