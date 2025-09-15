package ru.craft.classic.mappers;

import ru.craft.classic.model.Warp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WarpMapperImpl implements WarpMapper {

    @Override
public Warp toWarp(ResultSet rs) throws SQLException {
        return Warp.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .ownerUuid(UUID.fromString(rs.getString("owner_uuid")))
                .name(rs.getString("name"))
                .world(rs.getString("world"))
                .x(rs.getDouble("x"))
                .y(rs.getDouble("y"))
                .z(rs.getDouble("z"))
                .yaw(rs.getFloat("yaw"))
                .pitch(rs.getFloat("pitch"))
                .createdAt(rs.getLong("created_at"))
                .isPublic(rs.getBoolean("is_public"))
                .build();
    }
}
