/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.MultipartConfig;

/**
 *
 * @author
 */
@MultipartConfig
public class MainController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String method = request.getMethod();
        String url = "";
        switch (action) {

            case "register":
                if ("GET".equalsIgnoreCase(method)) {
                    url = "register.jsp";      // mở form đăng ký
                } else {
                    url = "AuthController";    // xử lý đăng ký
                }
                break;

            // ===== ORDER =====
            case "getOrder":
            case "updateOrder":
                url = "orderController";
                break;

            // ===== RECIPE =====
            case "getRecipe":
            case "viewRecipesDetail":
            case "deletetRecipe":
            case "addRecipe":
            case "updateRecipe":
                url = "recipesController";
                break;

            // ===== FOOD =====
            case "updateStatusFood":
            case "uploadFoodCsv":
            case "uploadImgFood":
                url = "foodController";
                break;

            // ===== INGREDIENT =====
            case "getIngredient":
            case "deleteIngredient":
            case "addIngredient":
            case "getInventory":
            case "uploadIngredientCsv":
                url = "ingredientController";
                break;

            // ===== USER =====
            case "getAccounts":
            case "deleteUser":
            case "addUser":
            case "uploadUserCsv":
                url = "UserController";
                break;

            // ===== TABLE =====
            case "bills":
            case "pay":
                url = "diningTableController";
                break;
            // ===== HOME =====   
            case "home":
            case "addToCart":
            case "updateCart":
            case "applyVoucher":
            case "customerOrder":
            case "checkout":
            case "history":
                url = "homeController";
                break;

            // ===== AUTH =====
            case "login":
            case "logout":
                url = "AuthController";
                break;

            default:
                url = "login.jsp";
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

}
