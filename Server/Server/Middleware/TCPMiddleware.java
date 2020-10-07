package Server.Middleware;


import Server.Common.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;

public class TCPMiddleware {
    Socket carSocket;
    Socket roomSocket;
    Socket flightSocket;

    private String car_host = "localhost";
    private String room_host = "localhost";
    private String flight_host = "localhost";

    private int car_port = 1083;
    private int room_port = 1081;
    private int flight_port = 1082;

    private int middleware_port = 1080;

    ServerSocket serverSocket;
    protected RMHashMap m_data = new RMHashMap();

    public TCPMiddleware(int middleware_port, int carP, int roomP,int flightP,String carH, String roomH,String flightH){
        this.middleware_port = middleware_port;
        this.car_host = carH;
        this.room_host = roomH;
        this.flight_host = flightH;
        this.car_port = carP;
        this.room_port = roomP;
        this.flight_port = flightP;
    }
    public TCPMiddleware(){

    }


    public static void main(String[] args){
        System.out.println("started");
        TCPMiddleware middleware;
        if(args.length > 7){
            System.out.println("Error: Invalid Input");
            return;
        }
        else if(args.length > 0){
            for(String temp:args){
                System.out.println(temp);
            }
            middleware = new TCPMiddleware(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),Integer.parseInt(args[3]),args[4],args[5],args[6]);
        }
        else{
            middleware = new TCPMiddleware();
        }
        try{
            middleware.serverSocket = new ServerSocket(middleware.middleware_port);
            System.out.println(123);
            while(true){
                System.out.println(456);
                Socket socket = middleware.serverSocket.accept();
                new SocketThread(socket,middleware).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static class SocketThread extends Thread{
        Socket socket;
        TCPMiddleware middleware;
        public SocketThread(Socket client, TCPMiddleware middleware){
            socket = client;
            this.middleware = middleware;
        }

        public void run(){
            try {
                System.out.println(middleware.car_host + ":" + middleware.car_port);
                System.out.println(middleware.room_host + ":" + middleware.room_port);
                System.out.println(middleware.flight_host + ":" + middleware.flight_port);
                middleware.carSocket = new Socket(middleware.car_host,middleware.car_port);
                middleware.roomSocket = new Socket(middleware.room_host,middleware.room_port);
                middleware.flightSocket =new Socket(middleware.flight_host, middleware.flight_port);


                BufferedReader in_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out_client = new PrintWriter(socket.getOutputStream(),true);

                BufferedReader fromCar = new BufferedReader(new InputStreamReader(middleware.carSocket.getInputStream()));
                PrintWriter toCar = new PrintWriter(middleware.carSocket.getOutputStream(), true);

                BufferedReader fromRoom = new BufferedReader(new InputStreamReader(middleware.roomSocket.getInputStream()));
                PrintWriter toRoom = new PrintWriter(middleware.roomSocket.getOutputStream(), true);
//
                BufferedReader fromFlight = new BufferedReader(new InputStreamReader(middleware.flightSocket.getInputStream()));
                PrintWriter toFlight = new PrintWriter(middleware.flightSocket.getOutputStream(), true);

                String msg = null;
                msg=in_client.readLine();

                System.out.println(msg);
                ArrayList<Integer> servers = allocate(msg);
                String car_reply = "";
                String room_reply = "";
                String flight_reply ="";
                String customer_reply = "";
                String bundle_reply = "";

                    if (servers.contains(0)){
                        System.out.println("go to car server");
                        toCar.println(msg);
                        String m;
                        while((m= fromCar.readLine())!= null) {
                            System.out.println("Reply From CarManager: " + m);
                            car_reply = car_reply +"\n"+m;
                        }
                    }
//
                    if(servers.contains(1)){
                        System.out.println("go to room server");
                        toRoom.println(msg);
                        String m;
                        while((m=fromRoom.readLine())!=null){
                            System.out.println("Reply From RoomManager: " + m);
                            room_reply = room_reply+ "\n" + m;
                        }
                    }
//
                    if(servers.contains(2)){
                        System.out.println("go to flight server");
                        toFlight.println(msg);
                        String m;
                        while((m=fromFlight.readLine())!=null){
                            System.out.println("Reply From FlightManager: " + m);
                            flight_reply = flight_reply +"\n" +m;
                        }
                    }

                if(servers.contains(3)){
                    System.out.println("I'm here");
                    String[] cmds = msg.split(",");
                    if(cmds[0].equals("AddCustomer")){
                        int cid =  this.middleware.newCustomer(Integer.parseInt(cmds[1]));
                        String parsedCmd = "AddCustomerId,"+cmds[1]+","+cid;
                        toCar.println(parsedCmd);
                        toFlight.println(parsedCmd);
                        toRoom.println(parsedCmd);
                        customer_reply =  "The customer id is: "+ String.valueOf(cid);
                    }
                    if(cmds[0].equals("AddCustomerId")){
                        toCar.println(msg);
                        toFlight.println(msg);
                        toRoom.println(msg);
                        customer_reply = customer_reply + fromCar.readLine();

                    }
                    if(cmds[0].toLowerCase().equals("querycustomer")){
                        toCar.println(msg);
                        toFlight.println(msg);
                        toRoom.println(msg);
                        String m;
                        while((m=fromFlight.readLine())!=null){
                            System.out.println(m);
                            customer_reply = customer_reply +"\n" +m;
                        }

                        while((m=fromCar.readLine())!=null){
                            System.out.println(m);
                            if(m.contains("$")) {
                                customer_reply = customer_reply + "\n" + m;
                            }
                        }

                        while((m=fromRoom.readLine())!=null) {
                            System.out.println(m);
                            if (m.contains("$")) {
                                customer_reply = customer_reply + "\n" + m;
                            }
                        }
                    }

                    if(cmds[0].toLowerCase().contains("deletecustomer")){
                        this.middleware.deleteCustomer(Integer.parseInt(cmds[1]),Integer.parseInt(cmds[2]));
                        toCar.println(msg);
                        toFlight.println(msg);
                        toRoom.println(msg);
                        String m;
                        while((m=fromFlight.readLine())!=null){
                            System.out.println(m);
                            customer_reply = customer_reply +"\n" +m;
                        }

//                        while((m=fromCar.readLine())!=null){
//                            System.out.println(m);
//                            customer_reply = customer_reply +"\n" +m;
//                        }
//
//                        while((m=fromRoom.readLine())!=null){
//                            System.out.println(m);
//                            customer_reply = customer_reply +"\n" +m;
//                        }


                    }

                }
                if(servers.contains(4)){
                    String[] cmds = msg.split(",");
                    int n = cmds.length;
                    String location = cmds[n-3];
                    int car = Integer.parseInt(cmds[n-2]);
                    int room = Integer.parseInt(cmds[n-1]);
                    String parsedCmd = cmds[0];
                    for(int i = 1; i < n-3;i++){
                        parsedCmd = parsedCmd +","+ cmds[i];
                    }
                    toFlight.println(parsedCmd);
                    String m;
                    while((m=fromFlight.readLine())!=null){
                        System.out.println(m);
                        bundle_reply = bundle_reply +"\n" +m;
                    }

                    if(car == 1){
                        String car_cmd = "ReserveCar,"+cmds[1]+","+cmds[2]+","+cmds[n-3];
                        toCar.println(car_cmd);
                        while((m=fromCar.readLine())!=null){
                            System.out.println(m);
                            bundle_reply = bundle_reply +"\n" +m;
                        }
                    }

                    if(room == 1){
                        String room_cmd = "ReserveRoom,"+cmds[1]+","+cmds[2]+","+cmds[n-3];
                        toRoom.println(room_cmd);
                        while((m=fromRoom.readLine())!=null){
                            System.out.println(m);
                            bundle_reply = bundle_reply +"\n" +m;
                        }

                    }


                }
                if(!bundle_reply.equals("")){
                    out_client.println(bundle_reply);
                }

                if(!customer_reply.equals("")){
                    out_client.println(customer_reply);
                }

                if(!flight_reply.equals("")){
                    out_client.println(flight_reply);
                }
                if(!room_reply.equals("")){
                    out_client.println(room_reply);
                }
                if(!car_reply.equals("")){
                    out_client.println(car_reply);
                }
                System.out.println("end of a session");

                in_client.close();
                out_client.close();
                this.socket.close();
                this.middleware.flightSocket.close();
                this.middleware.roomSocket.close();
                this.middleware.carSocket.close();

            }catch (IOException e){
                e.printStackTrace();
            }catch(Exception e1){
                e1.printStackTrace();
                System.out.println("Client quit the session");
            }
        }

        public static ArrayList<Integer> allocate(String command){
            ArrayList<Integer> ret = new ArrayList<>();
            if(!command.contains(",")){
                return ret;
            }

            String[] comm = command.split(",");
            if(comm[0].toLowerCase().contains("car")){
                ret.add(0);
            }
            if(comm[0].toLowerCase().contains("room")){
                ret.add(1);
            }
            if(comm[0].toLowerCase().contains("flight")){
                ret.add(2);
            }
            if(comm[0].toLowerCase().contains("customer")){
                ret.add(3);

            }
            if(comm[0].toLowerCase().contains("bundle")){
                ret.add(4);
            }

            return ret;
        }

    }
    public int newCustomer(int xid) throws RemoteException
    {
        Trace.info("RM::newCustomer(" + xid + ") called");
        // Generate a globally unique ID for the new customer
        int cid = Integer.parseInt(String.valueOf(xid) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer customer = new Customer(cid);
        writeData(xid, customer.getKey(), customer);
        Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
        return cid;
    }

    public boolean newCustomer(int xid, int customerID) throws RemoteException
    {
        Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            customer = new Customer(customerID);
            writeData(xid, customer.getKey(), customer);
            Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") created a new customer");
            return true;
        }
        else
        {
            Trace.info("INFO: RM::newCustomer(" + xid + ", " + customerID + ") failed--customer already exists");
            return false;
        }
    }
    protected void writeData(int xid, String key, RMItem value)
    {
        synchronized(m_data) {
            m_data.put(key, value);
        }
    }
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
    public boolean deleteCustomer(int xid, int customerID) throws RemoteException
    {
        Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
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
            removeData(xid, customer.getKey());
            Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
            return true;
        }
    }

    protected void removeData(int xid, String key)
    {
        synchronized(m_data) {
            m_data.remove(key);
        }
    }

    public String queryCustomerInfo(int xid, int customerID) throws RemoteException
    {
        Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
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
