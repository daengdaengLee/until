package hello.until.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.until.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

}
