package ru.practicum.ewm.main.user;

import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(NewUserRequest dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
