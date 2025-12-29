<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.rentingsystem.model.Property" %>
<html>
<head>
    <title>Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>
<body>
    <jsp:include page="/WEB-INF/pages/header.jsp" />

    <div class="container">

        <div id="notification-area" class="notification-area"></div>

        <div class="filter-container">
            <form action="${pageContext.request.contextPath}/home" method="get">
                <select name="loc
                ation">
                    <option value="">All Locations</option>
                    <option value="Kathmandu" ${"Kathmandu".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Kathmandu</option>
                    <option value="Pokhara" ${"Pokhara".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Pokhara</option>
                    <option value="Lalitpur" ${"Lalitpur".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Lalitpur</option>
                    <option value="Bhaktapur" ${"Bhaktapur".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Bhaktapur</option>
                    <option value="Biratnagar" ${"Biratnagar".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Biratnagar</option>
                    <option value="Birgunj" ${"Birgunj".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Birgunj</option>
                    <option value="Dharan" ${"Dharan".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Dharan</option>
                    <option value="Butwal" ${"Butwal".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Butwal</option>
                    <option value="Hetauda" ${"Hetauda".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Hetauda</option>
                    <option value="Janakpur" ${"Janakpur".equals(request.getAttribute("selectedLocation")) ? "selected" : ""}>Janakpur</option>
                </select>
                <select name="type">
                    <option value="">All Types</option>
                    <option value="flat" ${"flat".equals(request.getAttribute("selectedType")) ? "selected" : ""}>Flat</option>
                    <option value="room" ${"room".equals(request.getAttribute("selectedType")) ? "selected" : ""}>Room</option>
                    <option value="shop" ${"shop".equals(request.getAttribute("selectedType")) ? "selected" : ""}>Shop</option>
                    <option value="house" ${"house".equals(request.getAttribute("selectedType")) ? "selected" : ""}>House</option>
                </select>
                <button type="submit">Filter</button>
            </form>
        </div>

        <div class="property-list">
            <%
                List<Property> allProperties = (List<Property>) request.getAttribute("allProperties");
                if (allProperties != null && !allProperties.isEmpty()) {
            %>
                <% for (Property property : allProperties) { %>
                        <div class="property-card">
                            <a href="${pageContext.request.contextPath}/propertyDetails?propertyId=<%= property.getId() %>" class="card-overlay-link"></a>
                            <%
                                String imageUrl = "https://via.placeholder.com/300x180?text=No+Image";
                                if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                                    imageUrl = property.getImageUrls().get(0);
                                }
                            %>
                            <img src="<%= imageUrl %>" alt="<%= property.getTitle() %>">
                            <div class="property-card-content">
                                <h4><%= property.getTitle() %></h4>
                                <p><strong>Type:</strong> <%= property.getTypeName() %></p>
                                <p><strong>Location:</strong> <%= property.getLocationName() != null && !property.getLocationName().isEmpty() ? property.getLocationName() : "N/A" %></p>
                                <p class="price"><%= property.getCurrency() %> <%= String.format("%.2f", property.getPrice()) %></p>
                                <p><strong>Status:</strong> <%= property.getStatus() %></p>
                                <p><strong>Posted:</strong> <%= property.getPostedDate() %></p>
                            </div>
                        </div>
                <% } %>
            <%
                } else {
            %>
                <p class="no-posts">No properties found matching your criteria.</p>
            <%
                }
            %>
        </div>
    </div>


</body>
</html>