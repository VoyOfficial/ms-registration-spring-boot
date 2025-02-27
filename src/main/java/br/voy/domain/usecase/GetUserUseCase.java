package br.voy.domain.usecase;

import br.voy.domain.entity.User;

public interface GetUserUseCase {

    User getUserById(Long userId);

}
