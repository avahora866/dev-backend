package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.entity.ProductEntity;
import com.milk4u.doorstep.delivery.repository.ProductRepository;
import com.milk4u.doorstep.delivery.request.TypeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.milk4u.doorstep.delivery.entity.UserEntity;
import com.milk4u.doorstep.delivery.repository.UserRepository;
import com.milk4u.doorstep.delivery.request.LoginDetails;

import java.util.*;

@RestController // This means that this class is a Controller
public class Controller {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ProductRepository prodRepo;

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/verifyLogin")
	public ResponseEntity<String>  verifyLogin(@RequestBody LoginDetails loginDetails ) {
		if(userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).isPresent()) {
			String type = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).get().getType();
			int id = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).get().getUserId();
			String data = type +"-"+ String.valueOf(id);
			return new ResponseEntity<>(data, HttpStatus.OK);
		}else{
			String userAndPass = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).toString();
			return new ResponseEntity<>("Login Failed", HttpStatus.UNAUTHORIZED);
		}
	}


	//ADMIN----------------------------------------------------------------------------------------------
	//GetUsers: has a requestBody requireing a string which will require the front-end to specify wheather they are requesting customers, drivers or admins
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getUsers")
	public ResponseEntity<List<UserEntity>> getUsers(@RequestBody TypeDetails typeDetails ) {
		List<UserEntity> rows = userRepo.findByType(typeDetails.getType());
		return new ResponseEntity<>(rows, HttpStatus.OK);
	}

	//Returns a response entity of all the products
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getProducts")
	public ResponseEntity<List<ProductEntity>> getProducts() {
		List<ProductEntity> products = new ArrayList<>();
		Iterator<ProductEntity> iterator = prodRepo.findAll().iterator();
		while (iterator.hasNext()) {
			products.add(iterator.next());
		}

		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	//DRIVER---------------------------------------------------------------------------------------------

	//CUSTOMER-------------------------------------------------------------------------------------------


}
