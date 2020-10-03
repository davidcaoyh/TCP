all: java.policy compile-server-rmi

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;



compile-server-rmi:  Server/Common/*.java Server/Middleware/*.java
	javac   Server/Common/*.java Server/Middleware/*.java

clean:
	rm -f  Server/Common/*.class  Server/Middleware/*.java
	rm -f java.policy
