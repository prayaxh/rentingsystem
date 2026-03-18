package com.rentingsystem.controller;

import com.rentingsystem.dao.UserDAO;
import com.rentingsystem.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();
        String phone = request.getParameter("phone").trim();
        String password = request.getParameter("password").trim();
        String confirmPassword = request.getParameter("confirmPassword").trim();

        if (name == null || name.isEmpty() || username == null || username.isEmpty() ||
            email == null || email.isEmpty() || phone == null || phone.isEmpty() ||
            password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("errorMessage", "Please fill out all required fields.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        if (!passwordMatcher.matches()) {
            request.setAttribute("errorMessage", "Password: 8+ characters, with letters and numbers.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            request.setAttribute("errorMessage", "Invalid email format.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        Pattern phonePattern = Pattern.compile("^\\d{10}$");
        Matcher phoneMatcher = phonePattern.matcher(phone);
        if (!phoneMatcher.matches()) {
            request.setAttribute("errorMessage", "Phone number must be 10 digits.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByUsernameOrEmailOrPhone(username) != null) {
            request.setAttribute("errorMessage", "Username already exists.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }
        if (userDAO.getUserByUsernameOrEmailOrPhone(email) != null) {
            request.setAttribute("errorMessage", "Email already exists.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }
        if (userDAO.getUserByUsernameOrEmailOrPhone(phone) != null) {
            request.setAttribute("errorMessage", "Phone number already exists.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        User user = new User(name, username, email, phone, password);
        if (userDAO.addUser(user)) {
            HttpSession session = request.getSession();
            session.setAttribute("registrationSuccessMessage", "Registration successful! Please log in.");
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
        }
    }
}