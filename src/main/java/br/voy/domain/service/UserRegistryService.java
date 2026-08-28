package br.voy.domain.service;

import static java.util.Objects.isNull;

import br.voy.domain.entity.User;
import br.voy.domain.exception.InvalidUserException;
import br.voy.domain.repository.UserRepository;
import br.voy.domain.usecase.UserRegistryUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistryService implements UserRegistryUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private UserRepository repository;

    @Override
    public Long registry(User userDomain) {

        userIsValid(userDomain);

        logger.info("USER REGISTRY SERVICE - REGISTRY - User: {}", userDomain.getName());

        var savedUser = repository.saveUser(userDomain);

        logger.info("USER REGISTRY SERVICE - REGISTRY - Registered User: {}", savedUser.getId());

        return savedUser.getId();
    }

    private void userIsValid(User userDomain) {
        boolean isValid = isNull(userDomain) || isNull(userDomain.getName());

        if (isValid) {
            throw new InvalidUserException();
        }
    }
}
