package idv.jhuang78.simplerest;

import idv.jhuang78.simplerest.Database.Collection;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;



public class Database extends HashMap<String, Collection> {
	private static final long serialVersionUID = 1L;

	private String path;
	
	public Database() {
		super();
		
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	public void commit() throws IOException {
		try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
			out.writeObject(this);
		} 
	}
	
	
	
	public Collection getCollection(String name, boolean create) {
		if(!super.containsKey(name) && create)
			super.put(name, new Collection());
		
		return (get(name) == null) ? new Collection() : get(name);
	}
	
	
	

	public static class Collection extends TreeMap<Integer, Item> {
		private static final long serialVersionUID = 1L;
		
		private int nextId;
		public Collection() {
			super();
			nextId = 1;
		}
		
		public int add(Item item) {
			while(super.containsKey(nextId))
				nextId++;
			
			int id = nextId;
			nextId++;
			
			add(id, item);
			return id;
		}
		
		public void add(int id, Item item) {
			item.put("_id", id);
			super.put(id, item);
		}
	
	}
	
	public static class Item extends LinkedHashMap<String, Object> {
		private static final long serialVersionUID = 1L;
		
		public Item() {
			super();
			super.put("_id", null);
		}
		
		public int id() {
			try {
				if(!super.containsKey("_id"))
					return -1;
				
				Object idObj = super.get("_id");
				if(idObj instanceof Integer)
					return (Integer) idObj;
				else if(idObj instanceof String)
					return Integer.parseInt((String)super.get("_id"));
				else
					return -1;
			} catch(Exception e) {
				return -1;
			}
		}
	}
}
