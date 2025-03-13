package org.example.lab_6_n01581413.repository;

import org.example.lab_6_n01581413.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByUsername(String username);
}