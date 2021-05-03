package com.milk4u.doorstep.delivery.controller;

import com.milk4u.doorstep.delivery.email.*;
import com.milk4u.doorstep.delivery.entity.*;
import com.milk4u.doorstep.delivery.repository.*;
import com.milk4u.doorstep.delivery.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.milk4u.doorstep.delivery.response.CustomerResponse;
import com.milk4u.doorstep.delivery.response.DroplistResponse;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.util.*;
import java.util.List;

import static com.milk4u.doorstep.delivery.pdf.Droplist.createPDF;
import static com.milk4u.doorstep.delivery.pdf.PrintingDroplist.Print;


@RestController // This means that this class is a Controller
@Component
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
	@Autowired
	private InvoiceRepository invoiceRepo;
	@Autowired
	private EmailServiceImpl emailSender;

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
	public ResponseEntity<List<Optional<UserEntity>>> getUsers(@RequestParam String type) {
		if(type.equals("Admin") || type.equals("Driver") || type.equals("Customer")){
			List<Optional<UserEntity>> rows = userRepo.findByType(type);
			return new ResponseEntity<>(rows, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
	public ResponseEntity<String> editUsers(@RequestBody EditUser eu) {
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

	public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
		return java.sql.Timestamp.valueOf(dateToConvert);
	}

	//Adds a new User can be used for Drivers, Admins and Customers
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path="/addUser")
	public ResponseEntity<String> addUser(@RequestBody AddUser au ) {
		if(au.getType().equals("Customer")){
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			Date compare = convertToDateViaSqlTimestamp(now);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -18);
			cal.add(Calendar.DATE, -1);
			Date min = cal.getTime();
			if(au.getDateOfBirth().before(compare) && au.getDateOfBirth().before(min)){
				Optional<UserEntity> temp = Optional.of(new UserEntity());
				temp.get().setUsername(au.getUserName());
				temp.get().setPassword(au.getPassword());
				temp.get().setEmail(au.getEmail());
				temp.get().setfName(au.getfName());
				temp.get().setlName(au.getlName());
				temp.get().setDateOfBirth(au.getDateOfBirth());
				temp.get().setPostcode(au.getPostCode());
				temp.get().setArea(au.getArea());
				temp.get().setType(au.getType());
				userRepo.save(temp.get());
				return new ResponseEntity<>("User added", HttpStatus.OK);
			}else{
				return new ResponseEntity<>("Date of birth is invalid", HttpStatus.NOT_ACCEPTABLE);
			}
		}else{
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
	}

	//Deletes a User - If user is a customer deletes their order table as well as trolly - if user is a driver deletes their droplist -
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delUser")
	public ResponseEntity<String> delUser(@RequestParam int id) {
		if(userRepo.findById(id).isPresent()){
			String type = userRepo.findById(id).get().getType();
			if(type.equals("Customer")){
				if(!(trollyRepo.findByCustomerId(id).isEmpty())){
					List<TrollyEntity> rows = new ArrayList<>();
					Iterator<TrollyEntity> iterator = trollyRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows.add(iterator.next());
					}
					for(int i = 0; i < rows.size(); i++){
						if(rows.get(i).getCustomerId() == id){
							trollyRepo.deleteById(rows.get(i).getTrollyId());
						}
					}
				}

				if(!(currentOrderRepo.findByCustomerId(id).isEmpty())){
					List<CurrentOrderEntity> rows2 = new ArrayList<>();
					Iterator<CurrentOrderEntity> iterator = currentOrderRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows2.add(iterator.next());
					}
					for(int i = 0; i < rows2.size(); i++){
						if(rows2.get(i).getCustomerId() == id){
							currentOrderRepo.deleteById(rows2.get(i).getOrderId());
						}
					}
				}

				if(dropListRepo.findByCustomerId(id).isPresent()){
					dropListRepo.deleteById(dropListRepo.findByCustomerId(id).get().getDroplistId());
				}

			}else if (type.equals("Driver")){
				if(!(dropListRepo.findByDriverId(id).isEmpty())){
					List<DroplistEntity> rows3 = new ArrayList<>();
					Iterator<DroplistEntity> iterator = dropListRepo.findAll().iterator();
					while (iterator.hasNext()) {
						rows3.add(iterator.next());
					}
					for(int i = 0; i < rows3.size(); i++){
						if(rows3.get(i).getDriverId() == id){
							dropListRepo.deleteById(rows3.get(i).getDroplistId());
						}
					}
				}

				}
			userRepo.deleteById(id);
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
	public ResponseEntity<String> delProduct(@RequestParam int pId ) {
		if(prodRepo.findById(pId).isPresent()){
			prodRepo.deleteById(pId);
			return new ResponseEntity<>("Deletion succesfull", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("No product Id found", HttpStatus.NOT_FOUND);
		}
	}

	//DRIVER---------------------------------------------------------------------------------------------

	//Returns the drop list for the specified driver
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getDroplist")
	public ResponseEntity<List<DroplistResponse>> getDroplist(@RequestParam int id ) {
		if(userRepo.findById(id).isPresent()){
			List<Optional<DroplistEntity>> driverRow = dropListRepo.findByDriverId(id);
			List<DroplistResponse> result = new ArrayList<>();

			for(int i =0; i < driverRow.size(); i++){
				DroplistResponse droplistResponse = new DroplistResponse();
				droplistResponse.setCstId(driverRow.get(i).get().getCustomerId());
				droplistResponse.setEmail(userRepo.findById(driverRow.get(i).get().getCustomerId()).get().getEmail());
				droplistResponse.setfName(userRepo.findById(driverRow.get(i).get().getCustomerId()).get().getfName());
				droplistResponse.setlName(userRepo.findById(driverRow.get(i).get().getCustomerId()).get().getlName());
				droplistResponse.setPostcode(userRepo.findById(driverRow.get(i).get().getCustomerId()).get().getPostcode());
				List<CustomerResponse> customerResponseList = new ArrayList<>();
				List<Optional<CurrentOrderEntity>> currOrders = currentOrderRepo.findByCustomerId(driverRow.get(i).get().getCustomerId());
				for(int j = 0; j < currOrders.size(); j++){
					CustomerResponse customerResponse = new CustomerResponse();
					customerResponse.setProductId(currOrders.get(j).get().getProductId());
					customerResponse.setName(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getName());
					customerResponse.setDescription(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getDescription());
					customerResponse.setPrice(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getPrice());
					customerResponse.setQuantity(currOrders.get(j).get().getQuantity());
					customerResponseList.add(customerResponse);
				}
				droplistResponse.setOrders(customerResponseList);
				result.add(droplistResponse);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateInvoice")
	public void updateInvoice() {
		Iterator<CurrentOrderEntity> allCurrOrders = currentOrderRepo.findAll().iterator();
		while(allCurrOrders.hasNext()){
			CurrentOrderEntity temp1 = allCurrOrders.next();
			InvoiceEntity newInvoice = new InvoiceEntity();
			newInvoice.setCustomerId(temp1.getCustomerId());
			newInvoice.setProductId(temp1.getProductId());
			newInvoice.setQuantity(temp1.getQuantity());
			invoiceRepo.save(newInvoice);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(path="/updateDroplist")
	public void updateDroplist() {
		Iterator<InvoiceEntity> allInvoices = invoiceRepo.findAll().iterator();
		List<Optional<UserEntity>> allDriver = userRepo.findByType("Driver");
		List<Integer> cstIdsDone = new ArrayList<>();

		while (allInvoices.hasNext()){
			InvoiceEntity temp1 = allInvoices.next();
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

	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delInvoice")
	public void delInvoice() {
		invoiceRepo.deleteAll();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/printDroplist")
	public void printDroplist(@RequestParam int id) {
		List<Optional<DroplistEntity>> droplistRows = dropListRepo.findByDriverId(id);
		List<Optional<UserEntity>> allCustomers = new ArrayList<>();
		List<List<CustomerResponse>> finalList = new ArrayList<>();

		for(int i = 0; i< droplistRows.size(); i++){
			allCustomers.add(userRepo.findById(dropListRepo.findById(droplistRows.get(i).get().getDroplistId()).get().getCustomerId()));
		}

		for(int i = 0; i < allCustomers.size(); i++){
			List<Optional<InvoiceEntity>> invoiceRow = invoiceRepo.findByCustomerId(allCustomers.get(i).get().getUserId());

			List fin = new ArrayList();

			for (int l = 0; l < invoiceRow.size(); l++){
				Optional<InvoiceEntity> order = invoiceRow.get(l);
				Optional<ProductEntity> product = prodRepo.findById(invoiceRow.get(l).get().getProductId());
				CustomerResponse cstResponse = new CustomerResponse();
				cstResponse.setProductId(order.get().getProductId());
				cstResponse.setName(product.get().getName());
				cstResponse.setDescription(product.get().getDescription());
				cstResponse.setPrice(product.get().getPrice());
				cstResponse.setQuantity(order.get().getQuantity());
				fin.add(cstResponse);
			}

			finalList.add(fin);
		}
		createPDF(userRepo.findById(id).get(), allCustomers, finalList);
		Print("c:/Users/User/Documents/Droplist.pdf");
		File myObj = new File("c:/Users/User/Documents/Droplist.pdf");
		myObj.delete();

	}

	//CUSTOMER-------------------------------------------------------------------------------------------

	//Returns the customers current order
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getCurrentOrder")
	public ResponseEntity<List<Object>> getCurrentOrder(@RequestParam int id ) {
		if(userRepo.existsById(id)){
			List<Optional<CurrentOrderEntity>> currentOrderRow = currentOrderRepo.findByCustomerId(id);
			List<Optional<ProductEntity>> products = new ArrayList<>();

			for(int i =0; i < currentOrderRow.size(); i++){
				products.add(prodRepo.findById(currentOrderRow.get(i).get().getProductId()));
			}

			List fin = new ArrayList();
			for (int i = 0; i < currentOrderRow.size(); i++){
				Optional<CurrentOrderEntity> order = currentOrderRow.get(i);
				Optional<ProductEntity> product = products.get(i);
                CustomerResponse cstResponse = new CustomerResponse();
                cstResponse.setProductId(order.get().getProductId());
                cstResponse.setName(product.get().getName());
                cstResponse.setDescription(product.get().getDescription());
                cstResponse.setPrice(product.get().getPrice());
                cstResponse.setQuantity(order.get().getQuantity());
                fin.add(cstResponse);
			}

			return new ResponseEntity<>(fin, HttpStatus.ACCEPTED);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}


	}

	//Returns the customers trolly
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(path="/getTrolly")
	public ResponseEntity<List<CustomerResponse>> getTrolly(@RequestParam int id ) {
		if(userRepo.existsById(id)){
			List<Optional<TrollyEntity>> trollyRow = trollyRepo.findByCustomerId(id);
			List<Optional<ProductEntity>> products = new ArrayList<>();

			for(int i =0; i < trollyRow.size(); i++){
				products.add(prodRepo.findById(trollyRow.get(i).get().getProductId()));
			}

			List fin = new ArrayList();
			for (int i = 0; i < trollyRow.size(); i++){
				Optional<TrollyEntity> order = trollyRow.get(i);
				Optional<ProductEntity> product = products.get(i);
                CustomerResponse cstResponse = new CustomerResponse();
                cstResponse.setProductId(order.get().getProductId());
                cstResponse.setName(product.get().getName());
                cstResponse.setDescription(product.get().getDescription());
                cstResponse.setPrice(product.get().getPrice());
                cstResponse.setQuantity(order.get().getQuantity());
                fin.add(cstResponse);
			}

			return new ResponseEntity<>(fin, HttpStatus.ACCEPTED);
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
	public  ResponseEntity<String> delProductFromTrolly(@RequestParam int cstId, int prodId ) {
		List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(cstId);

		if(temp.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else{
			for(int i =0; i<temp.size();i++){
				if(temp.get(i).get().getProductId() == prodId){
					trollyRepo.deleteById(temp.get(i).get().getTrollyId());
				}
			}
			return new ResponseEntity<>("Deletion succesfull", HttpStatus.OK);
		}
	}

	//Deletes the trolly of a customer
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(path="/delTrolly")
	public  ResponseEntity<String> delTrolly(@RequestParam int id ) {
		if(userRepo.existsById(id)){
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(id);
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
			delOrder(id.getUserIdentification());
			List<Optional<TrollyEntity>> temp = trollyRepo.findByCustomerId(id.getUserIdentification());
				for(int i = 0; i < temp.size(); i++ ){
					CurrentOrderEntity t1 = new CurrentOrderEntity();
					t1.setCustomerId(temp.get(i).get().getCustomerId());
					t1.setProductId(temp.get(i).get().getProductId());
					t1.setQuantity(temp.get(i).get().getQuantity());
					currentOrderRepo.save(t1);
				}
			delTrolly(id.getUserIdentification());
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
	public  ResponseEntity<Optional<ProductEntity>> delProductFromOrder(@RequestParam int cstId, int prodId) {
		if(userRepo.existsById(cstId)){
			List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(cstId);
			Optional<ProductEntity> result = null;
			for(int i =0; i<temp.size();i++){
				if(temp.get(i).get().getProductId() == prodId){
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

			for(int i = 0; i < trollyRows.size(); i++){
				Optional<CurrentOrderEntity> tempEntity = null;
				for(int j =0; j< currOrderRows.size(); j++){
					if(currOrderRows.get(j).get().getProductId() == trollyRows.get(i).get().getProductId()){
					tempEntity = currentOrderRepo.findById(currOrderRows.get(j).get().getOrderId());
					tempEntity.get().setQuantity(tempEntity.get().getQuantity() + trollyRows.get(i).get().getQuantity());
					currentOrderRepo.save(tempEntity.get());
					trollyRepo.deleteById(trollyRows.get(i).get().getTrollyId());
				}
			}
		}

			trollyRows = trollyRepo.findByCustomerId(id.getUserIdentification());
			if(!trollyRows.isEmpty()){
				for(int i = 0; i<trollyRows.size(); i++){
					CurrentOrderEntity temp2 = new CurrentOrderEntity();
					temp2.setCustomerId(trollyRows.get(i).get().getCustomerId());
					temp2.setProductId(trollyRows.get(i).get().getProductId());
					temp2.setQuantity(trollyRows.get(i).get().getQuantity());
					currentOrderRepo.save(temp2);
					trollyRepo.deleteById(trollyRows.get(i).get().getTrollyId());
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
	public  ResponseEntity<String> delOrder(@RequestParam int id ) {
		if(userRepo.existsById(id)){
			List<Optional<CurrentOrderEntity>> temp = currentOrderRepo.findByCustomerId(id);
			for(int i = 0; i <temp.size(); i++ ){
				currentOrderRepo.deleteById(temp.get(i).get().getOrderId());
			}
			return new ResponseEntity<>("Order Deleted", HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(path="/sendInvoice", method = RequestMethod.POST)
	public void sendInvoice()  {
        Iterator<DroplistEntity> droplistRows = dropListRepo.findAll().iterator();
        List<UserEntity> customers = new ArrayList<>();
        List<UserEntity> drivers = new ArrayList<>();
        List<Optional<CurrentOrderEntity>> currOrders = new ArrayList<>();
        List<CustomerResponse> cstOrders = new ArrayList<>();
        while(droplistRows.hasNext()){
            DroplistEntity temp = droplistRows.next();
            customers.add(userRepo.findById(temp.getCustomerId()).get());
            drivers.add(userRepo.findById(temp.getDriverId()).get());
        }

        for(int i = 0; i < customers.size(); i++){
            currOrders.addAll(currentOrderRepo.findByCustomerId(customers.get(i).getUserId()));
            for(int j = 0; j < currOrders.size(); j++ ){
                CustomerResponse temp = new CustomerResponse();
                temp.setProductId(currOrders.get(j).get().getProductId());
                temp.setName(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getName());
                temp.setDescription(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getDescription());
                temp.setPrice(prodRepo.findById(currOrders.get(j).get().getProductId()).get().getPrice());
                temp.setQuantity(currOrders.get(j).get().getQuantity());
                cstOrders.add(temp);
            }
            currOrders.clear();

            String email = customers.get(i).getEmail();
            String subject = "Invoice for Customer Id: " + customers.get(i).getUserId();
            String message = "Customer Name: " + customers.get(i).getfName() +" "+ customers.get(i).getlName() +"\n\nDriver Name: "+ drivers.get(i).getfName() +" "+drivers.get(i).getlName() +"\n\nProducts: ";
            int totalPrice = 0;
            for(int l = 0; l < cstOrders.size(); l++){
                message += "\nName: " + cstOrders.get(l).getName() + "\nDescription: " + cstOrders.get(l).getDescription() + "\nPrice: £" +cstOrders.get(l).getPrice() + "\nQuantity: " + cstOrders.get(l).getQuantity() + "\n\n";
                totalPrice += cstOrders.get(l).getPrice() * cstOrders.get(l).getQuantity();
            }
            message += "\n\nTotal Price: £" + totalPrice;
            message += "\nThank you for shopping at Milk4u";
            emailSender.sendSimpleMessage(email, subject, message);
        }
	}




	//Method runs at 11:00pm - 23:00
	@Scheduled(cron = "0 0 23 * * ?")
	public void dailyMethodCall() {
		delDroplist();
		delInvoice();
		updateInvoice();
		updateDroplist();
		sendInvoice();
	}

}
