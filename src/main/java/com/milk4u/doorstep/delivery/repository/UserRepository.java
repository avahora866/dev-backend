package com.milk4u.doorstep.delivery.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	Optional<UserEntity> findByUsernameAndPassword(String username, String password);
//	Optional<UserEntity> findTypeOfUser(String username, String type);

}
