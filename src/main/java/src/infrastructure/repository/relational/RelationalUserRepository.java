package src.infrastructure.repository.relational;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import src.domain.entity.User;
import src.domain.exception.InvalidUserException;
import src.domain.exception.UserNotFoundException;
import src.domain.repository.UserRepository;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;

import java.util.Optional;

import static java.util.Objects.isNull;

@Repository
public class RelationalUserRepository implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserJpaRepository jpaRepository;

    @Override
    @Transactional
    public User saveUser(User userDomain) {

        userIsValid(userDomain);

        logger.info("RELATIONAL USER REPOSITORY - SAVE USER - User: {}", userDomain.getName());

        var userModel = new UserModel(userDomain);

        userModel = jpaRepository.save(userModel);

        logger.info("RELATIONAL USER REPOSITORY - REGISTERED USER - User ID: {}", userModel.getId());

        return userModel.toDomain();
    }

    @Override
    public Optional<User> findById(Long userId) {

        logger.info("RELATIONAL USER REPOSITORY - FIND BY ID - User ID: {}", userId);

        var optionalUserModel = jpaRepository.findById(userId);

        if(optionalUserModel.isPresent()){

            var userModel = optionalUserModel.get();

            logger.info("RELATIONAL USER REPOSITORY - FIND BY ID - ID: {}", userId);

            return Optional.of(userModel.toDomain());

        }

        logger.info("RELATIONAL USER REPOSITORY - FIND BY ID - USER NOT FOUND - ID : {}", userId);

        return Optional.empty();

    }

    @Override
    public Optional<User> findByCpf(String cpf) {

        logger.info("RELATIONAL USER REPOSITORY - FIND BY CPF - CPF: {}", cpf);

        var optionalUserModel = jpaRepository.findByCpf(cpf);

        if (optionalUserModel.isPresent()) {
            var userModel = optionalUserModel.get();

            logger.info("RELATIONAL USER REPOSITORY - FIND BY CPF - USER FOUND - ID: {}", userModel.getId());

            return Optional.of(userModel.toDomain());

        }

        logger.info("RELATIONAL USER REPOSITORY - FIND BY CPF - USER NOT FOUND - CPF : {}", cpf);

        return Optional.empty();

    }

    private void userIsValid(User userDomain) {
        boolean isValid = isNull(userDomain) || isNull(userDomain.getName());

        if (isValid) {
            throw new InvalidUserException();
        }

    }

}
