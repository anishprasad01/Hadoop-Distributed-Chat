cmake_minimum_required( VERSION 2.8 )

project( Hadoop-Distributed-Chat )

## Find the opencv library.
find_package( OpenCV REQUIRED )

## Setup the build directory.
set( EXECUTABLE_OUTPUT_PATH build )
add_executable( Hadoop-Distributed-Chat-Client ./src/Client.java )
add_executable( Hadoop-Distributed-Chat-Server ./src/Server.java )
