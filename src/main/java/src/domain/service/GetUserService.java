package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.User;
import src.domain.exception.UserNotFoundException;
import src.domain.repository.UserRepository;
import src.domain.usecase.GetUserUseCase;

@Service
public class GetUserService implements GetUserUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository repository;

    @Override
    public User getUserById(Long userId) {

        logger.info("GET USER SERVICE - GET USER BY ID - User ID: {}", userId);

        var userDomain = repository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        logger.info("GET USER SERVICE - GET USER BY ID - User found : {}", userDomain);

        return userDomain;

    }

}
