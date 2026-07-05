/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.OrderItem;
import model.Orders;

/**
 *
 * @author AN
 */
public class OrdersDAO extends DBContext {

    public ArrayList<Orders> getAll() {
        ArrayList<Orders> list = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [FPT_Food_PRJ].[dbo].[Orders]";
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int orderID = resultSet.getInt("orderID");
                int tableID = resultSet.getInt("tableID");
                int voucherID = resultSet.getInt("voucherID");
                String status = resultSet.getString("status");
                Double totalPrice = resultSet.getDouble("totalPrice");
                Double finalPrice = resultSet.getDouble("finalPrice");
                Timestamp createdTime = resultSet.getTimestamp("createdTime");
                // lay cac item
                ArrayList<OrderItem> listItem = new ArrayList<>();
                OrderItemDAO oiDAO = new OrderItemDAO();
                listItem.addAll(oiDAO.getOrderItemByOrderID(orderID));
                Orders o = Orders.builder()
                        .orderID(orderID)
                        .tableID(tableID)
                        .voucherID(voucherID)
                        .status(status)
                        .totalPrice(totalPrice)
                        .finalPrice(finalPrice)
                        .createdTime(createdTime)
                        .orderItem(listItem).build();
                list.add(o);
            }
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(OrdersDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int updateStatusOrder(int orderID, String curr_status) {
        connection = getConnection();
        String sql = "UPDATE [dbo].[Orders]\n"
                + "   SET \n"
                + "      [status] = ?\n"
                + " WHERE orderID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, curr_status);
            statement.setInt(2, orderID);
            return statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return 0;
    }

    public int insertOrder(int tableID,
            String status,
            double total,
            double finalTotal,
            Integer voucherID,
            int userID) {

        String sql = "INSERT INTO Orders(tableID, status, totalPrice, finalPrice, voucherID, userID) "
                + "VALUES (?, ?, ?, ?, ?,?)";

        try {
            connection = getConnection();

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, tableID);
            ps.setString(2, status);
            ps.setDouble(3, total);
            ps.setDouble(4, finalTotal);

            if (voucherID == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, voucherID);
            }
            
            ps.setInt(6, userID);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<Orders> getByTableID(int tableID) {

        ArrayList<Orders> list = new ArrayList<>();
        connection = getConnection();

        String sql = "SELECT * FROM Orders WHERE tableID = ? ORDER BY createdTime DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tableID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int orderID = rs.getInt("orderID");
                int voucherID = rs.getInt("voucherID");
                String status = rs.getString("status");
                Double totalPrice = rs.getDouble("totalPrice");
                Double finalPrice = rs.getDouble("finalPrice");

                Timestamp createdTime = rs.getTimestamp("createdTime");

                // Lấy danh sách món
                OrderItemDAO oiDAO = new OrderItemDAO();
                ArrayList<OrderItem> listItem
                        = oiDAO.getOrderItemByOrderID(orderID);

                Orders o = Orders.builder()
                        .orderID(orderID)
                        .tableID(tableID)
                        .voucherID(voucherID)
                        .status(status)
                        .totalPrice(totalPrice)
                        .finalPrice(finalPrice)
                        .createdTime(createdTime)
                        .orderItem(listItem)
                        .build();

                list.add(o);
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int updateStatusOrderByTable(int tableID, String status) {

        connection = getConnection();

        String sql = "UPDATE Orders SET status = ? "
                + "WHERE tableID = ? AND status <> 'paid'";

        try {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, tableID);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

//    public void updateOrderTotal(int orderID) {
//
//        String sql
//                = "UPDATE Orders SET totalPrice = ("
//                + "SELECT SUM(quantity * price) FROM OrderItem WHERE orderID = ?"
//                + "), finalPrice = ("
//                + "SELECT SUM(quantity * price) FROM OrderItem WHERE orderID = ?"
//                + ") WHERE orderID = ?";
//
//        try {
//
//            connection = getConnection();
//            PreparedStatement ps = connection.prepareStatement(sql);
//
//            ps.setInt(1, orderID);
//            ps.setInt(2, orderID);
//            ps.setInt(3, orderID);
//
//            ps.executeUpdate();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    public void updateOrderTotal(int orderID) {
    String sql = "UPDATE Orders SET totalPrice = ("
               + "SELECT SUM(quantity * price) FROM OrderItem WHERE orderID = ?"
               + ") WHERE orderID = ?";
    try {
        connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, orderID);
        ps.setInt(2, orderID);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
