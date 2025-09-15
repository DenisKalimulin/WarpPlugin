package ru.craft.classic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import ru.craft.classic.commands.WarpCommand;
import ru.craft.classic.dao.AuthMeDao;
import ru.craft.classic.dao.AuthMeDaoImpl;
import ru.craft.classic.dao.UserDao;
import ru.craft.classic.dao.UserDaoImpl;
import ru.craft.classic.dao.WarpDao;
import ru.craft.classic.dao.WarpDaoImpl;
import ru.craft.classic.db.DatabaseUtil;
import ru.craft.classic.listeners.PlayerJoinListener;
import ru.craft.classic.mappers.UserMapper;
import ru.craft.classic.mappers.UserMapperImpl;
import ru.craft.classic.mappers.WarpMapper;
import ru.craft.classic.mappers.WarpMapperImpl;
import ru.craft.classic.service.UserService;
import ru.craft.classic.service.UserServiceImpl;
import ru.craft.classic.service.WarpService;
import ru.craft.classic.service.WarpServiceImpl;

public class WarpPlugin extends JavaPlugin {
    private HikariDataSource appDataSource;
    private HikariDataSource authDataSource;

    // Сервисы
    private UserService userService;
    private WarpService warpService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        appDataSource = mkDs(
                getConfig().getString("database.app.url"),
                getConfig().getString("database.app.user"),
                getConfig().getString("database.app.pass")
        );

        authDataSource = mkDs(
                getConfig().getString("database.authme.url"),
                getConfig().getString("database.authme.user"),
                getConfig().getString("database.authme.pass")
        );

        DatabaseUtil.initDb(appDataSource);

        // Мапперы
        UserMapper userMapper = new UserMapperImpl();
        WarpMapper warpMapper = new WarpMapperImpl();

        // ДАО
        AuthMeDao authMeDao = new AuthMeDaoImpl(authDataSource);
        UserDao userDao = new UserDaoImpl(appDataSource, userMapper);
        WarpDao warpDao = new WarpDaoImpl(appDataSource, warpMapper);

        // Сервсиы
        userService = new UserServiceImpl(authMeDao, userDao, warpDao);
        int limit = getConfig().getInt("warps.per-user-limit", 3);
        warpService = new WarpServiceImpl(warpDao);

        // Регистрация команд и листенеров
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(userService, this), this);
        getCommand("warp").setExecutor(new WarpCommand(this, userService, warpService, limit));

        getLogger().info("WarpPlugin успешно запущен.");
    }

    @Override
    public void onDisable() {
        if (appDataSource != null) appDataSource.close();
        if (authDataSource != null) authDataSource.close();
    }

    private HikariDataSource mkDs(String url, String user, String pass) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("WarpPool-" + (user != null ? user : "app"));
        return new HikariDataSource(cfg);
    }
}