package com.milk4u.doorstep.delivery.repository;

import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}
