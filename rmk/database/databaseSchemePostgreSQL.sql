Drop table address;
DROP sequence address_addressid_seq;
Create Table Address	(
	AddressID	SERIAL primary key,
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
	TimesUsed	INTEGER,
	PrimaryCustomerAddress	BOOLEAN
);
grant all on Address to group rmk;
grant all on Address_Addressid_seq to group rmk;
CREATE INDEX customer_Address_index ON Address (Customerid);



Drop table Customers;
DROP sequence Customers_Customerid_seq;
Create Table Customers	(
	CustomerID	SERIAL primary key,
	PhoneNumber	VARCHAR(13),
	LastName	VARCHAR(30),
	FirstName	VARCHAR(30),
	CurrrentAddress	BIGINT,
	Balance	FLOAT,
	Discount	FLOAT,
	Dealer	INTEGER,
	Flag	BOOLEAN,
	Memo	TEXT,
	Prefix	VARCHAR(5),
	Suffix	VARCHAR(5),
	Terms	VARCHAR(1),
	TaxNumber	VARCHAR(20),
	BladeList	VARCHAR(30),
	CreditCardNumber	VARCHAR(30),
	CreditCardExpiration	DATE,
	EMailAddress	VARCHAR(50)
);
grant all on Customers to group rmk;
grant all on Customers_Customerid_seq to group rmk;



Drop table InvoiceEntries;
DROP sequence invoiceentrie_invoiceentryi_seq;
Create Table InvoiceEntries	(
	InvoiceEntryID	SERIAL primary key,
	Invoice	FLOAT,
	PartID	BIGINT,
	PartDescription	VARCHAR(10),
	Quantity	INTEGER,
	Price	FLOAT,
	Comment	TEXT
);
grant all on InvoiceEntries to group rmk;
grant all on invoiceentrie_invoiceentryi_seq to group rmk;
CREATE INDEX entry_index ON InvoiceEntries (Invoice);



Drop table InvoiceEntryAdditions;
DROP sequence invoiceentryaddi_additionid_seq;
Create Table InvoiceEntryAdditions	(
	AdditionID	SERIAL primary key,
	EntryID	FLOAT,
	PartID	BIGINT,
	Price	FLOAT
);
grant all on InvoiceEntryAdditions to group rmk;
grant all on invoiceentryaddi_additionid_seq to group rmk;
CREATE INDEX entryAddition_index ON  InvoiceEntryAdditions (EntryID);



Drop table Invoices;
DROP sequence invoices_invoice_seq;
Create Table Invoices	(
	Invoice	SERIAL primary key,
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
	StockOrder	BOOLEAN,
	PickUp	BOOLEAN,
	ShopSale	BOOLEAN
);
grant all on Invoices to group rmk;
grant all on Invoices_Invoice_seq to group rmk;

Drop table Payments;
DROP sequence payments_paymentid_seq;
Create Table Payments	(
	PaymentID	SERIAL primary key,
	CustomerID	BIGINT,
	Invoice	FLOAT,
	Payment	FLOAT,
	CheckNumber	VARCHAR(16),
	PaymentDate	DATE,
	VCODE		VARCHAR(6),
	expirationdate	DATE,
	PaymentType	INT
);
grant all on Payments to group rmk;
grant all on Payments_Paymentid_seq to group rmk;


;;;;;;;;;;;;;;;;;;;;;;;;
Drop table PartPrices;
DROP sequence partprices_partpriceid_seq;
Create Table PartPrices	(
	PartPriceID	SERIAL primary key,
	PartID	BIGINT,
	Year	BIGINT,
	Price	FLOAT,
	Discountable	BOOLEAN
);
grant all on PartPrices to group rmk;
grant all on PartPrices_PartPriceid_seq to group rmk;



Drop table Parts;
DROP sequence parts_partid_seq;
Create Table Parts	(
	PartID	SERIAL primary key,
	PartCode	VARCHAR(15),
	Description	VARCHAR(30),
	Discountable	BOOLEAN,
	BladeItem	BOOLEAN,
	Taxable	BOOLEAN,
	Sheath	BOOLEAN,
	Active	BOOLEAN,
	PartType BIGINT
);
grant all on Parts to group rmk;
grant all on Parts_Partid_seq to group rmk;


Drop table PartTypes;
DROP sequence parts_partid_seq;
Create Table PartTypes	(
	PartTypeID	SERIAL primary key,
	Description	VARCHAR(50)
);
grant all on PartTypes to group rmk;
grant all on PartTypes_Partid_seq to group rmk;



Drop table HistoryItems;
DROP sequence HistoryItems_HistoryItemid_seq;
Create Table HistoryItems	(
	HistoryItemID	SERIAL primary key,
	Invoice	BIGINT,
	Date	DATE
);
grant all on HistoryItems to group rmk;
grant all on HistoryItems_HistoryItemid_seq to group rmk;




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
update parts set parttype = 9999 where description is null or description = '';
update parts set parttype = 99 
       where description ~*  'book' 
       or description ~*  'name plate'
       or description ~*  'compass'
       or description ~*  'engraving';

update parts set parttype = 1 
where description ~* 'skinner' 
      or description ~* 'fisherman'
      or description ~* 'stainless steel'
      or description ~* 'sawteeth'
;

update parts set parttype = 1 
where description like '#%'
and (parttype = 9999 or parttype is null or parttype = 0);

update parts set parttype = 1 
where description ~* 'apfk'
and (parttype = 9999 or parttype is null or parttype = 0);

update parts set parttype = 2
where description ~* 'handle'
and (parttype = 9999 or parttype is null or parttype = 0);

update parts set parttype = 4
where description ~* 'butt'
and (parttype = 9999 or parttype is null or parttype = 0);


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
DROP sequence address_addressid_seq;
CREATE sequence address_addressid_seq 
START 60000;
grant all on address_addressid_seq  to group rmk;

DROP sequence Customers_Customerid_seq;
CREATE sequence Customers_Customerid_seq 
START 80000;
grant all on Customers_Customerid_seq  to group rmk;

DROP sequence invoiceentrie_invoiceentryi_seq;
CREATE sequence invoiceentrie_invoiceentryi_seq
START 150000;
grant all on invoiceentrie_invoiceentryi_seq to group rmk;

DROP sequence invoiceentryaddi_additionid_seq;
CREATE sequence invoiceentryaddi_additionid_seq
START 300000;
grant all on invoiceentryaddi_additionid_seq to group rmk;

DROP sequence invoices_invoice_seq;
CREATE sequence invoices_invoice_seq
START 60000;
grant all on invoices_invoice_seq to group rmk;

DROP sequence parts_partid_seq;
CREATE sequence parts_partid_seq
START 500;
grant all on parts_partid_seq to group rmk;

DROP sequence payments_paymentid_seq;
CREATE sequence payments_paymentid_seq
START 100000;
grant all on Payments_Paymentid_seq to group rmk;
