package ru.craft.classic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthMeAccount {
    private Integer id;
    private String username;
}
