@echo off
call mvn clean package
call docker build -t fr.grin/tpbanque .
call docker rm -f tpbanque
call docker run -d -p 9080:9080 -p 9443:9443 --name tpbanque fr.grin/tpbanque