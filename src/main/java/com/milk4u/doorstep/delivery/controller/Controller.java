package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.entity.*;
import com.milk4u.doorstep.delivery.repository.*;
import com.milk4u.doorstep.delivery.request.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController // This means that this class is a Controller
public class Controller {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ProductRepository prodRepo;
	@Autowired
	private DroplistRepository dropListRepo;
	@Autowired
	private CurrentOrderRepository currentOrderRepo;
	@Autowired
	private TrollyRepository trollyRepo;


	//Takes in a username and password - checks if they are present in database - ifPresnet returns the type of the user - ifNotPresent retuns a String
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

	//Requires a string which will require the front-end to specify weather they are requesting customers, drivers or admins - returns a lsit of the specified Users
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getUsers")
	public ResponseEntity<List<UserEntity>> getUsers(@RequestBody TypeDetails typeDetails ) {
		if(typeDetails.getType().equals("Admin") || typeDetails.getType().equals("Driver") || typeDetails.getType().equals("Customer")){
			List<UserEntity> rows = userRepo.findByType(typeDetails.getType());
			return new ResponseEntity<>(rows, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	//Returns a list of all products in database - NEED TO ADD SENSITIVITY
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

	//Edits users can be used for Drivers, Admins and Customers - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/editUsers")
	public ResponseEntity<String> editUsers(@RequestBody EditUser eu ) {
		if(userRepo.findById(eu.getId()).isPresent()){
			UserEntity temp = userRepo.findById(eu.getId()).get();
			temp.setUsername(eu.getUserName());
			temp.setPassword(eu.getPassword());
			temp.setEmail(eu.getEmail());
			temp.setfName(eu.getfName());
			temp.setlName(eu.getlName());
			temp.setPostcode(eu.getPostCode());
			temp.setArea(eu.getArea());
			userRepo.save(temp);
			return new ResponseEntity<>("Edited user", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("UserID not found", HttpStatus.NOT_FOUND);
		}

	}

	//Adds a new User can be used for Drivers, Admins and Customers - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/addUser")
	public ResponseEntity<String> addUser(@RequestBody AddUser au ) {
		UserEntity temp = new UserEntity();
		temp.setUsername(au.getUserName());
		temp.setPassword(au.getPassword());
		temp.setEmail(au.getEmail());
		temp.setfName(au.getfName());
		temp.setlName(au.getlName());
		temp.setDateOfBirth(au.getDateOfBirth());
		temp.setPostcode(au.getPostCode());
		temp.setArea(au.getArea());
		temp.setType(au.getType());
		userRepo.save(temp);
		return new ResponseEntity<>("User added", HttpStatus.OK);
	}

	//Deletes a User - If user is a customer deletes their order table as well as trolly - if user is a driver deltes their droplist
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delUser")
	public ResponseEntity<String> delUser(@RequestBody Identification id) {
		if(userRepo.findById(id.getUserIdentification()).isPresent()){
			String type = userRepo.findById(id.getUserIdentification()).get().getType();
			if(type.equals("Customer")){
				if(!(trollyRepo.findByCustomerId(id.getUserIdentification()).isEmpty())){
					List<TrollyEntity> rows = new ArrayList<>();
					Iterator<TrollyEntity> iterator = trollyRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows.add(iterator.next());
					}
					for(int i = 0; i < rows.size(); i++){
						if(rows.get(i).getCustomerId() == id.getUserIdentification()){
							trollyRepo.deleteById(rows.get(i).getTrollyId());
						}
					}
				}

				if(!(currentOrderRepo.findByCustomerId(id.getUserIdentification()).isEmpty())){
					List<CurrentOrderEntity> rows2 = new ArrayList<>();
					Iterator<CurrentOrderEntity> iterator = currentOrderRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows2.add(iterator.next());
					}
					for(int i = 0; i < rows2.size(); i++){
						if(rows2.get(i).getCustomerId() == id.getUserIdentification()){
							currentOrderRepo.deleteById(rows2.get(i).getOrderId());
						}
					}
				}
			}else if (type.equals("Driver")){
				if(!(dropListRepo.findByDriverId(id.getUserIdentification()).isEmpty())){
					List<DroplistEntity> rows3 = new ArrayList<>();
					Iterator<DroplistEntity> iterator = dropListRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows3.add(iterator.next());
					}
					for(int i = 0; i < rows3.size(); i++){
						if(rows3.get(i).getDriverId() == id.getUserIdentification()){
							dropListRepo.deleteById(rows3.get(i).getDroplistId());
						}
					}
				}
			}

			userRepo.deleteById(id.getUserIdentification());
			return new ResponseEntity("Deletion succsefull", HttpStatus.OK);
		}else{
			return new ResponseEntity("UserID not found", HttpStatus.NOT_FOUND);
		}

	}

	//Edits a Product
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/editProducts")
	public ResponseEntity<String> editProducts(@RequestBody EditProd ep ) {
		if(prodRepo.findById(ep.getId()).isPresent()){
			ProductEntity temp = prodRepo.findById(ep.getId()).get();
			temp.setName(ep.getName());
			temp.setDescription(ep.getDescription());
			temp.setPrice(ep.getPrice());
			prodRepo.save(temp);
			return new ResponseEntity<>("Product edited", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("Could not find product", HttpStatus.NOT_FOUND);
		}

	}

	//Adds a product - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/addProduct")
	public ResponseEntity<String> addProduct(@RequestBody AddProduct ap ) {
		ProductEntity temp = new ProductEntity();
		temp.setName(ap.getName());
		temp.setDescription(ap.getDescription());
		temp.setPrice(ap.getPrice());
		prodRepo.save(temp);
		return new ResponseEntity<>("Product added", HttpStatus.OK);
	}

	//Deletes a product
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delProduct")
	public ResponseEntity<String> delProduct(@RequestBody ProductId pId ) {
		if(prodRepo.findById(pId.getProductId()).isPresent()){
			prodRepo.deleteById(pId.getProductId());
			return new ResponseEntity<>("Deletion succesfull", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("No product Id found", HttpStatus.NOT_FOUND);
		}
	}

	//DRIVER---------------------------------------------------------------------------------------------

	//Returns the drop list for the specified deriver
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getDroplist")
	public ResponseEntity<List<Optional<UserEntity>>> getDroplist(@RequestBody Identification id ) {
		if(userRepo.findById(id.getUserIdentification()).isPresent()){
			List<Optional<DroplistEntity>> driverRow = dropListRepo.findByDriverId(id.getUserIdentification());
			List<Optional<UserEntity>> customers = new ArrayList<>();

			for(int i =0; i < driverRow.size(); i++){
				customers.add(userRepo.findById(driverRow.get(i).get().getCustomerId()));
			}
			return new ResponseEntity<>(customers, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	//CUSTOMER-------------------------------------------------------------------------------------------

	//Returns the customers curent order - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getCurrentOrder")
	public ResponseEntity<List<Object>> getCurrentOrder(@RequestBody Identification id ) {
		List<Optional<CurrentOrderEntity>> currentOrderRow = currentOrderRepo.findByCustomerId(id.getUserIdentification());
		List<Optional<ProductEntity>> products = new ArrayList<>();
		List<Object> result = new ArrayList<>();

		for(int i =0; i < currentOrderRow.size(); i++){
			products.add(prodRepo.findById(currentOrderRow.get(i).get().getProductId()));
		}

		Object[] temp = currentOrderRow.toArray();
		Object[] temp2 = products.toArray();

		for(int i =0 ; i<currentOrderRow.size(); i++){
			result.add(temp[i]);
			result.add(temp2[i]);
		}

		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	//Returns the customers trolly - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getTrolly")
	public ResponseEntity<List<Object>> getTrolly(@RequestBody Identification id ) {
		List<Optional<TrollyEntity>> trollyRow = trollyRepo.findByCustomerId(id.getUserIdentification());
		List<Optional<ProductEntity>> products = new ArrayList<>();
		List<Object> result = new ArrayList<>();

		for(int i =0; i < trollyRow.size(); i++){
			products.add(prodRepo.findById(trollyRow.get(i).get().getProductId()));
		}

		Object[] temp = trollyRow.toArray();
		Object[] temp2 = products.toArray();

		for(int i =0 ; i<trollyRow.size(); i++){
			result.add(temp[i]);
			result.add(temp2[i]);
		}

		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	//Adds a product to the trolly - if product is already in the trolly the product in the trolly increases its quantity - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/addToTrolly")
	public void addToTrolly(@RequestBody TrollyDetails td ) {
		TrollyEntity resp = new TrollyEntity();
		resp.setCustomerId(td.getCstId());
		resp.setProductId(td.getProdId());

		List<Optional<TrollyEntity>> temp2List = trollyRepo.findByCustomerId(td.getCstId());

		int target = 0;

		if(temp2List.isEmpty()) {
			resp.setQuantity(td.getQty());
			trollyRepo.save(resp);
		}else{
			for(int i = 0; i < temp2List.size(); i++){
				if(temp2List.get(i).get().getProductId() == td.getProdId()){
					target = temp2List.get(i).get().getTrollyId();
				}
			}
			if(target != 0) {
				TrollyEntity rowInDb = trollyRepo.findById(target).get();
				rowInDb.setQuantity(trollyRepo.findById(target).get().getQuantity() + td.getQty());
				trollyRepo.save(rowInDb);
			}else{
				resp.setQuantity(td.getQty());
				trollyRepo.save(resp);
			}
		}

	}

	//Updates the quantity of a product in a trolly - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateTrolly")
	public ResponseEntity<String> updateTrolly(@RequestBody TrollyDetails td) {
		List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(td.getCstId());
		Optional<TrollyEntity> result = null;

		for (int i = 0; i < temp.size(); i++){
			if(temp.get(i).get().getProductId() == td.getProdId()){
				result = trollyRepo.findById(temp.get(i).get().getTrollyId());
			}
		}

		result.get().setQuantity(td.getQty());
		trollyRepo.save(result.get());

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//Deletes a product form the trolly - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delProductFromTrolly")
	public  ResponseEntity<String> delProductFromTrolly(@RequestBody TrollyDetailsDel tdd ) {
		List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(tdd.getCstId());
		for(int i =0; i<temp.size();i++){
			if(temp.get(i).get().getProductId() == tdd.getProdId()){
				trollyRepo.deleteById(temp.get(i).get().getTrollyId());
			}
		}

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//Deletes the trolly of a customer - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delTrolly")
	public  ResponseEntity<String> delTrolly(@RequestBody TrollyDetailsCancel tdc ) {
		List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(tdc.getCstId());
		for(int i = 0; i <temp.size(); i++ ){
			trollyRepo.deleteById(temp.get(i).get().getTrollyId());
		}

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//Maybe try to do a saveAll method
	//Creates a new Order for the customer - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/createOrder")
	public ResponseEntity<String> createOrder (@RequestBody Identification id){
		List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(id.getUserIdentification());


		for(int i = 0; i < temp.size(); i++ ){
			CurrentOrderEntity t1 = new CurrentOrderEntity();
			t1.setCustomerId(temp.get(i).get().getCustomerId());
			t1.setProductId(temp.get(i).get().getProductId());
			t1.setQuantity(temp.get(i).get().getQuantity());
			currentOrderRepo.save(t1);
		}

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//TrollyDetails stores the cstID ProdID and Qty so can be used for order as well
	//Updates a product in the trolly - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateOrder")
	public ResponseEntity<String> updateOrder(@RequestBody TrollyDetails td) {
		List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(td.getCstId());
		Optional<CurrentOrderEntity> result = null;

		for (int i = 0; i < temp.size(); i++){
			if(temp.get(i).get().getProductId() == td.getProdId()){
				result = currentOrderRepo.findById(temp.get(i).get().getOrderId());
			}
		}

		result.get().setQuantity(td.getQty());
		currentOrderRepo.save(result.get());

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//Deletes a product in the order - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delProductFromOrder")
	public  ResponseEntity<String> delProductFromOrder(@RequestBody TrollyDetailsDel tdd ) {
		List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(tdd.getCstId());
		for(int i =0; i<temp.size();i++){
			if(temp.get(i).get().getProductId() == tdd.getProdId()){
				currentOrderRepo.deleteById(temp.get(i).get().getOrderId());
			}
		}

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

	//Deletes the order of a customer - NEED TO ADD SENSITIVITY
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delOrder")
	public  ResponseEntity<String> delOrder(@RequestBody TrollyDetailsCancel tdc ) {
		List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(tdc.getCstId());
		for(int i = 0; i <temp.size(); i++ ){
			currentOrderRepo.deleteById(temp.get(i).get().getOrderId());
		}

		return new ResponseEntity<>("Good", HttpStatus.OK);
	}

}
