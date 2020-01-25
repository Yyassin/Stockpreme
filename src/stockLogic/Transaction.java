/*
 * Transaction
 * by Shrish Mohapatra
 * 
 * 	- stores data regarding transaction type, amount, and date of transaction
 */

package stockLogic;

public class Transaction {
	
	String tType; // withdraw or deposit
	String amount;
	String date;

	public Transaction(String date, String tType, String amount) {
		super();
		this.date = date;
		this.tType = tType;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return date + " " + tType + " " + amount;
	}	
	
}
