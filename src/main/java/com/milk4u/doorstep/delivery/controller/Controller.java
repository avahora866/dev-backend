package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.entity.*;
import com.milk4u.doorstep.delivery.repository.*;
import com.milk4u.doorstep.delivery.request.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.net.PasswordAuthentication;
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


	//Takes in a username and password - checks if they are present in database - ifPresent returns the type of the user - ifNotPresent returns a String
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/verifyLogin")
	public ResponseEntity<Optional<UserEntity>>  verifyLogin(@RequestBody LoginDetails loginDetails ) {
		if(userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).isPresent()) {
			String type = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).get().getType();
			int id = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).get().getUserId();
			return new ResponseEntity<>(userRepo.findById(id), HttpStatus.OK);
		}else{
			String userAndPass = userRepo.findByUsernameAndPassword(loginDetails.getUserName(), loginDetails.getPassword()).toString();
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	//ADMIN----------------------------------------------------------------------------------------------

	//Requires a string which will require the front-end to specify weather they are requesting customers, drivers or admins - returns a list of the specified Users
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getUsers")
	public ResponseEntity<List<Optional<UserEntity>>> getUsers(@RequestBody TypeDetails typeDetails ) {
		if(typeDetails.getType().equals("Admin") || typeDetails.getType().equals("Driver") || typeDetails.getType().equals("Customer")){
			List<Optional<UserEntity>> rows = userRepo.findByType(typeDetails.getType());
			return new ResponseEntity<>(rows, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getUserGet")
	public ResponseEntity<Optional<UserEntity>> getUserGet(@RequestParam int userIdentification) {
		if(userRepo.existsById(userIdentification)){
			return new ResponseEntity<>(userRepo.findById(userIdentification), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		}
	}

	//Returns a list of all products in database
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

	//Edits users can be used for Drivers, Admins and Customers
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

	//Adds a new User can be used for Drivers, Admins and Customers
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

	//Deletes a User - If user is a customer deletes their order table as well as trolly - if user is a driver deletes their droplist
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

	//Adds a product
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

	//Returns the drop list for the specified driver
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

	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateDroplist")
	public void updateDroplist() {
		Iterator<CurrentOrderEntity> allCurrOrders = currentOrderRepo.findAll().iterator();
		List<Optional<UserEntity>> allDriver = userRepo.findByType("Driver");
		List<Integer> cstIdsDone = new ArrayList<>();

		while (allCurrOrders.hasNext()){
			CurrentOrderEntity temp1 = allCurrOrders.next();
			if(!cstIdsDone.contains(temp1.getCustomerId())){
				String cstArea = userRepo.findById(temp1.getCustomerId()).get().getPostcode();
				int driver = 0;
				List<Integer> driverIDs = new ArrayList<>();
				cstIdsDone.add(temp1.getCustomerId());
				for(int i =0; i< allDriver.size(); i++){
					if(allDriver.get(i).get().getArea().equals(cstArea.substring(0, 2))){
						driverIDs.add(allDriver.get(i).get().getUserId());
					}
				}
				if(allDriver.size() != 1){
					Random rand = new Random();
					driver = driverIDs.get(rand.nextInt(driverIDs.size()));
				}else{
					driver = driverIDs.get(0);
				}

				driverIDs.clear();

				DroplistEntity endRes = new DroplistEntity();
				endRes.setDriverId(driver);
				endRes.setCustomerId(temp1.getCustomerId());
				dropListRepo.save(endRes);
			}
		}
	}


	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delDroplist")
	public void delDroplist() {
		dropListRepo.deleteAll();
	}

	//CUSTOMER-------------------------------------------------------------------------------------------

	//Returns the customers current order
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getCurrentOrder")
	public ResponseEntity<List<Object>> getCurrentOrder(@RequestBody Identification id ) {
		if(userRepo.existsById(id.getUserIdentification())){
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
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	//Returns the customers trolly
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getTrolly")
	public ResponseEntity<List<Object>> getTrolly(@RequestBody Identification id ) {
		if(userRepo.existsById(id.getUserIdentification())){

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
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Adds a product to the trolly - if product is already in the trolly the product in the trolly increases its quantity
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/addToTrolly")
	public ResponseEntity<Optional<ProductEntity>> addToTroly(@RequestBody TrollyDetails td ) {
		if(userRepo.existsById(td.getCstId())){
			Optional<ProductEntity> result = prodRepo.findById(td.getProdId());
			TrollyEntity resp = new TrollyEntity();
			resp.setCustomerId(td.getCstId());
			resp.setProductId(td.getProdId());

			List<Optional<TrollyEntity>> temp2List = trollyRepo.findByCustomerId(td.getCstId());

			int target = 0;

			if(temp2List.isEmpty()) {
				resp.setQuantity(td.getQty());
				trollyRepo.save(resp);
				return new ResponseEntity<>(result, HttpStatus.OK);
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
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	//Updates the quantity of a product in a trolly
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path = "/updateTrolly")
	public ResponseEntity<Optional<TrollyEntity>> updateTrolly(@RequestBody TrollyDetails td) {
		if (userRepo.existsById(td.getCstId())) {
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(td.getCstId());
			Optional<TrollyEntity> result = null;

			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).get().getProductId() == td.getProdId()) {
					result = trollyRepo.findById(temp.get(i).get().getTrollyId());
				}
			}

			result.get().setQuantity(td.getQty());
			trollyRepo.save(result.get());

			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Deletes a product from the trolly
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delProductFromTrolly")
	public  ResponseEntity<String> delProductFromTrolly(@RequestBody TrollyDetailsDel tdd ) {
		if(trollyRepo.existsById(tdd.getCstId())){
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(tdd.getCstId());
			for(int i =0; i<temp.size();i++){
				if(temp.get(i).get().getProductId() == tdd.getProdId()){
					trollyRepo.deleteById(temp.get(i).get().getTrollyId());
				}
			}

			return new ResponseEntity<>("Deletion succesfull", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Deletes the trolly of a customer
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delTrolly")
	public  ResponseEntity<String> delTrolly(@RequestBody Identification id ) {
		if(userRepo.existsById(id.getUserIdentification())){
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(id.getUserIdentification());
			for(int i = 0; i <temp.size(); i++ ){
				trollyRepo.deleteById(temp.get(i).get().getTrollyId());
			}

			return new ResponseEntity<>("Trolly Deleted", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Maybe try to do a saveAll method
	//Creates a new Order for the customer
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/createOrder")
	public ResponseEntity<String> createOrder (@RequestBody Identification id){
		if (userRepo.existsById(id.getUserIdentification())) {
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(id.getUserIdentification());

			for(int i = 0; i < temp.size(); i++ ){
				CurrentOrderEntity t1 = new CurrentOrderEntity();
				t1.setCustomerId(temp.get(i).get().getCustomerId());
				t1.setProductId(temp.get(i).get().getProductId());
				t1.setQuantity(temp.get(i).get().getQuantity());
				currentOrderRepo.save(t1);
			}

			return new ResponseEntity<>("Order created", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//TrollyDetails stores the cstID ProdID and Qty so can be used for order as well
	//Updates a product in the Order
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateOrder")
	public ResponseEntity<String> updateOrder(@RequestBody TrollyDetails td) {
		if(userRepo.existsById(td.getCstId())){
			List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(td.getCstId());
			Optional<CurrentOrderEntity> result = null;

			for (int i = 0; i < temp.size(); i++){
				if(temp.get(i).get().getProductId() == td.getProdId()){
					result = currentOrderRepo.findById(temp.get(i).get().getOrderId());
				}
			}

			result.get().setQuantity(td.getQty());
			currentOrderRepo.save(result.get());

			return new ResponseEntity<>("Order Updated", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Deletes a product in the order
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delProductFromOrder")
	public  ResponseEntity<Optional<ProductEntity>> delProductFromOrder(@RequestBody TrollyDetailsDel tdd ) {
		if(userRepo.existsById(tdd.getCstId())){
			List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(tdd.getCstId());
			Optional<ProductEntity> result = null;
			for(int i =0; i<temp.size();i++){
				if(temp.get(i).get().getProductId() == tdd.getProdId()){
					currentOrderRepo.deleteById(temp.get(i).get().getOrderId());
					result = prodRepo.findById(temp.get(i).get().getCustomerId());
				}
			}

			return new ResponseEntity<>(result, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//This adds all the producst in the trolly ot the order. If there is a duplicates it increases the quantity
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/addToOrder")
	public ResponseEntity<String> addToOrder(@RequestBody Identification id){
		if(userRepo.existsById(id.getUserIdentification())){
			List<Optional<TrollyEntity>> trollyRows = trollyRepo.findByCustomerId(id.getUserIdentification());
			List<Optional<CurrentOrderEntity>> currOrderRows = currentOrderRepo.findByCustomerId(id.getUserIdentification());
			List<Integer> currOrderIds = new ArrayList<>();
			List<Integer> trollyIds = new ArrayList<>();



			for(int i = 0; i < currOrderRows.size(); i++){
				for(int j = 0; j < trollyRows.size(); j++){
					if(currOrderRows.get(i).get().getProductId() == trollyRows.get(j).get().getProductId()){
						currOrderIds.add(currOrderRows.get(i).get().getOrderId());
						trollyIds.add(trollyRows.get(j).get().getTrollyId());
					}
				}
			}

			for(int i = 0; i < trollyRows.size(); i++){
				CurrentOrderEntity tempEntity = new CurrentOrderEntity();
				Optional<CurrentOrderEntity> tempEntity2 = null;
				if(currOrderIds.contains(currOrderRows.get(i).get().getOrderId())){
					tempEntity2 = currentOrderRepo.findById(currOrderRows.get(i).get().getOrderId());
					tempEntity2.get().setQuantity(tempEntity2.get().getQuantity() + trollyRows.get(i).get().getQuantity());
					currentOrderRepo.save(tempEntity2.get());
				}else{
					tempEntity.setCustomerId(trollyRows.get(i).get().getCustomerId());
					tempEntity.setProductId(trollyRows.get(i).get().getProductId());
					tempEntity.setQuantity(trollyRows.get(i).get().getQuantity());
					currentOrderRepo.save(tempEntity);
				}
			}
			return new ResponseEntity<>("Order updated", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Deletes the order of a customer
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delOrder")
	public  ResponseEntity<String> delOrder(@RequestBody Identification id ) {
		if(userRepo.existsById(id.getUserIdentification())){
			List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(id.getUserIdentification());
			for(int i = 0; i <temp.size(); i++ ){
				currentOrderRepo.deleteById(temp.get(i).get().getOrderId());
			}

			return new ResponseEntity<>("Order Deleted", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/sendInvoice")
	public void sendInvoice(@RequestBody Identification id) throws MessagingException {
//		List<Optional<CurrentOrderEntity>> currOrder = currentOrderRepo.findByCustomerId(id.getUserIdentification());
//		Optional<UserEntity> driver = userRepo.findById(dropListRepo.findByCustomerId(id.getUserIdentification()).get().getDriverId());
//		List<Optional<ProductEntity>> products = new ArrayList<>();
//		for(int i =0; i< currOrder.size(); i++){
//			products.add(prodRepo.findById(currOrder.get(i).get().getProductId()));
//		}
		Optional<UserEntity> customer = userRepo.findById(id.getUserIdentification());
		String cstEmail = customer.get().getEmail();

	}

}
