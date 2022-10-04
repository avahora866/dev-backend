INSERT INTO `product` (`PRODUCT_ID`, `NAME`, `DESCRIPTION`, `PRICE`) VALUES (1,'12 Pack Eggs','Large Free Range Eggs 12 Pack',2.05),(2,'Semi Skimmed Milk','British Semi Skimmed Milk 2.272L, 4 Pints',1.09),(3,'Whole Milk','British Whole Milk 2.272L, 4 Pints',1.09),(4,'Cheddar Cheese','Cathedral City Mature Cheddar Cheese 550 G',3),(5,'Pink Lady Apple','Pink Lady Apple Minimum 5 Pack',2.75),(6,'Bananas','Ripe Bananas 5 Pack',0.69),(15,'Emperor Oranges','Finest Emperor Oranges 4 Pack',2.1),(16,'Granny Smith Apple','Granny Smith Apple Minimum 5 Pack',1.6),(17,'Seasonal Pears','Ready To Eat Seasonal Pears 4Pack 550G',2.1),(18,'Strawberries','Strawberries 600G',3),(19,'Blueberries','Organic Bluberries 250G',3),(20,'Rasberries','Finest Raspberries 150G',2.5),(21,'Blackberries','Blackberries 150G',2),(22,'Red Grapes','Red Seedless Grapes 500G',2),(23,'Green Grapes','Green Seedless Grapes Pack 500G',2),(24,'Pineapple','Large Pineapple Each',1.7),(25,'Watermellon','Watermelon Wedges 550G',2.5);

INSERT INTO users (`USER_ID`,
                 `USERNAME`,
                 `PASSWORD`,
                 `EMAIL`,
                 `FIRST_NAME`,
                 `LAST_NAME`,
                 `DATE_OF_BIRTH`,
                 `POSTCODE`,
                 `AREA`,
                 `TYPE`) VALUES (1,'LRobson','LRobsonPass','LR@hotmail.com','Laila','Robson',NULL,NULL,NULL,'Admin'),(3,'YHoffman','YHoffmanPass','YH@hotmail.com','Yasmine','Hoffman',NULL,NULL,NULL,'Admin'),(6,'ABaxter','ABaxterPass','AB@hotmail.com','Amielia','Baxter',NULL,NULL,'LE','Driver'),(8,'IFinley','IFinleyPass','IF@hotmail.com','Isobel','Finley',NULL,NULL,'LF','Driver'),(9,'EHoward','','EH@hotmail.com','Eathan','Howard',NULL,NULL,'LF','Driver'),(10,'FDiaz','FDiazPass','FD@hotmail.com','Faith','Diaz',NULL,NULL,'LJ','Driver'),(11,'JSmith','JSmithPass','abraarv7@gmail.com','James','Smith','1996-01-01','LE5 3ED',NULL,'Customer'),(12,'HSmith','HSmithPass','abraarv7@gmail.com','Hank','Smith','1998-02-11','LE8 90D',NULL,'Customer'),(14,'HPotter','HPotterPass','abraarv7@gmail.com','Harry','Potter','1999-08-07','LF9 37F',NULL,'Customer'),(15,'Abryan','AbryanPass','abraarv7@gmail.com','Abigail ','Bryan','1986-09-12','LJ2 10H',NULL,'Customer');

INSERT INTO current_order (`ORDER_ID`,
                          `CUSTOMER_ID`,
                          `PRODUCT_ID`,
                          `QUANTITY`) VALUES (9,15,6,2),(10,15,3,8),(102,14,4,4),(103,14,5,2),(139,11,1,6),(140,11,4,4);

INSERT INTO droplist (`DROPLIST_ID`,
                     `DRIVER_ID`,
                     `CUSTOMER_ID`) VALUES (3448,10,15),(3449,8,14),(3450,6,12),(3451,6,11);

INSERT INTO invoice (`INVOICE_ID`,
                    `CUSTOMER_ID`,
                    `PRODUCT_ID`,
                    `QUANTITY`) VALUES (25,15,6,2),(26,15,3,8),(27,14,4,4),(28,14,5,2),(29,12,4,2),(30,12,1,5),(31,11,2,3),(32,11,4,3);

INSERT INTO trolly (`TROLLY_ID`,
                   `CUSTOMER_ID`,
                   `PRODUCT_ID`,
                   `QUANTITY`) VALUES (9,15,1,2),(10,15,5,5),(49,14,3,2),(50,14,5,6);
