#Usage: ./run_server.sh <server_host> <car_port> <room_port> <flight_port>

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.RMIResourceManager $1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.Middleware.TCPMiddleware $1 $2 $3 $4 $5 $6 $7


