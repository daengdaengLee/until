package hello.until.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
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
		
		validateUser(email); 
			
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		userRepository.save(user);
		
	}
	
	private void validateUser(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		
		if(user.isPresent())
			throw new CustomException(ExceptionCode.DUPLICATE_EAMIL_USER_TO_CREATE);
	}

	public Optional<User> updateUser(long userId, String email, String password){
		Optional<User> opUser = this.getUserById(userId);
		if(opUser.isPresent()){
			User user = opUser.get();
			user.updateEmail(email);
			user.updatePassword(password);
			user = userRepository.save(user);
			return Optional.ofNullable(user);
		}
		else{
			return Optional.empty();
		}
	}

    @Transactional(readOnly = true)
    public Optional<User> getUserById(long id){
        return userRepository.findById(id);
    }
}
