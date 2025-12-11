package com.groupd.restaurant;

public class Dish {
	private long id;
	public long getId() { return id; }
	
	private String name;
	public String getName() { return name; }
	
	private float rating;
	public float getRating() { return rating; }
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public Dish(long id, String name, float rating) {
		this.id = id;
		this.name = name;
		this.rating = rating;
	}
}