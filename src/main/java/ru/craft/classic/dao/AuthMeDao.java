package ru.craft.classic.dao;

import ru.craft.classic.model.AuthMeAccount;

import java.util.Optional;

public interface AuthMeDao {
    /**
     * Поиск по нику
     */
    Optional<AuthMeAccount> findByUsername(String username);

    /**
     * Поиск по id
     */
    Optional<AuthMeAccount> findById(Integer id);
}