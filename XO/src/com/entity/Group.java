package com.entity;

public class Group {	
	private int id;
	private int countOfOnlinePlayer;
	private int countOfMaxPlayer;
	
    public Group(int id, int online, int max) {
		this.id= id;
		this.countOfMaxPlayer = max;
		this.countOfOnlinePlayer = online;
	}
	
	public void setCountOfMaxPlayer(int countOfMaxPlayer) {
		this.countOfMaxPlayer = countOfMaxPlayer;
	}
	
	public int getCountOfMaxPlayer() {
		return countOfMaxPlayer;
	}
	public void setCountOfOnlinePlayer(int countOfOnlinePlayer) {
		this.countOfOnlinePlayer = countOfOnlinePlayer;
	}

	public int getCountOfOnlinePlayer() {
		return countOfOnlinePlayer;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
