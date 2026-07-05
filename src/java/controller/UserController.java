/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.PasswordUtil;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.User;
// [THÊM MỚI] Import để đọc luồng dữ liệu tệp CSV
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

/**
 *
 * @author
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        action = action == null ? "" : request.getParameter("action");
        switch (action) {
            case "deleteUser":
                doDeleteAccount(request, response);
                break;
            case "addUser":
                doAdd(request, response);
                break;
            case "uploadUserCsv":
                handleUserCsvUpload(request, response);
                return;
        }
        displayAccount(request, response);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void displayAccount(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        UserDAO uDAO = new UserDAO();
        ArrayList<User> listUser = uDAO.getAllAccountWorkerAndAdmin();
        request.setAttribute("listUser", listUser);
        request.setAttribute("activeTab", "accounts");
    }

    private void doDeleteAccount(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int userID = Integer.parseInt(request.getParameter("id"));
        UserDAO uDAO = new UserDAO();
        int check = uDAO.deleteAccount(userID);
        if (check > 0) {
            System.out.println("So dong da thay doi la: " + check);
        } else {
            System.out.println("Error");
        }
    }

    private void doAdd(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        User u = User.builder()
                .username(username)
                .password(password)
                .role(role)
                .status("active").build();
        System.out.println(u.toString());
        UserDAO uDAO = new UserDAO();
        uDAO.register(u);
    }

    private void handleUserCsvUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<model.User> listNewUser = new ArrayList<>();
        UserDAO dao = new UserDAO();
        try {
            Part filePart = request.getPart("csvUserFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF-8"));
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    } // Bỏ qua tiêu đề
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    String[] data = line.split(",");
                    // Cấu trúc: username, password, role, fullname, phone, status, email
                    if (data.length >= 7) {
                        String hashedPassword = PasswordUtil.hashPassword(data[1].trim(), data[0].trim());
                        model.User u = model.User.builder()
                                .username(data[0].trim())
                                .password(hashedPassword)
                                .role(data[2].trim())
                                .fullname(data[3].trim())
                                .phone(data[4].trim())
                                .status(data[5].trim())
                                .email(data[6].trim())
                                .build();
                        listNewUser.add(u);
                    }
                }
                reader.close();

                if (!listNewUser.isEmpty()) {
                    
                    // Hàm insertBatch sẽ tạo ở Bước 4
                    int rowsAffected = dao.insertBatch(listNewUser);
                    if (rowsAffected > 0) {
                        request.getSession().setAttribute("mesgUser", "Tuyệt vời! Đã thêm thành công " + rowsAffected + " tài khoản.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mesgUser", "Lỗi xử lý tệp: " + e.getMessage());
        }
        // Quay về trang danh sách tài khoản qua MainController
        response.sendRedirect("MainController?action=getAccounts");
    }
}
