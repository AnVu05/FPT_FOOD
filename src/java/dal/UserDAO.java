/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import java.util.List;

/**
 *
 * @author AN
 */
public class UserDAO extends DBContext {

    public ArrayList<User> getAllAccountWorkerAndAdmin() {
        ArrayList<User> listUser = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [FPT_Food_PRJ].[dbo].[User]\n"
                + "  Where (role = ? or role = ?) and status = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, "manager");
            statement.setString(2, "worker");
            statement.setString(3, "active");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");
                User u = User.builder()
                        .userID(userID)
                        .username(username)
                        .role(role).build();
                listUser.add(u);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listUser;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM [User] WHERE username = ? AND [password] = ? AND status = 'active'";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password, username));
//            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .userID(rs.getInt("userID"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .fullname(rs.getString("fullname"))
                        .phone(rs.getString("phone"))
                        .status(rs.getString("status"))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Check trùng username
    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM [User] WHERE username = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Check trùng phone
    public boolean isPhoneExist(String phone) {
        String sql = "SELECT 1 FROM [User] WHERE phone = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Check trùng email
    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM [User] WHERE email = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Insert user mới
    public void register(User user) {
        String sql = "INSERT INTO [User] (username, [password], role, fullname, phone, email, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());      // "user"
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getStatus());    // "active"
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public int deleteAccount(int userID) {
        connection = getConnection();
        String sql = "UPDATE [dbo].[User]\n"
                + "   SET \n"
                + "      [status] = ?\n"
                + "WHERE userID = ?";
        int resultSet = 0;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, "inactive");
            statement.setInt(2, userID);
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    public String getEmailUserByTableID(int tableId) {
        String email = null;

        // Đảm bảo có khoảng trắng ở cuối mỗi dòng nối chuỗi
        String sql = "SELECT TOP 1 u.email "
                + "FROM [User] u "
                + "JOIN Orders o ON u.userID = o.userID "
                + "WHERE o.tableID = ? AND o.status = 'paid' "
                + "ORDER BY o.createdTime DESC";

        // Sử dụng try-with-resources để tự động đóng Statement và ResultSet
        try ( Connection conn = getConnection();  PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, tableId);

            try ( ResultSet resultSet = statement.executeQuery()) {
                // Dùng 'if' thay vì 'while' vì query đã có TOP 1
                if (resultSet.next()) {
                    email = resultSet.getString("email");
                }
            }

        } catch (Exception e) {
            // Nên in ra stack trace để dễ debug hơn nếu có lỗi khác
            e.printStackTrace();
        }

        return email;
    }

    // [THÊM MỚI]: Hàm chèn hàng loạt tài khoản bằng cơ chế Batch Processing
    public int insertBatch(List<User> list) {
        int count = 0;
        // SQL đầy đủ các trường dựa trên Model User của cậu
        String sql = "INSERT INTO [User] (username, [password], role, fullname, phone, status, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            // Tắt Auto-commit để quản lý giao dịch (Transaction) thủ công
            connection.setAutoCommit(false);

            java.sql.PreparedStatement ps = connection.prepareStatement(sql);

            for (User u : list) {
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getPassword());
                ps.setString(3, u.getRole());
                ps.setString(4, u.getFullname());
                ps.setString(5, u.getPhone());
                ps.setString(6, u.getStatus());
                ps.setString(7, u.getEmail());

                ps.addBatch(); // Xếp tài khoản vào "hàng chờ"
            }

            // Thực thi toàn bộ danh sách trong một lần gửi duy nhất
            int[] results = ps.executeBatch();

            // Nếu không có lỗi, chính thức lưu vào Database
            connection.commit();

            // Đếm số lượng tài khoản đã tạo thành công
            for (int res : results) {
                if (res > 0 || res == java.sql.PreparedStatement.SUCCESS_NO_INFO) {
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Nếu gặp lỗi (ví dụ: trùng username), rút lại toàn bộ các lệnh đã xếp hàng
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                // Trả connection về trạng thái bình thường
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
