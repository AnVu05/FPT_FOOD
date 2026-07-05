/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.DiningTableDAO;
import dal.OrdersDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.DiningTable;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author AN
 */
@WebServlet(name = "diningTableController", urlPatterns = {"/diningTableController"})
public class diningTableController extends HttpServlet {

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
        String activeTab = "bills";
        request.setAttribute("activeTab", activeTab);
        switch (action) {
            case "pay":
                doPay(request, response);
                break;
        }
        doBills(request, response);
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

    private void doBills(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        DiningTableDAO dDAO = new DiningTableDAO();
        Map<DiningTable, Double> listBills = new HashMap<>();
        listBills.putAll(dDAO.getDiningTableAndFinalPrice());
        request.setAttribute("listBills", listBills);
    }

    private void doPay(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        int tableId = Integer.parseInt(request.getParameter("tableId"));
        DiningTableDAO dDAO = new DiningTableDAO();
//        int check = dDAO.updateStatusTable(tableId);
//        if (check >= 0) {
//            System.out.println("So dong da duoc cap nhat la " + check);
//        } else {
//            System.out.println("Error");
//        }
        OrdersDAO odao = new OrdersDAO();
        // Lấy order hiện tại
//        Integer orderID = dDAO.getOrderIDByTableID(tableId);
//        if (orderID != null) {
//
//            // cập nhật order → paid
//            odao.updateStatusOrder(orderID, "paid");
//
//            // xóa order khỏi session
//            session.removeAttribute("orderID");
//        }
//
//        // bàn → empty
//        int check = dDAO.updateStatusTable(tableId);
//
//        if (check >= 0) {
//            System.out.println("So dong da duoc cap nhat la " + check);
//        } else {
//            System.out.println("Error");
//        }

        // Cập nhật TẤT CẢ order của bàn này → paid
        odao.updateStatusOrderByTable(tableId, "paid");

        // Bàn → empty
        dDAO.updateStatusTable(tableId);

        // Xóa session
        session.removeAttribute("orderID");
        session.removeAttribute("currentTable");
        sendMailThankYou(request, response, tableId);
    }

    private void sendMailThankYou(HttpServletRequest request, HttpServletResponse response, int tableId) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        UserDAO uDAO = new UserDAO();

        // 1. Lấy mail người dùng thông qua tableID 
        String email = uDAO.getEmailUserByTableID(tableId);
        System.out.println(email);
        // 2. Gọi hàm doSendMail(String email)
        int status = doSendMail(email);

        // 3. Kiểm tra status, = 1 in ra console ok, = 0 in ra Error
        if (status == 1) {
            System.out.println("Gửi mail cảm ơn thành công tới: " + email);
        } else {
            System.out.println("Error: Gửi mail thất bại cho bàn " + tableId);
        }

    }

    private int doSendMail(String toEmail) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.out.println("Không tìm thấy email người dùng, không thể gửi mail.");
            return 0; // Thất bại
        }

        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(465); // Port dùng cho SSL
            // Cậu thay email của nhà hàng và Mật khẩu ứng dụng (App Password) vào đây nhé
            email.setAuthenticator(new DefaultAuthenticator("vudinhan2k5@gmail.com", "bdgurtjczirduwlx"));
            email.setSSLOnConnect(true);
            email.setCharset("UTF-8"); // Hỗ trợ tiếng Việt

            // Cấu hình người gửi, người nhận
            email.setFrom("vudinhan2k5@gmail.com", "FPT-Food");
            email.addTo(toEmail);
            email.setSubject("Cảm ơn bạn đã dùng bữa tại Nhà Hàng!");

            // Nội dung Email bằng HTML
            String htmlMessage = "<h2>Xin chào!</h2>"
                    + "<p>Cảm ơn bạn đã lựa chọn và dùng bữa tại nhà hàng của chúng tôi.</p>"
                    + "<p>Hóa đơn của bạn đã được thanh toán thành công. Chúng tôi hy vọng bạn đã có một trải nghiệm ẩm thực tuyệt vời và ngon miệng.</p>"
                    + "<p>Rất mong được phục vụ bạn trong những lần tiếp theo!</p>"
                    + "<br><p>Trân trọng,</p>"
                    + "<p><b>Đội ngũ Nhà Hàng</b></p>";

            email.setHtmlMsg(htmlMessage);

            // Gửi mail
            email.send();
            return 1; // Thành công

        } catch (Exception e) {
            System.out.println("Lỗi khi gửi mail: " + e.getMessage());
            return 0; // Thất bại
        }
    }

}
