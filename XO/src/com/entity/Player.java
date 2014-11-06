package com.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.protocol.Protocol;

/**
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */

public class Player implements Serializable {

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
    private long playServiceScore;

    public long getPlayServiceScore() {
        return playServiceScore;
    }

    public void setPlayServiceScore(long playServiceScore) {
        this.playServiceScore = playServiceScore;
    }


    public Player() {
        playerMapWichWantedPlay = new HashMap<Integer, Player>();
        mapActivityPlayer = new HashMap<Integer, Player>();
    }

    public Player(int id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public Player(int id, String name, int rating) {
        this(id, name);
        this.rating = rating;
    }

    public Player(int id, String name, int rating, long playServiceScore) {
        this(id, name, rating);
        this.playServiceScore = playServiceScore;
    }

    public Player(int id, String name, Protocol.RegistrationType registrationType) {
        this(id, name);
        this.registrationType = registrationType;
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
