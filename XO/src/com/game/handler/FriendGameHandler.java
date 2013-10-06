package com.game.handler;

import java.util.List;

import android.os.Handler;

import com.entity.OneMove;
import com.game.GameLogicHandler;
import com.game.GameType;
import com.game.adapters.GameFieldAdapter;

public class FriendGameHandler implements GameHandler {
	private GameFieldAdapter gameFieldAdapter;
	private GameLogicHandler gameActionHandler;

	public FriendGameHandler() {
		gameActionHandler = new GameLogicHandler();
	}

	@Override
	public void sendMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public GameType getGameType() {

		return GameType.FRIEND;
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OneMove> oneMove(OneMove oneMove) {
		List<OneMove> list = gameActionHandler.oneMove(oneMove);
		return list;
	}

	@Override
	public void setAdapter(GameFieldAdapter adapter) {
		this.gameFieldAdapter = adapter;

	}

	@Override
	public void startNewGame() {
		gameActionHandler.newGame();

	}

    @Override
    public void exitFromGame() {

    }

    @Override
    public void unregisterHandler() {

    }

}
