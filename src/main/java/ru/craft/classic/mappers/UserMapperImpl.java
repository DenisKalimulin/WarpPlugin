package ru.craft.classic.mappers;

import ru.craft.classic.model.AuthMeAccount;
import ru.craft.classic.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(AuthMeAccount authMeAccount) {
        return User.builder()
                   .id(authMeAccount.getId())
                   .username(authMeAccount.getUsername())
                   .build();
    }

    @Override
    public User toUserFromRs(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .uuid(UUID.fromString(resultSet.getString("uuid")))
                .username(resultSet.getString("username"))
                .build();
    }
}
