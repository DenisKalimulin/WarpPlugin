package ru.craft.classic.dao;

import lombok.RequiredArgsConstructor;
import ru.craft.classic.model.AuthMeAccount;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthMeDaoImpl implements AuthMeDao {

    private final DataSource ds;

    @Override
    public Optional<AuthMeAccount> findByUsername(String username) {
        String sql = "SELECT id, username FROM authme WHERE username=? LIMIT 1";

        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Integer id = Integer.valueOf(rs.getString("id"));
                String name = rs.getString("username");
                return Optional.of(new AuthMeAccount(id, name));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка обращения к базе AuthMe при поиске по имени пользователя", ex);
        }
    }

    @Override
    public Optional<AuthMeAccount> findById(Integer id) {
        String sql = "SELECT id, username FROM authme WHERE id=? LIMIT 1";

        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new AuthMeAccount(id, rs.getString("username")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обращения к базе AuthMe при поиске по имени пользователя", e);
        }
    }
}
