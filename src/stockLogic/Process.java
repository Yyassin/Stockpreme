/*
 * Process
 * by Shrish Mohapatra & Yousef Yassin
 * 
 * 	- manages input checks
 * 	- keeps track of which account is signed in
 * 	- stores list of all users
 *	- methods for creating & finding users
 *	- save & load masterlist to textfile
 */

package stockLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

// MAIN LOGIC of program

public class Process {
	
	// Save Load vars
	static String FILENAME = "masterList.txt";
	static ArrayList<UserAccount> masterlist = new ArrayList<UserAccount>();
	
	public static UserAccount curAccount;
	public static StockData selectStock;
	
	public static String convertType = "CAD";
	
	// Input Check Vars
	static char[] SYM_BLACKLISTED = {' ', ','};
	static char[] SYM_WHITELISTED = {'@', '$', '#', '&', '!', '*'};
	
	public static void Initialize() {
		CurrencyConverter.GetRate();
		LoadData();
		DisplayAccount(-1);
	}
	
	// ACCOUNT METHODS
	public static void CreateAccount(String username, String password) {	
		UserAccount newUser = new UserAccount(username, password);
		masterlist.add(newUser);
		SaveData();
	}		
	
	public static void DisplayAccount(int ref) {
		String output = "";
		
		if(ref == -1) {
			// Display all accounts
			for(int i = 0; i<masterlist.size(); i++) {
				output += masterlist.get(i).toString() + "\n";
				output += masterlist.get(i).bankProfile.toString() + "\n";
				output += masterlist.get(i).stockProfile.toString() + "\n";
			}
		}
		
		System.out.println(output);
	}
	
	public static UserAccount FindAccount(String username) {
		UserAccount user = null;
		
		for(int i=0; i<masterlist.size(); i++) {
			if(masterlist.get(i).username.equals(username)) {
				user = masterlist.get(i);
				break;
			}
		}
		
		return user;
	}
	
	public static UserAccount FindAccount(String username, String password) {
		UserAccount user = null;
		
		for(int i=0; i<masterlist.size(); i++) {
			if(masterlist.get(i).username.equals(username)) {
				user = masterlist.get(i);
				break;
			}
		}
		
		if(user != null) {
			if(!user.password.equals(password)) {
				user = null;
			}
		}
		
		return user;
	}
	
	// TESTING
	private static void TestAddStock(int newRef) {
		int ref;
		
		if(newRef == -1) {
			ref = masterlist.size() - 1;
		} else { 
			ref = newRef;
		}
		
        masterlist.get(ref).stockProfile.AddStock("goog");
        masterlist.get(ref).stockProfile.AddStock("goog");
        masterlist.get(ref).stockProfile.AddStock("nke");
        masterlist.get(ref).stockProfile.AddStock("shop");
        
        masterlist.get(ref).bankProfile.NewTransaction("deposit", 999.23);
        masterlist.get(ref).bankProfile.NewTransaction("withdraw", 66.23);
        masterlist.get(ref).bankProfile.NewTransaction("deposit", 7777.23);
	}
	
	// SAVE & LOAD METHODS
	public static void LoadData() {
		ArrayList<UserAccount> ls = new ArrayList<UserAccount>();
		
		String fileName = FILENAME; // default	
		File file = new File(fileName);
		Scanner input;
		
		// check if file exists
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException ex) {
			System.out.println("ERROR: Cannot find file.");
			return;
		}
		
		int count = 0;
		String line;
		
		String[] baseParams = new String[2];
                
        ArrayList<String> bankParams = new ArrayList<String>();
        ArrayList<Transaction> ledger = new ArrayList<Transaction>(); 
        double balance = 0;
        
        ArrayList<String> stockParams = new ArrayList<String>();
        ArrayList<StockData> portfolio = new ArrayList<StockData>();
		
