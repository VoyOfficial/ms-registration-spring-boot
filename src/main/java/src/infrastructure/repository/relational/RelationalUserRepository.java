package src.infrastructure.repository.relational;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import src.domain.entity.User;
import src.domain.exception.InvalidUserException;
import src.domain.repository.UserRepository;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;

import java.util.Objects;
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
    public Optional<User> findByCpf(String cpf) {
        logger.info("RELATIONAL USER REPOSITORY - FIND USER BY CPF - CPF: {}", cpf);

        var optionalUserModel = jpaRepository.findByCpf(cpf);

        if (optionalUserModel.isPresent()) {
            var userModel = optionalUserModel.get();

            logger.info("RELATIONAL USER REPOSITORY - FIND USER BY CPF - USER ID: {}", userModel.getId());

            return Optional.of(userModel.toDomain());

        }

        logger.info("RELATIONAL USER REPOSITORY - FIND USER BY CPF - USER NOT FOUND BY CPF : {}", cpf);

        return Optional.empty();

    }

    private void userIsValid(User userDomain) {
        boolean isValid = isNull(userDomain) || isNull(userDomain.getName());

        if (isValid) {
            throw new InvalidUserException();
        }
    }

}
