/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.CategoryDAO;
import dal.DiningTableDAO;
import dal.FoodDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Category;
import model.DiningTable;
import model.Food;
import model.User;
// [THÊM MỚI 1] Import thư viện đọc file và List
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
/**
 *
 * @author Nitro 5 Tiger
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
@WebServlet(name = "dashboardController", urlPatterns = {"/dashboardController"})
public class dashboardController extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");

        if (action != null) {

            switch (action) {

                case "updateTable":
                    updateTable(request, response);
                    return;

                case "insertCategory":
                case "updateCategory":
                case "deleteCategory":
                    handleCategory(request, response);
                    return;

                case "addFood":
                case "updateFood":
                case "deleteFood":
                    handleFood(request, response);
                    return;
                case "uploadFoodCsvDashboard":
                    handleFoodCsvUpload(request, response);
                    return;
                case "uploadCategoryCsv":
                    handleCategoryCsvUpload(request, response);
                    return;
            }
        }

        // ===== LOAD DASHBOARD =====
        loadDashboard(request, response);
    }

    private void loadDashboard(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "tables";
        }

        request.setAttribute("activeTab", tab);

        DiningTableDAO tableDAO = new DiningTableDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        FoodDAO foodDAO = new FoodDAO();

        switch (tab) {

            case "tables":

                List<DiningTable> tables = tableDAO.getAll();
                System.out.println("TABLE SIZE = " + tables.size());

                for (DiningTable t : tables) {
                    String status = t.getStatus();

//                    int activeOrder = tableDAO.countActiveOrders(t.getTableID());
//
//                    if (activeOrder > 0) {
//
//                        int count = tableDAO.getServingItemCount(t.getTableID());
//                        double total = tableDAO.getTotalByTable(t.getTableID());
//
//                        t.setServingCount(count);
//                        t.setCurrentTotal(total);
                    if ("busy".equals(status)) {

                        int count = tableDAO.getServingItemCount(t.getTableID());
                        double total = tableDAO.getTotalByTable(t.getTableID());

                        t.setServingCount(count);
                        t.setCurrentTotal(total);

                    } else {

                        t.setServingCount(0);
                        t.setCurrentTotal(0.0);
                    }
                }

                request.setAttribute("tables", tables);
                break;

            case "categories":
                request.setAttribute("categories", categoryDAO.getAll());
                break;

            case "food":
                request.setAttribute("foods", foodDAO.getAllForAdmin());
                request.setAttribute("categories", categoryDAO.getAll());
                break;
        }

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }

    // ===== TABLE =====
    private void updateTable(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        int tableId = Integer.parseInt(request.getParameter("tableId"));
        String status = request.getParameter("status");

        DiningTableDAO dao = new DiningTableDAO();
        dao.updateStatus(tableId, status);

        response.sendRedirect("dashboardController?tab=tables");
    }

    // ===== CATEGORY =====
    private void handleCategory(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String action = request.getParameter("action");

        CategoryDAO dao = new CategoryDAO();

        if ("insertCategory".equals(action)) {

            Category c = new Category();
            c.setName(request.getParameter("name"));
            c.setDescription(request.getParameter("description"));

            dao.insert(c);

        } else if ("updateCategory".equals(action)) {

            Category c = new Category();
            c.setCategoryID(Integer.parseInt(request.getParameter("id")));
            c.setName(request.getParameter("name"));
            c.setDescription(request.getParameter("description"));

            dao.update(c);

        } else if ("deleteCategory".equals(action)) {

            int id = Integer.parseInt(request.getParameter("id"));
            dao.delete(id);
        }

        response.sendRedirect("dashboardController?tab=categories");
    }

    // ===== FOOD =====
    private void handleFood(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String action = request.getParameter("action");

        FoodDAO dao = new FoodDAO();

        if ("addFood".equals(action)) {

            Food f = new Food();

            f.setName(request.getParameter("name"));
            f.setPrice(Double.parseDouble(request.getParameter("price")));
            f.setCategoryID(Integer.parseInt(request.getParameter("categoryID")));
            f.setStatus(request.getParameter("status"));

            dao.insertFoodAdmin(f);

        } else if ("updateFood".equals(action)) {

            Food f = new Food();

            f.setFoodID(Integer.parseInt(request.getParameter("foodID")));
            f.setName(request.getParameter("name"));
            f.setPrice(Double.parseDouble(request.getParameter("price")));
            f.setCategoryID(Integer.parseInt(request.getParameter("categoryID")));
            f.setStatus(request.getParameter("status"));

            dao.updateFoodAdmin(f);

        } else if ("deleteFood".equals(action)) {

            int id = Integer.parseInt(request.getParameter("foodID"));
            dao.deleteFoodAdmin(id);
        }

        response.sendRedirect("dashboardController?tab=food");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // [THÊM MỚI 4] Hàm đọc file CSV và tạo danh sách Danh mục
    private void handleCategoryCsvUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Category> listNewCategory = new ArrayList<>();

        try {
            // Lấy tệp tin từ form gửi lên (name="csvCategoryFile")
            Part filePart = request.getPart("csvCategoryFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF-8"));
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Bỏ qua dòng tiêu đề
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    // Bỏ qua dòng trống
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Tách dữ liệu bằng dấu phẩy
                    String[] data = line.split(",");

                    // Chỉ cần ít nhất 1 cột (Tên danh mục), Mô tả có thể để trống
                    if (data.length >= 1) {
                        Category c = Category.builder()
                                .name(data[0].trim())
                                .description(data.length > 1 ? data[1].trim() : "") // Bắt lỗi nếu mô tả bị bỏ trống
                                .build();

                        listNewCategory.add(c);
                    }
                }
                reader.close();

                // Chuẩn bị lưu xuống Database
                if (!listNewCategory.isEmpty()) {
                    CategoryDAO dao = new CategoryDAO();
                    // Gọi hàm insertBatchCategory (Sẽ tạo ở Bước 3)
                    int rowsAffected = dao.insertBatchCategory(listNewCategory);

                    if (rowsAffected > 0) {
                        request.getSession().setAttribute("mesgCategory", "Tuyệt vời! Đã tải lên thành công " + rowsAffected + " danh mục mới.");
                    } else {
                        request.getSession().setAttribute("mesgCategory", "Có lỗi xảy ra khi lưu vào database.");
                    }
                } else {
                    request.getSession().setAttribute("mesgCategory", "Tệp CSV trống hoặc định dạng không đúng!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mesgCategory", "Lỗi xử lý file: " + e.getMessage());
        }

        // Điều hướng lại trang danh mục để hiển thị thông báo và dữ liệu mới
        response.sendRedirect("dashboardController?tab=categories");
    }

    // [THÊM MỚI 2] Hàm đọc file CSV và tạo danh sách Món ăn từ Dashboard
    private void handleFoodCsvUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Food> listNewFood = new ArrayList<>();

        try {
            // Lấy tệp tin từ form gửi lên (name="csvFoodFile")
            Part filePart = request.getPart("csvFoodFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF-8"));
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Bỏ qua dòng tiêu đề
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    // Bỏ qua dòng trống
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Tách dữ liệu bằng dấu phẩy
                    String[] data = line.split(",");

                    // Đảm bảo có đủ 5 cột (name, price, categoryID, status, imageURL)
                    if (data.length >= 5) {
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
                    // TÁI SỬ DỤNG LẠI HÀM insertBatch MÀ CHÚNG TA ĐÃ VIẾT TỪ TRƯỚC!
                    int rowsAffected = dao.insertBatch(listNewFood);

                    if (rowsAffected > 0) {
                        request.getSession().setAttribute("mesgFood", "Tuyệt vời! Đã tải lên thành công " + rowsAffected + " món ăn mới.");
                    } else {
                        request.getSession().setAttribute("mesgFood", "Có lỗi xảy ra khi lưu vào database.");
                    }
                } else {
                    request.getSession().setAttribute("mesgFood", "Tệp CSV trống hoặc định dạng không đúng!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mesgFood", "Lỗi xử lý file: " + e.getMessage());
        }

        // Điều hướng lại trang món ăn để hiển thị thông báo và dữ liệu mới
        response.sendRedirect("dashboardController?tab=food");
    }

}
