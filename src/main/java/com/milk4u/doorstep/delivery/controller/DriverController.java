package com.milk4u.doorstep.delivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milk4u.doorstep.delivery.entity.Driver;
import com.milk4u.doorstep.delivery.repository.DriverRepository;
import com.milk4u.doorstep.delivery.repository.DroplistRepository;

@RestController // This means that this class is a Controller
@RequestMapping(path="/driver") // This means URL's start with /demo (after Application path)
public class DriverController {
	
	@Autowired
	private DriverRepository driverRepo;
	
	@Autowired
	private DroplistRepository droplistRepo;
	
	@GetMapping(path="/getDriver")
	public Driver getDriver() {
		 return driverRepo.findById(1).get();
	}
	
	@GetMapping(path="/getDroplist")
	public Driver getDroplist() {
		 return driverRepo.findById(1).get();
	}
}
