package ru.craft.classic.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.craft.classic.service.UserService;
import ru.craft.classic.service.WarpService;

@RequiredArgsConstructor
public class WarpCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final UserService userService;
    private final WarpService warpService;
    private final int perUserLimit;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Только для игроков");
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage("§e/warp set <name> [public|private], /warp del <name>, /warp mylist, /warp list [page], /warp <name>");
            return true;
        }

        String sub = strings[0].toLowerCase();
        switch (sub) {
            case "set": {
                if (strings.length < 2) {
                    player.sendMessage("§cУкажи имя: /warp set <name> [public|private]");
                    return true;
                }
                String name = strings[1];
                boolean isPublic = strings.length >= 3 ? !"private".equalsIgnoreCase(strings[2]) : true;
                Location loc = player.getLocation();

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        var user = userService.ensureUser(player.getUniqueId(), player.getName());
                        if (!warpService.canCreate(user.getId(), perUserLimit)) {
                            sync(player, "§cДостигнут лимит варпов (" + perUserLimit + ").");
                            return;
                        }
                        var w = warpService.create(user.getId(), player.getUniqueId(), name, loc, isPublic);
                        sync(player, "§aВарп §e" + w.getName() + "§a создан.");
                    } catch (Exception ex) {
                        sync(player, "§cОшибка: " + ex.getMessage());
                    }
                });
                return true;
            }
            case "del": {
                if (strings.length < 2) {
                    player.sendMessage("§c/warp del <name>");
                    return true;
                }
                String name = strings[1];

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    var userOpt = userService.getByUuid(player.getUniqueId());
                    if (userOpt.isEmpty()) {
                        sync(player, "§cТы ещё не синхронизирован.");
                        return;
                    }
                    boolean ok = warpService.delete(name, userOpt.get().getId());
                    sync(player, ok ? "§aУдалено." : "§cНет прав или варп не найден.");
                });
                return true;
            }
            case "mylist": {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    var userOpt = userService.getByUuid(player.getUniqueId());
                    if (userOpt.isEmpty()) {
                        sync(player, "§cТы ещё не синхронизирован.");
                        return;
                    }
                    var list = warpService.listOwned(userOpt.get().getId());
                    if (list.isEmpty()) {
                        sync(player, "§7У тебя нет варпов.");
                        return;
                    }
                    String joined = String.join(", ", list.stream().map(w -> w.getName()).toList());
                    sync(player, "§aТвои варпы: §e" + joined);
                });
                return true;
            }

            case "list": {
                int tmp = 1;
                if (strings.length >= 2) {
                    try {
                        tmp = Math.max(1, Integer.parseInt(strings[1]));
                    } catch (NumberFormatException ignored) {}
                }

                final int page = tmp;
                final int pageSize = 20;

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    int total = warpService.countPublic();
                    if (total == 0) {
                        sync(player, "§7Публичных варпов пока нет.");
                        return;
                    }
                    int pages = (total + pageSize - 1) / pageSize;
                    int safePage = Math.min(page, pages);    // ок: page — final
                    int offset = (safePage - 1) * pageSize;

                    var list = warpService.listPublic(pageSize, offset);

                    // собираем вывод столбиком с нумерацией
                    StringBuilder body = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        var w = list.get(i);
                        int num = offset + i + 1;
                        body.append("§7").append(num).append(". §e").append(w.getName()).append('\n');
                    }
                    if (body.length() == 0) body.append("§7(пусто)");

                    sync(player, String.format("§aПубличные варпы (§eстр. %d/%d, всего %d§a):\n%s",
                            safePage, pages, total, body.toString().trim()));

                    if (safePage < pages) {
                        sync(player, "§7Следующая страница: §e/warp list " + (safePage + 1));
                    }
                });
                return true;
            }

            default: {
                String name = strings[0];
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    var userOpt = userService.getByUuid(player.getUniqueId());
                    if (userOpt.isEmpty()) {
                        sync(player, "§cТы ещё не синхронизирован.");
                        return;
                    }
                    var wOpt = warpService.resolveForTeleport(name, userOpt.get().getId());
                    if (wOpt.isEmpty()) {
                        sync(player, "§cВарп не найден или нет доступа.");
                        return;
                    }
                    var target = wOpt.get().toLocation();
                    if (target.getWorld() == null) {
                        sync(player, "§cМир не загружен: " + wOpt.get().getWorld());
                        return;
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.teleport(target);
                        player.sendMessage("§aТелепортация на §e" + name);
                    });
                });
                return true;
            }
        }
    }

    private void sync(Player p, String msg) {
        plugin.getServer().getScheduler().runTask(plugin, () -> p.sendMessage(msg));
    }
}
