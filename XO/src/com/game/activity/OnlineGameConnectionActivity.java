package com.game.activity;

import java.util.ArrayList;
import java.util.List;

import net.protocol.Protocol;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.entity.Player;
import com.game.Controler;
import com.game.adapters.AdapterActivityList;
import com.net.online.OnlineConectionGameWorker;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineGameConnectionActivity extends Activity implements
		OnClickListener {
	public static final String MARKONLINEGAME = "OnlineGameWorker";
	private Handler handler;
	private Button connect;
	private Button openGroupButton;
	private OnlineConectionGameWorker onlineGameWorker;
	private EditText status;
	private Player player;
	private ListView listActivityPlayer;
	private List<Player> players;
	private AdapterActivityList adapterActivityList;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Player pl = new Player();
		pl.setName("TEST PLAYER");
		players = new ArrayList<Player>();
		players.add(pl);
		setContentView(R.layout.activity_online_game_connection);
		connect = (Button) findViewById(R.id.connectToServer);
		connect.setOnClickListener(this);
		openGroupButton = (Button) findViewById(R.id.button_onlineconnection_opengroup);
		openGroupButton.setOnClickListener(this);
		openGroupButton.setEnabled(false);
		status = (EditText) findViewById(R.id.statusConected);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ProtoType protoType = ProtoType.fromInt(msg.what);
				if (msg.what == 100) {
					pd.cancel();
					createToastWithText("Sorry, server not avaible, plase try later");
					return;
				}
				switch (protoType) {
				/*
				 * case CUPDATEAOBOUTACTIVITYPLAYER:
				 * Protocol.CUpdateAboutActivityPlayer cActivityPlayer =
				 * (Protocol.CUpdateAboutActivityPlayer) msg.obj; for
				 * (Protocol.Player player: cActivityPlayer.getPlayerList()){
				 * players.add(new Player(player.getId(), player.getName())); }
				 * adapterActivityList.notifyDataSetChanged(); break;
				 */

				case CLOGINTOGAME:
					Protocol.CLoginToGame cLoginToGame = (Protocol.CLoginToGame) msg.obj;
					int id = cLoginToGame.getId();
					status.setText(status.getText() + " conected with id " + id);
					connect.setEnabled(false);
					status.setEnabled(false);
					player.setId(id);
					Controler.setPlayer(player);
					Loger.printLog("Conected to server with id " + id);
					openGroupButton.setEnabled(true);
					pd.cancel();
					break;

				}

			}
		};
	}

	private void createToastWithText(String s) {
		Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.connectToServer:
			pd = new ProgressDialog(this);
			ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				Loger.printLog("click" + v.getId());
				player = new Player();
				player.setName(status.getText().toString());
				onlineGameWorker = new OnlineConectionGameWorker(handler,
						player, pd);
				Controler.setOnl(onlineGameWorker);
				onlineGameWorker.start();

				pd.setTitle("Conection");
				pd.setMessage("conecting...");
				pd.show();

				// pd.cancel();
			} else {
				createToastWithText("No connection, please will make connection and reaped");

			}

			break;
		case R.id.button_onlineconnection_opengroup:
			Loger.printLog("click sentRequestToPlayButton");
			Intent intent = new Intent(this, OnlinePlayersActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	class HandlerOnlineConnection extends Handler {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
		}

	}

	@Override
	protected void onDestroy() {
		if (onlineGameWorker != null)
			onlineGameWorker.disconect();

		super.onDestroy();
	}

}
