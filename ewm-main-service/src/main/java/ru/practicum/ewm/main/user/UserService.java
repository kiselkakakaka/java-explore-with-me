package ru.practicum.ewm.main.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.BadRequestException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto create(NewUserRequest request) {
        try {
            User saved = userRepository.save(UserMapper.toEntity(request));
            return UserMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Email must be unique");
        }
    }

    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        validatePage(from, size);

        PageRequest page = PageRequest.of(from / size, size);
        List<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findByIdIn(ids, page);
        }

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }

    private void validatePage(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from must be >= 0 and size must be > 0");
        }
    }
}
