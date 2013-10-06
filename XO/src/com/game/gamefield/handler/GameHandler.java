package com.game.gamefield.handler;

import java.util.List;

import android.os.Handler;

import com.entity.OneMove;
import com.game.GameType;
import com.game.gamefield.GameFieldAdapter;

public interface GameHandler {
	// public List<Integer> oneMove(OneMove oneMove);
	public void sendMessage(String message);

	public GameType getGameType();

	public Handler getHandler();

	public List<OneMove> oneMove(OneMove oneMove);

	public void setAdapter(GameFieldAdapter adapter);

	public void startNewGame();
    public void exitFromGame();
    public void unregisterHandler();


}
