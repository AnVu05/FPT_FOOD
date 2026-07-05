package controller;

import dal.PasswordUtil;
import dal.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

@WebServlet("/AuthController")
public class AuthController extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "login";
        }
        
        switch (action) {
            
            case "login":
                login(request, response);
                break;
            
            case "logout":
                logout(request, response);
                break;
            
            case "register":
                register(request, response);
                break;
            
        }
    }
    
    private void login(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);
        
        if (user == null) {
            request.setAttribute("message",
                    "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp")
                    .forward(request, response);
            return;
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        
        String role = user.getRole().toLowerCase().trim();
        
        switch (role) {
            
            case "manager":
                response.sendRedirect("dashboardController?tab=tables");
                break;
            
            case "worker":
                response.sendRedirect("kitchen.jsp");
                break;
            
            default:
                response.sendRedirect("MainController?action=home");
        }
    }
    
    private void logout(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        session.invalidate();
        
        response.sendRedirect("login.jsp");
    }
    
    private void register(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        
        UserDAO dao = new UserDAO();
        
        String message = null;
        
        if (dao.isUsernameExist(username)) {
            message = "Username đã tồn tại!";
        } else if (dao.isPhoneExist(phone)) {
            message = "Số điện thoại đã được sử dụng!";
        } else if (dao.isEmailExist(email)) {
            message = "Email đã được sử dụng!";
        }
        
        if (message != null) {
            
            request.setAttribute("message", message);
            
            request.getRequestDispatcher("register.jsp")
                    .forward(request, response);
            return;
        }
        
        User user = new User();
        String hashedPassword = PasswordUtil.hashPassword(password, username);
        
        user.setUsername(username);
        user.setPassword(hashedPassword);
//        user.setPassword(password);
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setRole("user");
        user.setStatus("active");
        user.setEmail(email);
        
        dao.register(user);
        
        response.sendRedirect("login.jsp");
    }
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
