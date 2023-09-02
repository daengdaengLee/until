package hello.until.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	@Transactional 
	public void createUser(String email, String password) {
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		if(validateUser(email).isPresent()) {
			throw new IllegalStateException("이미 가입 된 회원 메일입니다.");
		};
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setCreatedAt(currentDateTime);
		user.setUpdatedAt(currentDateTime);
		userRepository.save(user);
		
	}
	
	private Optional<User> validateUser(String eamil) {
		User user = userRepository.findByEmail(eamil);
		return Optional.ofNullable(user);
		
	}
}
