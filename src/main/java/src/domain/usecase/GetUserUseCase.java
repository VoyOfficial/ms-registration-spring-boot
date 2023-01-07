package src.domain.usecase;

import src.domain.entity.User;

public interface GetUserUseCase {

    User getUserById(Long userId);

}
