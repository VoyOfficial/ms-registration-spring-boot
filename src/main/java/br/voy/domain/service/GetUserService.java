package br.voy.domain.service;

import br.voy.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import br.voy.domain.exception.UserNotFoundException;
import br.voy.domain.repository.UserRepository;
import br.voy.domain.usecase.GetUserUseCase;

@Service
public class GetUserService implements GetUserUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository repository;

    @Value("${user.not.found.default.message}")
    private String defaultMessage;

    @Override
    public User getUserById(Long userId) {

        logger.info("GET USER SERVICE - GET USER BY ID - User ID: {}", userId);

        var userDomain = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(defaultMessage));

        logger.info("GET USER SERVICE - GET USER BY ID - User found : {}", userDomain);

        return userDomain;

    }

}
