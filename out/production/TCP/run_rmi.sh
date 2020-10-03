#rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 1099 &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.Common.ResourceManager
