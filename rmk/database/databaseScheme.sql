Create Table Address	(
	AddressID	BIGINT NOT NULL AUTO_INCREMENT,
	CorrectedAddressID	BIGINT,
	AddressType	BIGINT,
	CustomerID	BIGINT,
	ADDRESS0	VARCHAR(30),
	ADDRESS1	VARCHAR(30),
	ADDRESS2	VARCHAR(30),
	CITY	VARCHAR(23),
	STATE	VARCHAR(2),
	ZIP	VARCHAR(10),
	COUNTRY	VARCHAR(30),
	ZONE	VARCHAR(3),
	TimesUsed	INTEGER(2),
	PrimaryCustomerAddress	TINYINT,
	PRIMARY KEY  (`AddressID`)
) ;

Create Table Customers	(
	CustomerID	BIGINT NOT NULL AUTO_INCREMENT,
	PhoneNumber	VARCHAR(13),
	LastName	VARCHAR(30),
	FirstName	VARCHAR(30),
	CurrrentAddress	BIGINT,
	Balance	FLOAT,
	Discount	FLOAT,
	Dealer	INTEGER(2),
	Flag	TINYINT,
	Memo	TEXT,
	Prefix	VARCHAR(5),
	Suffix	VARCHAR(5),
	Terms	VARCHAR(1),
	TaxNumber	VARCHAR(20),
	BladeList	VARCHAR(30),
	CreditCardNumber	VARCHAR(30),
	CreditCardExpiration	DATE,
	PRIMARY KEY  (`CustomerID`)
) ;

Create Table InvoiceEntries	(
	InvoiceEntryID	BIGINT NOT NULL AUTO_INCREMENT,
	Invoice	FLOAT,
	PartID	BIGINT,
	PartDescription	VARCHAR(10),
	Quantity	INTEGER(2),
	Price	FLOAT,
	Comment	TEXT,
	PRIMARY KEY  (`InvoiceEntryID`)
) ;
Create Table InvoiceEntryAdditions	(
	AdditionID	BIGINT NOT NULL AUTO_INCREMENT,
	EntryID	FLOAT,
	PartID	BIGINT,
	Price	FLOAT,
	PRIMARY KEY  (`AdditionID`)
) ;
Create Table Invoices	(
	Invoice	BIGINT NOT NULL AUTO_INCREMENT,
	DateOrdered	DATE,
	DateEstimated	DATE,
	DateEdit	DATE,
	DateShipped	DATE,
	CustomerID	BIGINT,
	BillingAddress	BIGINT,
	ShippingInfo	TEXT,
	TotalRetail	FLOAT,
	DiscountPercentage	FLOAT,
	TaxPercentage	FLOAT,
	ShippingAmount	FLOAT,
	ShippingInstructions	VARCHAR(15),
	Comment	TEXT,
	AmountPaid	FLOAT,
	Zone	VARCHAR(3),
	PONumber	VARCHAR(16),
	CreditCardNumber	VARCHAR(21),
	CreditCardExpiration	VARCHAR(5),
	StockOrder	TINYINT,
	PickUp	TINYINT,
	ShopSale	TINYINT,
	PRIMARY KEY  (`Invoice`)
) ;

Create Table PartPrices	(
	PartPriceID	BIGINT NOT NULL AUTO_INCREMENT,
	PartID	BIGINT,
	Year	BIGINT,
	Price	FLOAT,
	PRIMARY KEY  (`PartPriceID`)
) ;

Create Table Parts	(
	PartID	BIGINT NOT NULL AUTO_INCREMENT,
	PartCode	VARCHAR(10),
	Description	VARCHAR(30),
	Discountable	TINYINT,
	BladeItem	TINYINT,
	Taxable	TINYINT,
	Sheath	TINYINT,
	Active	TINYINT,
	PRIMARY KEY  (`PartID`)
) ;

Create Table Payments	(
	PaymentID	BIGINT NOT NULL AUTO_INCREMENT,
	CustomerID	BIGINT,
	Invoice	FLOAT,
	Payment	FLOAT,
	CheckNumber	VARCHAR(10),
	PaymentDate	DATE,
	PRIMARY KEY  (`PaymentID`)
) ;

ALTER TABLE Address AUTO_INCREMENT=60000;
ALTER TABLE Customers AUTO_INCREMENT=80000;
ALTER TABLE InvoiceEntries AUTO_INCREMENT=150000;
ALTER TABLE InvoiceEntryAdditions AUTO_INCREMENT=300000;
ALTER TABLE Invoices AUTO_INCREMENT=60000;
ALTER TABLE PartPrices AUTO_INCREMENT=5000;
ALTER TABLE Parts AUTO_INCREMENT=500;
ALTER TABLE Payments AUTO_INCREMENT=100000;


