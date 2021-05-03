package com.milk4u.doorstep.delivery.repository;

import com.milk4u.doorstep.delivery.entity.InvoiceEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<InvoiceEntity, Integer>{
    List<Optional<InvoiceEntity>> findByCustomerId(int id);
}
