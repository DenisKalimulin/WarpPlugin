package ru.craft.classic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Сущность пользователя.
 * Берем из таблицы плагина AuthMe
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // Из AuthMe
    private Integer id;

    // Из Bukkit
    private UUID uuid;

    // Из AuthMe
    private String username;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private transient List<Warp> warps = Collections.emptyList();
}
