/*
 * Stock Profile
 * by Shrish Mohapatra
 * 
 * 	- contains arraylist which stores StockData
 * 	- buying, selling, finding stocks
 */

package stockLogic;

import java.text.NumberFormat;
import java.util.ArrayList;

public class StockProfile {
    
    ArrayList<StockData> portfolio;
    
    public StockProfile() {
        portfolio = new ArrayList<StockData>();
    }
    
    public StockData FindStock(StockData searchStock) {
    	return FindStock(searchStock.getSymbol());
    }
    
    public StockData FindStock(String ticker) {
    	for(int i=0; i<portfolio.size(); i++) {
    		if(portfolio.get(i).getSymbol().equalsIgnoreCase(ticker)) {
    			return portfolio.get(i);
    		}
    	}
    	
    	return null;
    }
    
    public String BuyStock(StockData searchStock, int qty) {
    	String msg = "";
    	StockData stock = FindStock(searchStock);
    	
    	if(stock != null) {
    		if(stock.data[0]*qty > Process.curAccount.getBankProfile().balance) {
    			msg = "ERROR: Insufficient funds.";
    			return msg;
    		} else {
    			Process.curAccount.getBankProfile().balance -= stock.data[0]*qty;
    			FindStock(searchStock).shares += qty;
    			return msg;
    		}
    	}
    	
    	stock = searchStock;
    	
    	if(stock.data[0]*qty > Process.curAccount.getBankProfile().balance) {
			msg = "ERROR: Insufficient funds.";
			return msg;
		} else {
			Process.curAccount.getBankProfile().balance -= stock.data[0]*qty;
			stock.shares = qty;
			stock.startPrice = stock.data[0];
			portfolio.add(stock);
			return msg;
		}    	    	
    }
    
    public String SellStock(String ticker, int qty) {
    	String msg = "";
    	StockData stock = FindStock(ticker);
    	
    	if(stock == null) {
    		msg = "ERROR: You do not any shares of this stock.";
    		return msg;
    	}
    	
    	if(qty < 0) {
    		msg = "ERROR: Invalid quantity.";
    	} else if(stock.shares < qty) {
    		msg = "ERROR: You do not have enough shares.";
    	} else {
    		SellShare(ticker, qty);
    	}
    	
    	return msg;
    }
    
    public void SellShare(String ticker, int qty) {
    	FindStock(ticker).shares -= qty;
    	
    	Process.curAccount.getBankProfile().balance += FindStock(ticker).data[0]*qty;
    	
    	if(FindStock(ticker).shares <= 0) {
    		portfolio.remove(FindStock(ticker));
    	}
    }
    
    public void AddStock(String ticker) {
        StockData newStock = new StockData(ticker);
        portfolio.add(newStock);
    }
    
    public ArrayList<StockData> getPortfolio() {
    	return portfolio;
    }
    
    public String getPortfolioValue() {
    	String output = "";
    	double sum = 0;
    	
    	for(int i=0; i<portfolio.size(); i++) {
    		if(portfolio.get(i).data[0] < 1) {
    			portfolio.get(i).getData();
    		}
    		
    		//System.out.println(portfolio.get(i).data[0] + ", " + portfolio.get(i).shares);
    		sum += portfolio.get(i).data[0] * portfolio.get(i).shares;
    	}
    	
    	NumberFormat formatter = NumberFormat.getCurrencyInstance();
    	
    	output = formatter.format(sum);
    	
    	return output;
    }
    
    public String getPortfolioProfit() {
    	double sum = 0;
    	
    	for(int i=0; i<portfolio.size(); i++) {
    		sum += portfolio.get(i).sharePriceChange(false)* portfolio.get(i).shares;
    	}    	
    	
    	NumberFormat formatter = NumberFormat.getCurrencyInstance();    	
    	String formatProfit = formatter.format(sum);
    	
    	if(sum > 0) {
    		return "<html><FONT COLOR=GREEN>" + String.format("%s", formatProfit) + "</FONT>";
    	} else if(sum < 0) {
    		return "<html><FONT COLOR=RED>" + String.format("%s", formatProfit) + "</FONT>";
    	} else {
    		return "<html><FONT COLOR=GREY>" + String.format("%s", formatProfit) + "</FONT>";
    	}
    }
    
    @Override
    public String toString() {
        String output = "";
        
        for(int i=0; i<portfolio.size(); i++) {
            if(i != 0) {
                output += ",";
            }
            output += portfolio.get(i).toString();
        }
        
        return output;
    }
}
