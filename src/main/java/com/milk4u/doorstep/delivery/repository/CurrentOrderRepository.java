package com.milk4u.doorstep.delivery.repository;

import com.milk4u.doorstep.delivery.entity.DroplistEntity;
import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.CurrentOrderEntity;

import java.util.List;
import java.util.Optional;

public interface CurrentOrderRepository extends CrudRepository<CurrentOrderEntity, Integer>{
    List<Optional<CurrentOrderEntity>> findByCustomerId(int id);
    void deleteByCustomerId(int id);

}
