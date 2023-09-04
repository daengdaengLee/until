package hello.until.user.repository;


import hello.until.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새 User 저장")
    public void saveUser() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("12345678");
        user.setCreatedAt(currentDateTime);
        user.setUpdatedAt(currentDateTime);
        // when
        User dbUser = userRepository.save(user);

        // then
        assertEquals(dbUser.getEmail(), user.getEmail());
        assertEquals(dbUser.getPassword(), user.getPassword());
        assertEquals(dbUser.getCreatedAt(), currentDateTime);
        assertEquals(dbUser.getUpdatedAt(), currentDateTime);
    }

    @Test
    @DisplayName("회원 정보 수정")
    public void updateUser() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("12345678");
        user.setCreatedAt(currentDateTime);
        user.setUpdatedAt(currentDateTime);
        User dbUser = userRepository.save(user);
        LocalDateTime updateDateTime = LocalDateTime.now();
        dbUser.updateUser("test2@test.com", null, updateDateTime);
        // when
        User savedUser = userRepository.save(dbUser);

        // then
        assertEquals(dbUser.getEmail(), savedUser.getEmail());
        assertEquals(dbUser.getPassword(), savedUser.getPassword());
        assertEquals(dbUser.getCreatedAt(), savedUser.getCreatedAt());
        assertEquals(dbUser.getUpdatedAt(), savedUser.getUpdatedAt());
    }
}
