package com.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.protocol.Protocol;

public class Player implements Serializable{

	private int id = 0;
	private String name;
	private Map<Integer, Player> playerMapWichWantedPlay;
	private Map<Integer, Player> mapActivityPlayer;
	private Protocol.RegistrationType registrationType;
    private int groupId;
    private String uuid;
    private TypeOfMove moveType;
    private int rating;
    private int numOfAllWonGame;
    public Player() {
		playerMapWichWantedPlay = new HashMap<Integer, Player>();
	}

	public Player(int id, String name) {
		this.id = id;
		this.name = name;
		playerMapWichWantedPlay = new HashMap<Integer, Player>();
		mapActivityPlayer = new HashMap<Integer, Player>();
	}
    public Player(int id, String name, int rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        playerMapWichWantedPlay = new HashMap<Integer, Player>();
        mapActivityPlayer = new HashMap<Integer, Player>();
    }
	public Player(int id, String name, Protocol.RegistrationType registrationType) {
		this.registrationType = registrationType;
		this.id = id;
		this.name = name;
		playerMapWichWantedPlay = new HashMap<Integer, Player>();
		mapActivityPlayer = new HashMap<Integer, Player>();
	}


    public TypeOfMove getMoveType() {
        return moveType;
    }

    public void setMoveType(TypeOfMove moveType) {
        this.moveType = moveType;
    }

    public String getUuid() {
        return uuid;
    }

    public int getNumOfAllWonGame() {
        return numOfAllWonGame;
    }

    public void setNumOfAllWonGame(int numOfAllWonGame) {
        this.numOfAllWonGame = numOfAllWonGame;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
		return id;
	}

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Protocol.RegistrationType getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(Protocol.RegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, Player> getPlayerMapWichWantedPlay() {
		return playerMapWichWantedPlay;
	}

	public void setPlayerMapWichWantedPlay(
			Map<Integer, Player> playerMapWichWantedPlay) {
		this.playerMapWichWantedPlay = playerMapWichWantedPlay;
	}

	public void addPlayerWichWanPlay(Player player) {
		playerMapWichWantedPlay.put(player.getId(), player);

	}

	public Map<Integer, Player> getMapActivityPlayer() {
		return mapActivityPlayer;
	}

	public void setMapActivityPlayer(Map<Integer, Player> mapActivityPlayer) {
		this.mapActivityPlayer = mapActivityPlayer;
	}

}
