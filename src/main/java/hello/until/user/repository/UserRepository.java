package hello.until.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.until.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
}
