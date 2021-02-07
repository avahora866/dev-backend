package com.milk4u.doorstep.delivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.milk4u.doorstep.delivery.entity.UserEntity;
import com.milk4u.doorstep.delivery.repository.UserRepository;
import com.milk4u.doorstep.delivery.request.LoginDetails;

@RestController // This means that this class is a Controller
public class Controller {
	
	@Autowired
	private UserRepository userRepo;
		
	@GetMapping(path="/getCustomer")
	public UserEntity getCustomer() {
		 return userRepo.findById(1).get();
	}
	
	@PostMapping(path="/verifyLogin")
	public ResponseEntity<String>  verifyLogin(@RequestBody LoginDetails loginDetails ) {
		if(userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
		}
	}

}
