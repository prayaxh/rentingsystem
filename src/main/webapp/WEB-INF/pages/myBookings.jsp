<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Bookings</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/myBookings.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container my-bookings-container">
        <h2>My Bookings</h2>

        <c:if test="${not empty message}">
            <div class="alert alert-info">${message}</div>
            <c:remove var="message" scope="session"/>
        </c:if>

        <!-- Properties I've Booked -->
        <h3>Properties I've Booked</h3>
        <c:choose>
            <c:when test="${not empty propertiesBookedByUser}">
                <div class="property-list">
                    <c:forEach var="property" items="${propertiesBookedByUser}" varStatus="loop">
                        <c:set var="booking" value="${bookingsByUser[loop.index]}"/>
                        <c:set var="owner" value="${ownersOfBookedProperties[loop.index]}"/>
                        <div class="property-card">
                            <h4><c:out value="${property.title}"/></h4>
                            <p class="status-${booking.status}"><strong>Status:</strong> <c:out value="${booking.status}"/></p>
                            <p><strong>Owner:</strong> <c:out value="${owner.name}"/></p>
                            <p><strong>Booking Dates:</strong> <fmt:formatDate value="${booking.startDate}" pattern="yyyy-MM-dd"/> to <fmt:formatDate value="${booking.endDate}" pattern="yyyy-MM-dd"/></p>
                            <p><a href="${pageContext.request.contextPath}/propertyDetails?propertyId=<c:out value="${property.id}"/>">View Details</a></p>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p>You haven't booked any properties yet.</p>
            </c:otherwise>
        </c:choose>

        <hr>

        <!-- My Properties Booked by Others -->
        <h3>My Properties Booked by Others</h3>
        <c:choose>
            <c:when test="${not empty myPropertiesBookedByOthers}">
                <div class="property-list">
                    <c:forEach var="property" items="${myPropertiesBookedByOthers}" varStatus="loop">
                        <c:set var="booking" value="${bookingsForMyProperties[loop.index]}"/>
                        <c:set var="booker" value="${bookersOfMyProperties[loop.index]}"/>
                        <div class="property-card">
                            <h4><c:out value="${property.title}"/></h4>
                            <p class="status-${booking.status}"><strong>Status:</strong> <c:out value="${booking.status}"/></p>
                            <p><strong>Booked By:</strong> <c:out value="${booker.name}"/></p>
                            <p><strong>Booker Contact:</strong> <c:out value="${booker.email}"/>, <c:out value="${booker.phone}"/></p>
                            <p><strong>Booking Dates:</strong> <fmt:formatDate value="${booking.startDate}" pattern="yyyy-MM-dd"/> to <fmt:formatDate value="${booking.endDate}" pattern="yyyy-MM-dd"/></p>
                            <p><a href="${pageContext.request.contextPath}/propertyDetails?propertyId=<c:out value="${property.id}"/>">View Details</a></p>
                            <c:if test="${booking.status eq 'pending'}">
                                <div class="booking-actions">
                                    <form action="${pageContext.request.contextPath}/myBookings" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="acceptBooking">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <button type="submit" class="btn btn-success btn-small">Accept</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/myBookings" method="post" style="display:inline; margin-left: 10px;">
                                        <input type="hidden" name="action" value="rejectBooking">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <button type="submit" class="btn btn-danger btn-small">Reject</button>
                                    </form>
                                </div>
                            </c:if>
                            <c:if test="${booking.status eq 'confirmed'}">
                                <div class="booking-actions">
                                    <form action="${pageContext.request.contextPath}/myBookings" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="cancelBooking">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <button type="submit" class="btn btn-warning btn-small">Cancel Booking</button>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p>None of your properties have been booked by others yet.</p>
            </c:otherwise>
        </c:choose>
    </div>


</body>
</html>