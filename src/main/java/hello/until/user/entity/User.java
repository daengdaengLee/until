package hello.until.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateUser(String email, String password, LocalDateTime updatedAt){
        if(email != null){
            this.email = email;
            this.updatedAt = updatedAt;
        }
        if(password != null){
            this.password = password;
            this.updatedAt = updatedAt;
        }
    }

}
