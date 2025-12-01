package ru.practicum.ewm.main.user;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(Collection<Long> ids, Pageable pageable);
}
