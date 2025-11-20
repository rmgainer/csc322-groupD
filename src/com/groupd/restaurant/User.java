package com.groupd.restaurant;

public class User {
	private long id;
	public long getId() { return id; }
	
	private boolean vip;
	public boolean isVIP() { return vip; }
	public void setVIP(boolean vip) { this.vip = vip; }
	
	private boolean admin;
	public boolean isAdmin() { return admin; }
	public void setAdmin(boolean admin) { this.admin = admin; }
	
	private String username;
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	
	private String email;
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public User(long id, String username, String email) {
		this.id = id;
		vip = false;
		admin = false;
		this.username = username;
		this.email = email;
	}
}