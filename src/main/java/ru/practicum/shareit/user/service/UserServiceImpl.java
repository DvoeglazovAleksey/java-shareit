package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoty.UserRepository;
import ru.practicum.shareit.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Valid valid;

    @Override
    public List<UserDto> findAllUsersDto() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long id) {
        User user = valid.checkUser(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto addUserDto(UserDto userDto) {
        validUser(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUserDto(long id, UserDto userDto) {
        User user = valid.checkUser(id);
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
            if (!user.getEmail().equals(userDto.getEmail()) && isEmailPresentInRepository(UserMapper.toUser(userDto))) {
                throw new EmailException(userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private void validUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email null");
        } else if (userDto.getName() == null) {
            throw new ValidationException("Name null");
        }
    }

    private boolean isEmailPresentInRepository(User user) {
        boolean flag = false;
        for (User user2 : userRepository.findAll()) {
            if (user2.getEmail().equals(user.getEmail())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
