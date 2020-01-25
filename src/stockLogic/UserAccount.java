/*
 * UserAccount
 * by Shrish Mohapatra
 * 
 * 	- stores basic user data (username, password)
 * 	- stores objects for bankProfile & stockProfile
 */

package stockLogic;

public class UserAccount {
	
	String username;
	String password;
	
	BankProfile bankProfile;
	StockProfile stockProfile;

	public UserAccount(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		
		this.bankProfile = new BankProfile();
		this.stockProfile = new StockProfile();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	public BankProfile getBankProfile() {
		return this.bankProfile;
	}
	
	public StockProfile getStockProfile() {
		return this.stockProfile;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return username + "," + password;
	}
}
