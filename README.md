# SimpleRest #
A simple [REST](http://en.wikipedia.org/wiki/Representational_state_transfer) service built in Java with [Jetty](http://eclipse.org/jetty/), [Resteasy](http://resteasy.jboss.org/), and [Java Collections](http://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html).

## Purpose ##
I just thought it would be cool to build a minimal yet full-functioning REST service. It uses embedded Jetty as the servlet engine, a data store built out of native java classes, and persistence through file serialization. 

If you application plans to access some sort of database with REST API but you don't want to go through the trouble setting it yet, you can use this service as a proxy instead.

## Setup ##
This project use [Gradle](https://gradle.org/) to manage dependencies and build process. Please install Gradle first.

Either download the project as a zip file and unzip it, or clone the repository to your machine. Navigate to the root directory of the project, then execute
     
    > gradle run

Gradle will download all needed dependencies, build the project, then run it. An HTTP server will be running on localhost:9090. To verify that the server is running, execute

    > curl -XGET localhost:9090/rest
    SimpleRestDb



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

## Example ##
Create a few student records.

    > curl -XPOST -d "{\"name\": \"John\"}" localhost:9090/rest/students
    1

    > curl -XPOST -d "{\"name\": \"Adam\"}" localhost:9090/rest/students
    2

Read records

    > curl -XGET localhost:9090/rest/students/1
    {"_id":1,"name":"John"}

    > curl -XGET localhost:9090/rest/students/2
    {"_id":2,"name":"Adam"}

    > curl -XGET localhost:9090/rest/students
    [{"_id":1,"name":"John"},{"_id":2,"name":"Adam"}]

Update records

    > curl -XPUT -d "{\"name\": \"Adam Smith\", \"age\": 238}" localhost:9090/rest/students/2

    > curl -XGET localhost:9090/rest/students/2
    {"_id":2,"name":"Adam Smith","age":238}

Delete records

    > curl -XDELETE localhost:9090/rest/students/1
    
    > curl -XGET localhost:9090/rest/students/1
    Item students[1] does not exist.

    > curl -XDELETE localhost:9090/rest/students

    > curl -XGET localhost:9090/rest/students
    []