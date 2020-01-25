/*
 * StockData
 * by Yousef Yassin
 * 
 * 	- grab stock data from Yahoo Finance
 * 	- store information to list (data[])
 * 	- various methods for displaying certain ticker info
 */

package stockLogic;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.Arrays;

public class StockData {
    String symbol; // ticker
    String identifier; // stockName
    double[] data = new double[12];
    int shares;
    
    double startPrice;
    
    public boolean initalized = false;
    
    //Constructor
    public StockData(String sym) {
        symbol = sym;
    }
    
    public void AssignUserData(String ticker, int shares, double startPrice) {
        this.symbol = ticker;
        this.shares = shares;
        this.startPrice = startPrice;
    }
    
    /**Object Methods*/
    
    //Acquires ticker object data to place within list attribute
    public void getData() {
        String ticker = symbol.toUpperCase();
        String site = "";
        String[] commands = {",\"ask\":{",  ",\"askSize\":{", ",\"bid\":{", "bidSize", "regularMarketOpen", "regularMarketPreviousClose", "fiftyTwoWeekLow", "fiftyTwoWeekHigh", "dayHigh", "dayLow", "regularMarketVolume", "dividendRate"};
        
        Scanner sc = new Scanner (System.in);
        
        //Access site for info
        try {
        	site = readHTML(ticker);
        } catch(IOException e) {
        	System.out.println("ERROR: Url does not exist");
        	return;
        }
        
        this.identifier = getName(site);
        
        for (int i = 0; i <12; i++){
            String c = commands[i];
            
            try{
                if (c.equals("bid")) {
                    this.data[i] = Double.parseDouble(infoOf(site, ticker, ",\"bid\":{" ));
                } else if (c.equals("ask")) {                    
                    this.data[i] = Double.parseDouble(infoOf(site, ticker, ",\"ask\":{" ));
                } else {
                    this.data[i] = Double.parseDouble(infoOf(site, ticker, c));
                }
            }
            catch (NumberFormatException e){
                System.out.println("Invalid Ticker!");
                return;
            }
        }
        
        initalized = true;
    }
    
    public double getDataComp(int ref) {
    	return data[ref];
    }
    
    //Reads and copies ticker yahoo html code to gather info
    private String readHTML(String SYM) throws MalformedURLException, IOException{
        BufferedReader br = null;
        String html;
        
        try {

            URL url = new URL ("https://finance.yahoo.com/quote/" + SYM );
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                sb.append(System.lineSeparator());
            }

            //System.out.println(sb);
            html = sb.toString();
            
            
        } finally {

            if (br != null) {
                br.close();
            }
          
        }
        
        return html;
    }
        
    //Finds according value from within input html string
    public static String infoOf(String html, String symbol, String command){
        /*"ask" - current live price
        
        */
        
        int p = html.indexOf(command, 0);
        
        int r = html.indexOf("raw", p);
        
        int to = html.indexOf(",", r);
        
        String output = html.substring(r+5, to);
        
        return output;      
    }   
        
    //Finds ticker name from html input
    public static String getName(String html){
        int p = html.indexOf("title", 0);
        
        int to = html.indexOf("Stock", p);
        
        String output = html.substring(p+6, to);
        
        return output;   
    }
    
    public String getSymbol() {
    	return this.symbol;
    }
    
    public String getIdentifier() {
    	return this.identifier;
    }
    
    public String getStockPrice() {
    	return String.format("$%.2f", data[0]);
    }
    
    public String getStockInfo() {
    	return getStockInfo(0,0);
    }
    
    public String getStockInfo(int start, int end) {
    	String[] dataLabel = {"Ask", "Ask Size", "Bid", "Bid Size", "Open", "Close", "Year Low", "Year High", "Day High", "Day Low", "Vol", "Dividend"};
    	String output = "";
    	
    	if (this.data[0] != 0.0) {
    		for (int i = start; i< (dataLabel.length-end); i++){
                //output += dataLabel[i] + ": " + this.data[i] + "\n";
    			output += String.format("%s : %.2f\n", dataLabel[i], this.data[i]);
            }
    	}
    	
    	return output;
    }

    public String getShareCount() {
    	String output = "";
    	
    	if(this.shares == 0) {
    		output = "You have no shares in your portfolio.";
    	} else if(this.shares == 1){
    		output = String.format("<html>You have one share, bought for <b>$%.2f</b>", startPrice);
    	} else {
    		output = String.format("<html>You have %d shares, bought for <b>$%.2f</b> each.", shares, startPrice);
    	}
    	
    	return output;
    }
    
    //Acquires net percent growth of stock since opening
    public double prcntGrowth(int x){
        double[] data = new double[2];
        double delta = this.data[0] - this.data[4]; //diffrence between ask and open
        double percentG = delta/this.data[4]*100;
        data[0] = delta;
        data[1]=percentG;
        
        return data[x];
    }
    
    public double sharePriceChange(boolean percent) {
    	double diff = data[0] - startPrice;
    	
    	if(diff == data[0]) {
    		// there is no startPrice
    		return prcntGrowth(1);
    	}
    	
    	if(percent) {
    		return (diff/data[0])*100;
    	} else {
    		return diff;
    	}
    }
    
    public String getPercentGrowth() {
    	double percent = sharePriceChange(true);
    	
    	if(percent > 0) {
    		return "<html><FONT COLOR=GREEN>" + String.format("%.2f", percent) + "%</FONT>";
    	} else if(percent < 0) {
    		return "<html><FONT COLOR=RED>" + String.format("%.2f", percent) + "%</FONT>";
    	} else {
    		return "<html><FONT COLOR=GREY>" + String.format("%.2f", percent) + "%</FONT>";
    	}    	    	
    }
    
    @Override
    public String toString() {
        return this.symbol + " " + this.shares + " " + this.startPrice;
    }
    
    public String toStringFormat(int type) {
    	String output = "";
    	
    	output = String.format("%s $%.2f", identifier, shares*data[0]);
    	
    	return output;
    }
    
    //Displays ticker information
    public void disString(){
    	String[] dataLabel = {"Ask", "Ask Size", "Bid", "Bid Size", "Regular Market Open", "Regular Market Previous Close", "Fifty-Two Week Low", "Fifty-Two Week High", "Day High", "Day Low", "Regular Market Volume", "Dividend Rate"};
        if (this.data[0] != 0.0){
            System.out.println(this.identifier);
            for (int i = 0; i<11; i++){
                System.out.println(dataLabel[i] + ": " + this.data[i]);
            }
            
            System.out.println("Delta: " + this.prcntGrowth(1));
            System.out.println("Percent Growth: " + this.prcntGrowth(0));
        }
    }
}
   
