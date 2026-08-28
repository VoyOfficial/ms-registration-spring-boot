package br.voy.domain.usecase;

import br.voy.domain.entity.User;

public interface UserRegistryUseCase {

    Long registry(User user);
}
