package ru.craft.classic.mappers;

import ru.craft.classic.model.Warp;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface WarpMapper {
    Warp toWarp(ResultSet rs) throws SQLException;
}
