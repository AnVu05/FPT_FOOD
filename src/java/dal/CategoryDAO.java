/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;

/**
 *
 * @author AN
 */
public class CategoryDAO extends DBContext {

    public ArrayList<Category> getAll() {

        ArrayList<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category c = Category.builder()
                        .categoryID(rs.getInt("categoryID"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .build();

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(Category c) {

        String sql = "INSERT INTO Category(name,description) VALUES (?,?)";

        try {

            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Category c) {

        String sql = "UPDATE Category SET name=?, description=? WHERE categoryID=?";

        try {

            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getCategoryID());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        String sql = "DELETE FROM Category WHERE categoryID=?";

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
    // [THÊM MỚI] TÍNH NĂNG MỚI: THÊM NHIỀU DANH MỤC TỪ CSV BẰNG BATCH
    // ==========================================
    public int insertBatchCategory(List<Category> listCategory) {
        int count = 0;
        // Câu lệnh SQL chèn dữ liệu (ID tự động tăng nên chỉ cần name và description)
        String sql = "INSERT INTO Category(name, description) VALUES (?, ?)";

        try {
            connection = getConnection();
            // Tắt tự động lưu để gom nhóm các lệnh
            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement(sql);

            for (Category c : listCategory) {
                ps.setString(1, c.getName());
                ps.setString(2, c.getDescription());

                ps.addBatch(); // Đưa danh mục này vào "xe tải" (hàng đợi)
            }

            // Nhấn ga! Thực thi toàn bộ lệnh trong "xe tải" cùng một lúc
            int[] results = ps.executeBatch();

            // Chính thức xác nhận lưu vĩnh viễn vào Database
            connection.commit();

            // Đếm số lượng danh mục đã lưu thành công
            for (int res : results) {
                if (res > 0 || res == PreparedStatement.SUCCESS_NO_INFO) {
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Nếu đang lưu dở mà có lỗi (ví dụ trùng tên), hoàn tác toàn bộ chuyến xe
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                // Trả lại cài đặt mặc định cho connection
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
