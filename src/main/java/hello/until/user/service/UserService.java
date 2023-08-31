package hello.until.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	public void createUser(String email, String password) {
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		validateUser(email);
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setCreateAt(currentDateTime);
		user.setUpdateAt(currentDateTime);
		userRepository.save(user);
		
	}
	
	public void validateUser(String eamil) {
		User findUser = userRepository.findByEmail(eamil);
		if(findUser != null) {
			throw new IllegalStateException("이미 가입된 회원 메일입니다.");
		}
	}
}
