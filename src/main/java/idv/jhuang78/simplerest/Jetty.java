package idv.jhuang78.simplerest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Jetty {
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
}