package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.User;
import src.domain.repository.UserRepository;
import src.domain.usecase.UserRegistryUseCase;

@Service
public class UserRegistryService implements UserRegistryUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository repository;

    @Override
    public Long registry(User userDomain) {

        logger.info("USER REGISTRY SERVICE - REGISTRY - User: {}", userDomain.getName());

        var savedUser = repository.saveUser(userDomain);

        logger.info("USER REGISTRY SERVICE - REGISTRY - Registered User: {}", savedUser.getId());

        return savedUser.getId();

    }

}
