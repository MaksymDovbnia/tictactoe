package com.game;

import com.entity.Player;
import com.game.gamefield.handler.GameHandler;
import com.net.bluetooth.BluetoothService;
import com.net.online.WorkerOnlineConnection;

public class Controller {

	private WorkerOnlineConnection onl;
    private BluetoothService bluetoothService;
    private static Controller controller;

	private  Player player;
	private  GameHandler gameFiledSource;

	public  GameHandler getGameHandler() {
		return gameFiledSource;
	}

    public static Controller getInstance(){
     if (controller == null) {
         controller = new Controller();
     }
     return controller;
    }

    public  BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public  void setGameHandler(GameHandler handler) {
		gameFiledSource = handler;
	}

	public  WorkerOnlineConnection getOnlineWorker() {
		return onl;
	}

	public  void setOnlineWorker(WorkerOnlineConnection onl) {
		this.onl = onl;
	}

	public Player getPlayer() {
		return player;
	}

	public  void setPlayer(Player player) {
		this.player = player;
	}

}
