package hello.until.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateEmail(String email){
        if(email != null){
            this.email = email;
        }
    }
    public void updatePassword(String password){
        if(password != null){
            this.password = password;
        }
    }

}
