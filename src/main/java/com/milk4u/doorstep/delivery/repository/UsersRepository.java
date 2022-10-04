package com.milk4u.doorstep.delivery.repository;

import java.util.List;
import java.util.Optional;

import com.milk4u.doorstep.delivery.entity.UsersEntity;
import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.UsersEntity;

public interface UsersRepository extends CrudRepository<UsersEntity, Integer> {
	Optional<UsersEntity> findByUsernameAndPassword(String username, String password);
	List<Optional<UsersEntity>> findByType(String type);

}
