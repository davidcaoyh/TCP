package Client;


import java.net.Socket;
import java.util.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;

public class Client
{
	boolean flag = false;
	Socket socket;
	BufferedReader from_server;
	PrintWriter to_server;
	private static String server_host = "localhost";
	private static int server_port = 1080;

	public Client(){}

	public Client(String host, int port)
	{
		server_host = host;
		server_port = port;
	}

	public static void main(String[] args){
		if(args.length > 2){
			System.out.println("Error: Invalid Input");
			return;
		}
		else if(args.length == 2){
			Client client = new Client(args[0], Integer.parseInt(args[1]));
			client.start();
		}
		else{
			Client client = new Client();
			client.start();
		}

	}

	public void connectServer(){
		try{
			socket = new Socket(server_host,server_port);
			from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			to_server = new PrintWriter(socket.getOutputStream(), true);
			flag = true;
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void request (String cmd){
		try{
			to_server.println(cmd);
			String reply;
			while ((reply = from_server.readLine()) != null) {

//				if(reply.length()==0){
//					System.out.println(123);
//					break;
//				}
				System.out.println(reply);
			}
			this.socket.close();
			this.flag = false;

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void start()
	{
		// Prepare for reading commands
		System.out.println();
		System.out.println("Location \"help\" for list of supported commands");

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{

			// Read the next command
			String command = "";
			Vector<String> arguments = new Vector<String>();
			try {
				System.out.print((char)27 + "[32;1m\n>] " + (char)27 + "[0m");
				command = stdin.readLine().trim();
			}
			catch (IOException io) {
				System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0m" + io.getLocalizedMessage());
				io.printStackTrace();
				System.exit(1);
			}

			try {
				arguments = parse(command);
				Command cmd = Command.fromString((String)arguments.elementAt(0));
				if(!flag){
					connectServer();
				}

				this.connectServer();
				execute(cmd,arguments);

				String reply = null;


//				try {
//					execute(cmd, arguments);
//				}
//				catch (ConnectException e) {
//					connectServer();
//					execute(cmd, arguments);
//				}
			}
			catch (IllegalArgumentException|ServerException e) {
				System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0m" + e.getLocalizedMessage());
			}
			catch (ConnectException|UnmarshalException e) {
				System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mConnection to server lost");
			}
			catch (Exception e) {
				System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mUncaught exception");
				e.printStackTrace();
			}
		}
	}





	public void execute(Command cmd, Vector<String> arguments) throws RemoteException, NumberFormatException
	{
		switch (cmd)
		{
			case Help:
			{
				if (arguments.size() == 1) {
					System.out.println(Command.description());
				} else if (arguments.size() == 2) {
					Command l_cmd = Command.fromString((String)arguments.elementAt(1));
					System.out.println(l_cmd.toString());
				} else {
					System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
				}
				break;
			}
			case AddFlight: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));
				System.out.println("-Flight Seats: " + arguments.elementAt(3));
				System.out.println("-Flight Price: " + arguments.elementAt(4));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);

				break;
			}
			case AddCars: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));
				System.out.println("-Number of Cars: " + arguments.elementAt(3));
				System.out.println("-Car Price: " + arguments.elementAt(4));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case AddRooms: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));
				System.out.println("-Number of Rooms: " + arguments.elementAt(3));
				System.out.println("-Room Price: " + arguments.elementAt(4));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);

				break;
			}
			case AddCustomer: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case AddCustomerID: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case DeleteFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case DeleteCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case DeleteRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case DeleteCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;               
			}
			case QueryFlightPrice: {
				checkArgumentsCount(3, arguments.size());
				
				System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryCarsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case QueryRoomsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case ReserveFlight: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Flight Number: " + arguments.elementAt(3));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);

				break;
			}
			case ReserveCar: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Car Location: " + arguments.elementAt(3));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case ReserveRoom: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Room Location: " + arguments.elementAt(3));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case Bundle: {
				if (arguments.size() < 7) {
					System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
					break;
				}

				System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				for (int i = 0; i < arguments.size() - 6; ++i)
				{
					System.out.println("-Flight Number: " + arguments.elementAt(3+i));
				}
				System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size()-3));
				System.out.println("-Book Car: " + arguments.elementAt(arguments.size()-2));
				System.out.println("-Book Room: " + arguments.elementAt(arguments.size()-1));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));
				Vector<String> flightNumbers = new Vector<String>();
				for (int i = 0; i < arguments.size() - 6; ++i)
				{
					flightNumbers.addElement(arguments.elementAt(3+i));
				}
				String location = arguments.elementAt(arguments.size()-3);
				boolean car = toBoolean(arguments.elementAt(arguments.size()-2));
				boolean room = toBoolean(arguments.elementAt(arguments.size()-1));

				String forward = arguments.elementAt(0);

				for(int i = 1; i < arguments.size();i++){
					forward = forward+",";
					forward = forward + arguments.elementAt(i);
				}

				this.request(forward);
				break;
			}
			case Quit:
				checkArgumentsCount(1, arguments.size());

				System.out.println("Quitting client");
				System.exit(0);
		}
	}

	public static Vector<String> parse(String command)
	{
		Vector<String> arguments = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(command,",");
		String argument = "";
		while (tokenizer.hasMoreTokens())
		{
			argument = tokenizer.nextToken();
			argument = argument.trim();
			arguments.add(argument);
		}
		return arguments;
	}

	public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException
	{
		if (expected != actual)
		{
			throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received " + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
		}
	}

	public static int toInt(String string) throws NumberFormatException
	{
		return (Integer.valueOf(string)).intValue();
	}

	public static boolean toBoolean(String string)// throws Exception
	{
		return (Boolean.valueOf(string)).booleanValue();
	}
}
