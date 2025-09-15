package ru.craft.classic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Warp {
    private Integer id;
    private String name;
    private UUID ownerUuid;
    private String world;
    private Double x, y, z;
    private Float yaw, pitch;
    private Long createdAt; // epoch millis
    private Boolean isPublic;
    private Integer userId;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
