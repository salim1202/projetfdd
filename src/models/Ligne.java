package models;

import java.util.ArrayList;


public class Ligne {
	
	private String id; 
	private ArrayList<String> items = new ArrayList<>();
	
	public Ligne(String id, ArrayList<String> items) {
		this.id = id;
		this.items = items;
	}
	
	public Ligne(){
		
	}
	
	public void addItem(String item){
		this.items.add(item);
	}
	

	public String getId() {
		return id;
	}

	public ArrayList<String> getItems() {
		return items;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}
	
	public String toString(){
		String str = "";
		str = this.id;
		for(int i = 0;i<this.items.size();i++){
			str +=  this.items.get(i) + "|" ;
		}
		return str + "\n";
		
	}

	
	
	

}
