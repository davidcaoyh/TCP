// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.rmi.RemoteException;
import java.io.*;
public class FlightManager implements ResourceManager
{
	protected String m_name = "";
	protected RMHashMap m_data = new RMHashMap();
	protected RMHashMap c_data = new RMHashMap();
	ServerSocket serverSocket;
	private int port;

	public static void main(String[] args){
		FlightManager rm = new FlightManager("flight_manager",1082);
		try{
			rm.serverSocket = new ServerSocket(rm.port);
			System.out.println("working");
			while(true){
				Socket socket = rm.serverSocket.accept();
				new SocketThread(socket, rm).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FlightManager(String p_name, int port)
	{
		m_name = p_name;
		this.port = port;
	}

	static class SocketThread extends Thread{
		Socket socket;
		FlightManager flightManager;

		public SocketThread (Socket middleware, FlightManager rm){
			socket = middleware;
			flightManager = rm;
		}

		public void run(){
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String msg = null;
				msg = in.readLine();
				System.out.println("Command Received: \"" + msg + "\"");
				if(msg!=null) {
					String[] command = msg.split(",");
					String returnV = execute(command, flightManager);
					System.out.println(returnV);
					out.println(returnV);
				}
				in.close();
				out.close();
				socket.close();

			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	protected static String execute(String[] cmd, FlightManager rm) throws NumberFormatException, RemoteException{
		String returnV = "";
		switch(cmd[0].toLowerCase()){
            case "addflight":{
                returnV = returnV + "Adding a new flight [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Flight Number: " + cmd[2] + "\n";
                returnV = returnV + "-Flight Seats: " + cmd[3] + "\n";
                returnV = returnV + "-Flight Price: " + cmd[4] + "\n";

                int id = toInt(cmd[1]);
                int flightNum = toInt(cmd[2]);
                int flightSeats = toInt(cmd[3]);
                int flightPrice = toInt(cmd[4]);

                if (rm.addFlight(id, flightNum, flightSeats, flightPrice)) {
                    returnV = returnV + "Flight added"+ "\n";
                } else {
                    returnV = returnV + "Flight could not be added"+ "\n";
				}
				return returnV;
            }
            case "deleteflight":{
                returnV = returnV + "Deleting a flight [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Flight Number: " + cmd[2] + "\n";

                int id = toInt(cmd[1]);
                int flightNum = toInt(cmd[2]);

                if (rm.deleteFlight(id, flightNum)) {
                    returnV = returnV + "Flight Deleted"+ "\n";
                } else {
                    returnV = returnV + "Flight could not be deleted"+ "\n";
                }
				return returnV;
            }
            case "queryflight":{
                returnV = returnV + "Querying a flight [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Flight Number: " + cmd[2]+ "\n";
                
                int id = toInt(cmd[1]);
                int flightNum = toInt(cmd[2]);
                int seats = rm.queryFlight(id, flightNum);
                returnV = returnV + "Number of seats available: " + seats+ "\n";
				return returnV;
            }
            case "queryflightprice":{
                returnV = returnV + "Querying a flight price [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Flight Number: " + cmd[2]+ "\n";
                int id = toInt(cmd[1]);
                int flightNum = toInt(cmd[2]);

                int price = rm.queryFlightPrice(id, flightNum);
                returnV = returnV + "Price of a seat: " + price+ "\n";
				return returnV;
            }
            case "reserveflight":{
                returnV = returnV + "Reserving seat in a flight [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Customer ID: " + cmd[2]+ "\n";
                returnV = returnV + "-Flight Number: " + cmd[3]+ "\n";

                int id = toInt(cmd[1]);
                int customerID = toInt(cmd[2]);
                int flightNum = toInt(cmd[3]);

                if (rm.reserveFlight(id, customerID, flightNum)) {
                    returnV = returnV + "Flight Reserved"+ "\n";
                } else {
                    returnV = returnV + "Flight could not be reserved"+ "\n";
                }
				return returnV;
            }
			case "bundle":{
				int id = toInt(cmd[1]);
				int customerID = toInt(cmd[2]);
				ArrayList<Integer> flightList = new ArrayList<Integer>();

				for(int i = 3; i < cmd.length; i++){
					flightList.add(toInt(cmd[i]));
				}
				for(int flight: flightList){
					returnV = returnV + "Reserving seat in a flight [xid=" + id + "]\n";
					returnV = returnV + "-Customer ID: " + customerID + "\n";
					returnV = returnV + "-Flight Number: " + flight + "\n";

					if (rm.reserveFlight(id, customerID, flight)) {
						returnV = returnV + "Flight Reserved"+ "\n";
					} else {
						returnV = returnV + "Flight could not be reserved"+ "\n";
					}
				}
				return returnV;
			}
			
			case "addcustomerid":{
                returnV = returnV + "Adding a new customer [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Customer ID: " + cmd[2] + "\n";

                int id = toInt(cmd[1]);
                int customerID = toInt(cmd[2]);

                if (rm.newCustomer(id, customerID)) {
                    returnV = returnV + "Add customer ID: " + customerID+ "\n";
                } else {
                    returnV = returnV + "Customer could not be added"+ "\n";
                }
				return returnV;
			}
            case "deletecustomer":{
                returnV = returnV + "Deleting a customer from the database [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Customer ID: " +cmd[2] + "\n";

                int id = toInt(cmd[1]);
                int customerID = toInt(cmd[2]);

                if (rm.deleteCustomer(id, customerID)) {
                    returnV = returnV + "Customer Deleted";
                } else {
                    returnV = returnV + "Customer could not be deleted";
				}
				return returnV;
            }
            case "querycustomer":{
                returnV = returnV + "Querying customer information [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Customer ID: " + cmd[2] + "\n";

                int id = toInt(cmd[1]);
				int customerID = toInt(cmd[2]);
				
                String bill = rm.queryCustomerInfo(id, customerID);
				returnV = returnV + bill;
				return returnV;
            }

			
			default:{
				return "Wrong Input";
			}

		}
	}


	// Reads a data item
	protected RMItem readData(int xid, String key)
	{
		synchronized(m_data) {
			RMItem item = m_data.get(key);
			if (item != null) {
				return (RMItem)item.clone();
			}
			return null;
		}
	}


	// Writes a data item
	protected void writeData(int xid, String key, RMItem value)
	{
		synchronized(m_data) {
			m_data.put(key, value);
		}
	}
	// Remove the item out of storage
	protected void removeData(int xid, String key)
	{
		synchronized(m_data) {
			m_data.remove(key);
		}
	}

	// Deletes the encar item
	protected boolean deleteItem(int xid, String key)
	{
		Trace.info("RM::deleteItem(" + xid + ", " + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(xid, key);
		// Check if there is such an item in the storage
		if (curObj == null)
		{
			Trace.warn("RM::deleteItem(" + xid + ", " + key + ") failed--item doesn't exist");
			return false;
		}
		else
		{
			if (curObj.getReserved() == 0)
			{
				removeData(xid, curObj.getKey());
				Trace.info("RM::deleteItem(" + xid + ", " + key + ") item deleted");
				return true;
			}
			else
			{
				Trace.info("RM::deleteItem(" + xid + ", " + key + ") item can't be deleted because some  s have reserved it");
				return false;
			}
		}
	}

	
	// Query the number of available seats/rooms/cars
	protected int queryNum(int xid, String key)
	{
		Trace.info("RM::queryNum(" + xid + ", " + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(xid, key);
		int value = 0;  
		if (curObj != null)
		{
			value = curObj.getCount();
		}
		Trace.info("RM::queryNum(" + xid + ", " + key + ") returns count=" + value);
		return value;
	}    

	// Query the price of an item
	protected int queryPrice(int xid, String key)
	{
		Trace.info("RM::queryPrice(" + xid + ", " + key + ") called");
		ReservableItem curObj = (ReservableItem)readData(xid, key);
		int value = 0; 
		if (curObj != null)
		{
			value = curObj.getPrice();
		}
		Trace.info("RM::queryPrice(" + xid + ", " + key + ") returns cost=$" + value);
		return value;        
	}

	// Reserve an item
	protected boolean reserveItem(int xid, int customerID, String key, String location)
	{
		Trace.info("RM::reserveItem(" + xid + ", customer=" + customerID + ", " + key + ", " + location + ") called" );        
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer)readCustomer(xid, Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ")  failed--customer doesn't exist");
			return false;
		} 

		// Check if the item is available
		ReservableItem item = (ReservableItem)readData(xid, key);
		if (item == null)
		{
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
			return false;
		}
		else if (item.getCount() == 0)
		{
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--No more items");
			return false;
		}
		else
		{            
			customer.reserve(key, location, item.getPrice());        
			writeCustomer(xid, customer.getKey(), customer);

			// Decrease the number of available items in the storage
			item.setCount(item.getCount() - 1);
			item.setReserved(item.getReserved() + 1);
			writeData(xid, item.getKey(), item);

			Trace.info("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") succeeded");
			return true;
		}        
	}

	// Create a new flight, or add seats to existing flight
	// NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException
	{
		Trace.info("RM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
		Flight curObj = (Flight)readData(xid, Flight.getKey(flightNum));
		if (curObj == null)
		{
			// Doesn't exist yet, add it
			Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
			writeData(xid, newObj.getKey(), newObj);
			Trace.info("RM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
		}
		else
		{
			// Add seats to existing flight and update the price if greater than zero
			curObj.setCount(curObj.getCount() + flightSeats);
			if (flightPrice > 0)
			{
				curObj.setPrice(flightPrice);
			}
			writeData(xid, curObj.getKey(), curObj);
			Trace.info("RM::addFlight(" + xid + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
		}
		return true;
	}


	// Deletes flight
	public boolean deleteFlight(int xid, int flightNum) throws RemoteException
	{
		return deleteItem(xid, Flight.getKey(flightNum));
	}


	// Returns the number of empty seats in this flight
	public int queryFlight(int xid, int flightNum) throws RemoteException
	{
		return queryNum(xid, Flight.getKey(flightNum));
	}


	// Returns price of a seat in this flight
	public int queryFlightPrice(int xid, int flightNum) throws RemoteException
	{
		return queryPrice(xid, Flight.getKey(flightNum));
	}

	public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException
	{
		return reserveItem(xid, customerID, Flight.getKey(flightNum), String.valueOf(flightNum));
	}

	public String getName() throws RemoteException
	{
		return m_name;
	}

	public static int toInt(String string) throws NumberFormatException
	{
		return (Integer.valueOf(string)).intValue();
	}

	public static boolean toBoolean(String string)// throws Exception
	{
		return (Boolean.valueOf(string)).booleanValue();
	}




	protected RMItem readCustomer(int xid, String key)	{
		synchronized(c_data) {
			RMItem item = c_data.get(key);
			if (item != null) {
				return (RMItem)item.clone();
			}
			return null;
		}
	}

	protected void writeCustomer(int xid, String key, RMItem value)
	{
		synchronized(c_data) {
			c_data.put(key, value);
		}
	}

	public boolean newCustomer(int xid, int customerID){
		Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") called");
		Customer customer = (Customer)readCustomer(xid, Customer.getKey(customerID));
		if(customer == null){
			customer = new Customer(customerID);
			writeCustomer(xid, customer.getKey(), customer);
			Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") created a new customer");
			return true;
		}
		else{
			Trace.info("INFO: RM::newCustomer(" + xid + ", " + customerID + ") failed--customer already exists");
			return false;
		}
	}

	public boolean deleteCustomer(int xid, int customerID){
		Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") called");
		Customer customer = (Customer)readCustomer(xid, Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
			return false;
		}
		else
		{            
			// Increase the reserved numbers of all reservable items which the customer reserved. 
 			RMHashMap reservations = customer.getReservations();
			for (String reservedKey : reservations.keySet())
			{        
				ReservedItem reserveditem = customer.getReservedItem(reservedKey);
				Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times");
				ReservableItem item  = (ReservableItem)readData(xid, reserveditem.getKey());
				Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved " + reserveditem.getKey() + " which is reserved " +  item.getReserved() +  " times and is still available " + item.getCount() + " times");
				item.setReserved(item.getReserved() - reserveditem.getCount());
				item.setCount(item.getCount() + reserveditem.getCount());
				writeData(xid, item.getKey(), item);
			}

			// Remove the customer from the storage
			removeCustomer(xid, customer.getKey());
			Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
			return true;
		}
	}

	public void removeCustomer(int xid, String key){
		synchronized(c_data){
			c_data.remove(key);
		}
	}

	public String queryCustomerInfo(int xid, int customerID){
		Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ") called");
		Customer customer = (Customer)readCustomer(xid, Customer.getKey(customerID));
		if (customer == null)
		{
			Trace.warn("RM::queryCustomerInfo(" + xid + ", " + customerID + ") failed--customer doesn't exist");
			// NOTE: don't change this--WC counts on this value indicating a customer does not exist...
			return "";
		}
		else
		{
			Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ")");
			System.out.println(customer.getBill());
			return customer.getBill();
		}
	}

}
 
