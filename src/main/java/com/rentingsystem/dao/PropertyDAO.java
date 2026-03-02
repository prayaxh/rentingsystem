package com.rentingsystem.dao;

import com.rentingsystem.model.Property;
import com.rentingsystem.utility.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {

    public int addProperty(Property property) {
        String sql = "INSERT INTO properties (user_id, title, description, type_id, price, currency, location_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int propertyId = -1;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, property.getUserId());
            stmt.setString(2, property.getTitle());
            stmt.setString(3, property.getDescription());
            stmt.setInt(4, property.getTypeId());
            stmt.setDouble(5, property.getPrice());
            stmt.setString(6, property.getCurrency());
            stmt.setInt(7, property.getLocationId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        propertyId = generatedKeys.getInt(1);
                        property.setId(propertyId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyId;
    }

    public boolean addPropertyImage(int propertyId, String imageUrl) {
        String sql = "INSERT INTO property_images (property_id, image_url) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            stmt.setString(2, imageUrl);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePropertyImage(int propertyId, String imageUrl) {
        String sql = "DELETE FROM property_images WHERE property_id = ? AND image_url = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            stmt.setString(2, imageUrl);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<String> getPropertyImages(int propertyId) {
        List<String> imageUrls = new ArrayList<>();
        String sql = "SELECT image_url FROM property_images WHERE property_id = ? ORDER BY image_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    imageUrls.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

    public boolean updateProperty(Property property) {
        String sql = "UPDATE properties SET title = ?, description = ?, type_id = ?, price = ?, currency = ?, location_id = ?, status = ? WHERE property_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, property.getTitle());
            stmt.setString(2, property.getDescription());
            stmt.setInt(3, property.getTypeId());
            stmt.setDouble(4, property.getPrice());
            stmt.setString(5, property.getCurrency());
            stmt.setInt(6, property.getLocationId());
            stmt.setString(7, property.getStatus());
            stmt.setInt(8, property.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Property> getPropertiesByUserId(int userId) {
        List<Property> properties = new ArrayList<>();
        String sql = "SELECT p.property_id, p.user_id, p.title, p.description, p.type_id, pt.type_name, p.price, p.currency, p.location_id, l.location_name, p.posted_date, p.status FROM properties p JOIN property_types pt ON p.type_id = pt.type_id JOIN locations l ON p.location_id = l.location_id WHERE p.user_id = ? ORDER BY p.posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Property property = new Property();
                    property.setId(rs.getInt("property_id"));
                    property.setUserId(rs.getInt("user_id"));
                    property.setTitle(rs.getString("title"));
                    property.setDescription(rs.getString("description"));
                    property.setTypeId(rs.getInt("type_id"));
                    property.setTypeName(rs.getString("type_name"));
                    property.setPrice(rs.getDouble("price"));
                    property.setCurrency(rs.getString("currency"));
                    property.setLocationId(rs.getInt("location_id"));
                    property.setLocationName(rs.getString("location_name"));
                    property.setPostedDate(rs.getTimestamp("posted_date"));
                    property.setStatus(rs.getString("status"));
                    property.setImageUrls(getPropertyImages(property.getId()));
                    properties.add(property);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        String sql = "SELECT p.property_id, p.user_id, p.title, p.description, p.type_id, pt.type_name, p.price, p.currency, p.location_id, l.location_name, p.posted_date, p.status FROM properties p JOIN property_types pt ON p.type_id = pt.type_id JOIN locations l ON p.location_id = l.location_id ORDER BY p.posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Property property = new Property();
                property.setId(rs.getInt("property_id"));
                property.setUserId(rs.getInt("user_id"));
                property.setTitle(rs.getString("title"));
                property.setDescription(rs.getString("description"));
                property.setTypeId(rs.getInt("type_id"));
                property.setTypeName(rs.getString("type_name"));
                property.setPrice(rs.getDouble("price"));
                property.setCurrency(rs.getString("currency"));
                property.setLocationId(rs.getInt("location_id"));
                property.setLocationName(rs.getString("location_name"));
                property.setPostedDate(rs.getTimestamp("posted_date"));
                property.setStatus(rs.getString("status"));
                property.setImageUrls(getPropertyImages(property.getId()));
                properties.add(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public boolean deleteProperty(int propertyId) {
        String deleteImagesSql = "DELETE FROM property_images WHERE property_id = ?";
        String deleteBookingsSql = "DELETE FROM bookings WHERE property_id = ?"; // NEW: Delete associated bookings
        String deletePropertySql = "DELETE FROM properties WHERE property_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            System.out.println("PropertyDAO: Attempting to delete property with ID: " + propertyId);

            try (PreparedStatement stmtImages = conn.prepareStatement(deleteImagesSql)) {
                stmtImages.setInt(1, propertyId);
                int imagesDeleted = stmtImages.executeUpdate();
                System.out.println("PropertyDAO: Deleted " + imagesDeleted + " images for property ID: " + propertyId);
            }

            // NEW: Delete associated bookings before deleting the property
            try (PreparedStatement stmtBookings = conn.prepareStatement(deleteBookingsSql)) {
                stmtBookings.setInt(1, propertyId);
                int bookingsDeleted = stmtBookings.executeUpdate();
                System.out.println("PropertyDAO: Deleted " + bookingsDeleted + " bookings for property ID: " + propertyId);
            }

            try (PreparedStatement stmtProperty = conn.prepareStatement(deletePropertySql)) {
                stmtProperty.setInt(1, propertyId);
                int rowsAffected = stmtProperty.executeUpdate();
                System.out.println("PropertyDAO: Deleted " + rowsAffected + " properties for property ID: " + propertyId);
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("PropertyDAO: SQL Exception during property deletion, rolling back transaction. Property ID: " + propertyId + ", Error: " + e.getMessage());
                throw e; // Re-throw to propagate the original error
            }
        } catch (SQLException e) {
            System.err.println("PropertyDAO: SQL Exception during transaction setup/rollback for property ID: " + propertyId + ", Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("PropertyDAO: Unexpected Exception during property deletion for property ID: " + propertyId + ", Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePropertyStatus(int propertyId, String status) {
        String sql = "UPDATE properties SET status = ? WHERE property_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, propertyId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Property> getFilteredProperties(String locationName, String typeName) {
        List<Property> properties = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.property_id, p.user_id, p.title, p.description, p.type_id, pt.type_name, p.price, p.currency, p.location_id, l.location_name, p.posted_date, p.status FROM properties p JOIN property_types pt ON p.type_id = pt.type_id JOIN locations l ON p.location_id = l.location_id WHERE 1=1");

        if (locationName != null && !locationName.isEmpty()) {
            sql.append(" AND l.location_name = ?");
        }
        if (typeName != null && !typeName.isEmpty()) {
            sql.append(" AND pt.type_name = ?");
        }
        sql.append(" ORDER BY p.posted_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (locationName != null && !locationName.isEmpty()) {
                stmt.setString(paramIndex++, locationName);
            }
            if (typeName != null && !typeName.isEmpty()) {
                stmt.setString(paramIndex++, typeName);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Property property = new Property();
                    property.setId(rs.getInt("property_id"));
                    property.setUserId(rs.getInt("user_id"));
                    property.setTitle(rs.getString("title"));
                    property.setDescription(rs.getString("description"));
                    property.setTypeId(rs.getInt("type_id"));
                    property.setTypeName(rs.getString("type_name"));
                    property.setPrice(rs.getDouble("price"));
                    property.setCurrency(rs.getString("currency"));
                    property.setLocationId(rs.getInt("location_id"));
                    property.setLocationName(rs.getString("location_name"));
                    property.setPostedDate(rs.getTimestamp("posted_date"));
                    property.setStatus(rs.getString("status"));
                    property.setImageUrls(getPropertyImages(property.getId()));
                    properties.add(property);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Property getPropertyById(int propertyId) {
        String sql = "SELECT p.property_id, p.user_id, p.title, p.description, p.type_id, pt.type_name, p.price, p.currency, p.location_id, l.location_name, p.posted_date, p.status FROM properties p JOIN property_types pt ON p.type_id = pt.type_id JOIN locations l ON p.location_id = l.location_id WHERE p.property_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Property property = new Property();
                    property.setId(rs.getInt("property_id"));
                    property.setUserId(rs.getInt("user_id"));
                    property.setTitle(rs.getString("title"));
                    property.setDescription(rs.getString("description"));
                    property.setTypeId(rs.getInt("type_id"));
                    property.setTypeName(rs.getString("type_name"));
                    property.setPrice(rs.getDouble("price"));
                    property.setCurrency(rs.getString("currency"));
                    property.setLocationId(rs.getInt("location_id"));
                    property.setLocationName(rs.getString("location_name"));
                    property.setPostedDate(rs.getTimestamp("posted_date"));
                    property.setStatus(rs.getString("status"));
                    property.setImageUrls(getPropertyImages(property.getId()));
                    return property;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getPropertyTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT type_name FROM property_types";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                types.add(rs.getString("type_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public List<String> getLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT location_name FROM locations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(rs.getString("location_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    public int getTypeId(String typeName) {
        String sql = "SELECT type_id FROM property_types WHERE type_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, typeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("type_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getLocationId(String locationName) {
        String sql = "SELECT location_id FROM locations WHERE location_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, locationName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("location_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
