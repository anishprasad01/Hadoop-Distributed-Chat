#! /bin/bash

# A bash scripts which builds and runs the server.

# Build the code.
mvn package

echo '--------------- Running ------------------'

# Run the script
java -jar ./target/Hadoop-Distributed-Chat-1.0.jar
