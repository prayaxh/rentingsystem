package com.rentingsystem.controller;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/postProperty")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50)
public class PostPropertyServlet extends HttpServlet {
    private PropertyDAO propertyDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        propertyDAO = new PropertyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            request.setAttribute("errorMessage", "Please login to post a property.");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
            return;
        }

        List<String> propertyTypes = propertyDAO.getPropertyTypes();
        List<String> locations = propertyDAO.getLocations();

        request.setAttribute("propertyTypes", propertyTypes);
        request.setAttribute("locations", locations);

        request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
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
            request.setAttribute("propertyTypes", propertyDAO.getPropertyTypes());
            request.setAttribute("locations", propertyDAO.getLocations());
            request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid price format.");
            request.setAttribute("propertyTypes", propertyDAO.getPropertyTypes());
            request.setAttribute("locations", propertyDAO.getLocations());
            request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
            return;
        }

        int typeId = propertyDAO.getTypeId(typeName);
        int locationId = propertyDAO.getLocationId(locationName);

        if (typeId == -1 || locationId == -1) {
            request.setAttribute("errorMessage", "Invalid property type or location selected.");
            request.setAttribute("propertyTypes", propertyDAO.getPropertyTypes());
            request.setAttribute("locations", propertyDAO.getLocations());
            request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
            return;
        }

        Property property = new Property(userId, title, description, typeId, price, currency, locationId);

        int propertyId = propertyDAO.addProperty(property);

        if (propertyId != -1) {
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "images";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("imageFiles") && part.getSize() > 0) {
                    // Sanitize filename: remove spaces and special chars, keep extension
                    String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int i = originalFileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = originalFileName.substring(i);
                    }
                    String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    
                    String fileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;
                    String filePath = uploadPath + File.separator + fileName;
                    
                    try (InputStream input = part.getInputStream()) {
                        // Save to deployment directory (target/...)
                        Files.copy(input, new File(filePath).toPath());
                        
                        // Attempt to save to source directory for persistence (Dev mode)
                        // Assuming standard Maven structure: target/rentingsystem/ -> src/main/webapp/
                        File deployDir = new File(getServletContext().getRealPath(""));
                        File projectRoot = deployDir.getParentFile().getParentFile(); // Up from target/rentingsystem
                        File srcDir = new File(projectRoot, "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "uploads" + File.separator + "images");
                        
                        if (srcDir.exists() || srcDir.mkdirs()) {
                             Files.copy(new File(filePath).toPath(), new File(srcDir, fileName).toPath());
                        }

                        String imageUrl = request.getContextPath() + "/uploads/images/" + fileName;
                        propertyDAO.addPropertyImage(propertyId, imageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Error uploading image: " + e.getMessage());
                        request.setAttribute("propertyTypes", propertyDAO.getPropertyTypes());
                        request.setAttribute("locations", propertyDAO.getLocations());
                        request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
                        return;
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("errorMessage", "Failed to post property. Please try again.");
            request.setAttribute("propertyTypes", propertyDAO.getPropertyTypes());
            request.setAttribute("locations", propertyDAO.getLocations());
            request.getRequestDispatcher("/WEB-INF/pages/post.jsp").forward(request, response);
        }
    }
}