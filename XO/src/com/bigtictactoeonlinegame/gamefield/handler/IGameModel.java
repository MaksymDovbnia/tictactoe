package com.bigtictactoeonlinegame.gamefield.handler;

import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.entity.*;

import java.util.*;

/**
 * @author Maksim Dovbnya(m.dovbnya@samsung.com).
 */
public interface IGameModel {


    public GameType getGameType();


    public void userMadeMove(OneMove oneMove);


    public void startNewGame(boolean isOpponentMoveFirst);

    public void exitFromGame();


    public void unregisterHandler();

    public void setWonGameListener(IWonGameListener listener);


    public void setOpponentActionListener(IOpponentActionListener listener);


    public void userTimeForMoveEnd();

}
