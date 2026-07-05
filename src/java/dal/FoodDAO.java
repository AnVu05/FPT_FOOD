/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Food;

/**
 *
 * @author AN
 */
public class FoodDAO extends DBContext {

    public List<Food> getAllAvailable() {
        List<Food> list = new ArrayList<Food>();
        String sql
                = "SELECT f.foodID, f.name, f.price, f.categoryID, f.status, f.imageURL, "
                + "c.name AS categoryName "
                + "FROM Food f "
                + "LEFT JOIN Category c ON f.categoryID = c.categoryID "
                + "WHERE f.status = 'available'";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Food f = new Food();
                f.setFoodID(rs.getInt("foodID"));
                f.setName(rs.getString("name"));
                f.setPrice(rs.getDouble("price"));
                f.setCategoryID(rs.getInt("categoryID"));
                f.setStatus(rs.getString("status"));
                f.setCategoryName(rs.getString("categoryName"));
                f.setImageURL(rs.getString("imageURL"));
                list.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Food> getByCategoryName(String cat) {
        List<Food> list = new ArrayList<Food>();
        String sql
                = "SELECT f.foodID, f.name, f.price, f.categoryID, f.status, f.imageURL, "
                + "c.name AS categoryName "
                + "FROM Food f "
                + "LEFT JOIN Category c ON f.categoryID = c.categoryID "
                + "WHERE c.name = ? AND f.status = 'available'";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, cat);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Food f = new Food();
                f.setFoodID(rs.getInt("foodID"));
                f.setName(rs.getString("name"));
                f.setPrice(rs.getDouble("price"));
                f.setCategoryID(rs.getInt("categoryID"));
                f.setStatus(rs.getString("status"));
                f.setCategoryName(rs.getString("categoryName"));
                f.setImageURL(rs.getString("imageURL"));
                list.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Food getById(int id) {
        String sql
                = "SELECT f.foodID, f.name, f.price, f.categoryID, f.status, f.imageURL, "
                + "c.name AS categoryName "
                + "FROM Food f "
                + "LEFT JOIN Category c ON f.categoryID = c.categoryID "
                + "WHERE f.foodID = ?";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Food f = new Food();
                f.setFoodID(rs.getInt("foodID"));
                f.setName(rs.getString("name"));
                f.setPrice(rs.getDouble("price"));
                f.setCategoryID(rs.getInt("categoryID"));
                f.setStatus(rs.getString("status"));
                f.setCategoryName(rs.getString("categoryName"));
                f.setImageURL(rs.getString("imageURL"));
                return f;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFoodNameByFoodID(int foodID) {
        connection = getConnection();
        String sql = "SELECT [name]\n"
                + "  FROM [dbo].[Food]\n"
                + "  Where foodID = ?";
        String name = "";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, foodID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString("name");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return name;
    }

    public ArrayList<Food> getAll() {
        ArrayList<Food> listFood = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [dbo].[Food]";
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int foodID = resultSet.getInt("foodID");
                String name = resultSet.getString("name");
                Double price = resultSet.getDouble("price");
                int categoryID = resultSet.getInt("categoryID");
                String status = resultSet.getString("status");
                Food f = Food.builder()
                        .foodID(foodID)
                        .name(name)
                        .price(price)
                        .categoryID(categoryID)
                        .status(status).build();
                listFood.add(f);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listFood;
    }

    public int updateStatus(int foodID, String status) {
        status = switchStatus(status);
        int resultSet = 0;
        connection = getConnection();
        String sql = "UPDATE [dbo].[Food]\n"
                + "   SET \n"
                + "      [status] = ?\n"
                + "	  WHERE foodID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, foodID);
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    private String switchStatus(String status) {
        switch (status) {
            case "available":
                status = "unavailable";
                break;
            case "unavailable":
                status = "available";
                break;
        }
        return status;
    }

    public List<Food> getAllForAdmin() {
        List<Food> list = new ArrayList<>();

        String sql
                = "SELECT f.foodID, f.name, f.price, f.status, f.imageURL, "
                + "f.categoryID, c.name AS categoryName "
                + "FROM Food f "
                + "LEFT JOIN Category c ON f.categoryID = c.categoryID "
                + "ORDER BY f.foodID DESC";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Food f = new Food();
                f.setFoodID(rs.getInt("foodID"));
                f.setName(rs.getString("name"));
                f.setPrice(rs.getDouble("price"));
                f.setStatus(rs.getString("status"));
                f.setCategoryID(rs.getInt("categoryID"));
                f.setCategoryName(rs.getString("categoryName"));
                f.setImageURL(rs.getString("imageURL"));
                list.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertFoodAdmin(Food f) {
        String sql = "INSERT INTO Food(name, price, categoryID, status) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, f.getName());
            ps.setDouble(2, f.getPrice());
            ps.setInt(3, f.getCategoryID());
            ps.setString(4, f.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFoodAdmin(Food f) {
        String sql = "UPDATE Food SET name=?, price=?, categoryID=?, status=? WHERE foodID=?";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, f.getName());
            ps.setDouble(2, f.getPrice());
            ps.setInt(3, f.getCategoryID());
            ps.setString(4, f.getStatus());
            ps.setInt(5, f.getFoodID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFoodAdmin(int id) {

        String sql = "UPDATE Food SET status = 'unavailable' WHERE foodID=?";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // [THÊM MỚI] TÍNH NĂNG MỚI: THÊM NHIỀU MÓN ĂN TỪ CSV
    // ==========================================
    public int insertBatch(List<Food> listFood) {
        int count = 0;
        // Câu lệnh SQL chèn dữ liệu
        String sql = "INSERT INTO Food(name, price, categoryID, status, imageURL) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            // Tắt chế độ tự động lưu để gom nhóm các câu lệnh lại
            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement(sql);

            for (Food f : listFood) {
                ps.setString(1, f.getName());
                ps.setDouble(2, f.getPrice());
                ps.setInt(3, f.getCategoryID());
                ps.setString(4, f.getStatus());
                ps.setString(5, f.getImageURL());

                ps.addBatch(); // Đưa món ăn này vào "xe tải" (hàng đợi)
            }

            // Nhấn ga! Thực thi toàn bộ lệnh trong "xe tải" cùng một lúc
            int[] results = ps.executeBatch();

            // Chính thức xác nhận lưu vĩnh viễn vào Database
            connection.commit();

            // Đếm số lượng món ăn đã lưu thành công
            for (int res : results) {
                if (res > 0 || res == PreparedStatement.SUCCESS_NO_INFO) {
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Nếu đang lưu dở mà báo lỗi, hoàn tác (hủy bỏ) toàn bộ chuyến xe
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                // Trả lại cài đặt mặc định cho connection
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return count;
    }

}
