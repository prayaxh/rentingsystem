<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rentingsystem.model.Property" %>
<%@ page import="com.rentingsystem.model.User" %>
<html>
<head>
    <title>Property Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/propertyDetails.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>
<body>
<jsp:include page="/WEB-INF/pages/header.jsp" />

<div class="details-wrapper">
    <div class="property-details-container">
        <%
            Property property = (Property) request.getAttribute("property");
            if (property != null) {
        %>
        <h2><%= property.getTitle() %></h2>
        <div class="property-images">
            <%
                if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                    for (String url : property.getImageUrls()) {
            %>
            <img src="<%= url.trim() %>" alt="<%= property.getTitle() %>" onclick="openLightbox('<%= url.trim() %>')">
            <%
                }
            } else {
            %>
            <img src="https://via.placeholder.com/800x400?text=No+Image" alt="No Image" onclick="openLightbox('https://via.placeholder.com/800x400?text=No+Image')">
            <%
                }
            %>
        </div>
        <div class="property-info">
            <p><strong>Type:</strong> <%= property.getTypeName() %></p>
            <p><strong>Location:</strong> <%= property.getLocationName() %></p>
            <p class="price"><%= property.getCurrency() %> <%= String.format("%.2f", property.getPrice()) %></p>
            <p><strong>Description:</strong> <%= property.getDescription() %></p>
            <p><strong>Status:</strong> <%= property.getStatus() %></p>
            <p><strong>Posted:</strong> <%= property.getPostedDate() %></p>
            <%
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null && property.getStatus().equals("available") && currentUser.getId() != property.getUserId()) {
            %>
            <div class="book-now-section">
                <h4 style="color: #28a745;">Book this Property</h4>
                <form action="${pageContext.request.contextPath}/bookProperty" method="post">
                    <input type="hidden" name="propertyId" value="<%= property.getId() %>">

                    <div class="form-group">
                        <label for="startDate">Start Date:</label>
                        <input type="date" id="startDate" name="startDate" style="border: 1px solid #ccc; padding: 5px; border-radius: 4px;" required>
                    </div>

                    <div class="form-group">
                        <label for="endDate">End Date:</label>
                        <input type="date" id="endDate" name="endDate" style="border: 1px solid #ccc; padding: 5px; border-radius: 4px;" required>
                    </div>

                    <div style="text-align: center; margin-top: 10px;">
                        <button type="submit" style="background-color: red; border: 1px solid #aaa; padding: 5px 10px; border-radius: 4px; cursor: pointer; color: white; font-weight: bold;">Book Now</button>
                    </div>
                </form>
            </div>
            <%
                }
            %>
        </div>
        <%
        } else {
        %>
        <h2>Property not found</h2>
        <%
            }
        %>
    </div>
    <div class="user-profile-container">
        <h3>Owner Information</h3>
        <%
            User owner = (User) request.getAttribute("owner");
            if (owner != null) {
        %>
        <p><strong>Name:</strong> <%= owner.getName() %></p>
        <p><strong>Username:</strong> <%= owner.getUsername() %></p>
        <p><strong>Email:</strong> <%= owner.getEmail() %></p>
        <p><strong>Phone:</strong> <%= owner.getPhone() != null && !owner.getPhone().isEmpty() ? owner.getPhone() : "N/A" %></p>
        <%
        } else {
        %>
        <p>Owner information not available.</p>
        <%
            }
        %>
    </div>
</div>

<div id="lightbox" class="lightbox" onclick="closeLightbox()">
    <span class="close-button">&times;</span>
    <img class="lightbox-content" id="lightbox-img">
</div>

<script>
    function openLightbox(imageUrl) {
        document.getElementById('lightbox').style.display = 'flex';
        document.getElementById('lightbox-img').src = imageUrl;
        document.body.style.overflow = 'hidden';
    }

    function closeLightbox() {
        document.getElementById('lightbox').style.display = 'none';
        document.body.style.overflow = 'auto';
    }
</script>
</body>
</html>