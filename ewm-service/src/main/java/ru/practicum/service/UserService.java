package ru.practicum.service;

import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> usersId, int from, int size);

    void deleteUser(long userId);

}
