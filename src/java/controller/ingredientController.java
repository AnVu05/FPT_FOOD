/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.IngredientDAO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Ingredient;

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
public class ingredientController extends HttpServlet {

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
        switch (action) {
            case "deleteIngredient":
                deleteIngredient(request, response);
                break;
            case "addIngredient":
                doAdd(request, response);
                break;

            // [THÊM MỚI] Ngã rẽ xử lý file CSV
            case "uploadIngredientCsv":
                handleIngredientCsvUpload(request, response);
                return; // Ngắt luồng tại đây
        }
        displayIngredient(request, response);

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

    private void displayIngredient(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String from = request.getParameter("from");
        IngredientDAO iDAO = new IngredientDAO();
        ArrayList<Ingredient> listIngredient = (ArrayList<Ingredient>) iDAO.getAll();
        setRequest(request, response, listIngredient, from);
    }

    private void setRequest(HttpServletRequest request, HttpServletResponse response, ArrayList<Ingredient> listIngredient, String from) throws ServletException, IOException {
        switch (from) {
            case "kitchen":
                String mesg = "";
                String activeSection = "";
                if (listIngredient.size() <= 0) {
                    mesg += "Kho rỗng, không có nguyên liệu tồn kho";
                    request.setAttribute("mesg", mesg);
                    request.getRequestDispatcher("kitchen.jsp").forward(request, response);
                } else {
                    request.setAttribute("listIngredient", listIngredient);
                    activeSection += "inventory";
                    request.setAttribute("activeSection", activeSection);
                    request.getRequestDispatcher("kitchen.jsp").forward(request, response);
                }
                break;
            case "dashboard":
                request.setAttribute("listIngredient", listIngredient);
                request.setAttribute("activeTab", "inventory");
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
                break;
        }
    }

    private void deleteIngredient(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        IngredientDAO iDAO = new IngredientDAO();
        int check = iDAO.deleteByID(id);
        if (check >= 0) {
            System.out.println("So dong da duoc cap nhat la " + check);
        } else {
            System.out.println("Error");
        }
    }

    private void doAdd(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String name = request.getParameter("name");
        Double qyt = Double.parseDouble(request.getParameter("qty"));
        Ingredient i = Ingredient.builder()
                .name(name)
                .quantityInStock(qyt).build();
        IngredientDAO iDAO = new IngredientDAO();
        int check = iDAO.insert(i);
        if (check >= 0) {
            System.out.println("So dong da duoc cap nhat la " + check);
        } else {
            System.out.println("Error");
        }
    }

    private void handleIngredientCsvUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Ingredient> listNewIngredient = new ArrayList<>();
        try {
            Part filePart = request.getPart("csvIngredientFile");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF-8"));
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] data = line.split(",");
                    // Cấu trúc file: name, unit, quantityInStock, minThreshold
                    if (data.length >= 4) {
                        Ingredient ing = Ingredient.builder()
                                .name(data[0].trim())
                                .unit(data[1].trim())
                                .quantityInStock(Double.parseDouble(data[2].trim()))
                                .minThreshold(Double.parseDouble(data[3].trim()))
                                .build();
                        listNewIngredient.add(ing);
                    }
                }
                reader.close();

                if (!listNewIngredient.isEmpty()) {
                    IngredientDAO dao = new IngredientDAO();
                    // Hàm insertBatch sẽ được tạo ở Bước 4
                    int rowsAffected = dao.insertBatch(listNewIngredient);
                    if (rowsAffected > 0) {
                        request.getSession().setAttribute("mesgInventory", "Đã nhập thành công " + rowsAffected + " nguyên liệu từ CSV.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mesgInventory", "Lỗi: " + e.getMessage());
        }
        // Quay lại trang Dashboard tab Kho
        response.sendRedirect("MainController?action=getInventory&from=dashboard");
    }
}
