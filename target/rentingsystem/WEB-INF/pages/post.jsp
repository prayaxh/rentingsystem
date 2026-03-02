<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Post New Property</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/post.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Alan+Sans:wght@300..900&display=swap" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/pages/header.jsp" />

    <div class="post-container">
        <h2>Post New Property for Rent</h2>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <p class="message error"><%= errorMessage %></p>
        <%
            }
            List<String> propertyTypes = (List<String>) request.getAttribute("propertyTypes");
            List<String> locations = (List<String>) request.getAttribute("locations");
        %>
        <form action="${pageContext.request.contextPath}/postProperty" method="post" enctype="multipart/form-data">
            <div class="form-row">
                <div class="input-group">
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" value="<%= request.getParameter("title") != null ? request.getParameter("title") : "" %>" required>
                </div>
                <div class="input-group">
                    <label for="type">Property Type:</label>
                    <select id="type" name="type" required>
                        <option value="">Select Type</option>
                        <%
                            if (propertyTypes != null) {
                                for (String type : propertyTypes) {
                        %>
                                    <option value="<%= type %>" <%= type.equals(request.getParameter("type")) ? "selected" : "" %>><%= type %></option>
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
                    <input type="number" id="price" name="price" step="0.01" min="0" value="<%= request.getParameter("price") != null ? request.getParameter("price") : "" %>" required>
                </div>
                <div class="input-group">
                    <label for="currency">Currency:</label>
                    <select id="currency" name="currency" required>
                        <option value="NPR" <%= "NPR".equals(request.getParameter("currency")) ? "selected" : "" %>>NPR</option>
                        <option value="USD" <%= "USD".equals(request.getParameter("currency")) ? "selected" : "" %>>USD</option>
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
                                <option value="<%= loc %>" <%= loc.equals(request.getParameter("location")) ? "selected" : "" %>><%= loc %></option>
                    <%
                            }
                        }
                    %>
                </select>
            </div>

            <div class="input-group full-width">
                <label for="description">Description:</label>
                <textarea id="description" name="description" rows="5"><%= request.getParameter("description") != null ? request.getParameter("description") : "" %></textarea>
            </div>

            <div class="input-group full-width">
                <label for="imageFiles">Upload Images:</label>
                <input type="file" id="imageFiles" name="imageFiles" multiple accept="image/*">
            </div>

            <button class="postbut" type="submit">Post Property</button>
        </form>
    </div>
</body>
</html>