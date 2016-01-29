Name: Manhong Ren 
Assignment: proj1 (Building web server)
Date: 1/28/2016

Description:
This is a multi-threaded web server that serves static web page resources copied from www.scu.edu. It spins up a server socket that listen on a port number, and it creates a new task whenever a new request comes in and let it run in the thread pool. Each task parses the socket using http protocol, load the file from local directory (If file is not found or read permission is denied, a 404 or 403 is returned.) and return 200 OK with appropriate headers and body. If the worker thread crashes, server try to return 500 back.

Submitted files:
  Readme.txt
  script.sh

  ClientTask.java
  ContentType.java
  FileLoader.java
  HttpRequest.java
  HttpStatusCode.java
  Server.java

Instructions to run program:
  1. Unzip project files
  2. cd to project root folder
  3. ./script.sh (this scrip compiles java files and starts the server)
