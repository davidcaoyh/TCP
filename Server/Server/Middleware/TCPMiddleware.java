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

    private static String car_host = "localhost";
    private static String room_host = "localhost";
    private static String flight_host = "localhost";

    private static int car_port = 1083;
    private static int room_port = 1081;
    private static int flight_port = 1082;

    private static int middleware_port = 1080;

    ServerSocket serverSocket;
    protected RMHashMap m_data = new RMHashMap();


//    public void connect_car(){
//        try{
//            socket = new Socket(server_host,server_port);
//            from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            to_server = new PrintWriter(socket.getOutputStream(), true);
//            flag = true;
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }



    public static void main(String[] args){
        System.out.println("started");
        TCPMiddleware middleware = new TCPMiddleware();
        try{
//            middleware.carSocket = new Socket(car_host,car_port);

//            middleware.flightSocket =new Socket(flight_host, flight_port);
//            middleware.roomSocket = new Socket(room_host,room_port);
            middleware.serverSocket = new ServerSocket(middleware_port);
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
                middleware.carSocket = new Socket(car_host,car_port);
                middleware.roomSocket = new Socket(room_host,room_port);
                middleware.flightSocket =new Socket(flight_host, flight_port);


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

                    if (servers.contains(0)){
                        System.out.println("go to car server");
                        toCar.println(msg);
                        String m;
                        while((m= fromCar.readLine())!= null) {
                            System.out.println(m);
                            car_reply = car_reply +"\n"+m;
                        }
                    }
//
                    if(servers.contains(1)){
                        System.out.println("go to room server");
                        toRoom.println(msg);
                        String m;
                        while((m=fromRoom.readLine())!=null){
                            System.out.println(m);
                            room_reply = room_reply+ "\n" + m;
                        }
                    }
//
                    if(servers.contains(2)){
                        System.out.println("go to flight server");
                        toFlight.println(msg);
                        String m;
                        while((m=fromFlight.readLine())!=null){
                            System.out.println(m);
                            flight_reply = flight_reply +"\n" +m;
                        }
                    }

//                if(servers.contains(3)){
//                    String[] cmds = msg.split(",");
//                    if(cmds[0].toLowerCase().contains("addcustomer")){
//                        int cid =  this.middleware.newCustomer(Integer.parseInt(cmds[1]));
//                        out_client.println("The customer id is: "+ String.valueOf(cid));
//                    }
//                    if(cmds[0].toLowerCase().contains("addcustomerid")){
//                        boolean exist = this.middleware.newCustomer(Integer.parseInt(cmds[1]), Integer.parseInt(cmds[2]));
//                        if(exist){
//                            out_client.println("Customer created");
//                        }else{
//                            out_client.println("Customer already existed");
//                        }
//                    }
//                    if(cmds[0].toLowerCase().contains("querycustomer")){
//                        String res = middleware.queryCustomerInfo(Integer.parseInt(cmds[1]), Integer.parseInt(cmds[2]));
//                        if(res.equals("")){
//                            out_client.println("The customer does not exist");
//                        }else{
//                            out_client.println(res);
//                        }
//                    }
//
//                    if(cmds[0].toLowerCase().contains("deletecustomer")){
//
//                    }
//                if(servers.contains(3)){
//                    System.out.println("go to three servers");
//                    toCar.println(msg);
//                    toRoom.println(msg);
//
//                    String m1;
//                    String m2;
//                    String m3;
//                    while((m1=fromCar))
//                }
//                }

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
                ret.add(0);
                ret.add(1);
                ret.add(2);
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
