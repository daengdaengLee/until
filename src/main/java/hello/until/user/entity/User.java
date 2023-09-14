package hello.until.user.entity;

import hello.until.user.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateEmail(String email) {
        if (email != null) {
            this.email = email;
        }
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        if (password != null) {
            this.password = passwordEncoder.encode(password);
        }
    }

    public void updateRole(Role role) {
        if (role != null) {
            this.role = role;
        }
    }

    public boolean isSameUser(User user) {
        return this.id != null && user != null && this.id.equals(user.getId());
    }
}
