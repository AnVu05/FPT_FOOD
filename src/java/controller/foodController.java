/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.FoodDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import model.Food;

/**
 *
 * @author
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class foodController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        action = action == null ? "" : request.getParameter("action");
        String url = "";
        switch (action) {
            case "updateStatusFood":
                updateStatusFood(request, response);
                url = "recipesController";
                break;

            // [THÊM MỚI 3] Bắt sự kiện upload file CSV
            case "uploadFoodCsv":
                uploadFoodCsv(request, response); // Gọi hàm xử lý file
                response.sendRedirect("MainController?action=getRecipe");
                return;
            case "uploadImgFood":
                uploadImgFood(request, response);
                // Sau khi upload xong, quay trở lại đúng tab Món ăn trên Dashboard
                response.sendRedirect("dashboardController?tab=food");
                return;
        }
        request.getRequestDispatcher(url).forward(request, response);
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

    private void updateStatusFood(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        FoodDAO fDAO = new FoodDAO();
        int foodID = Integer.parseInt(request.getParameter("foodId"));
        String status = request.getParameter("status");
        int check = fDAO.updateStatus(foodID, status);
        if (check >= 0) {
            System.out.println("So dong da duoc cap nhat la " + check);
        } else {
            System.out.println("Error");
        }
    }

    // [THÊM MỚI 4] Hàm đọc file CSV và tạo danh sách món ăn
    private void uploadFoodCsv(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Food> listNewFood = new ArrayList<>();

        try {
            // Lấy tệp tin từ form gửi lên (name="csvFile")
            Part filePart = request.getPart("csvFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                // Dùng BufferedReader để đọc từng dòng của tệp tin
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF-8"));
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Bỏ qua dòng tiêu đề đầu tiên trong file CSV
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Bỏ qua các dòng trống
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Tách dữ liệu bằng dấu phẩy
                    String[] data = line.split(",");

                    // Đảm bảo dòng có đủ 5 cột dữ liệu (name, price, categoryID, status, imageURL)
                    if (data.length >= 5) {
                        // Dùng Lombok Builder để tạo đối tượng Food
                        Food food = Food.builder()
                                .name(data[0].trim())
                                .price(Double.parseDouble(data[1].trim()))
                                .categoryID(Integer.parseInt(data[2].trim()))
                                .status(data[3].trim())
                                .imageURL(data[4].trim())
                                .build();

                        listNewFood.add(food);
                    }
                }
                reader.close();

                // Chuẩn bị lưu xuống Database
                if (!listNewFood.isEmpty()) {
                    FoodDAO dao = new FoodDAO();
                    // Ta sẽ gọi hàm insertBatch() ở Bước 4
                    int rowsAffected = dao.insertBatch(listNewFood);

                    if (rowsAffected > 0) {
                        request.setAttribute("mesg", "Tuyệt vời! Đã tải lên thành công " + rowsAffected + " món ăn mới.");
                    } else {
                        request.setAttribute("mesg", "Có lỗi xảy ra khi lưu vào database.");
                    }
                } else {
                    request.setAttribute("mesg", "Tệp CSV trống hoặc định dạng không đúng!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mesg", "Lỗi xử lý file: " + e.getMessage());
        }
    }
    
    // [THÊM MỚI] Hàm xử lý nhận và lưu hình ảnh (Đã sửa lỗi NoSuchFileException)
    private void uploadImgFood(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            // 1. Lấy đường dẫn thư mục Build
            String buildPath = request.getServletContext().getRealPath("/img");
            java.io.File buildDir = new java.io.File(buildPath);
            if (!buildDir.exists()) buildDir.mkdir();

            // 2. Tự động suy ra đường dẫn Source
            String sourcePath = buildPath.replace("build" + java.io.File.separator + "web", "web"); 
            java.io.File sourceDir = new java.io.File(sourcePath);
            boolean canSaveToSource = sourceDir.exists();

            int count = 0;
            
            // 3. Đọc nhiều file được tải lên cùng lúc
            for (Part part : request.getParts()) {
                String fileName = part.getSubmittedFileName();
                
                if (fileName != null && !fileName.trim().isEmpty()) {
                    String baseFileName = java.nio.file.Paths.get(fileName).getFileName().toString();
                    
                    // Tạo đối tượng File cho cả 2 đích đến
                    java.io.File buildFile = new java.io.File(buildPath + java.io.File.separator + baseFileName);
                    java.io.File sourceFile = new java.io.File(sourcePath + java.io.File.separator + baseFileName);

                    // BƯỚC A: Copy dữ liệu từ request vào thư mục Build (Dùng REPLACE_EXISTING để ghi đè nếu trùng tên)
                    try (java.io.InputStream is = part.getInputStream()) {
                        java.nio.file.Files.copy(is, buildFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    // BƯỚC B: Nhân bản file từ Build sang Source để cất giữ vĩnh viễn
                    if (canSaveToSource) {
                        java.nio.file.Files.copy(buildFile.toPath(), sourceFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    count++;
                }
            }

            request.getSession().setAttribute("mesgFood", "Tuyệt vời! Đã tải lên thành công " + count + " hình ảnh.");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mesgFood", "Lỗi khi upload ảnh: " + e.getMessage());
        }
    }
}
