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
import idv.jhuang78.simplerest.RestException.InvalidIdException;
import idv.jhuang78.simplerest.RestException.ItemAlreadyExistException;
import idv.jhuang78.simplerest.RestException.ItemNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.spi.NotImplementedYetException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Provider 
@Path("/rest")
public class Rest implements ExceptionMapper<RestException> {
	private static final Logger log = LogManager.getLogger(Rest.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static TypeReference<List<Item>> listItemType = new TypeReference<List<Item>>() {};
	
	
	//================================================================================
	//	REST Services
	//================================================================================
	
	@GET @Path("/")
	public Response __(@Context HttpServletRequest req) {
		try {
			request(req, null);
			return response(Status.SEE_OTHER, TEXT_PLAIN_TYPE, null, "/rest/status");
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
	}
	
	@GET @Path("/status")
	public Response status(@Context HttpServletRequest req, 
			@Context Database db) {
		
		try {
			request(req, null);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SimpleRest\n");
			for(String collectionName : db.keySet()) {
				sb.append("\t" + collectionName + ": " + db.get(collectionName).size() + "\n");
			}
			
			return response(OK, TEXT_PLAIN_TYPE, null, "%s\n", sb.toString());
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	/**
	 * Adds a new item into the collection. Generates a unique ID for the item and returns it 
	 * in the response. Also creates the collection if it did not exist previously.
	 */
	@POST @Path("/{collection}")
	public Response create(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			String itemJson) {
		
		try {
			request(req, itemJson);
			
			boolean single = itemJson.trim().startsWith("{");
			
			List<Item> items = single ?
					Arrays.asList(mapper.readValue(itemJson, Item.class)) :
					mapper.readValue(itemJson, listItemType);
					
			Collection collection = db.getCollection(collectionName, true);
			
			List<Integer> ids = new ArrayList<>();
			for(Item item : items) {
				int id = collection.add(item);
				ids.add(id);
			}			
			db.commit();
			
			StringBuffer buf = new StringBuffer();
			for(int id : ids) {
				buf.append(id + ",");
			}
			
			
			
			
			return response(CREATED, TEXT_PLAIN_TYPE, null, "%s", buf.substring(0, buf.length() - 1));
					
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	/**
	 * Adds a new item with the specified ID into the collection. Returns error if an item
	 * with the same ID already exists.
	 */
	@POST @Path("/{collection}/{id}")
	public Response create(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id,
			String itemJson) {
		
		try {
			request(req, null);
			throw new NotImplementedYetException("POST /rest/{collection}/{id}");
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	@GET @Path("/{collection}")
	public Response read(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName) {
		
		try {
			request(req, null);
			
			Collection collection = db.getCollection(collectionName, false);
			List<Item> items = new ArrayList<>(collection.values());
			String itemsJson = mapper.writeValueAsString(items);
			
			return response(OK, APPLICATION_JSON_TYPE, null, "%s\n", itemsJson);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	@GET @Path("/{collection}/{id}")
	public Response read(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id) {
		
		try {
			request(req, null);
			
			Collection collection = db.getCollection(collectionName, false);
			if(!collection.containsKey(id))
				throw new ItemNotFoundException(String.format("%s[%d]", collectionName, id));
			Item item = collection.get(id);
			String itemJson = mapper.writeValueAsString(item);
			
			return response(OK, APPLICATION_JSON_TYPE, null, "%s\n", itemJson);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
		
	}
	
	@PUT @Path("/{collection}")
	public Response update(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			@QueryParam("patch") @DefaultValue("true") boolean patch,	// TODO implement
			String itemsJson) {
		
		try {
			request(req, itemsJson);
			
			List<Item> newItems = mapper.readValue(itemsJson, listItemType);
			for(Item item : newItems) 
				if(item.id() < 0)
					throw new InvalidIdException("%s", mapper.writeValueAsString(item));
			
			Collection collection = db.getCollection(collectionName, true);
			collection.clear();
			
			for(Item item : newItems) {
				int id = item.id();
				collection.put(id, item);
			}
			db.commit();
			
			return response(NO_CONTENT, null, null, null);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	@PUT @Path("/{collection}/{id}")
	public Response update(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id, 
			@QueryParam("patch") @DefaultValue("true") boolean patch,
			String itemJson) {
		
		try {
			request(req, itemJson);
			
			Collection collection = db.getCollection(collectionName, true);
			if(!collection.containsKey(id))
				throw new ItemNotFoundException("%s[%d]", collectionName, id);
			Item item = collection.get(id);
			Item newItem = (Item) mapper.readValue(itemJson, Item.class);
			
			if(!patch) {
				item.clear();
			}
			item.putAll(newItem);
			
			collection.add(id, item);
			db.commit();
			
			return response(NO_CONTENT, null, null, null);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	@DELETE @Path("/{collection}")
	public Response delete(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName) {
		
		try {
			request(req, null);
			
			db.remove(collectionName);
			db.commit();
			
			return response(NO_CONTENT, null, null, null);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
		
	}
	
	@DELETE @Path("/{collection}/{id}")
	public Response delete(@Context HttpServletRequest req,
			@Context Database db,
			@PathParam("collection") String collectionName,
			@PathParam("id") int id) throws Exception {
		
		try {
			request(req, null);
			
			Collection collection = db.getCollection(collectionName, false);
			if(!collection.containsKey(id))
				throw new ItemNotFoundException("%s[%d]", collectionName, id);
			collection.remove(id);
			db.commit();
			
			return response(NO_CONTENT, null, null, null);
			
		} catch(Exception e) {
			throw new RestException(e, req, "");
		}
	}
	
	
	//================================================================================
	//	Helpers to Handle Request and Response
	//================================================================================
	private static final int LOG_BODY_LENGTH = 128;
	private void request(HttpServletRequest req, String body) {
		String queryStr = (req.getQueryString() == null) ? "" : "?" + req.getQueryString();
		
		if(body != null) {
			body = body.replaceAll("[(^\\s+)|\t\n]+", "");
			body = (body.length() > LOG_BODY_LENGTH) ? 
					body.substring(0, LOG_BODY_LENGTH) + "..." : 
					body;
		}
		
		log.info("HTTP request {} {}{} {}", req.getMethod(), req.getPathInfo(), queryStr, body);
	}
	
	private Response response(Status status, MediaType type, Throwable error, String format, Object... args) {
		
		String ansiBegin = "";
		String ansiReset = "\u001B[0m";
		switch(status.getFamily()) {
		case INFORMATIONAL: case SUCCESSFUL: 
			ansiBegin = "\u001B[32m"; break;
			
		case REDIRECTION: case OTHER:
			ansiBegin = "\u001B[33m"; break;
			
		case CLIENT_ERROR: case SERVER_ERROR: 
			ansiBegin = "\u001B[31m"; break;
		}
		log.info("HTTP response {}{}{} {}", ansiBegin, status.getStatusCode(), ansiReset, status.getReasonPhrase());
		
		
		ResponseBuilder rb = Response.status(status);
		if(type != null)
			rb.type(type);
		
		if(format != null) {
			String message = String.format(format, args);
			if(error != null) {
				StringWriter writer = new StringWriter();
				error.printStackTrace(new PrintWriter(writer));
				message += "\n" + writer.toString();
			}
			
			if(status.getFamily() == Status.Family.REDIRECTION)
				rb.header("Location", message);
			else
				rb.entity(message);
		}
			
		
		return rb.build();
		
		
		
				
	}
	
	//================================================================================
	//	Error Handler
	//================================================================================

	/**
	 * Catches all errors and returns proper response.
	 */
	public Response toResponse(RestException e) {
		
		Exception cause = (Exception) e.getCause();
		
    	if(cause instanceof NotImplementedYetException) {
    		return response(NOT_IMPLEMENTED, TEXT_PLAIN_TYPE, null, "Method not implemented yet: %s.", cause.getMessage());
    	
    	} else if(cause instanceof JsonParseException || cause instanceof JsonMappingException) {
    		return response(Status.BAD_REQUEST, TEXT_PLAIN_TYPE, null, "Bad format for request JSON: %s.", cause.getMessage());
    		
    	} else if(cause instanceof ItemAlreadyExistException) {
    		return response(Status.CONFLICT, TEXT_PLAIN_TYPE, null, "Item %s already exists.", cause.getMessage());
    		
    	} else if(cause instanceof ItemNotFoundException) {
    		return response(Status.NOT_FOUND, TEXT_PLAIN_TYPE, null, "Item %s does not exist.", cause.getMessage());
    	
    	} else if(cause instanceof InvalidIdException) {
    		return response(Status.BAD_REQUEST, TEXT_PLAIN_TYPE, null, "Item has invalid id: %s.", cause.getMessage());
    		
    	} else if(cause instanceof IOException) {
    		log.error("Failed to write database to file.", cause);
    		return response(INTERNAL_SERVER_ERROR, TEXT_PLAIN_TYPE, null, "Server failed to persist database changes. Please check server log for more information.");
    		
    		
    	} else {
    		log.error("Exception", e);
    		return response(INTERNAL_SERVER_ERROR, TEXT_PLAIN_TYPE, cause, "Internal server error.");
    	}
    	
    	
    }
}
