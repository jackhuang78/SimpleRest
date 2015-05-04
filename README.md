# SimpleRest #
A simple [REST](http://en.wikipedia.org/wiki/Representational_state_transfer) service built in Java with [Jetty](http://eclipse.org/jetty/), [Resteasy](http://resteasy.jboss.org/), and [Java Collections](http://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html).

## Setup ##
This project use [Gradle](https://gradle.org/) to manage dependencies and build process.

To build the project, execute at the root directory:
    
     gradle build

To run the project, execute at the root directory:
     
     gradle run

Then, an HTTP server will be running on localhost:8080.

## REST API ##

Method | URL Format              | Description
-------|-------------------------|-------------
HEAD   | /rest/<collection>/<id> | Test whether an item exists in a collection
POST   | /rest/<collection>      | Crean an item in a collection
GET    | /rest/<collection>/<id> | Read an item from a collection
GET    | /rest/<collection>      | Read all items in a collection
PUT    | /rest/<collection>/<id> | Update an item in a collection
DELETE | /rest/<collection>/<id> | Delete an item from a collection
DELETE | /rest/<collection>      | Delete all items in a collection