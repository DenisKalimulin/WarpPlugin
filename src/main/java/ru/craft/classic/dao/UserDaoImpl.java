package ru.craft.classic.dao;

import lombok.RequiredArgsConstructor;
import ru.craft.classic.mappers.UserMapper;
import ru.craft.classic.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final DataSource ds;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT id, uuid, username FROM users WHERE id = ?";
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper.toUserFromRs(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка в методе UserDao#findById", ex);
        }
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        String sql = "SELECT id, uuid, username FROM users WHERE uuid = ?";
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper.toUserFromRs(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка в методе UserDao#findByUuid", ex);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, uuid, username FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper.toUserFromRs(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка в методе UserDao#findByUsername", ex);
        }
    }

    @Override
    public User upsertByAuthmeId(int authmeId, UUID uuid, String username) {
        String sql = """
            INSERT INTO users (id, uuid, username, created_at, updated_at)
            VALUES (?, ?, ?, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000)
            ON DUPLICATE KEY UPDATE
              uuid = VALUES(uuid),
              username = VALUES(username),
              updated_at = UNIX_TIMESTAMP()*1000
            """;
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, authmeId);
            ps.setString(2, uuid.toString());
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка в методе UserDAO#upsertByAuthmeId", ex);
        }
        return findById(authmeId).orElseThrow();
    }
}
