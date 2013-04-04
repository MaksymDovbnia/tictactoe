package com.game;

import com.entity.Player;
import com.net.online.OnlineConectionGameWorker;

public class Controler {

	private static OnlineConectionGameWorker onl;

	private static Player player;
	private static GameFiledSource gameFiledSource;

	public static GameFiledSource getGameFiledSource() {
		return Controler.gameFiledSource;
	}

	public static void setGameFiledSource(GameFiledSource gameFiledSource) {
		Controler.gameFiledSource = gameFiledSource;
	}

	public static OnlineConectionGameWorker getOnl() {
		return Controler.onl;
	}

	public static void setOnl(OnlineConectionGameWorker onl) {
		Controler.onl = onl;
	}

	public static Player getPlayer() {
		return Controler.player;
	}

	public static void setPlayer(Player player) {
		Controler.player = player;
	}

}
