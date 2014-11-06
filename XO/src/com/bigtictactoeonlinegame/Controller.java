package com.bigtictactoeonlinegame;

import com.bigtictactoeonlinegame.gamefield.handler.IGameModel;
import com.entity.Player;
import com.bluetooth.BluetoothService;
import com.net.online.OnlineConnectionManager;

public class Controller {

	private OnlineConnectionManager onl;
    private BluetoothService bluetoothService;
    private static Controller controller;

	private  Player player;
	private IGameModel gameFiledSource;

	public IGameModel getGameHandler() {
		return gameFiledSource;
	}

    public static synchronized Controller getInstance(){
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

    public  void setGameHandler(IGameModel handler) {
		gameFiledSource = handler;
	}

	public OnlineConnectionManager getOnlineWorker() {
		return onl;
	}

	public  void setOnlineWorker(OnlineConnectionManager onl) {
		this.onl = onl;
	}

	public Player getPlayer() {
		return player;
	}

	public  void setPlayer(Player player) {
		this.player = player;
	}

}
