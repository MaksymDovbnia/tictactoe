package com.game;

import com.entity.Player;
import com.game.gamefield.handler.GameHandler;
import com.net.bluetooth.BluetoothService;
import com.net.online.WorkerOnlineConnection;

public class Controler {

	private static WorkerOnlineConnection onl;
    private static BluetoothService bluetoothService;

	private static Player player;
	private static GameHandler gameFiledSource;

	public static GameHandler getGameHandler() {
		return Controler.gameFiledSource;
	}

    public static BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public static void setBluetoothService(BluetoothService bluetoothService) {
        Controler.bluetoothService = bluetoothService;
    }

    public static void setGameHandler(GameHandler handler) {
		Controler.gameFiledSource = handler;
	}

	public static WorkerOnlineConnection getOnl() {
		return Controler.onl;
	}

	public static void setOnl(WorkerOnlineConnection onl) {
		Controler.onl = onl;
	}

	public static Player getPlayer() {
		return Controler.player;
	}

	public static void setPlayer(Player player) {
		Controler.player = player;
	}

}
