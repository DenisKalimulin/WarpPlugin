package ru.craft.classic.service;

import lombok.RequiredArgsConstructor;
import ru.craft.classic.dao.AuthMeDao;
import ru.craft.classic.dao.UserDao;
import ru.craft.classic.dao.WarpDao;
import ru.craft.classic.model.AuthMeAccount;
import ru.craft.classic.model.User;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthMeDao authMeDao;

    private final UserDao userDao;

    private final WarpDao warpDao;


    @Override
    public User ensureUser(UUID uuid, String bukkitName) {
        AuthMeAccount acc = authMeDao.findByUsername(bukkitName)
                .orElseThrow(() -> new IllegalStateException(
                        "Пользователь '" + bukkitName + "' не найден в AuthMe"));

        return userDao.upsertByAuthmeId(acc.getId(), uuid, acc.getUsername());
    }

    @Override
    public Optional<User> getByUuid(UUID uuid) {
        return userDao.findByUuid(uuid);
    }

    @Override
    public Optional<User> getById(int id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public int getWarpCount(int userId) {
        return warpDao.getWarpsOwnedByPlayer(userId).size();
    }

    @Override
    public boolean canCreateWarp(int userId, int limit) {
        return getWarpCount(userId) < limit;
    }
}
