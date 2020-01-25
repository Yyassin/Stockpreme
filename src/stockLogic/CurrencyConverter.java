/*
 * Currency Converter
 * by Kimberly Dao
 * 
 * 	- grabs realtime currency rates for CAD to USD (vice versa)
 */

package stockLogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;

public class CurrencyConverter {

    public static double rate;
    
    public static double Convert (double input, String type){
        
        double output;
        
        if (type == "USD"){
            output = (rate) * input;
        } else{
            output = (1/rate) * input;
        }
        
        return output;
        
        
    }
    
    public static void GetRate(){
        String html = GetData();
        
        if(html.equalsIgnoreCase("error")) {return;}
        
        int p = html.indexOf("regularMarketDayHigh", 0);
        
        int to = html.indexOf(",", p);
        double findRate = Double.parseDouble(html.substring(p+29, to));
        
        rate = findRate;
    }
        
        
    private static String GetData(){
       BufferedReader br = null;
       StringBuilder sb = null;
        
        try {
            
            URL url = new URL ("https://ca.finance.yahoo.com/quote/CADUSD=X/");
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                sb.append(System.lineSeparator());
            }                       
            
        } catch(IOException e) {
            System.out.println("ERROR: url doesn't work");
            return "error";
            
        }  finally {

            if (br != null) {
                try {
                    br.close();
                } catch(IOException e) {
                    System.out.println("ERROR: input doesn't work");
                }
            }
        }
        
        return sb.toString();
    }
    
    /*        
    public static void main(String[] args) {
        // TODO code application logic here
        rate = GetRate();
        
        System.out.println(Convert(100, "CAD"));
        System.out.println(Convert(100, "USD"));
        
    }
    */
    
}