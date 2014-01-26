package com.bigtictactoeonlinegame.openedroom;

import android.os.Message;

import com.entity.Player;
import com.bigtictactoeonlinegame.mainactivity.IAdViewProvider;

import java.util.List;

/**
 * Created by Maksym on 10.11.13.
 */
public interface IOnlineOpenedRoomAction extends IAdViewProvider {

    public void updateAboutActivityPlayer(Message msg);

    public void wantToPlay(Message msg);

    public void startGame(Message msg);

    public void cancelPlayDesire(Message msg);

    public List<Player> getListActivePlayer();

}