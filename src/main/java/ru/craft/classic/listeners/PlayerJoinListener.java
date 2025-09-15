package ru.craft.classic.listeners;


import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import ru.craft.classic.service.UserService;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final UserService userService;
    private final Plugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                userService.ensureUser(player.getUniqueId(), player.getName());
            } catch (Exception ex) {
                plugin.getLogger().warning("User ensure failed for " + player.getName() + ": " + ex.getMessage());
            }
        });
    }
}