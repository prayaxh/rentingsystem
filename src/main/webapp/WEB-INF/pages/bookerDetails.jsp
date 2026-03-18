<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rentingsystem.model.User" %>
<html>
<head>
    <title>Booker Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/propertyDetails.css"> <%-- Re-using for general styling --%>
</head>
<body>
<jsp:include page="/WEB-INF/pages/header.jsp" />

<div class="details-wrapper">
    <div class="user-profile-container">
        <h2>Booker Information</h2>
        <%
            User booker = (User) request.getAttribute("booker");
            if (booker != null) {
        %>
        <p><strong>Name:</strong> <%= booker.getName() %></p>
        <p><strong>Username:</strong> <%= booker.getUsername() %></p>
        <p><strong>Email:</strong> <%= booker.getEmail() %></p>
        <p><strong>Phone:</strong> <%= booker.getPhone() != null && !booker.getPhone().isEmpty() ? booker.getPhone() : "N/A" %></p>
        <%
            } else {
        %>
        <p>Booker information not available.</p>
        <%
            }
        %>
        <p><a href="javascript:history.back()" class="btn btn-primary">Go Back</a></p>
    </div>
</div>

</body>
</html>