/*
 * Dashboard
 * by Shrish Mohapatra
 * 
 * 	- users can view, buy, sell stocks
 * 	- withdraw or deposit to balance
 * 	- convert currency
 * 	- help
 */

package stockUI;

import stockLogic.CurrencyConverter;
import stockLogic.Process;
import stockLogic.StockData;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLayeredPane;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JSeparator;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

public class Dashboard extends JFrame {

	private JPanel contentPane;	
	private JFrame frame;
	
	private JLayeredPane layeredPane;
	private ArrayList<JPanel> cards = new ArrayList<JPanel>();
	
	// COLORS
	Color COL_TEXT = Color.DARK_GRAY;
	Color COL_HOVER = Color.LIGHT_GRAY;
	Color COL_ERROR = Color.LIGHT_GRAY;
	
	Color COL_BACK = Color.white;
	Color COL_BACK2 = new Color(240, 240, 240);
	
	Color COL_THEME = new Color(217, 98, 98); // red
	
	// used for making window draggable
	int xMouse;
	int yMouse;
	
	// STOCKS CARD
	JPanel card_stocks;
	JLabel lbl_balanceStocks;
	
	// Portfolio
	JPanel portfolioPane;
	JLabel lbl_portfolioValue;
	JLabel lbl_portfolioProfit;
	ArrayList<JLabel> stockLabels = new ArrayList<JLabel>();
	
	// Market
	JTextField txt_searchTicker;
	JLabel lbl_errorMsgSearch;
	
	// Stock Viewer
	JLabel lbl_stockTicker;
	JLabel lbl_stockName;
	JLabel lbl_stockPrice;
	JLabel lbl_percentChange;
	JTextArea lblArea_stockInfo1;
	JTextArea lblArea_stockInfo2;
	JLabel lbl_shareCount;
	
	// Exchange Stock
	JTextField txt_quantity;
	JLabel lbl_errorMsgStockBuy;
	
	// BANKING CARD
	JTextField txt_amount;
	JLabel lbl_balanceBank;
	JLabel lbl_errorMsgBank;
	JTextArea txtr_transactionArea;
	private JTextField txtField_convertAmount;
	
	// Converter
	JLabel lblUSD;
	JLabel lblCAD;
	JLabel lbl_convertError;
	JLabel lbl_convertedAmount;
	
	// UI ACTIONS
	private void UpdateBalanceLabels() {	
		lbl_balanceStocks.setText(Process.curAccount.getBankProfile().getBalanceString());
		
		if(lbl_balanceBank != null) {
			lbl_balanceBank.setText(Process.curAccount.getBankProfile().getBalanceString());
		}
	}
	
		// Stocks Card
	private void UpdateStockViewer() {
		if(Process.selectStock == null) { return; }
		
		lbl_stockTicker.setText(Process.selectStock.getSymbol().toUpperCase());
		lbl_stockName.setText(Process.selectStock.getIdentifier());
		
		lbl_stockPrice.setText(Process.selectStock.getStockPrice());
		lbl_percentChange.setText(Process.selectStock.getPercentGrowth());
		
		lblArea_stockInfo1.setText(Process.selectStock.getStockInfo(0,6));
		lblArea_stockInfo2.setText(Process.selectStock.getStockInfo(6,0));
		
		lbl_shareCount.setText(Process.selectStock.getShareCount());
	}
	
	private void UpdatePortfolio(JPanel portfolioPane) {
		
		for(int i=0; i<stockLabels.size(); i++) {
			portfolioPane.remove(stockLabels.get(i));
			card_stocks.remove(stockLabels.get(i));
		}
		
		stockLabels = new ArrayList<JLabel>();
		
		for(int i=0; i<Process.curAccount.getStockProfile().getPortfolio().size(); i++) {
			StockData curStock = Process.curAccount.getStockProfile().getPortfolio().get(i);
			_stockLabel(portfolioPane, curStock);
		}
		
		lbl_portfolioValue.setText(Process.curAccount.getStockProfile().getPortfolioValue());
		lbl_portfolioProfit.setText(Process.curAccount.getStockProfile().getPortfolioProfit());
		
		UpdateBalanceLabels();
		
		portfolioPane.revalidate();
		portfolioPane.repaint();
	}
	
