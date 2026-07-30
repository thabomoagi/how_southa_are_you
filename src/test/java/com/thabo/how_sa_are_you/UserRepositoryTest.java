package com.thabo.how_sa_are_you;

import com.thabo.how_sa_are_you.user.User;
import com.thabo.how_sa_are_you.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesAndReadsUserBack() {
        User user = new User();
        user.setUsername("thabo_test");
        user.setPasswordHash("not-a-real-hash-yet");

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("thabo_test");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("USER");
    }
}