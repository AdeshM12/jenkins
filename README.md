sudo netstat -tulnp | grep 9090  -- To check running sesison in jenkins
sudo lsof -i :8080      --- To get active pid session to kill
sudo kill -9 PID   --- kill using PID
sudo systemctl stop jenkins- to stop all jenkin sessions

sudo java -jar /path/to/jenkins.war --httpPort=9090-- to open jenkin in port 9090




Part-5
mvn clean install-- all success build
mvn clean test
mvn clean comppile 