	private void SearchStock() {
		String inpTick = txt_searchTicker.getText();
		
		if(inpTick.equalsIgnoreCase("") || inpTick.equalsIgnoreCase("enter stock ticker")) {
			lbl_errorMsgSearch.setText("ERROR: Please enter a ticker");
			return;
		}
		
		StockData searchStock = Process.curAccount.getStockProfile().FindStock(inpTick);
		
		if(searchStock != null) {
			Process.selectStock = searchStock;
			UpdateStockViewer();
			return;
		}
		
		searchStock = new StockData(inpTick);
		searchStock.getData();
		
		if(!searchStock.initalized) {
			lbl_errorMsgSearch.setText("ERROR: Invalid ticker");
			return;
		}
		
		Process.selectStock = searchStock;
		UpdateStockViewer();
	}
	
	private void ExchangeStock(String type) {
		if(Process.selectStock == null) {
			lbl_errorMsgStockBuy.setText("ERROR: Please select a stock first");
			return;
		}
		
		String inpQty = txt_quantity.getText();		
		
		if(inpQty.equalsIgnoreCase("") || inpQty.equalsIgnoreCase("quantity")) {
			lbl_errorMsgStockBuy.setText("ERROR: Please enter quantity");
			return;
		}
		
		String msg = Process.CheckInput(inpQty, "number");
		
		if(!msg.equalsIgnoreCase("")) {
			lbl_errorMsgStockBuy.setText(msg);
			return;
		}
		
		int qty = Integer.valueOf(inpQty);
		
		if(type.equalsIgnoreCase("Sell")) {
			msg = Process.curAccount.getStockProfile().SellStock(Process.selectStock.getSymbol(), qty);
			if(msg.equalsIgnoreCase("")) {
				NewTransaction("Deposit", String.valueOf(Process.selectStock.getDataComp(0)*qty));
				UpdatePortfolio(portfolioPane);
				UpdateStockViewer();
			} else {
				lbl_errorMsgStockBuy.setText(msg);
				return;
			}
		} else { // BUY
			msg = Process.curAccount.getStockProfile().BuyStock(Process.selectStock, qty);
			if(msg.equalsIgnoreCase("")) {
				NewTransaction("Withdraw", String.valueOf(Process.selectStock.getDataComp(0)*qty));
				UpdatePortfolio(portfolioPane);
				UpdateStockViewer();
			} else {
				lbl_errorMsgStockBuy.setText(msg);
				return;
			}
		}
		
		lbl_errorMsgStockBuy.setText("");
	}
	
		// Banking Card
	private void NewTransaction(String type, String setAmount) {
		String inpAmount = setAmount;
		String msg = Process.CheckInput(inpAmount, "number");
		
		if(!msg.equalsIgnoreCase("")) {
			lbl_errorMsgBank.setText(msg);
			return;
		}
		
		double amount = Double.valueOf(inpAmount);
		
		if(type.equalsIgnoreCase("Withdraw")) {
			if(amount > Process.curAccount.getBankProfile().getBalance()) {
				msg = "ERROR: Insufficient funds";
				lbl_errorMsgBank.setText(msg);
				return;
			}
		}
		
		lbl_errorMsgBank.setText("");
		
		Process.curAccount.getBankProfile().changeBalance(type, amount);
		Process.curAccount.getBankProfile().NewTransaction(type, amount);		
		
		UpdateBalanceLabels();
		txtr_transactionArea.setText(Process.curAccount.getBankProfile().displayLedger());
	}
	
	private void NewTransaction(String type) {		
		NewTransaction(type, txt_amount.getText());
	}
	
	private void Convert(String type) {
		lbl_convertError.setText("");
		
		String inpAmount = txtField_convertAmount.getText();
		String msg = Process.CheckInput(inpAmount, "number");
		
		if(!msg.equalsIgnoreCase("")) {
			lbl_convertError.setText(msg);
			return;
		}
		
		double amount = Double.valueOf(inpAmount);
		String result = String.format("%.2f", CurrencyConverter.Convert(amount, type));
		lbl_convertedAmount.setText(result);
	}
	
	// CARDS LAYOUT
	private void _mainCards(JPanel contentPane) {
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(220, 0, 770, 610);
		contentPane.add(layeredPane);
		layeredPane.setLayout(new CardLayout(0, 0));
		
		_cardStocks(contentPane);
		
		_cardBanking(contentPane);
		
		_cardHelp(contentPane);
	}
	
