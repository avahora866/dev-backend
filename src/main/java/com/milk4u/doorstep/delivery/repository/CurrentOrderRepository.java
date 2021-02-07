package com.milk4u.doorstep.delivery.repository;

import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.CurrentOrder;

public interface CurrentOrderRepository extends CrudRepository<CurrentOrder, Integer>{

}
