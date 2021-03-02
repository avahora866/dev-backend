package com.milk4u.doorstep.delivery.repository;

import org.springframework.data.repository.CrudRepository;

import com.milk4u.doorstep.delivery.entity.InvoiceEntity;

public interface InvoiceRepository extends CrudRepository<InvoiceEntity, Integer>{

}
