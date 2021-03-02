package com.milk4u.doorstep.delivery.repository;

import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.ProductEntity;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer>{

}
