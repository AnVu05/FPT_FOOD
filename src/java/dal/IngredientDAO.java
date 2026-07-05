/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Ingredient;
import java.util.List;

/**
 *
 * @author
 */
public class IngredientDAO extends DBContext {

    public ArrayList<Ingredient> getAll() {
        ArrayList<Ingredient> list = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [FPT_Food_PRJ].[dbo].[Ingredient]";
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ingredientID");
                String name = resultSet.getString("name");
                String unit = resultSet.getString("unit");
                Double quantityInStock = resultSet.getDouble("quantityInStock");
                Double minThreshold = resultSet.getDouble("minThreshold");
                Ingredient i = Ingredient.builder()
                        .ingredientID(id)
                        .name(name)
                        .unit(unit)
                        .quantityInStock(quantityInStock)
                        .minThreshold(minThreshold).build();
                list.add(i);
            }
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(IngredientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int deleteByID(int id) {
        connection = getConnection();
        int resultSet = 0;
        String sql = "DELETE FROM [dbo].[Ingredient]\n"
                + "      WHERE ingredientID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    public int insert(Ingredient i) {
        int resultSet = 0;
        connection = getConnection();
        String sql = "INSERT INTO [dbo].[Ingredient]\n"
                + "           ([name]\n"
                + "           ,[unit]\n"
                + "           ,[quantityInStock]\n)"
                + "     VALUES\n("
                + "           ?\n"
                + "           ,?\n"
                + "           ,?)";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, i.getName());
            statement.setString(2, "kg");
            statement.setDouble(3, i.getQuantityInStock());
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    // [THÊM MỚI]: Hàm lưu hàng loạt nguyên liệu từ danh sách CSV
    public int insertBatch(List<Ingredient> list) {
        int count = 0;
        // Câu lệnh SQL đầy đủ các trường dữ liệu theo ERD của cậu
        String sql = "INSERT INTO [dbo].[Ingredient] ([name], [unit], [quantityInStock], [minThreshold]) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            // Tắt chế độ tự động lưu (Auto-commit) để bắt đầu gom nhóm các lệnh
            connection.setAutoCommit(false);

            java.sql.PreparedStatement ps = connection.prepareStatement(sql);

            for (Ingredient i : list) {
                ps.setString(1, i.getName());
                ps.setString(2, i.getUnit());
                ps.setDouble(3, i.getQuantityInStock());
                ps.setDouble(4, i.getMinThreshold());

                ps.addBatch(); // Đưa lệnh này vào hàng đợi (batch)
            }

            // Thực thi toàn bộ danh sách lệnh trong batch
            int[] results = ps.executeBatch();

            // Xác nhận lưu vĩnh viễn dữ liệu xuống Database
            connection.commit();

            // Đếm số lượng bản ghi thành công
            for (int res : results) {
                if (res > 0 || res == java.sql.PreparedStatement.SUCCESS_NO_INFO) {
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Nếu có bất kỳ lỗi nào xảy ra, hủy bỏ toàn bộ các lệnh trong batch này để tránh rác dữ liệu
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                // Trả lại trạng thái mặc định cho connection
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return count;
    }

}
