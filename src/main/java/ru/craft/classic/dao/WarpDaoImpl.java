package ru.craft.classic.dao;

import lombok.RequiredArgsConstructor;
import ru.craft.classic.mappers.WarpMapper;
import ru.craft.classic.model.Warp;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class WarpDaoImpl implements WarpDao {

    private final DataSource ds;
    private final WarpMapper warpMapper;

    @Override
    public Warp getWarpById(Integer id) {
        String sql = "SELECT id, user_id, owner_uuid, name, world, x, y, z, yaw, pitch, created_at, is_public " +
                "FROM warps WHERE id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? warpMapper.toWarp(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось получить варп по id=" + id, ex);
        }
    }

    @Override
    public Warp getWarpByName(String name) {
        String sql = "SELECT id, user_id, owner_uuid, name, world, x, y, z, yaw, pitch, created_at, is_public " +
                "FROM warps WHERE name = ? AND is_public = 1 LIMIT 1";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? warpMapper.toWarp(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось получить публичный варп по имени '" + name + "'", ex);
        }
    }

    @Override
    public List<Warp> getWarpsOwnedByPlayer(Integer playerId) {
        String sql = "SELECT id, user_id, owner_uuid, name, world, x, y, z, yaw, pitch, created_at, is_public " +
                "FROM warps WHERE user_id = ? ORDER BY id ASC";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Warp> list = new ArrayList<>();
                while (rs.next()) list.add(warpMapper.toWarp(rs));
                return list;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось получить список варпов игрока user_id=" + playerId, ex);
        }
    }

    @Override
    public List<Warp> getAllPublicWarps(int limit, int offset) {
        String sql = """
            SELECT id, user_id, owner_uuid, name, world,
                   x, y, z, yaw, pitch, created_at, is_public
            FROM warps
            WHERE is_public = 1
            ORDER BY name ASC
            LIMIT ? OFFSET ?
        """;

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<Warp> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(warpMapper.toWarp(rs));
                }
                return list;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось получить список публичных варпов (limit=" + limit + ", offset=" + offset + ")", ex);
        }
    }

    @Override
    public int countPublicWarps() {
        String sql = "SELECT COUNT(*) FROM warps WHERE is_public = 1";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось посчитать количество публичных варпов", ex);
        }
    }

    @Override
    public Warp insert(int ownerUserId, UUID ownerUuid, String name, String world,
                       double x, double y, double z, float yaw, float pitch, boolean isPublic) {
        String sql = "INSERT INTO warps " +
                "(user_id, owner_uuid, name, world, x, y, z, yaw, pitch, created_at, is_public) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP()*1000, ?)";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ownerUserId);
            ps.setString(2, ownerUuid.toString());
            ps.setString(3, name);
            ps.setString(4, world);
            ps.setDouble(5, x);
            ps.setDouble(6, y);
            ps.setDouble(7, z);
            ps.setFloat(8, yaw);
            ps.setFloat(9, pitch);
            ps.setBoolean(10, isPublic);
            ps.executeUpdate();

            int id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                id = keys.next() ? keys.getInt(1) : 0;
            }

            return getWarpById(id);
        } catch (SQLException ex) {
            throw new RuntimeException(" удалось создать варп '" + name + ex);
        }
    }

    @Override
    public boolean deleteByOwnerAndName(int ownerUserId, String name) {
        String sql = "DELETE FROM warps WHERE user_id = ? AND name = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ownerUserId);
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось удалить варп '" + name + "' владельца user_id=" + ownerUserId, ex);
        }
    }

    @Override
    public boolean setPublic(int ownerUserId, String name, boolean isPublic) {
        String sql = "UPDATE warps SET is_public = ? WHERE user_id = ? AND name = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isPublic);
            ps.setInt(2, ownerUserId);
            ps.setString(3, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось изменить публичность варпа '" + name + "' владельца user_id=" + ownerUserId, ex);
        }
    }
}
