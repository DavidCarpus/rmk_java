package rmk.database;

import java.io.*;
import java.sql.SQLException;

import rmk.database.dbobjects.*;

public class TxtXfer
{
	carpus.database.DBInterface db = null;

	public TxtXfer() throws Exception
	{
		db = Configuration.Config.getDB();
		db.connect();
		db.getConn().setAutoCommit(false);
	}
	public void xferAddress(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("Address");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[Address.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);
				
			if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), Address.lengths);
				Address address = new Address(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(address);
				if (db.saveItems("Address", outputLst) == null)
					return;
			}
			row++;
		}
		db.getConn().commit();
	}

	public void xferCustomers(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("Customers");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[Customer.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

			if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), Customer.lengths);
				Customer customer = new Customer(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(customer);
				if (db.saveItems("Customers", outputLst) == null)
					return;
			}
			row++;
		}
	}

	public void xferInvoices(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("Invoices");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[Invoice.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

			if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), Invoice.lengths);
				Invoice invoice = new Invoice(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				if (db.saveItems("Invoice", outputLst) == null)
					return;
			}
			row++;
		}

	}

	public void xferInvoiceEntries(long startRow) throws Exception
	{
		String fileName =
			Configuration.Config.getDataFileLocation("InvoiceEntries");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput =
			new byte[InvoiceEntries.getTotalFieldLengths_txt() + 3];
		// CR-LF + 1 since memofield is last field
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst =
					fixed.getArray(
						new String(currInput),
						InvoiceEntries.lengths);
				InvoiceEntries invoice = new InvoiceEntries(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				if (db.saveItems("InvoiceEntries", outputLst) == null)
					return;
			}
			row++;
		}

	}

	public void xferInvoiceEntryAdditions(long startRow) throws Exception
	{
		String fileName =
			Configuration.Config.getDataFileLocation("InvoiceEntryAdditions");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput =
			new byte[InvoiceEntryAdditions.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst =
					fixed.getArray(
						new String(currInput),
						InvoiceEntryAdditions.lengths);
				InvoiceEntryAdditions invoice = new InvoiceEntryAdditions(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				if (db.saveItems("InvoiceEntryAdditions", outputLst) == null)
					return;
			}
			row++;
		}

	}

	public void xferPartPrices(long startRow) throws Exception
	{
		String fileName =
			Configuration.Config.getDataFileLocation("PartPrices");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[PartPrices.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), PartPrices.lengths);
				PartPrices invoice = new PartPrices(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				if (db.saveItems("PartPrices", outputLst) == null)
					return;
			}
			row++;
		}

	}

	public void xferParts(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("Parts");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[Parts.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), Parts.lengths);
				//    		System.out.println(fixed.list(lst));
				Parts invoice = new Parts(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				//  		System.out.println(this.getClass().getName() + ":"+ invoice);		
				if (db.saveItems("Parts", outputLst) == null)
					return;
				//  		System.out.println(this.getClass().getName() + ":"+ invoice);		
			}
			row++;
		}

	}

	public void xferPartTypes(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("PartTypes");
		System.out.println("\nXFer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();
		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[PartTypes.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), PartTypes.lengths);
//				System.out.println(fixed.list(lst));
				PartTypes item = new PartTypes(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(item);
				//  		System.out.println(this.getClass().getName() + ":"+ invoice);		
				if (db.saveItems("PartTypes", outputLst) == null)
					return;
				//  		System.out.println(this.getClass().getName() + ":"+ invoice);		
			}
			row++;
		}

	}

	public void xferPayments(long startRow) throws Exception
	{
		String fileName = Configuration.Config.getDataFileLocation("Payments");
		System.out.println("\nXfer: " + fileName);

		carpus.database.Fixed fixed = new carpus.database.Fixed();

		BufferedInputStream in =
			(new BufferedInputStream(new FileInputStream(fileName)));
		int row = 0;
		byte[] currInput = new byte[Payments.getTotalFieldLengths_txt() + 2];
		// CR-LF
		Object[] lst;
		while (in.read(currInput) != -1)
		{
		    preDB(row);

		    if (row >= startRow)
			{
				lst = fixed.getArray(new String(currInput), Payments.lengths);
				//  		System.out.println(fixed.list(lst));		
				Payments invoice = new Payments(lst);
				java.util.Vector outputLst = new java.util.Vector();
				outputLst.add(invoice);
				if (db.saveItems("Payments", outputLst) == null)
					return;
			}
			row++;
		}

	}

	void preDB(int row) throws SQLException{
		if (row % 100 == 0){
			System.out.print(row + "-");
			db.getConn().commit();
		}
		if (row % 2000 == 0)
			System.out.print("\n");
	}
	
	public static void main(String args[]) throws Exception
	{
		TxtXfer xfer = new TxtXfer();
//		xfer.xferAddress(0);
//		xfer.db.getConn().commit();
//		xfer.xferCustomers(0);
//		xfer.db.getConn().commit();
//		xfer.xferInvoices(0);
//		xfer.db.getConn().commit();
//		xfer.xferInvoiceEntries(0);
//		xfer.db.getConn().commit();
//		xfer.xferInvoiceEntryAdditions(0);
//		xfer.db.getConn().commit();
//		xfer.xferPartPrices(0);
//		xfer.db.getConn().commit();
		xfer.xferParts(0);
		xfer.db.getConn().commit();
//		xfer.xferPayments(0);
//		xfer.db.getConn().commit();
//		xfer.xferPartTypes(0);
//		xfer.db.getConn().commit();
	}

}
