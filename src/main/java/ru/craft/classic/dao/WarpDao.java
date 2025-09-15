package ru.craft.classic.dao;

import ru.craft.classic.model.Warp;

import java.util.List;
import java.util.UUID;

public interface WarpDao {
    /**
     * Получить варп по id
     */
    Warp getWarpById(Integer id);

    /**
     * Получить варп по имени
     */
    Warp getWarpByName(String name);

    /**
     * Получить варпы определенного игрока
     */
    List<Warp> getWarpsOwnedByPlayer(Integer playerId);

    /**
     * Получить список всех варпов
     */
    List<Warp> getAllPublicWarps(int limit, int offset);

    /**
     * Подстчет всех публичных варпов
     */
    int countPublicWarps();

    /**
     * Записать в таблицу варп
     */
    Warp insert(int ownerUserId,
                UUID ownerUuid,
                String name,
                String world,
                double x, double y, double z,
                float yaw, float pitch,
                boolean isPublic);

    /**
     * Удалить варп
     */
    boolean deleteByOwnerAndName(int ownerUserId, String name);

    /**
     * Сделать публичным
     */
    boolean setPublic(int ownerUserId, String name, boolean isPublic);
}
