package ru.craft.classic.mappers;

import ru.craft.classic.model.AuthMeAccount;
import ru.craft.classic.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserMapper {
    /**
     * Преобразование сущности в user из AuthMeAccount
     */
    User toUser(AuthMeAccount authMeAccount);

    /**
     * Преобразование в сущность User из ResultSet
     */
    User toUserFromRs(ResultSet resultSet) throws SQLException;
}
