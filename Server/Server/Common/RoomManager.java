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

public class RoomManager implements ResourceManager
{
	protected String m_name = "";
	protected RMHashMap m_data = new RMHashMap();
	protected RMHashMap c_data = new RMHashMap();
	ServerSocket serverSocket;
	private int port;

	public static void main(String[] args){
		RoomManager rm = new RoomManager("room_manager",1081);
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




	public RoomManager(String p_name, int port)
	{
		m_name = p_name;
		this.port = port;
	}

	static class SocketThread extends Thread{
		Socket socket;
		RoomManager roomManager;

		public SocketThread (Socket middleware, RoomManager rm){
			socket = middleware;
			roomManager = rm;
		}

		public void run(){
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String msg = null;
				msg = in.readLine();
//				System.out.println(msg);
				if(msg!=null){
					String[] command = msg.split(",");
					String returnV = execute(command, roomManager);
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

	protected static String execute(String[] cmd, RoomManager rm) throws NumberFormatException, RemoteException{
		String returnV = "";
		switch(cmd[0].toLowerCase()){

            case "addrooms":{
                returnV = returnV + "Adding new rooms [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Room Location: " + cmd[2] + "\n";
                returnV = returnV + "-Number of Rooms: " + cmd[3] + "\n";
                returnV = returnV + "-Room Price: " + cmd[4] + "\n";

                int id = toInt(cmd[1]);
                String location = cmd[2];
                int numRooms = toInt(cmd[3]);
                int price = toInt(cmd[4]);

                if (rm.addRooms(id, location, numRooms, price)) {
                    returnV = returnV + "Rooms added"+ "\n";
                } else {
                    returnV = returnV + "Rooms could not be added"+ "\n";
                }
				return returnV;
            }
            case "deleterooms":{
                returnV = returnV + "Deleting all rooms at a particular location [xid=" + cmd[1] + "]\n";
                returnV = returnV + "-Car Location: " + cmd[2] + "\n";

                int id = toInt(cmd[1]);
                String location = cmd[2];

                if (rm.deleteRooms(id, location)) {
                    returnV = returnV + "Rooms Deleted"+ "\n";
                } else {
                    returnV = returnV + "Rooms could not be deleted"+ "\n";
                }
				return returnV;
            }
            case "queryrooms":{

                returnV = returnV + "Querying rooms location [xid=" + cmd[1] + "\n";
                returnV = returnV + "-Room Location: " + cmd[2] + "\n";
                
                int id = toInt(cmd[1]);
                String location = cmd[2];

                int numRoom = rm.queryRooms(id, location);
                returnV = returnV + "Number of rooms at this location: " + numRoom+ "\n";
				return returnV;
            }
            case "queryroomsprice":{
                returnV = returnV + "Querying rooms price [xid=" + cmd[1] + "\n";
                returnV = returnV + "-Room Location: " + cmd[2]+ "\n";

                int id = toInt(cmd[1]);
                String location = cmd[2];

                int price = rm.queryRoomsPrice(id, location);
                returnV = returnV + "Price of rooms at this location: " + price+ "\n";
				return returnV;
            }
            case "reserveroom":{
                returnV = returnV + "Reserving a room at a location [xid=" + cmd[1] + "\n";
                returnV = returnV + "-Customer ID: " + cmd[2];
                returnV = returnV + "-Room Location: " + cmd[3];
                
                int id = toInt(cmd[1]);
                int customerID = toInt(cmd[2]);
                String location = cmd[3];

                if (rm.reserveRoom(id, customerID, location)) {
                    returnV = returnV + "Room Reserved"+ "\n";
                } else {
                    returnV = returnV + "Room could not be reserved"+ "\n";
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


	// Create a new room location or add rooms to an existing location
	// NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int xid, String location, int count, int price) throws RemoteException
	{
		Trace.info("RM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
		Room curObj = (Room)readData(xid, Room.getKey(location));
		if (curObj == null)
		{
			// Room location doesn't exist yet, add it
			Room newObj = new Room(location, count, price);
			writeData(xid, newObj.getKey(), newObj);
			Trace.info("RM::addRooms(" + xid + ") created new room location " + location + ", count=" + count + ", price=$" + price);
		} else {
			// Add count to existing object and update price if greater than zero
			curObj.setCount(curObj.getCount() + count);
			if (price > 0)
			{
				curObj.setPrice(price);
			}
			writeData(xid, curObj.getKey(), curObj);
			Trace.info("RM::addRooms(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
		}
		return true;
	}

	// Delete rooms at a location
	public boolean deleteRooms(int xid, String location) throws RemoteException
	{
		return deleteItem(xid, Room.getKey(location));
	}

	// Returns the amount of rooms available at a location
	public int queryRooms(int xid, String location) throws RemoteException
	{
		return queryNum(xid, Room.getKey(location));
	}

	// Returns room price at this location
	public int queryRoomsPrice(int xid, String location) throws RemoteException
	{
		return queryPrice(xid, Room.getKey(location));
	}

	// Adds room reservation to this customer
	public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException
	{
		return reserveItem(xid, customerID, Room.getKey(location), location);
	}

	// Reserve bundle 
	public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException
	{
		return false;
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
 
