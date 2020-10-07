# Usage: ./run_client.sh <server_hostname> <server_port>

#java -Djava.security.policy=java.policy -cp ../Server/RMIInterface.jar:. Client.RMIClient $1 $2
java -Djava.security.policy=java.policy  -Djava.rmi.server.codebase=file:$(pwd)/ Client.Client $1 $2

