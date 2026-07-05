/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Voucher;

/**
 *
 * @author AN
 */
public class VoucherDAO extends DBContext {

    public Voucher getByCode(String code) {
        String sql = "SELECT * FROM Voucher WHERE code = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, code);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher v = Voucher.builder()
                        .voucherID(rs.getInt("voucherID"))
                        .code(rs.getString("code"))
                        .discountPercent(rs.getInt("discountPercent"))
                        .startDate(rs.getTimestamp("startDate").toLocalDateTime())
                        .endDate(rs.getTimestamp("endDate").toLocalDateTime())
                        .minOrderValue(rs.getDouble("minOrderValue"))
                        .status(rs.getString("status"))
                        .build();
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
