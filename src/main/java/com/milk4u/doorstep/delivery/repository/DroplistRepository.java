package com.milk4u.doorstep.delivery.repository;

import com.milk4u.doorstep.delivery.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.DroplistEntity;

import java.util.List;
import java.util.Optional;

public interface DroplistRepository extends CrudRepository<DroplistEntity, Integer>{
    List<Optional<DroplistEntity>> findByDriverId(int id);
}
