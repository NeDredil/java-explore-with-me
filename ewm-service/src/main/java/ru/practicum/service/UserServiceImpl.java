package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.Constant.ALREADY_EXIST_USER;
import static ru.practicum.Constant.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userRepository.existsAllByName(user.getName())) {
            throw new ValidationException(String.format(ALREADY_EXIST_USER, user.getName()));
        }
        User saved = userRepository.save(user);
        return UserMapper.toUserDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> usersId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users;
        if (usersId == null) {
            users = userRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        } else {
            users = userRepository.findAllByIdIn(usersId, pageRequest);
        }
        return UserMapper.listToUserDto(users);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        userRepository.deleteById(userId);
    }

}
