package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.entity.*;
import com.milk4u.doorstep.delivery.repository.*;
import com.milk4u.doorstep.delivery.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.swing.text.html.Option;
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
		if(typeDetails.getType().equals("Admin") || typeDetails.getType().equals("Driver") || typeDetails.getType().equals("Customer")){
			List<UserEntity> rows = userRepo.findByType(typeDetails.getType());
			return new ResponseEntity<>(rows, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
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
	//Returns all the customers that the driver will have to take the order for
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
	//Can get all the products to appear but not the quantity of each product
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getCurrentOrder")
	public ResponseEntity<List<Optional<ProductEntity>>> getCurrentOrder(@RequestBody Identification id ) {
		List<Optional<CurrentOrderEntity>> currentOrderRow = currentOrderRepo.findByCustomerId(id.getUserIdentification());
		List<Optional<ProductEntity>> products = new ArrayList<>();

		for(int i =0; i < currentOrderRow.size(); i++){
			products.add(prodRepo.findById(currentOrderRow.get(i).get().getProductId()));
			int quantity = currentOrderRow.get(i).get().getQuantity();
		}
		return new ResponseEntity<>(products, HttpStatus.ACCEPTED);
	}

	//NEED HELP HERE BECAUSE I CAN ADD A ROW TO A TROLLY BUT CANNOT UPDATE AN ALREAY EXSISTING ROW
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/addToTrolly")
	public void addToTrolly(@RequestBody AddToTrollyDetails td ) {
		TrollyEntity resp = new TrollyEntity();
		resp.setCustomerId(td.getCstId());
		resp.setProductId(td.getProdId());

		List<Optional<TrollyEntity>> temp2List = trollyRepo.findByCustomerId(td.getCstId());

		int target = 0;
		if(!temp2List.isEmpty()) {
			for(int i = 0; i < temp2List.size(); i++){
				if(temp2List.get(i).get().getProductId() == td.getProdId()){
					target = temp2List.get(i).get().getTrollyId();
				}
			}
			if(target != 0) {
				resp.setQuantity(trollyRepo.findById(target).get().getQuantity() + td.getQty());
			}
		}else{
			resp.setQuantity(td.getQty());
		}

		trollyRepo.save(resp);
	}

	//May need to modify tables os that the customer in the trolly tables is a primary key and a foreign key
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateTrolly")
	public void updateTrolly(@RequestBody UpdateTrollyDetails utd) {
		TrollyEntity temp = trollyRepo.findByProductId(utd.getProdId());
		temp.setQuantity(utd.getQty());
		trollyRepo.save(temp);
	}
}
