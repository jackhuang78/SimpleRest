package idv.jhuang78.simplerest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_IMPLEMENTED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import idv.jhuang78.simplerest.Database.Collection;
import idv.jhuang78.simplerest.Database.Item;
import idv.jhuang78.simplerest.exception.InvalidIdException;
import idv.jhuang78.simplerest.exception.ItemAlreadyExistException;
import idv.jhuang78.simplerest.exception.ItemNotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.spi.NotImplementedYetException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 *	POST /<collection> - add an item to a collection
 *	GET /<collection>/<id> - get a specific item from a collection
 *	GET /<collection> - get all items from a collection
 *	PUT /<collection>/<id> - update a specific item in a collection
 *	DELETE /<collection>/<id> - delete a specific item from a collection
 *	DELETE /<collection> - delete all collections in a collection
 *
 */
@Provider 
@Path("/rest")
public class Rest implements ExceptionMapper<Exception> {
	private static final Logger log = LogManager.getLogger(Rest.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static TypeReference<List<Item>> listItemType = new TypeReference<List<Item>>() {};
	
	
	
	@GET @Path("/")
	public Response status(@Context Database db) {
		log.info("HTTP request GET /rest.");
		
		StringBuffer sb = new StringBuffer();
		sb.append("SimpleRest\n");
		for(String collectionName : db.keySet()) {
			sb.append("\t" + collectionName + ": " + db.get(collectionName).size() + "\n");
		}
		
		return response(OK, TEXT_PLAIN_TYPE, null, "%s\n", sb.toString());
	}
	
	/**
	 * Adds a new item into the collection. Generates a unique ID for the item and returns it 
	 * in the response. Also creates the collection if it did not exist previously.
	 */
	@POST @Path("/{collection}")
	public Response create(
			@Context Database db,
			@PathParam("collection") String collectionName,
			String itemJson
			) throws Exception {
		
		log.info("HTTP request POST /rest/{}.", collectionName);
		
		Collection collection = db.getCollection(collectionName, true);
		Item item = mapper.readValue(itemJson, Item.class);
		int id = collection.add(item);
		
		return response(CREATED, TEXT_PLAIN_TYPE, null, "%d\n", id);
	}
	
	/**
	 * Adds a new item with the specified ID into the collection. Returns error if an item
	 * with the same ID already exists.
	 */
	@POST @Path("/{collection}/{id}")
	public Response create(
			@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id,
			String itemJson
			) throws Exception {
		
		throw new NotImplementedYetException("POST /rest/{collection}/{id}");
	}
	
	@GET @Path("/{collection}")
	public Response read(@Context Database db,
			@PathParam("collection") String collectionName) throws Exception {
		log.info("HTTP request GET /rest/{}.", collectionName);
		
		Collection collection = db.getCollection(collectionName, false);
		List<Item> items = new ArrayList<>(collection.values());
		String itemsJson = mapper.writeValueAsString(items);
		
		return response(OK, APPLICATION_JSON_TYPE, null, "%s\n", itemsJson);
	}
	
	@GET @Path("/{collection}/{id}")
	public Response read(@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id) throws Exception {
		log.info("HTTP request GET /rest/{}/{}.", collectionName);
		
		Collection collection = db.getCollection(collectionName, false);
		if(!collection.containsKey(id))
			throw new ItemNotFoundException(String.format("%s[%d]", collectionName, id));
		
		Item item = collection.get(id);
		String itemJson = mapper.writeValueAsString(item);
		
		return response(OK, APPLICATION_JSON_TYPE, null, "%s\n", itemJson);
		
	}
	
	@PUT @Path("/{collection}")
	public Response update(@Context Database db,
			@PathParam("collection") String collectionName,
			String itemsJson) throws Exception {
		throw new NotImplementedYetException("PUT /rest/{collection}");
	}
	
	@PUT @Path("/{collection}/{id}")
	public Response update(@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id, 
			@QueryParam("partial") @DefaultValue("false") boolean partial,	//TODO implement partial update
			String itemJson) throws Exception {
		
		log.info("HTTP request PUT /rest/{}/{}.", collectionName, id);
		
		Collection collection = db.getCollection(collectionName, true);
		if(!collection.containsKey(id))
			throw new ItemNotFoundException("%s[%d]", collectionName, id);
		
		Item item = (Item) mapper.readValue(itemJson, Item.class);
		collection.add(id, item);
		
		return response(NO_CONTENT, TEXT_PLAIN_TYPE, null, "");
	}
	
	@DELETE @Path("/{collection}")
	public Response delete(@Context Database db,
			@PathParam("collection") String collectionName) {
		log.info("HTTP request DELETE /rest/{}.", collectionName);
		db.remove(collectionName);
		
		return response(NO_CONTENT, TEXT_PLAIN_TYPE, null, "");
		
	}
	
	@DELETE @Path("/{collection}/{id}")
	public Response delete(@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id) {
		log.info("HTTP request DELETE /rest/{}/{}.", collectionName, id);
		
		Collection collection = db.getCollection(collectionName, false);
		
		if(!collection.containsKey(id))
			throw new ItemNotFoundException("%s[%d]", collectionName, id);
		
		collection.remove(id);
		return response(NO_CONTENT, TEXT_PLAIN_TYPE, null, "");
	}
	
	
	private Response response(Status status, MediaType type, Throwable error, String format, Object... args) {
		
		String ansiBegin = "";
		String ansiReset = "\u001B[0m";
		switch(status.getFamily()) {
		case INFORMATIONAL: case SUCCESSFUL: 
			ansiBegin = "\u001B[32m"; break;
			
		case REDIRECTION: case OTHER:
			ansiBegin = "\u001B[36m"; break;
			
		case CLIENT_ERROR: case SERVER_ERROR: 
			ansiBegin = "\u001B[31m"; break;
		}
		
		log.info("HTTP response {}{}{} {}.", ansiBegin, status.getStatusCode(), ansiReset, status.getReasonPhrase());
		
		String message = String.format(format, args);
		if(error != null) {
			StringWriter writer = new StringWriter();
			error.printStackTrace(new PrintWriter(writer));
			message += "\n" + writer.toString();
		}
		
		return Response.status(status).type(type).entity(message).build();		
	}
	
	//================================================================================
	//	Error Handling
	//================================================================================

	/**
	 * Catches all errors and returns proper response.
	 */
	public Response toResponse(Exception e) {
		

    	if(e instanceof WebApplicationException) {
    		WebApplicationException webEx = (WebApplicationException) e;
    		Status status = Status.fromStatusCode(webEx.getResponse().getStatus());
    		Pattern p;
    		Matcher m;
    		
    		switch(status) {
    		case NOT_FOUND:
    			p = Pattern.compile("javax\\.ws\\.rs\\.NotFoundException\\: Could not find resource for full path: ([\\w+\\:/]*)");
    			m = p.matcher(webEx.toString());
    			if(m.matches())
    				return response(status, TEXT_PLAIN_TYPE, null, "Invalid URL %s.\n", m.group(1));

    		case METHOD_NOT_ALLOWED:
    			p = Pattern.compile("javax\\.ws\\.rs\\.NotAllowedException\\: No resource method found for (\\w+)\\, return 405 with Allow header");
    			m = p.matcher(webEx.toString());
    			if(m.matches())
    				return response(status, TEXT_PLAIN_TYPE, null, "Method %s not allowed on this URL.\n", m.group(1));
    			
    		default:
    			log.warn("Exception", e);
    			return response(status, TEXT_PLAIN_TYPE, webEx, "");
    			
    		}
    		
    	} else if(e instanceof NotImplementedYetException) {
    		return response(NOT_IMPLEMENTED, TEXT_PLAIN_TYPE, null, "Method not implemented yet: %s.\n", e.getMessage());
    	
    	} else if(e instanceof JsonParseException || e instanceof JsonMappingException) {
    		return response(Status.BAD_REQUEST, TEXT_PLAIN_TYPE, null, "Bad format for request JSON: %s.\n", e.getMessage());
    		
    	} else if(e instanceof ItemAlreadyExistException) {
    		return response(Status.CONFLICT, TEXT_PLAIN_TYPE, null, "Item %s already exists.\n", e.getMessage());
    		
    	} else if(e instanceof ItemNotFoundException) {
    		return response(Status.NOT_FOUND, TEXT_PLAIN_TYPE, null, "Item %s does not exist.\n", e.getMessage());
    	
    	} else if(e instanceof InvalidIdException) {
    		return response(Status.BAD_REQUEST, TEXT_PLAIN_TYPE, null, "Item has invalid id: %s.\n", e.getMessage());
    		
    	} else {
    		log.warn("Exception", e);
    		return response(INTERNAL_SERVER_ERROR, TEXT_PLAIN_TYPE, e, "Internal server error.");
    	}
    	
    	
    }
}
