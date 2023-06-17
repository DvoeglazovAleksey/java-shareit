package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAllUsersDto() {
        return userRepository.findAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserDtoById(long id) {
        return UserMapper.toUserDto(userRepository.findUserById(id));
    }

    @Override
    public UserDto addUserDto(UserDto userDto) {
        validUser(userDto);
        if (userRepository.isEmailPresentInRepository(UserMapper.toUser(userDto))) {
            throw new EmailException(userDto.getEmail());
        } else {
            return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
        }
    }

    @Override
    public UserDto updateUserDto(long id, UserDto userDto) {
        User user = userRepository.findUserById(id);
        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new ValidationException("Name не может быть пустым");
            }
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank()) {
                throw new ValidationException("E-mail не может быть пустым");
            }
            if (!user.getEmail().equals(userDto.getEmail()) && userRepository.isEmailPresentInRepository(UserMapper.toUser(userDto))) {
                throw new EmailException(userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(id, user));
    }

    @Override
    public boolean deleteUser(long id) {
        return userRepository.deleteUser(id);
    }

    private void validUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email null");
        } else if (userDto.getName() == null) {
            throw new ValidationException("Name null");
        }
    }
}
