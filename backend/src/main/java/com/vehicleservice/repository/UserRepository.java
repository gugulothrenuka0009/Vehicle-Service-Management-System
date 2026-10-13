package com.vehicleservice.repository;

import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Role> findByRole(Role role);
    List<User> findAllByRole(Role role);
    List<User> findAllByRoleAndActive(Role role, boolean active);
}
