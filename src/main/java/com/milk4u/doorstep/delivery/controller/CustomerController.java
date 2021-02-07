package com.milk4u.doorstep.delivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milk4u.doorstep.delivery.entity.Customer;
import com.milk4u.doorstep.delivery.repository.CurrentOrderRepository;
import com.milk4u.doorstep.delivery.repository.CustomerRepository;
import com.milk4u.doorstep.delivery.repository.InvoiceRepository;
import com.milk4u.doorstep.delivery.repository.ProductRepository;
import com.milk4u.doorstep.delivery.repository.TrollyRepository;

@RestController // This means that this class is a Controller
@RequestMapping(path="/customer") // This means URL's start with /demo (after Application path)
public class CustomerController {
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CurrentOrderRepository currentOrderRepo;
	
	@Autowired
	private InvoiceRepository invoiceRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private TrollyRepository trollyRepo;
	
	@GetMapping(path="/getCustomer")
	public Customer getCustomer() {
		 return customerRepo.findById(1).get();
	}

}
