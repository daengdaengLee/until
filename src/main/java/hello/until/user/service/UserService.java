package hello.until.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	
	@Transactional 
	public void createUser(String email, String password) {
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		validateUser(email); 
			
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setCreatedAt(currentDateTime);
		user.setUpdatedAt(currentDateTime);
		userRepository.save(user);
		
	}
	
	private void validateUser(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		
		if(user.isPresent())
			throw new IllegalStateException("이미 가입 된 회원 메일입니다.");
	}

    @Transactional(readOnly = true)
    public Optional<User> getUserById(long id){
        return userRepository.findById(id);
    }
}
