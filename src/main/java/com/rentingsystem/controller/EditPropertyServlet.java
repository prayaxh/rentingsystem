package com.rentingsystem.controller;

import com.rentingsystem.dao.BookingDAO;
import com.rentingsystem.dao.PropertyDAO;
import com.rentingsystem.model.Property;
import com.rentingsystem.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@WebServlet("/editProperty")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50)
public class EditPropertyServlet extends HttpServlet {
    private PropertyDAO propertyDAO;
    private BookingDAO bookingDAO; // Declare BookingDAO

    @Override
    public void init() throws ServletException {
        super.init();
        propertyDAO = new PropertyDAO();
        bookingDAO = new BookingDAO(); // Initialize BookingDAO
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int userId = currentUser.getId();
        int propertyId = 0;
        try {
            propertyId = Integer.parseInt(request.getParameter("propertyId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Property ID");
            return;
        }

        Property property = propertyDAO.getPropertyById(propertyId);

        if (property == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Property not found");
            return;
        }

        if (property.getUserId() != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to edit this property.");
            return;
        }

        List<String> propertyTypes = propertyDAO.getPropertyTypes();
        List<String> locations = propertyDAO.getLocations();

        request.setAttribute("property", property);
        request.setAttribute("propertyTypes", propertyTypes);
        request.setAttribute("locations", locations);

        request.getRequestDispatcher("/WEB-INF/pages/editProperty.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int userId = currentUser.getId();
        String action = request.getParameter("action");
        int propertyId = Integer.parseInt(request.getParameter("propertyId"));

        Property existingProperty = propertyDAO.getPropertyById(propertyId);
        if (existingProperty == null || existingProperty.getUserId() != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Property not found or you are not authorized to edit it.");
            return;
        }

        switch (action) {
            case "update":
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String typeName = request.getParameter("type");
                String priceStr = request.getParameter("price");
                String currency = request.getParameter("currency");
                String locationName = request.getParameter("location");

                if (title == null || title.isEmpty() || typeName == null || typeName.isEmpty() ||
                    priceStr == null || priceStr.isEmpty() || currency == null || currency.isEmpty() ||
                    locationName == null || locationName.isEmpty()) {
                    request.setAttribute("errorMessage", "Title, Type, Price, Currency, and Location are required fields.");
                    doGet(request, response);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid price format.");
                    doGet(request, response);
                    return;
                }

                int typeId = propertyDAO.getTypeId(typeName);
                int locationId = propertyDAO.getLocationId(locationName);

                if (typeId == -1 || locationId == -1) {
                    request.setAttribute("errorMessage", "Invalid property type or location selected.");
                    doGet(request, response);
                    return;
                }

                existingProperty.setTitle(title);
                existingProperty.setDescription(description);
                existingProperty.setTypeId(typeId);
                existingProperty.setPrice(price);
                existingProperty.setCurrency(currency);
                existingProperty.setLocationId(locationId);

                boolean updated = propertyDAO.updateProperty(existingProperty);

                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "images";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                for (Part part : request.getParts()) {
                    if (part.getName().equals("newImageFiles") && part.getSize() > 0) {
                        String fileName = UUID.randomUUID().toString() + "_" + Paths.get(part.getSubmittedFileName()).getFileName().toString();
                        String filePath = uploadPath + File.separator + fileName;
                        try (InputStream input = part.getInputStream()) {
                            Files.copy(input, new File(filePath).toPath());
                            String imageUrl = request.getContextPath() + "/uploads/images/" + fileName;
                            propertyDAO.addPropertyImage(propertyId, imageUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                            request.setAttribute("errorMessage", "Error uploading image: " + e.getMessage());
                            doGet(request, response);
                            return;
                        }
                    }
                }

                if (updated) {
                    request.setAttribute("successMessage", "Property updated successfully!");
                } else {
                    request.setAttribute("errorMessage", "Failed to update property.");
                }
                doGet(request, response);
                break;

            case "toggleStatus":
                String currentStatus = existingProperty.getStatus();
                String newStatus = "available".equalsIgnoreCase(currentStatus) ? "unavailable" : "available";
                if (propertyDAO.updatePropertyStatus(propertyId, newStatus)) {
                    request.setAttribute("successMessage", "Property status updated to " + newStatus + "!");
                } else {
                    request.setAttribute("errorMessage", "Failed to update property status.");
                }
                doGet(request, response);
                break;

            case "delete":
                if (bookingDAO.hasConfirmedBookings(propertyId)) {
                    request.setAttribute("errorMessage", "Cannot delete property with active confirmed bookings.");
                    doGet(request, response);
                    return;
                }

                if (propertyDAO.deleteProperty(propertyId)) {
                    response.sendRedirect(request.getContextPath() + "/postHistory");
                } else {
                    request.setAttribute("errorMessage", "Failed to delete property.");
                    doGet(request, response);
                }
                break;

            case "removeImage":
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                String imageUrlToRemove = request.getParameter("imageUrl");
                if (imageUrlToRemove != null && !imageUrlToRemove.isEmpty()) {
                    if (propertyDAO.deletePropertyImage(propertyId, imageUrlToRemove)) {
                        String contextPath = request.getContextPath();
                        String relativePath = imageUrlToRemove.substring(contextPath.length());
                        String fullPath = getServletContext().getRealPath("") + relativePath;
                        File imageFile = new File(fullPath);
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                        out.print("{\"success\": true, \"message\": \"Image removed successfully.\"}");
                    } else {
                        out.print("{\"success\": false, \"message\": \"Failed to remove image from database.\"}");
                    }
                } else {
                    out.print("{\"success\": false, \"message\": \"Image URL not provided.\"}");
                }
                break;

            default:
                request.setAttribute("errorMessage", "Unknown action.");
                doGet(request, response);
                break;
        }
    }
}