#! /bin/bash

# A bash scripts which builds and runs the server.

# Build the code.
mvn package

echo '--------------- Running ------------------'

# Move to the right directory, and run it.
cd target/classes
java com.steve.hdc.Server
cd ../..
