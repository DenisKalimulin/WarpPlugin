package ru.craft.classic.service;

import org.bukkit.Location;
import ru.craft.classic.model.Warp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarpService {
    /**
     * Можно ли создавать новый варп, с учетом лимита.
     */
    boolean canCreate(int ownerUserId, int limit);

    /**
     * Подсчет количества варпов игрока
     */
    int count(int ownerUserId);

    /**
     * Создать варп: проверяет лимит и коллизии имени; сохраняет в БД.
     */
    Warp create(int ownerUserId, UUID ownerUuid, String name,
                Location loc, boolean isPublic);

    /**
     * Удалить варп по имени (при глобальной уникальности достаточно имени + проверка прав).
     */
    boolean delete(String name, int requesterUserId);

    /**
     * Список варпов владельца.
     */
    List<Warp> listOwned(int ownerUserId);

    /**
     * Список публичных
     */
    List<Warp> listPublic(int limit, int offset);

    /**
     * Подсчет публичных варпов
     */
    int countPublic();

    /**
     * Найти варп по имени (имя глобально уникально).
     */
    Optional<Warp> getByName(String name);

    /**
     * Проверка доступа и получение цели телепорта (без вызова Bukkit API).
     */
    Optional<Warp> resolveForTeleport(String name, int requesterUserId);

    /**
     * Изменить публичность
     */
    boolean setPublic(String name, int requesterUserId, boolean requesterIsAdmin);
}

