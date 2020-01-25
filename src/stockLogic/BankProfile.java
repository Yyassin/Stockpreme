/*
 * BankProfile
 * by Shrish Mohapatra
 * 
 * 	- stores information regarding balance, ledger (list of transaction)
 * 	- methods for displaying bank profile information
 */

package stockLogic;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BankProfile {
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	LocalDateTime now = LocalDateTime.now();
	
	double balance;
	ArrayList<Transaction> ledger;

	public BankProfile() {
		super();
		
		balance = 0.0;
		ledger = new ArrayList<Transaction>();
	}
        
    public void NewTransaction(String tType, double amount) {
        String date = dtf.format(now);
        
        Transaction trans = new Transaction(date, tType, String.valueOf(amount));
        ledger.add(trans);
    }

    public double getBalance() {
    	return this.balance;
    }
    
    public String getBalanceString() {
    	String newBalance = "";
    	
    	NumberFormat formatter = NumberFormat.getCurrencyInstance();
    	newBalance = formatter.format(this.balance);
    	
    	return newBalance;
    }
    
    public void changeBalance(String tType, double amount) {
    	if(tType.equalsIgnoreCase("Withdraw")) {
    		balance -= amount;
    	} else {
    		balance += amount;
    	}
    }
    
    public String displayLedger() {
    	String date = dtf.format(now);
    	String output = "";	
		
    	boolean todayCheck = false;
    	
		for(int i=0; i<ledger.size(); i++) {
			if(!todayCheck) {
				if(ledger.get(i).date.equalsIgnoreCase(date)) {
					output += "\nToday's Transactions\n";
					todayCheck = true;
				}
			}
			
			output += ledger.get(i).toString() + "\n";
		}
		
		return output;
    }
    
	@Override
	public String toString() {
		String output = "";
		
		output += balance;
		
		for(int i=0; i<ledger.size(); i++) {
			output += "," + ledger.get(i).toString();
		}
		
		return output;
	}		

}
