package src.infrastructure.repository.relational;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import src.domain.entity.User;
import src.domain.repository.UserRepository;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;


@Repository
public class RelationalUserRepository implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserJpaRepository jpaRepository;

    @Override
    public User saveUser(User userDomain) {

        logger.info("RELATIONAL USER REPOSITORY - SAVE USER - User: {}", userDomain.getName());

        var userModel = new UserModel(userDomain);

        userModel = jpaRepository.save(userModel);

        logger.info("RELATIONAL USER REPOSITORY - SAVE USER - User ID: {}", userModel.getId());

        return userModel.toDomain();
    }

}
