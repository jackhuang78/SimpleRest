package idv.jhuang78.simplerest;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Properties;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.core.Dispatcher;

public class Config extends Application {
	private static final Logger log = LogManager.getLogger(Config.class);
	
	private static final String PROPERTY_PATH = "config.properties";
	private static final String DEFAULT_DB_PATH = "dat/db.ser";
	
	private Database db;
	private Properties config;
	
	public Config(@Context Dispatcher dispatcher) {
		super();
		
		
		
		log.info("Load configuration from {}.", PROPERTY_PATH);
		try(InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTY_PATH)) {
			config = new Properties();
			config.load(in);
		} catch (IOException e) {
			log.fatal("Error while reading configuration file. Terminating...", e);
			System.exit(1);
		}
		
		
		final String dbPath = config.getProperty("srdb.db.path", DEFAULT_DB_PATH);
		log.info("Load database from {}.", dbPath);
		
		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(Paths.get(dbPath).toFile()))) {
			db = (Database) in.readObject();
			
		} catch(FileNotFoundException e) {
			log.warn("Database file not found at {}. Creating new database.", dbPath);
			db = new Database();
			
		} catch(InvalidPathException | ClassNotFoundException | IOException e) {
			log.fatal(String.format("Failed to load database from %s. Terminating...", dbPath), e);
			System.exit(1);
		}
		
		db.setPath(dbPath);
		
		dispatcher.getDefaultContextObjects().put(Database.class, db);
	
		
	}
	
	        
}