		while(input.hasNext()) {
			line = input.nextLine();
			
			switch(count) {
				case 1: // bankProfile
                    bankParams = new ArrayList<String>(Arrays.asList(line.split(",")));
                    balance = Double.parseDouble(bankParams.get(0));
                    
                    for(int i=1; i<bankParams.size(); i++) {
                        ArrayList<String> transParams = new ArrayList<String>(Arrays.asList(bankParams.get(i).split(" ")));
                        
                        Transaction trans = new Transaction(transParams.get(0), transParams.get(1), transParams.get(2));
                        ledger.add(trans);
                    }
					break;
				case 2: // stockProfile
                    stockParams = new ArrayList<String>(Arrays.asList(line.split(",")));
                    
                    for(int i=0; i<stockParams.size(); i++) {
                        ArrayList<String> stockObjParams = new ArrayList<String>(Arrays.asList(stockParams.get(i).split(" ")));
                        
                        StockData newStock = new StockData(stockObjParams.get(0));
                        newStock.AssignUserData(stockObjParams.get(0), Integer.valueOf(stockObjParams.get(1)), Double.valueOf(stockObjParams.get(2)));
                        portfolio.add(newStock);
                    }
                    
                    // instantiate user
                    UserAccount newUser = new UserAccount(baseParams[0], baseParams[1]);
                    
                    BankProfile bankProfile = new BankProfile();
                    bankProfile.balance = balance;
                    bankProfile.ledger = ledger;
                    
                    StockProfile stockProfile = new StockProfile();
                    stockProfile.portfolio = portfolio;
                    
                    newUser.bankProfile = bankProfile;
                    newUser.stockProfile = stockProfile;
                    
                    ls.add(newUser);                   
                    
					break;
				default: // base params
					baseParams = line.split(",");
			}
			
			if(count == 2) {
				// reset vars
				count = 0;
				baseParams = new String[2];
                
		        bankParams = new ArrayList<String>();
		        ledger = new ArrayList<Transaction>(); 
		        balance = 0;
		        
		        stockParams = new ArrayList<String>();
		        portfolio = new ArrayList<StockData>();
			} else {
				count++;
			}
		}
		
		input.close();
		
		masterlist = ls;
	}
	
	public static void SaveData() {
		String fileName = FILENAME; // Default		
		File file = new File(fileName);
		PrintWriter wr;
		
		// check for file writing issues
		try {
			wr = new PrintWriter(file);
		} catch(IOException ex) {
			System.out.println("ERROR: Cannot write to file.");
			return;
		}
		
		for(int x=0; x<masterlist.size(); x++) {
			UserAccount user = masterlist.get(x);
			wr.print(user.getUsername() + "," + user.getPassword()); 			
			wr.print("\n");
			wr.print(user.bankProfile.toString());
			wr.print("\n");
            wr.print(user.stockProfile.toString());
            wr.print("\n");            
		}
		
		wr.close();
		
		System.out.println("Saved to " + fileName);
	}
	
	// INPUT CHECKS
	public static String CheckInput(String input, String iType) {
		String error = "";
		
		switch(iType) {
			case "number":
				error = _checkNum(input);
				break;
			case "username":
				error = _checkUsername(input);
				break;
			case "password":
				error = _checkPassword(input);
				break;
			default:
				//
		}
		
		return error;
	}
	
	private static String _checkInputLen(String input, int min, int max) {
		String error = "";
		
		if(input.length() > max) {
			error = "ERROR: Input is more than " + max + " characters.";
		} else if(input.length() < min) {
			error = "ERROR: Input is less than " + min + " characters.";
		} else if (input == "") {
			error = "ERROR: Input is empty.";
		}
		
		return error;
	}
	
	private static String _checkUsername(String input) {
		String error = "";
		
		error = _checkInputLen(input, 5, 30);
		
		if(error == "") {
			char[] symbols = SYM_BLACKLISTED; // username cannot have these symbols
			
			for(int i=0; i<input.length(); i++) {
				for(int j=0; j<symbols.length; j++) {
					
					if(input.charAt(i) == symbols[j]) {
						error = "ERROR: Username contains <" + symbols[j] + ">";
						break;
					}
				}
				
				if (error != "") {
					break;
				}
			}
		}		
		
		return error;
	}
	
	private static String _checkPassword(String input) {
		String error = "";
		
		error = _checkInputLen(input, 5, 30);
		
		if(error != "") { return error; }
		
		error = "ERROR: Password is missing:";
		
		// look for capitals, lowercase, symbols
		boolean capital = false;
		boolean lowerCase = false;
		boolean symbol = false;
		
		char[] symbols = SYM_WHITELISTED;				
		
		for(int i=0; i<input.length(); i++) {
			
			if(Character.isUpperCase(input.charAt(i))) { // upper case
				capital = true;
			} else if(Character.isLowerCase(input.charAt(i))) { // lower case
				lowerCase = true;
			} else {
				// check for special symbols
				for(int x=0; x<symbols.length; x++) {
					if(input.charAt(i) == symbols[x]) {
						symbol = true;
						break;
					}
				}
			}
			
			if(capital && lowerCase && symbol) {
				// password meets criteria
				error = "";
				break;
			}
			
		}
		
		if(!capital) { error = error + ", capital"; }
		
		if(!lowerCase) { error = error + ", lowercase"; }
		
		if(!symbol) { error = error + ", symbol"; }
		
		return error;
	}
	
	private static String _checkNum(String input) {
		String error = "";
		
		try {
			Double num = Double.parseDouble(input);
			
			if(num < 0) { error = "ERROR: Invalid number"; }
		} catch (NumberFormatException e) {
			error = "ERROR: Input is not a number.";
		}
		
		return error;
	}

}
