package ru.craft.classic.service;

import ru.craft.classic.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    /**
     * Гарантирует, что пользователь есть в таблице.
     * Если нет - создаёт запись, username берём из AuthMe или Bukkit.
     * Возвращает актуальную запись.
     */
    User ensureUser(UUID uuid, String bukkitName);

    /**
     * Поиск существующего пользователя по UUID
     */
    Optional<User> getByUuid(UUID uuid);

    /**
     * Поиск существующего пользователя по id
     */
    Optional<User> getById(int id);

    /**
     * Поиск существующего пользователя по username
     */
    Optional<User> getByUsername(String username);

    /**
     * Подсчёт и проверка лимита варпов. Делегирует в WarpDao.
     */
    int getWarpCount(int userId);

    /**
     * Может ли игрок создать варп
     */
    boolean canCreateWarp(int userId, int limit);
}
