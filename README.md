# SimpleRest #
A simple [REST](http://en.wikipedia.org/wiki/Representational_state_transfer) service built in Java with [Jetty](http://eclipse.org/jetty/), [Resteasy](http://resteasy.jboss.org/), and Java native classes.

## Purpose ##
This project is mainly for myself (and anyone who needs it) as a skeleton to setting up a REST service. If someone wants to use a simple database with REST API this can come in handy too.


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

Items are given and returned in JSON format.

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