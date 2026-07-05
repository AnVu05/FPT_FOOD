/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.OrderItem;

/**
 *
 * @author AN
 */
public class OrderItemDAO extends DBContext {

    public ArrayList<OrderItem> getOrderItemByOrderID(int orderID) {
        ArrayList<OrderItem> listItem = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [dbo].[OrderItem]\n"
                + "  Where orderID = ?;";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int orderItemID = resultSet.getInt("orderItemID");
                int foodID = resultSet.getInt("foodID");
                FoodDAO fDAO = new FoodDAO();
                String foodName = fDAO.getFoodNameByFoodID(foodID);
                int quantity = resultSet.getInt("quantity");
                Double price = resultSet.getDouble("price");
                OrderItem oi = OrderItem.builder()
                        .orderItemID(orderItemID)
                        .orderID(orderID)
                        .foodName(foodName)
                        .quantity(quantity)
                        .price(price).build();
                listItem.add(oi);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listItem;
    }

    public void insertItem(int orderID,
            int foodID,
            int quantity,
            double price) {

        String sql = "INSERT INTO OrderItem(orderID, foodID, quantity, price) "
                + "VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, orderID);
            ps.setInt(2, foodID);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
