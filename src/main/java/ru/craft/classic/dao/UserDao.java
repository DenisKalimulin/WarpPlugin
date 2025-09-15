package ru.craft.classic.dao;

import ru.craft.classic.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    /**
     * Поиск по id
     */
    Optional<User> findById(Integer id);

    /**
     * Поиск по UUID
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Поиск по username
     */
    Optional<User> findByUsername(String username);


    /**
     * Гарантия того, что пользователь будет в таблице плагина WarpPlugin
     * использует authmeId как первичный ключ.
     * @return Пользователь из БД warpPlugin
     */
    User upsertByAuthmeId(int authmeId, UUID uuid, String username);
}
