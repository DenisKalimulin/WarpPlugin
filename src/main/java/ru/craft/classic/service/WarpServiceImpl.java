package ru.craft.classic.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.craft.classic.dao.WarpDao;
import ru.craft.classic.model.Warp;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class WarpServiceImpl implements WarpService {

    private final WarpDao warpDao;

    private static final int LIMIT_FOR_USER = 3;

    @Override
    public boolean canCreate(int ownerUserId, int limit) {
        return count(ownerUserId) < limit;
    }

    @Override
    public int count(int ownerUserId) {
        return warpDao.getWarpsOwnedByPlayer(ownerUserId).size();
    }

    @Override
    public Warp create(int ownerUserId, UUID ownerUuid, String name, Location loc, boolean isPublic) {
        Player player = Bukkit.getPlayer(ownerUuid);
        // валидация
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя варпа пустое");
        }
        if (name.length() > 48) {
            throw new IllegalArgumentException("Имя варпа слишком длинное (макс 48)");
        }
        if (loc == null || loc.getWorld() == null) {
            throw new IllegalArgumentException("Локация/мир не заданы");
        }

        // лимит
        if (!canCreate(ownerUserId, LIMIT_FOR_USER)) {
            throw new IllegalStateException("Достигнут лимит варпов");
        }

        // коллизия имени (имя глобально уникально)
        if (warpDao.getWarpByName(name) != null) {
            throw new IllegalStateException("Варп с таким именем уже существует");
        }

        // вставка
        return warpDao.insert(
                ownerUserId,
                ownerUuid,
                name,
                loc.getWorld().getName(),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch(),
                isPublic
        );
    }


    @Override
    public boolean delete(String name, int requesterUserId) {
        Warp warp = warpDao.getWarpByName(name);
        if (warp == null) {
            return false;
        }
        if (!Objects.equals(warp.getUserId(), requesterUserId)) {
            return false;
        }

        return warpDao.deleteByOwnerAndName(warp.getUserId(), name);
    }

    @Override
    public List<Warp> listOwned(int ownerUserId) {
        return warpDao.getWarpsOwnedByPlayer(ownerUserId);
    }

    @Override
    public List<Warp> listPublic(int limit, int offset) {
        List<Warp> all = warpDao.getAllPublicWarps(limit, offset);
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            return Collections.emptyList();
        }

        return warpDao.getAllPublicWarps(limit, offset);
    }

    @Override public int countPublic() {
        return warpDao.countPublicWarps();
    }

    @Override
    public Optional<Warp> getByName(String name) {
        return Optional.ofNullable(warpDao.getWarpByName(name));
    }

    @Override
    public Optional<Warp> resolveForTeleport(String name, int requesterUserId) {
        Warp warp = warpDao.getWarpByName(name);
        if (warp == null) {
            return Optional.empty();
        }

        boolean owner = Objects.equals(warp.getUserId(), requesterUserId);
        return (owner || Boolean.TRUE.equals(warp.getIsPublic())) ? Optional.of(warp) : Optional.empty();
    }

    @Override
    public boolean setPublic(String name, int requesterUserId, boolean isPublic) {
        Warp warp = warpDao.getWarpByName(name);
        if (warp == null) {
            return false;
        }
        if (!Objects.equals(warp.getUserId(), requesterUserId)) {
            return false;
        }

        return warpDao.setPublic(warp.getUserId(), name, isPublic);
    }
}
