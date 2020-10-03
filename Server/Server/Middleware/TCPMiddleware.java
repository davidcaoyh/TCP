package Server.Middleware;


import Server.Common.RMHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPMiddleware {
    Socket carSocket;
    Socket roomSocket;
    Socket flightSocket;

    private static String car_host = "localhost";
//    private static String room_host = "localhost";
//    private static String flight_host = "localhost";

    private static int car_port = 1090;
//    private static int room_port = 1091;
//    private static int flight_port = 1092;

    private static int middleware_port = 1097;

    ServerSocket serverSocket;
    protected RMHashMap m_data = new RMHashMap();



    public static void main(String[] args){
        System.out.println("started");
        TCPMiddleware middleware = new TCPMiddleware();
        try{
            middleware.carSocket = new Socket(car_host,car_port);

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
                BufferedReader in_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out_client = new PrintWriter(socket.getOutputStream(),true);

                BufferedReader fromCar = new BufferedReader(new InputStreamReader(middleware.carSocket.getInputStream()));
                PrintWriter toCar = new PrintWriter(middleware.carSocket.getOutputStream(), true);

//                BufferedReader fromRoom = new BufferedReader(new InputStreamReader(middleware.roomSocket.getInputStream()));
//                PrintWriter toRoom = new PrintWriter(middleware.roomSocket.getOutputStream(), true);
//
//                BufferedReader fromFlight = new BufferedReader(new InputStreamReader(middleware.flightSocket.getInputStream()));
//                PrintWriter toFlight = new PrintWriter(middleware.flightSocket.getOutputStream(), true);

                String msg = null;
                while((msg=in_client.readLine())!=null){

                    System.out.println(msg);
                    ArrayList<Integer> servers = allocate(msg);
                    if (servers.contains(0)){
                        System.out.println("go to car server");
                        toCar.println(msg);
                    }

//                    if(servers.contains(1)){
//                        toRoom.println(msg);
//                    }
//
//                    if(servers.contains(2)){
//                        toFLight.print(msg);
//                    }


                }

            }catch (IOException e){
                e.printStackTrace();
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
//            if(comm[0].toLowerCase().contains("customer")){
//                ret.add(3);
//            }
            return ret;
        }

    }

}
