package hello.until.user.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import hello.until.user.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새 User 저장")
    public void saveUser() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("12345678");
        LocalDateTime expected = LocalDateTime.now().plusSeconds(1);
        
        // when
        User dbUser = userRepository.save(user);

        // then
        assertEquals(dbUser.getEmail(), user.getEmail());
        assertEquals(dbUser.getPassword(), user.getPassword());
        assertThat(dbUser.getCreatedAt()).isBefore(expected);
        assertThat(dbUser.getUpdatedAt()).isBefore(expected);
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
        User dbUser = userRepository.save(user);
        
        LocalDateTime expected = LocalDateTime.now().plusSeconds(1);
        
        int delaySeconds = 3;
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(delaySeconds))
                .until(() -> true);
       
        LocalDateTime expectedUpdate1 = dbUser.getCreatedAt().plusSeconds(delaySeconds);
        LocalDateTime expectedUpdate2 = LocalDateTime.now().plusSeconds(1);
        
        // when
        dbUser.updateEmail("test2@test.com");
        User savedUser = userRepository.saveAndFlush(dbUser);

        // then
        assertEquals(dbUser.getEmail(), savedUser.getEmail());
        assertEquals(dbUser.getPassword(), savedUser.getPassword());
        
        assertThat(savedUser.getUpdatedAt()).isAfterOrEqualTo(expectedUpdate1);
        assertThat(savedUser.getUpdatedAt()).isBeforeOrEqualTo(expectedUpdate2);
    }
}
