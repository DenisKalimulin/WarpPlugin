package ru.craft.classic.db;

import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@NoArgsConstructor
public class DatabaseUtil {

    public static void initDb(DataSource ds) {

        exec(ds, """
                    CREATE TABLE IF NOT EXISTS users (
                      id         INT         NOT NULL,
                      uuid       CHAR(36)    NOT NULL,
                      username   VARCHAR(16) NOT NULL,
                      is_admin   TINYINT(1)  NOT NULL DEFAULT 0,
                      created_at BIGINT      NOT NULL DEFAULT (UNIX_TIMESTAMP()*1000),
                      updated_at BIGINT      NOT NULL DEFAULT (UNIX_TIMESTAMP()*1000),
                      PRIMARY KEY (id),
                      UNIQUE KEY uq_users_uuid (uuid),
                      UNIQUE KEY uq_users_username (username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
                """);

        exec(ds, """
                    CREATE TABLE IF NOT EXISTS warps (
                      id           INT AUTO_INCREMENT PRIMARY KEY,
                      user_id      INT         NOT NULL,
                      owner_uuid   CHAR(36)    NOT NULL,
                      name         VARCHAR(48) NOT NULL,
                      world        VARCHAR(64) NOT NULL,
                      x DOUBLE NOT NULL,
                      y DOUBLE NOT NULL,
                      z DOUBLE NOT NULL,
                      yaw  FLOAT NOT NULL,
                      pitch FLOAT NOT NULL,
                      created_at   BIGINT      NOT NULL DEFAULT (UNIX_TIMESTAMP()*1000),
                      is_public    TINYINT(1)  NOT NULL DEFAULT 1,
                      UNIQUE KEY uq_warp_name (name),
                      KEY idx_warps_user (user_id),
                      KEY idx_warps_owner_uuid (owner_uuid),
                      CONSTRAINT fk_warps_user
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
                """);

    }

    private static void exec(DataSource ds, String sql) {
        try (Connection connection = ds.getConnection(); Statement st = connection.createStatement()) {
            for (String str : sql.split(";")) {
                String trimmed = str.trim();
                if (!trimmed.isEmpty()) st.execute(trimmed);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка миграции БД", ex);
        }
    }
}
