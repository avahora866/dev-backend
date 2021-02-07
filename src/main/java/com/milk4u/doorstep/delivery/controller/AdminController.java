package com.milk4u.doorstep.delivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milk4u.doorstep.delivery.entity.Admin;
import com.milk4u.doorstep.delivery.repository.AdminRepository;
import com.milk4u.doorstep.delivery.repository.CustomerRepository;
import com.milk4u.doorstep.delivery.repository.ProductRepository;

@RestController // This means that this class is a Controller
@RequestMapping(path="/admin") // This means URL's start with /demo (after Application path)
public class AdminController {

	@Autowired
	private AdminRepository adminRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@GetMapping(path="/getAdmin")
	public Admin getAdmin() {
		 return adminRepo.findById(1).get();
	}
}
