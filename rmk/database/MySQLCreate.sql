use mysql;
delete from user where user='gtr';
GRANT ALL PRIVILEGES ON *.* to 'gtr'@'carpus'  WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* to 'gtr'  WITH GRANT OPTION;
UPDATE user set password = old_password('skeet') where user = 'gtr';
FLUSH PRIVILEGES;

use rmk;

# Host: localhost
# Database: rmk
# Table: 'Address'
# 
DROP TABLE Address;
CREATE TABLE `Address` (
  `AddressID` bigint(20) NOT NULL auto_increment,
  `CorrectedAddressID` bigint(20) default NULL,
  `AddressType` bigint(20) default NULL,
  `CustomerID` bigint(20) default NULL,
  `ADDRESS0` varchar(30) default NULL,
  `ADDRESS1` varchar(30) default NULL,
  `ADDRESS2` varchar(30) default NULL,
  `CITY` varchar(23) default NULL,
  `STATE` char(2) default NULL,
  `ZIP` varchar(10) default NULL,
  `COUNTRY` varchar(30) default NULL,
  `ZONE` char(3) default NULL,
  `TimesUsed` int(2) default NULL,
  `PrimaryCustomerAddress` tinyint(4) default NULL,
  PRIMARY KEY  (`AddressID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'Customers'
# 
DROP TABLE Customers;
CREATE TABLE `Customers` (
  `CustomerID` bigint(20) NOT NULL auto_increment,
  `PhoneNumber` varchar(13) default NULL,
  `LastName` varchar(30) default NULL,
  `FirstName` varchar(30) default NULL,
  `CurrrentAddress` bigint(20) default NULL,
  `Balance` float default NULL,
  `Discount` float default NULL,
  `Dealer` int(2) default NULL,
  `Flag` tinyint(4) default NULL,
  `Memo` text,
  `Prefix` varchar(5) default NULL,
  `Suffix` varchar(5) default NULL,
  `Terms` char(1) default NULL,
  `TaxNumber` varchar(20) default NULL,
  `BladeList` varchar(30) default NULL,
  `CreditCardNumber` varchar(30) default NULL,
  `CreditCardExpiration` date default NULL,
  `EMailAddress` varchar(50) default NULL,
  PRIMARY KEY  (`CustomerID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'HistoryItems'
# 
DROP TABLE HistoryItems;
CREATE TABLE `HistoryItems` (
  `HistoryItemID` bigint(20) NOT NULL auto_increment,
  `Invoice` bigint(20) default NULL,
  `DateStamp` datetime default NULL,
  PRIMARY KEY  (`HistoryItemID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'InvoiceEntries'
# 
DROP TABLE InvoiceEntries;
CREATE TABLE `InvoiceEntries` (
  `InvoiceEntryID` bigint(20) NOT NULL auto_increment,
  `Invoice` float default NULL,
  `PartID` bigint(20) default NULL,
  `PartDescription` varchar(10) default NULL,
  `Quantity` int(2) default NULL,
  `Price` float default NULL,
  `Comment` text,
  PRIMARY KEY  (`InvoiceEntryID`),
  KEY `invNum` (`Invoice`,`PartID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'InvoiceEntryAdditions'
# 
DROP TABLE InvoiceEntryAdditions;
CREATE TABLE `InvoiceEntryAdditions` (
  `AdditionID` bigint(20) NOT NULL auto_increment,
  `EntryID` float default NULL,
  `PartID` bigint(20) default NULL,
  `Price` float default NULL,
  PRIMARY KEY  (`AdditionID`),
  KEY `entry` (`EntryID`,`PartID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'Invoices'
# 
DROP TABLE Invoices;
CREATE TABLE `Invoices` (
  `Invoice` bigint(20) NOT NULL auto_increment,
  `DateOrdered` date default NULL,
  `DateEstimated` date default NULL,
  `DateEdit` date default NULL,
  `DateShipped` date default NULL,
  `CustomerID` bigint(20) default NULL,
  `BillingAddress` bigint(20) default NULL,
  `ShippingInfo` text,
  `TotalRetail` float default NULL,
  `DiscountPercentage` float default NULL,
  `TaxPercentage` float default NULL,
  `ShippingAmount` float default NULL,
  `ShippingInstructions` varchar(15) default NULL,
  `Comment` text,
  `AmountPaid` float default NULL,
  `Zone` char(3) default NULL,
  `PONumber` varchar(16) default NULL,
  `CreditCardNumber` varchar(21) default NULL,
  `CreditCardExpiration` varchar(5) default NULL,
  `StockOrder` tinyint(4) default NULL,
  `PickUp` tinyint(4) default NULL,
  `ShopSale` tinyint(4) default NULL,
  PRIMARY KEY  (`Invoice`),
  KEY `estimated` (`DateEstimated`),
  KEY `Cust` (`CustomerID`,`DateShipped`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'PartPrices'
# 
DROP TABLE PartPrices;
CREATE TABLE `PartPrices` (
  `PartPriceID` bigint(20) NOT NULL auto_increment,
  `PartID` bigint(20) default NULL,
  `Year` bigint(20) default NULL,
  `Price` float default NULL,
  `discountable` tinyint(4) default NULL,
  PRIMARY KEY  (`PartPriceID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'PartTypes'
# 
DROP TABLE PartTypes;
CREATE TABLE `PartTypes` (
  `PartTypeID` bigint(20) NOT NULL auto_increment,
  `Description` varchar(50) default NULL,
  PRIMARY KEY  (`PartTypeID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'Parts'
# 
DROP TABLE Parts;
CREATE TABLE `Parts` (
  `PartID` bigint(20) NOT NULL auto_increment,
  `PartCode` varchar(10) default NULL,
  `Description` varchar(30) default NULL,
  `Discountable` tinyint(4) default NULL,
  `BladeItem` tinyint(4) default NULL,
  `Taxable` tinyint(4) default NULL,
  `Sheath` tinyint(4) default NULL,
  `Active` tinyint(4) default NULL,
  `PartType` bigint(20) default NULL,
  `AskPrice` tinyint(4) default NULL,
  PRIMARY KEY  (`PartID`)
) TYPE=MyISAM; 

# Host: localhost
# Database: rmk
# Table: 'Payments'
# 
DROP TABLE Payments;
CREATE TABLE `Payments` (
  `PaymentID` bigint(20) NOT NULL auto_increment,
  `CustomerID` bigint(20) default NULL,
  `Invoice` float default NULL,
  `Payment` float default NULL,
  `CheckNumber` varchar(10) default NULL,
  `PaymentDate` date default NULL,
  `PaymentType` smallint(6) default NULL,
  `VCODE` varchar(6) default NULL,
  `expirationdate` date default NULL,
  PRIMARY KEY  (`PaymentID`),
  KEY `InvPayments` (`Invoice`,`CustomerID`),
  KEY `custPayments` (`CustomerID`,`Invoice`)
) TYPE=MyISAM; 

