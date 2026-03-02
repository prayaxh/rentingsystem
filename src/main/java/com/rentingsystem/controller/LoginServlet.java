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

@WebServlet("/")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");

//        System.out.println("LoginServlet: Attempting login for identifier: " + identifier);
//        System.out.println("LoginServlet: Password provided: " + password);

        if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("LoginServlet: Empty identifier or password.");
            request.setAttribute("errorMessage", "Please enter both username/email/phone and password.");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.getUserByUsernameOrEmailOrPhone(identifier);

        if (user != null) {
            System.out.println("LoginServlet: User found in DB. Username: " + user.getUsername() + ", Stored Password: " + user.getPassword());
            if (user.getPassword().equals(password)) {
                System.out.println("LoginServlet: Password matches. Login successful.");
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                System.out.println("LoginServlet: Password mismatch.");
                request.setAttribute("errorMessage", "Invalid credentials.");
                request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
            }
        } else {
            System.out.println("LoginServlet: User not found for identifier: " + identifier);
            request.setAttribute("errorMessage", "Invalid credentials.");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
        }
    }
}