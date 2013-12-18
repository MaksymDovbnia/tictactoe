package com.game.fragments;

import android.os.Message;

import com.entity.Player;

import java.util.List;

/**
 * Created by Maksym on 10.11.13.
 */
public interface IOnlineOpenedRoomAction {

    public void updateAboutActivityPlayer(Message msg);
    public void wantToPlay(Message msg);
    public void startGame(Message msg);
    public void cancelPlayDesire(Message msg);
    public List<Player> getListActivePlayer();

}