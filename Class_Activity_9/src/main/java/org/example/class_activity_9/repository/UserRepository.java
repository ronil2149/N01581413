package org.example.class_activity_9.repository;

import org.example.class_activity_9.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<User> findByUsername(String username);
}