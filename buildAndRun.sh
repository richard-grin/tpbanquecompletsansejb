#!/bin/sh
mvn clean package && docker build -t fr.grin/tpbanque .
docker rm -f tpbanque || true && docker run -d -p 9080:9080 -p 9443:9443 --name tpbanque fr.grin/tpbanque