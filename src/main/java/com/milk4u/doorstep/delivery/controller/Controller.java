package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.request.TypeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.milk4u.doorstep.delivery.entity.UserEntity;
import com.milk4u.doorstep.delivery.repository.UserRepository;
import com.milk4u.doorstep.delivery.request.LoginDetails;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController // This means that this class is a Controller
public class Controller {
	
	@Autowired
	private UserRepository userRepo;

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getUser")
	public UserEntity getUser() {
		 return userRepo.findById(1).get();
	}

//	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getAdmins")
	public ResponseEntity<List<UserEntity>> getAdmins(@RequestBody TypeDetails typeDetails ) {
		List<UserEntity> admins = userRepo.findByType(typeDetails.getType());
		return new ResponseEntity<>(admins, HttpStatus.OK);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/verifyLogin")
	public ResponseEntity<String>  verifyLogin(@RequestBody LoginDetails loginDetails ) {
		if(userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).isPresent()) {
			String type = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).get().getType();
			return new ResponseEntity<>(type, HttpStatus.OK);
		}else{
			String userAndPass = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).toString();
			return new ResponseEntity<>("Login Failed", HttpStatus.UNAUTHORIZED);
		}
	}
}
