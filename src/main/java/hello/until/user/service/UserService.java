package hello.until.user.service;

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
	
    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());
    }
}
