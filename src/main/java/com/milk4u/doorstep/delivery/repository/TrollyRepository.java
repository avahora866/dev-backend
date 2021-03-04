package com.milk4u.doorstep.delivery.repository;

import com.milk4u.doorstep.delivery.entity.CurrentOrderEntity;
import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.TrollyEntity;

import java.util.List;
import java.util.Optional;

public interface TrollyRepository extends CrudRepository<TrollyEntity, Integer>{
    List<Optional<TrollyEntity>> findByCustomerId(int id);
    void deleteByCustomerId(int id);

}