		// Stocks Card
	private void _cardStocks(JPanel contentPane) {
		card_stocks = new JPanel();
		card_stocks.setBackground(COL_BACK);
		card_stocks.setBounds(0, 30, 770, 565);
		layeredPane.add(card_stocks);
		card_stocks.setLayout(null);
		cards.add(card_stocks);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setForeground(COL_TEXT);
		separator.setBackground(COL_TEXT);
		separator.setBounds(386, 55, 1, 520);
		card_stocks.add(separator);
		
		JLabel lbl_balanceHeader = new JLabel("Your Balance");
		lbl_balanceHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_balanceHeader.setForeground(COL_TEXT);
		lbl_balanceHeader.setBounds(30, 55, 123, 20);
		card_stocks.add(lbl_balanceHeader);
		
		lbl_balanceStocks = new JLabel(Process.curAccount.getBankProfile().getBalanceString());
		lbl_balanceStocks.setForeground(COL_TEXT);
		lbl_balanceStocks.setVerticalAlignment(SwingConstants.TOP);
		lbl_balanceStocks.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 35));
		lbl_balanceStocks.setBounds(30, 75, 324, 50);
		card_stocks.add(lbl_balanceStocks);
		
		JLabel lbl_portfolioHeader = new JLabel("Your Portfolio");
		lbl_portfolioHeader.setForeground(Color.DARK_GRAY);
		lbl_portfolioHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_portfolioHeader.setBounds(30, 145, 120, 20);
		card_stocks.add(lbl_portfolioHeader);				
		
		_portfolio(card_stocks);
		
		JLabel lbl_marketHeader = new JLabel("Market");
		lbl_marketHeader.setForeground(Color.DARK_GRAY);
		lbl_marketHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_marketHeader.setBounds(416, 55, 123, 20);
		card_stocks.add(lbl_marketHeader);
		
		txt_searchTicker = new JTextField() {
			@Override public void setBorder(Border border) {
		        // remove border
		    }
		};
		txt_searchTicker.setForeground(Color.LIGHT_GRAY);
		txt_searchTicker.setText("enter stock ticker");
		txt_searchTicker.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		txt_searchTicker.setBounds(416, 99, 230, 26);
		card_stocks.add(txt_searchTicker);
		txt_searchTicker.setColumns(10);
		
		lbl_errorMsgSearch = new JLabel("");
		lbl_errorMsgSearch.setForeground(Color.RED);
		lbl_errorMsgSearch.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_errorMsgSearch.setBounds(416, 136, 230, 20);
		card_stocks.add(lbl_errorMsgSearch);
		
		JSeparator searchLine = new JSeparator();
		searchLine.setBackground(Color.DARK_GRAY);
		searchLine.setForeground(Color.DARK_GRAY);
		searchLine.setBounds(416, 133, 230, 1);
		card_stocks.add(searchLine);
		
		JLabel lbl_searchButton = new JLabel("S E A R C H");
		lbl_searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SearchStock();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_searchButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_searchButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_searchButton.setForeground(Color.LIGHT_GRAY);
		lbl_searchButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_searchButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_searchButton.setBounds(660, 99, 79, 26);
		card_stocks.add(lbl_searchButton);
		
		_stockview(card_stocks);
		
		txt_quantity = new JTextField() {
			public void setBorder(Border border) {
			}
		};
		txt_quantity.setText("quantity");
		txt_quantity.setForeground(Color.LIGHT_GRAY);
		txt_quantity.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		txt_quantity.setColumns(10);
		txt_quantity.setBounds(416, 496, 64, 26);
		card_stocks.add(txt_quantity);
		
		JSeparator quantityLine = new JSeparator();
		quantityLine.setForeground(Color.DARK_GRAY);
		quantityLine.setBackground(Color.DARK_GRAY);
		quantityLine.setBounds(416, 530, 64, 1);
		card_stocks.add(quantityLine);
		
		JLabel lbl_buyButton = new JLabel("B U Y");
		lbl_buyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ExchangeStock("Buy");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_buyButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_buyButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_buyButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_buyButton.setForeground(Color.LIGHT_GRAY);
		lbl_buyButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_buyButton.setBounds(490, 496, 60, 26);
		card_stocks.add(lbl_buyButton);
		
		JLabel lbl_sellButton = new JLabel("S E L L");
		lbl_sellButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ExchangeStock("Sell");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_sellButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_sellButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_sellButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_sellButton.setForeground(Color.LIGHT_GRAY);
		lbl_sellButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_sellButton.setBounds(560, 496, 60, 26);
		card_stocks.add(lbl_sellButton);
		
		lbl_errorMsgStockBuy = new JLabel("");
		lbl_errorMsgStockBuy.setForeground(Color.RED);
		lbl_errorMsgStockBuy.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_errorMsgStockBuy.setBounds(416, 533, 230, 20);
		card_stocks.add(lbl_errorMsgStockBuy);
	}

	private void _stockview(JPanel card_stocks) {
		JPanel stockviewPane = new JPanel();
		stockviewPane.setBackground(COL_BACK2);
		stockviewPane.setBounds(416, 176, 323, 309);
		card_stocks.add(stockviewPane);
		stockviewPane.setLayout(null);
		
		lbl_stockTicker = new JLabel("Hello.");
		lbl_stockTicker.setBounds(2, 5, 188, 55);
		stockviewPane.add(lbl_stockTicker);
		lbl_stockTicker.setVerticalAlignment(SwingConstants.BOTTOM);
		lbl_stockTicker.setFont(new Font("Microsoft JhengHei Light", Font.BOLD, 45));
		
		lbl_stockName = new JLabel("Please choose a stock.");
		lbl_stockName.setBounds(10, 60, 225, 24);
		stockviewPane.add(lbl_stockName);
		lbl_stockName.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 16));
		lbl_stockName.setVerticalAlignment(SwingConstants.TOP);
		
		lbl_stockPrice = new JLabel("");
		lbl_stockPrice.setBounds(196, 5, 125, 55);
		stockviewPane.add(lbl_stockPrice);
		lbl_stockPrice.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 25));
		lbl_stockPrice.setHorizontalAlignment(SwingConstants.TRAILING);
		
		lbl_percentChange = new JLabel("");
		lbl_percentChange.setBounds(245, 60, 68, 24);
		stockviewPane.add(lbl_percentChange);
		lbl_percentChange.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_percentChange.setVerticalAlignment(SwingConstants.TOP);
		lbl_percentChange.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 16));
		
		lblArea_stockInfo1 = new JTextArea();
		lblArea_stockInfo1.setBackground(COL_BACK2);
		lblArea_stockInfo1.setBounds(10, 109, 158, 165);
		stockviewPane.add(lblArea_stockInfo1);
		lblArea_stockInfo1.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 16));
		lblArea_stockInfo1.setLineWrap(true);
		
		lblArea_stockInfo2 = new JTextArea();
		lblArea_stockInfo2.setBackground(COL_BACK2);
		lblArea_stockInfo2.setBounds(173, 109, 152, 165);
		stockviewPane.add(lblArea_stockInfo2);
		lblArea_stockInfo2.setLineWrap(true);
		lblArea_stockInfo2.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 16));
		
		lbl_shareCount = new JLabel("");
		lbl_shareCount.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_shareCount.setVerticalAlignment(SwingConstants.TOP);
		lbl_shareCount.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_shareCount.setBounds(10, 285, 303, 24);
		stockviewPane.add(lbl_shareCount);
	}
	private void _portfolio(JPanel card_stocks) {
		
		lbl_portfolioValue = new JLabel(Process.curAccount.getStockProfile().getPortfolioValue());
		lbl_portfolioValue.setVerticalAlignment(SwingConstants.BOTTOM);
		lbl_portfolioValue.setForeground(Color.DARK_GRAY);
		lbl_portfolioValue.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 35));
		lbl_portfolioValue.setBounds(30, 176, 170, 50);
		card_stocks.add(lbl_portfolioValue);
		
		lbl_portfolioProfit = new JLabel(Process.curAccount.getStockProfile().getPortfolioProfit());
		lbl_portfolioProfit.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_portfolioProfit.setVerticalAlignment(SwingConstants.BOTTOM);
		lbl_portfolioProfit.setForeground(Color.DARK_GRAY);
		lbl_portfolioProfit.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 25));
		lbl_portfolioProfit.setBounds(184, 176, 170, 50);
		card_stocks.add(lbl_portfolioProfit);
		
		portfolioPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) portfolioPane.getLayout();
		flowLayout.setVgap(10);
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		portfolioPane.setBounds(30, 246, 324, 309);
		card_stocks.add(portfolioPane);
		
		UpdatePortfolio(portfolioPane);
	}
	private void _stockLabel(JPanel portfolioPane, StockData curStock) {
		//curStock.getData();
		
		String labelTxt = curStock.toStringFormat(0);
		
		JLabel stockLabel1 = new JLabel();
		stockLabel1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Process.selectStock = curStock;
				UpdateStockViewer();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				stockLabel1.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				if(curStock != Process.selectStock) {
					stockLabel1.setForeground(Color.GRAY);
				}
			}
		});
		stockLabel1.setText(labelTxt);
		
		if(curStock == Process.selectStock) {
			stockLabel1.setForeground(COL_TEXT);
		} else {
			stockLabel1.setForeground(Color.GRAY);
		}
		
		stockLabel1.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 18));
		portfolioPane.add(stockLabel1);
		stockLabels.add(stockLabel1);
	}
		
		// Banking Card
	private void _cardBanking(JPanel contentPane) {
		JPanel card_bank = new JPanel();
		card_bank.setBounds(0, 30, 770, 565);
		layeredPane.add(card_bank);
		card_bank.setLayout(null);
		card_bank.setBackground(COL_BACK);
		cards.add(card_bank);
		
		JLabel lbl_balanceHeader = new JLabel("Your Balance");
		lbl_balanceHeader.setForeground(Color.DARK_GRAY);
		lbl_balanceHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_balanceHeader.setBounds(30, 55, 123, 20);
		card_bank.add(lbl_balanceHeader);
		
		lbl_balanceBank = new JLabel(Process.curAccount.getBankProfile().getBalanceString());
		lbl_balanceBank.setVerticalAlignment(SwingConstants.TOP);
		lbl_balanceBank.setForeground(Color.DARK_GRAY);
		lbl_balanceBank.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 35));
		lbl_balanceBank.setBounds(30, 75, 194, 50);
		card_bank.add(lbl_balanceBank);
		
		lbl_errorMsgBank = new JLabel("");
		lbl_errorMsgBank.setForeground(Color.RED);
		lbl_errorMsgBank.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_errorMsgBank.setBounds(30, 135, 194, 20);
		card_bank.add(lbl_errorMsgBank);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setForeground(Color.DARK_GRAY);
		separator.setBackground(Color.DARK_GRAY);
		separator.setBounds(386, 55, 1, 520);
		card_bank.add(separator);
		
		JLabel lbl_amountHeader = new JLabel("Amount");
		lbl_amountHeader.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_amountHeader.setForeground(Color.DARK_GRAY);
		lbl_amountHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_amountHeader.setBounds(234, 55, 139, 27);
		card_bank.add(lbl_amountHeader);
		
		txt_amount = new JTextField();
		txt_amount.setText("0.00");
		txt_amount.setHorizontalAlignment(SwingConstants.TRAILING);
		txt_amount.setForeground(new Color(77, 119, 95));
		txt_amount.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 20));
		txt_amount.setColumns(10);
		txt_amount.setBounds(234, 79, 139, 45);
		card_bank.add(txt_amount);
		
		JLabel lbl_withdrawButton = new JLabel("Withdraw");
		lbl_withdrawButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				NewTransaction("Withdraw");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_withdrawButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_withdrawButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_withdrawButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_withdrawButton.setForeground(Color.LIGHT_GRAY);
		lbl_withdrawButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_withdrawButton.setBounds(234, 135, 77, 27);
		card_bank.add(lbl_withdrawButton);
		
		JLabel lbl_depositButton = new JLabel("Deposit");
		lbl_depositButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				NewTransaction("Deposit");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_depositButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_depositButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_depositButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_depositButton.setForeground(Color.LIGHT_GRAY);
		lbl_depositButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_depositButton.setBounds(309, 140, 64, 17);
		card_bank.add(lbl_depositButton);
		
		JLabel lbl_transactionHeader = new JLabel("Past Transactions");
		lbl_transactionHeader.setForeground(Color.DARK_GRAY);
		lbl_transactionHeader.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_transactionHeader.setBounds(30, 173, 139, 27);
		card_bank.add(lbl_transactionHeader);
		
		txtr_transactionArea = new JTextArea();
		txtr_transactionArea.setForeground(Color.DARK_GRAY);
		txtr_transactionArea.setText(Process.curAccount.getBankProfile().displayLedger());
		txtr_transactionArea.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		txtr_transactionArea.setEditable(false);
		txtr_transactionArea.setBounds(30, 211, 324, 345);
		card_bank.add(txtr_transactionArea);
		
		_converter(card_bank);				
	}

	private void _converter (JPanel card_bank) {
		JLabel lblCurrencyConverter = new JLabel("Currency Converter");
		lblCurrencyConverter.setForeground(Color.DARK_GRAY);
		lblCurrencyConverter.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lblCurrencyConverter.setBounds(425, 165, 295, 20);
		card_bank.add(lblCurrencyConverter);
		
		txtField_convertAmount = new JTextField() {
			public void setBorder(Border border) {
			}
		};
		txtField_convertAmount.setText("amount");
		txtField_convertAmount.setForeground(Color.LIGHT_GRAY);
		txtField_convertAmount.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		txtField_convertAmount.setColumns(10);
		txtField_convertAmount.setBounds(425, 417, 64, 26);
		card_bank.add(txtField_convertAmount);
		
		JSeparator convertLine = new JSeparator();
		convertLine.setForeground(Color.DARK_GRAY);
		convertLine.setBackground(Color.DARK_GRAY);
		convertLine.setBounds(425, 451, 64, 1);
		card_bank.add(convertLine);
		
		lblCAD = new JLabel("C A D");
		lblCAD.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Process.convertType = "CAD";
				lblUSD.setForeground(Color.LIGHT_GRAY);
				lblCAD.setForeground(COL_TEXT);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblCAD.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				if(!Process.convertType.equalsIgnoreCase("CAD")) {
					lblCAD.setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		lblCAD.setHorizontalAlignment(SwingConstants.CENTER);
		lblCAD.setForeground(Color.LIGHT_GRAY);
		lblCAD.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lblCAD.setBounds(499, 417, 51, 26);
		card_bank.add(lblCAD);
		
		lblUSD = new JLabel("U S D");
		lblUSD.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Process.convertType = "USD";
				lblCAD.setForeground(Color.LIGHT_GRAY);
				lblUSD.setForeground(COL_TEXT);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblUSD.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				if(!Process.convertType.equalsIgnoreCase("USD")) {
					lblUSD.setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		lblUSD.setHorizontalAlignment(SwingConstants.CENTER);
		lblUSD.setForeground(Color.LIGHT_GRAY);
		lblUSD.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lblUSD.setBounds(560, 417, 48, 26);
		card_bank.add(lblUSD);
		
		lbl_convertError = new JLabel("Select currency to convert to.");
		lbl_convertError.setForeground(Color.RED);
		lbl_convertError.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_convertError.setBounds(425, 454, 230, 20);
		card_bank.add(lbl_convertError);
		
		JLabel lbl_convertButton = new JLabel("Convert");
		lbl_convertButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Convert(Process.convertType);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_convertButton.setForeground(COL_TEXT);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_convertButton.setForeground(Color.LIGHT_GRAY);
			}
		});
		lbl_convertButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_convertButton.setForeground(Color.LIGHT_GRAY);
		lbl_convertButton.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 16));
		lbl_convertButton.setBounds(660, 417, 60, 26);
		card_bank.add(lbl_convertButton);
		
		lbl_convertedAmount = new JLabel("0.00");
		lbl_convertedAmount.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 50));
		lbl_convertedAmount.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_convertedAmount.setBounds(421, 196, 299, 189);
		card_bank.add(lbl_convertedAmount);
	}
	
		// Help Card
	private void _cardHelp(JPanel contentPane) {
		JPanel card_help = new JPanel();
		card_help.setLayout(null);
		card_help.setBackground(COL_BACK);
		card_help.setBounds(0, 30, 770, 565);
		layeredPane.add(card_help);
		cards.add(card_help);
		
		JTextArea txtr_stocksExplain = new JTextArea();
		txtr_stocksExplain.setForeground(Color.DARK_GRAY);
		txtr_stocksExplain.setLineWrap(true);
		txtr_stocksExplain.setWrapStyleWord(true);
		txtr_stocksExplain.setText("Plain and simple, a stock is a type of security that allows stockholders a share in the ownership of a company. \r\n\r\nThey represent a claim on the company\u2019s assets and earnings. The more stocks invested, the larger the ownership claim on the organization.");
		txtr_stocksExplain.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 14));
		txtr_stocksExplain.setEditable(false);
		txtr_stocksExplain.setBounds(41, 142, 297, 173);
		card_help.add(txtr_stocksExplain);
		
		JLabel lbl_stocksExplainHeader = new JLabel("What Are Stocks");
		lbl_stocksExplainHeader.setForeground(Color.DARK_GRAY);
		lbl_stocksExplainHeader.setFont(new Font("Microsoft JhengHei Light", Font.BOLD | Font.ITALIC, 16));
		lbl_stocksExplainHeader.setBounds(41, 104, 139, 27);
		card_help.add(lbl_stocksExplainHeader);
		
		JTextArea txtr_whyInvest = new JTextArea();
		txtr_whyInvest.setForeground(Color.DARK_GRAY);
		txtr_whyInvest.setWrapStyleWord(true);
		txtr_whyInvest.setText("There exist various reasons to invest in shares, namely to support a company\u2019s products or mission. Often times, organisation may offer dividend percentages on the amount shareholders have invested as a token of gratitude. Other times, the success of an organisation may provide significant benefits.");
		txtr_whyInvest.setLineWrap(true);
		txtr_whyInvest.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 14));
		txtr_whyInvest.setEditable(false);
		txtr_whyInvest.setBounds(41, 364, 297, 173);
		card_help.add(txtr_whyInvest);
		
		JLabel lbl_whyInvestHeader = new JLabel("Why Invest");
		lbl_whyInvestHeader.setForeground(Color.DARK_GRAY);
		lbl_whyInvestHeader.setFont(new Font("Microsoft JhengHei Light", Font.BOLD | Font.ITALIC, 16));
		lbl_whyInvestHeader.setBounds(41, 326, 139, 27);
		card_help.add(lbl_whyInvestHeader);
		
		JLabel lbl_howInvestHeader = new JLabel("How To Invest");
		lbl_howInvestHeader.setForeground(Color.DARK_GRAY);
		lbl_howInvestHeader.setFont(new Font("Microsoft JhengHei Light", Font.BOLD | Font.ITALIC, 16));
		lbl_howInvestHeader.setBounds(409, 58, 139, 27);
		card_help.add(lbl_howInvestHeader);
		
		JTextArea txtr_howInvest = new JTextArea();
		txtr_howInvest.setForeground(Color.DARK_GRAY);
		txtr_howInvest.setWrapStyleWord(true);
		txtr_howInvest.setText("To begin your investor journey, simply head over to the \u201CStocks\u201D tab where you will view your current balance and portfolio. To display a ticker\u2019s information, type the name under the \u201CMarket\u201D title and press search. A list of information will be displayed to help inform your decision, such as live ask and bid prices. To invest in a stock, input the amount of shares you would like to purchase and select the \u201CBuy\u201D Option. To sell a stock, select the \u201CSell\u201D option. \r\n\r\nWe wish you the best of luck!");
		txtr_howInvest.setLineWrap(true);
		txtr_howInvest.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 14));
		txtr_howInvest.setEditable(false);
		txtr_howInvest.setBounds(409, 96, 297, 273);
		card_help.add(txtr_howInvest);
		
		JLabel lbl_popularHeader = new JLabel("Popular Stocks");
		lbl_popularHeader.setForeground(Color.DARK_GRAY);
		lbl_popularHeader.setFont(new Font("Microsoft JhengHei Light", Font.BOLD | Font.ITALIC, 16));
		lbl_popularHeader.setBounds(409, 377, 139, 27);
		card_help.add(lbl_popularHeader);
		
		JTextArea txtr_popularStocks = new JTextArea();
		txtr_popularStocks.setWrapStyleWord(true);
		txtr_popularStocks.setText("New users are often interested in similar stocks. Here are a few to help you get started!\r\n\r\nApple Inc (AAPL), Shopify (SHOP), Nike (NKE), Sony Entertainment (SNE) and Google (GOOG).\r\n");
		txtr_popularStocks.setLineWrap(true);
		txtr_popularStocks.setForeground(Color.DARK_GRAY);
		txtr_popularStocks.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 14));
		txtr_popularStocks.setEditable(false);
		txtr_popularStocks.setBounds(409, 415, 297, 173);
		card_help.add(txtr_popularStocks);
	}
	
	// SIDEMENU LAYOUT
	
	// SIDE MENU
	private void _sideMenu(JPanel contentPane) {
		JPanel menuPane = new JPanel();
		menuPane.setBackground(Color.DARK_GRAY);
		menuPane.setBounds(0, 0, 220, 700);
		contentPane.add(menuPane);
		menuPane.setLayout(null);
		
		JLabel lbl_title = new JLabel("Stockpreme");
		lbl_title.setVerticalAlignment(SwingConstants.TOP);
		lbl_title.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 35));
		lbl_title.setForeground(Color.white);
		lbl_title.setBounds(10, 11, 200, 54);
		menuPane.add(lbl_title);
		
		JLabel lbl_username = new JLabel(Process.curAccount.getUsername());
		lbl_username.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		lbl_username.setForeground(COL_BACK);
		lbl_username.setBounds(10, 63, 131, 20);
		menuPane.add(lbl_username);
		
		JLabel lbl_stockButton = new JLabel("S T O C K S");
		lbl_stockButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				layeredPane.removeAll();
				layeredPane.add(cards.get(0));
				layeredPane.repaint();
				layeredPane.revalidate();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_stockButton.setForeground(Color.LIGHT_GRAY);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_stockButton.setForeground(Color.white);
			}
		});
		lbl_stockButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_stockButton.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 18));
		lbl_stockButton.setForeground(Color.white);
		lbl_stockButton.setBounds(10, 203, 200, 39);
		menuPane.add(lbl_stockButton);
		
		JLabel lbl_bankButton = new JLabel("B A N K I N G");
		lbl_bankButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				layeredPane.removeAll();
				layeredPane.add(cards.get(1));
				layeredPane.repaint();
				layeredPane.revalidate();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_bankButton.setForeground(Color.LIGHT_GRAY);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_bankButton.setForeground(Color.white);
			}
		});
		lbl_bankButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_bankButton.setForeground(Color.white);
		lbl_bankButton.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 18));
		lbl_bankButton.setBounds(10, 253, 200, 39);
		menuPane.add(lbl_bankButton);
		
		JLabel lbl_settingsButton = new JLabel("H E L P");
		lbl_settingsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				layeredPane.removeAll();
				layeredPane.add(cards.get(2));
				layeredPane.repaint();
				layeredPane.revalidate();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_settingsButton.setForeground(Color.LIGHT_GRAY);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_settingsButton.setForeground(Color.white);
			}
		});
		lbl_settingsButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_settingsButton.setForeground(Color.white);
		lbl_settingsButton.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 18));
		lbl_settingsButton.setBounds(10, 303, 200, 39);
		menuPane.add(lbl_settingsButton);
		
		JLabel lbl_signoutButton = new JLabel("S I G N  O U T");
		lbl_signoutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Process.SaveData();
				contentPane.setVisible(false);
				dispose();
				LoginMenu.main(null);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_signoutButton.setForeground(Color.LIGHT_GRAY);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_signoutButton.setForeground(Color.white);
			}
		});
		lbl_signoutButton.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_signoutButton.setForeground(Color.white);
		lbl_signoutButton.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 18));
		lbl_signoutButton.setBounds(10, 353, 200, 39);
		menuPane.add(lbl_signoutButton);
	}
	
	// MISC
	private void _draggable(JPanel contentPane) {
		JLabel lbl_draggable = new JLabel();		
		lbl_draggable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				xMouse = arg0.getX();
				yMouse = arg0.getY();
			}
		});
		lbl_draggable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				
				frame.setLocation(x - xMouse,y - yMouse);
			}
		});
		lbl_draggable.setBounds(0, 0, 965, 28);
		contentPane.add(lbl_draggable);
	}
	
	
	public Dashboard() {
		frame = this;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 625);
		contentPane = new JPanel();
		contentPane.setBackground(COL_BACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lbl_Quit = new JLabel("X");
		lbl_Quit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.exit(0);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_Quit.setForeground(Color.LIGHT_GRAY);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_Quit.setForeground(Color.DARK_GRAY);
			}
		});
		lbl_Quit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_Quit.setVerticalAlignment(SwingConstants.TOP);
		lbl_Quit.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_Quit.setForeground(Color.DARK_GRAY);
		lbl_Quit.setBounds(960, 11, 23, 28);
		contentPane.add(lbl_Quit);				
		
		_mainCards(contentPane);
		_sideMenu(contentPane);
		
		_draggable(contentPane);				
	}
}
