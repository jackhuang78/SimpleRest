package idv.jhuang78.simplerest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

@Provider
public class Jetty implements ExceptionMapper<WebApplicationException>{
    public static void main(String[] args) throws Exception {
    	WebAppContext context = new WebAppContext();
    	context.setDescriptor("./src/main/resources/web.xml");
    	context.setResourceBase("./src/main/resources");
    	context.setParentLoaderPriority(true);          
    	

    	Server server = new Server(9090);
    	server.setHandler(context);
    	server.start();
    	server.join();
    }

	@Override
	public Response toResponse(WebApplicationException e) {
		return Response.fromResponse(e.getResponse()).entity(e.getMessage().toString()).build();
				
	}
    
    
}