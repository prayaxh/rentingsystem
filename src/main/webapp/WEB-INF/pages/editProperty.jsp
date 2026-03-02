<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rentingsystem.model.Property" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Edit Property</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editProperty.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Alan+Sans:wght@300..900&display=swap" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/pages/header.jsp" />

    <div class="edit-property-container">
        <h2>Edit Property</h2>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            String successMessage = (String) request.getAttribute("successMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <p class="message error"><%= errorMessage %></p>
        <%
            } else if (successMessage != null && !successMessage.isEmpty()) {
        %>
            <p class="message success"><%= successMessage %></p>
        <%
            }

            Property property = (Property) request.getAttribute("property");
            List<String> propertyTypes = (List<String>) request.getAttribute("propertyTypes");
            List<String> locations = (List<String>) request.getAttribute("locations");

            if (property != null) {
        %>
                <form action="${pageContext.request.contextPath}/editProperty" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="propertyId" value="<%= property.getId() %>">

                    <div class="form-row">
                        <div class="input-group">
                            <label for="title">Title:</label>
                            <input type="text" id="title" name="title" value="<%= property.getTitle() %>" required>
                        </div>
                        <div class="input-group">
                            <label for="type">Property Type:</label>
                            <select id="type" name="type" required>
                                <option value="">Select Type</option>
                                <%
                                    if (propertyTypes != null) {
                                        for (String type : propertyTypes) {
                                %>
                                            <option value="<%= type %>" <%= type.equals(property.getTypeName()) ? "selected" : "" %>><%= type %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="input-group">
                            <label for="price">Price:</label>
                            <input type="number" id="price" name="price" step="0.01" min="0" value="<%= property.getPrice() %>" required>
                        </div>
                        <div class="input-group">
                            <label for="currency">Currency:</label>
                            <select id="currency" name="currency" required>
                                <option value="NPR" <%= "NPR".equals(property.getCurrency()) ? "selected" : "" %>>NPR</option>
                                <option value="USD" <%= "USD".equals(property.getCurrency()) ? "selected" : "" %>>USD</option>
                            </select>
                        </div>
                    </div>

                    <div class="input-group full-width">
                        <label for="location">Location:</label>
                        <select id="location" name="location" required>
                            <option value="">Select Location</option>
                            <%
                                if (locations != null) {
                                    for (String loc : locations) {
                            %>
                                        <option value="<%= loc %>" <%= loc.equals(property.getLocationName()) ? "selected" : "" %>><%= loc %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="input-group full-width">
                        <label for="description">Description:</label>
                        <textarea id="description" name="description" rows="5"><%= property.getDescription() %></textarea>
                    </div>

                    <div class="input-group full-width">
                        <label>Current Images:</label>
                        <div class="current-images">
                            <%
                                if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                                    for (String imageUrl : property.getImageUrls()) {
                            %>
                                        <div class="image-preview">
                                            <img src="<%= imageUrl %>" alt="Property Image">
                                            <button type="button" class="remove-image-button" data-image-url="<%= imageUrl %>">Remove</button>
                                        </div>
                            <%
                                    }
                                } else {
                            %>
                                <p>No images uploaded yet.</p>
                            <%
                                }
                            %>
                        </div>
                    </div>

                    <div class="input-group full-width">
                        <label for="newImageFiles">Add New Images:</label>
                        <input type="file" id="newImageFiles" name="newImageFiles" multiple accept="image/*">
                    </div>

                    <div class="form-actions">
                        <button type="submit" name="action" value="update" class="update-button">Update Property</button>
                        <button type="submit" name="action" value="toggleStatus" class="toggle-status-button <%= "available".equalsIgnoreCase(property.getStatus()) ? "mark-unavailable" : "mark-available" %>">
                            <%= "available".equalsIgnoreCase(property.getStatus()) ? "Mark as Unavailable" : "Mark as Available" %>
                        </button>
                        <button type="submit" name="action" value="delete" class="delete-button" onclick="return confirm('Are you sure you want to delete this property?');">Delete Property</button>
                    </div>
                </form>
        <%
            } else {
        %>
                <p>Property not found or you are not authorized to edit it.</p>
        <%
            }
        %>
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

        document.querySelectorAll('.remove-image-button').forEach(button => {
            button.addEventListener('click', function() {
                const imageUrl = this.dataset.imageUrl;
                const propertyId = document.querySelector('input[name="propertyId"]').value;
                if (confirm('Are you sure you want to remove this image?')) {
                    fetch('${pageContext.request.contextPath}/editProperty', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: 'action=removeImage&propertyId=' + propertyId + '&imageUrl=' + encodeURIComponent(imageUrl)
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            this.closest('.image-preview').remove();
                        } else {
                            alert('Failed to remove image: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('An error occurred while removing the image.');
                    });
                }
            });
        });
    </script>
</body>
</html>