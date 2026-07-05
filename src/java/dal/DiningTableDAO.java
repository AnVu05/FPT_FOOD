/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.DiningTable;

/**
 *
 * @author AN
 */
public class DiningTableDAO extends DBContext {

    //Lấy bàn empty nhỏ nhất
    public Integer getFirstEmptyTable() {

        String sql = "SELECT TOP 1 tableID FROM DiningTable WHERE status = 'empty' ORDER BY tableID";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("tableID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update trạng thái bàn 
    public void updateStatus(Integer tableID, String status) {

        String sql = "UPDATE DiningTable SET status = ? WHERE tableID = ?";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, tableID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy toàn bộ bàn 
    public ArrayList<DiningTable> getAll() {

        ArrayList<DiningTable> list = new ArrayList<>();

        String sql = "SELECT * FROM DiningTable ORDER BY tableID ASC";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DiningTable t = DiningTable.builder()
                        .tableID(rs.getInt("tableID"))
                        .tableName(rs.getString("tableName"))
                        .seatCount(rs.getInt("seatCount"))
                        .status(rs.getString("status"))
                        .build();
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Integer getServingItemCount(int tableID) {

//        String sql = "SELECT SUM(oi.quantity) "
//                + "FROM Orders o "
//                + "JOIN OrderItem oi ON o.orderID = oi.orderID "
//                + "WHERE o.tableID = ? "
//                + "AND o.status IN ('pending','cooking')";
        String sql = "SELECT SUM(oi.quantity) "
                + "FROM Orders o "
                + "JOIN OrderItem oi ON o.orderID = oi.orderID "
                + "WHERE o.tableID = ? "
                + "AND o.status <> 'paid'";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tableID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Double getTotalByTable(int tableID) {

//        String sql = "SELECT SUM(o.finalPrice) "
//                + "FROM Orders o "
//                + "WHERE o.tableID = ? "
//                + "AND o.status IN ('pending','cooking')";
        String sql = "SELECT SUM(o.finalPrice) "
               + "FROM Orders o "
               + "WHERE o.tableID = ? "
               + "AND o.status = 'pending'";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tableID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public int countActiveOrders(int tableID) {

        String sql
                = "SELECT COUNT(*) "
                + "FROM Orders "
                + "WHERE tableID = ? "
                + "AND status <> 'paid'";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tableID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Map<? extends DiningTable, ? extends Double> getDiningTableAndFinalPrice() {
        Map<DiningTable, Double> listBills = new HashMap<>();
        connection = getConnection();
        String sql = "SELECT \n"
                + "    d.tableID,\n"
                + "    d.tableName,\n"
                + "    d.seatCount,\n"
                + "    d.status,\n"
                + "    o.finalPrice\n"
                + "FROM \n"
                + "    DiningTable d\n"
                + "JOIN \n"
                + "    Orders o ON d.tableID = o.tableID\n"
                + "WHERE \n"
                + "    d.status = 'busy';";
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int tableID = resultSet.getInt("tableID");
                String tableName = resultSet.getString("tableName");
                int seatCount = resultSet.getInt("seatCount");
                String status = resultSet.getString("status");
                Double finalPrice = resultSet.getDouble("finalPrice");
                DiningTable d = DiningTable.builder()
                        .tableID(tableID)
                        .tableName(tableName)
                        .seatCount(seatCount)
                        .status(status).build();
                listBills.put(d, finalPrice);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listBills;
    }

    public int updateStatusTable(int tableId) {
        int resultSet = 0;
        connection = getConnection();
        String sql = "UPDATE [dbo].[DiningTable]\n"
                + "   SET \n"
                + "      [status] = ?\n"
                + " WHERE tableID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, "empty");
            statement.setInt(2, tableId);
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    public Integer getOrderIDByTableID(int tableId) {
        connection = getConnection();
        String sql = "SELECT O.orderID\n"
                + "FROM Orders O\n"
                + "JOIN DiningTable D ON O.tableID = D.tableID\n"
                + "WHERE D.tableID = ?\n"
                + "  AND O.status = 'pending'";
        int orderID = 0;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, tableId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderID = resultSet.getInt("orderID");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return orderID;
    }
}
