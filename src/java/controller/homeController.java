package controller;

import dal.CategoryDAO;
import dal.DiningTableDAO;
import dal.FoodDAO;
import dal.OrderItemDAO;
import dal.OrdersDAO;
import dal.VoucherDAO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Food;
import model.Orders;
import model.User;
import model.Voucher;

@WebServlet(name = "homeController", urlPatterns = {"/homeController"})
public class homeController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String tab = request.getParameter("tab");

        if (action == null) {
            action = "home";
        }
        if (tab == null) {
            tab = "menu";
        }

        request.setAttribute("activeTab", tab);

        switch (action) {

            case "addToCart":
                addToCart(request, response);
                return;

            case "updateCart":
                updateCart(request, response);
                return;

            case "applyVoucher":
                applyVoucher(request, response);
                return;

            case "customerOrder":
                customerOrder(request, response);
                return;

            case "checkout":
                checkout(request, response);
                return;
                
            case "history":
                request.setAttribute("activeTab", "history");
                showHistory(request, response);
                return;
        }

        if ("menu".equals(tab)) {
            showMenu(request, response);
        } else if ("history".equals(tab)) {
            showHistory(request, response);
        }
    }

    // ================= MENU =================
    private void showMenu(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        FoodDAO fdao = new FoodDAO();

        String cat = request.getParameter("cat");

        List<Food> foods;

        if (cat == null || cat.trim().isEmpty()) {
            foods = fdao.getAllAvailable();
        } else {
            foods = fdao.getByCategoryName(cat);
        }

        request.setAttribute("foods", foods);

        Map<Integer, Integer> cart
                = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        CategoryDAO cdao = new CategoryDAO();
        request.setAttribute("categories", cdao.getAll());

        Map<Integer, Food> foodMap = new HashMap<>();

        int totalQty = 0;
        double total = 0;

        for (Integer foodId : cart.keySet()) {

            Food f = fdao.getById(foodId);

            if (f != null) {

                int qty = cart.get(foodId);

                foodMap.put(foodId, f);

                totalQty += qty;
                total += f.getPrice() * qty;
            }
        }

        request.setAttribute("cart", cart);
        request.setAttribute("foodMap", foodMap);

        Voucher v = (Voucher) session.getAttribute("voucher");

        double discount = 0;
        double finalTotal = total;

        if (v != null) {

            LocalDateTime now = LocalDateTime.now();

            boolean validTime
                    = now.isAfter(v.getStartDate())
                    && now.isBefore(v.getEndDate());

            boolean validStatus
                    = "active".equals(v.getStatus());

            boolean validMinOrder
                    = total >= v.getMinOrderValue();

            if (validTime && validStatus && validMinOrder) {

                discount = total * v.getDiscountPercent() / 100;
                finalTotal = total - discount;

            } else {

                session.removeAttribute("voucher");
                session.setAttribute("voucherError", "Voucher không hợp lệ");
            }
        }

        request.setAttribute("totalQty", totalQty);
        request.setAttribute("finalTotal", finalTotal);
        request.setAttribute("discount", discount);
        request.setAttribute("total", total);

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    // ================= ADD CART =================
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        Map<Integer, Integer> cart
                = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
        }

        int foodId = Integer.parseInt(request.getParameter("foodId"));

        cart.put(foodId, cart.getOrDefault(foodId, 0) + 1);

        session.setAttribute("cart", cart);

        response.sendRedirect("MainController?action=home");
    }

    // ================= UPDATE CART =================
    private void updateCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        Map<Integer, Integer> cart
                = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            response.sendRedirect("MainController?action=home");
            return;
        }

        int foodId = Integer.parseInt(request.getParameter("foodId"));
        String action = request.getParameter("cartAction");

        int qty = cart.getOrDefault(foodId, 0);

        if ("plus".equals(action)) {
            cart.put(foodId, qty + 1);
        } else if ("minus".equals(action)) {

            if (qty <= 1) {
                cart.remove(foodId);
            } else {
                cart.put(foodId, qty - 1);
            }
        }

        session.setAttribute("cart", cart);

        response.sendRedirect("MainController?action=home");
    }

    // ================= APPLY VOUCHER =================
    private void applyVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        String code = request.getParameter("code");

        VoucherDAO vdao = new VoucherDAO();

        Voucher v = vdao.getByCode(code);

        if (v == null) {
            session.setAttribute("voucherError", "Mã không tồn tại");
        } else {
            session.setAttribute("voucher", v);
            session.removeAttribute("voucherError");
        }

        response.sendRedirect("MainController?action=home");
    }

    // ================= CHECK LOGIN BEFORE ORDER =================
    private void customerOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        if (user == null) {

            session.setAttribute("loginError",
                    "Bạn cần đăng nhập trước khi đặt món");

            response.sendRedirect("login.jsp");
            return;
        }

        Map<Integer, Integer> cart
                = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {

            session.setAttribute("cartError",
                    "Giỏ hàng đang trống");

            response.sendRedirect("MainController?action=home");
            return;
        }

        response.sendRedirect("MainController?action=checkout");
    }

    // ================= CHECKOUT =================
    private void checkout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("user");
        int userID = u.getUserID();

        try {

            FoodDAO fdao = new FoodDAO();
            OrdersDAO odao = new OrdersDAO();
            OrderItemDAO itemDAO = new OrderItemDAO();
            DiningTableDAO tableDAO = new DiningTableDAO();

            Map<Integer, Integer> cart
                    = (Map<Integer, Integer>) session.getAttribute("cart");

            Integer tableID
                    = (Integer) session.getAttribute("currentTable");

            if (tableID == null) {

                tableID = tableDAO.getFirstEmptyTable();

                if (tableID == null) {

                    session.setAttribute("cartError",
                            "Hiện tại không còn bàn trống");

                    response.sendRedirect("MainController?action=home");
                    return;
                }

                session.setAttribute("currentTable", tableID);
            }

            double total = 0;

            for (Integer foodId : cart.keySet()) {

                Food f = fdao.getById(foodId);

                if (f != null) {
                    total += f.getPrice() * cart.get(foodId);
                }
            }

            Voucher v = (Voucher) session.getAttribute("voucher");

            double finalTotal = total;
            Integer voucherID = null;

            if (v != null) {

                finalTotal = total
                        - total * v.getDiscountPercent() / 100;

                voucherID = v.getVoucherID();
            }

            Integer orderID
                    = (Integer) session.getAttribute("orderID");

            if (orderID == null) {

                orderID = odao.insertOrder(
                        tableID,
                        "pending",
                        total,
                        finalTotal,
                        voucherID,
                        userID
                );

                session.setAttribute("orderID", orderID);

                tableDAO.updateStatus(tableID, "busy");
            }

            for (Integer foodId : cart.keySet()) {

                Food f = fdao.getById(foodId);

                if (f != null) {

                    itemDAO.insertItem(
                            orderID,
                            foodId,
                            cart.get(foodId),
                            f.getPrice()
                    );
                }
            }

            odao.updateOrderTotal(orderID);

            session.removeAttribute("voucher");
            session.setAttribute("cart", new HashMap<>());

            session.setAttribute("orderSuccess", "Đặt món thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("MainController?action=home");
    }

    // ================= HISTORY =================
    private void showHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Integer tableID
                = (Integer) session.getAttribute("currentTable");

        if (tableID != null) {

            OrdersDAO odao = new OrdersDAO();

            ArrayList<Orders> orders
                    = odao.getByTableID(tableID);

            request.setAttribute("orders", orders);
        }

        request.getRequestDispatcher("home.jsp")
                .forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
