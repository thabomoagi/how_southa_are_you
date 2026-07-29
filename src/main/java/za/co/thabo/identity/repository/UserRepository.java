package za.co.thabo.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.thabo.identity.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}