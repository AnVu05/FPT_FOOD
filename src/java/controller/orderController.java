/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.OrdersDAO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Orders;

/**
 *
 * @author AN
 */
public class orderController extends HttpServlet {

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
        String action = request.getParameter("action");
        action = action == null ? "" : request.getParameter("action");
        switch (action) {
            case "updateOrder":
                updateOrder(request,response);
                break;
            default:
                displayOrders(request, response);
        }
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

    private void displayOrders(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        OrdersDAO oDAO = new OrdersDAO();
        ArrayList<Orders> listOrders = (ArrayList<Orders>) oDAO.getAll();
        String mesg = "";
        String activeSection = "";
        if (listOrders.size() <= 0) {
            mesg += "Hiện tại không có đơn hàng nào";
            request.setAttribute("mesg", mesg);
            request.getRequestDispatcher("kitchen.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("listOrders", listOrders);
            activeSection += "orders";
            request.setAttribute("activeSection", activeSection);
            request.getRequestDispatcher("kitchen.jsp").forward(request, response);
        }
    }

    private void updateOrder(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException  {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int orderID = Integer.parseInt(request.getParameter("orderId"));
        String curr_status = request.getParameter("status");
        switch (curr_status) {
            case "pending":
                curr_status = "cooking";
                break;
            case "cooking":
                curr_status = "done";
                break;
        }
        OrdersDAO oDAO = new OrdersDAO();
        int number = oDAO.updateStatusOrder(orderID,curr_status);
        System.out.println("Số dòng thay đổi là " + number);
        displayOrders(request, response);
    }
}
