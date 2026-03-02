<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Header</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Alan+Sans:wght@300..900&display=swap" rel="stylesheet">
</head>
<body>
    <header class="header">
        <div class="header__left">
            <a href="javascript:window.location.reload()">
                <img class="header__logo" src="${pageContext.request.contextPath}/images/logo.png" alt="Logo">
            </a>
        </div>

        <div class="header__right">
            <a href="${pageContext.request.contextPath}/postProperty" class="header__link">
                <i class="material-icons">store</i>
                <span>Post</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="header__link">
                <i class="material-icons">person</i>
                <span>Profile</span>
            </a>
            <a href="${pageContext.request.contextPath}/postHistory" class="header__link">
                <i class="material-icons">history</i>
                <span>My Posts</span>
            </a>
            <a href="${pageContext.request.contextPath}/myBookings" class="header__link">
                <i class="material-icons">book</i>
                <span>Bookings</span>
            </a>
        </div>
    </header>
</body>
</html>