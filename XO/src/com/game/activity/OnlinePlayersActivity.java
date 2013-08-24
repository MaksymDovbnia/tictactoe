package com.game.activity;

import java.util.ArrayList;
import java.util.List;

import net.protocol.Protocol;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.entity.Player;
import com.game.Controler;
import com.game.adapters.OnlinePlayersAdapter;
import com.game.handler.OnlineGameHandler;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlinePlayersActivity extends Activity implements OnClickListener,
		OnTouchListener {

	private ListView lvActivityPlayer;
	private ListView lvDesirePlayer;
	private ListView lvWantPlayPlayer;

	private List<Player> listActivityPlayer = new ArrayList<Player>();
	private List<Player> listDesirePlayer = new ArrayList<Player>();
	private List<Player> listWantToPlayPlayer = new ArrayList<Player>();

	private OnlinePlayersAdapter adapterForActivityList;
	private OnlinePlayersAdapter adapterForDesireList;
	private OnlinePlayersAdapter adapterForWantPlayList;

	private Handler handler;
	private WorkerOnlineConnection onlineGameWorker;
    private Context context;
	private Button desire;
	private Button cancelPlayer;
	private Button startGame;
	private Button updateList;
	private Player myPlayer;
    private int groupId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online);
		context = getApplicationContext();
        Intent intent = getIntent();
        groupId = intent.getIntExtra(OnlineGroupsActivity.NUMBER_OF_GROUP,-10);
		myPlayer = Controler.getPlayer();

		desire = (Button) findViewById(R.id.btn_invite_to_play);
		desire.setOnClickListener(this);
		desire.setOnTouchListener(this);
		startGame = (Button) findViewById(R.id.btn_start_game);
		startGame.setOnClickListener(this);
		startGame.setOnTouchListener(this);
		cancelPlayer = (Button) findViewById(R.id.btn_cancel_player);
		cancelPlayer.setOnClickListener(this);
		cancelPlayer.setOnTouchListener(this);
		updateList = (Button) findViewById(R.id.button_update_activitylist);
		updateList.setOnTouchListener(this);
		updateList.setOnClickListener(this);
		// updateList.setOnTouchListener(this);
		/*
		 * for (int i =1; i <=50; i++){ Player player = new Player();
		 * player.setName("player " + i); playerList.add(player);
		 * //OnlineGameConnectionActivity on =
		 * 
		 * }
		 */

		// create adapter for all list
		adapterForActivityList = new OnlinePlayersAdapter(this, listActivityPlayer);
		adapterForDesireList = new OnlinePlayersAdapter(this, listDesirePlayer);
		adapterForWantPlayList = new OnlinePlayersAdapter(this, listWantToPlayPlayer);

		// define lists and add appropriate adapter
		lvActivityPlayer = (ListView) findViewById(R.id.list_activity_players);
		lvActivityPlayer.setAdapter(adapterForActivityList);
		lvDesirePlayer = (ListView) findViewById(R.id.list_disered_players);
		lvDesirePlayer.setAdapter(adapterForDesireList);
		lvWantPlayPlayer = (ListView) findViewById(R.id.list_player_witch_want_to_play);
		lvWantPlayPlayer.setAdapter(adapterForWantPlayList);

		lvActivityPlayer.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Loger.printLog("click" + id + " p " + position);

			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ProtoType protoType = ProtoType.fromInt(msg.what);
				switch (protoType) {
				case CUPDATEAOBOUTACTIVITYPLAYER:
					Protocol.CUpdateAboutActivityPlayer cActivityPlayer = (Protocol.CUpdateAboutActivityPlayer) msg.obj;
				
					for (Protocol.Player player : cActivityPlayer
							.getNewPlayerList()) {
						Loger.printLog("get updt + " + player.getName());
						if (player.getId() != -1)
							listActivityPlayer.add(new Player(player.getId(),
									player.getName()));
					}
					for (Protocol.CExitFromGroup exitFromGroup : cActivityPlayer
				.getExitPlayerList()) {
						int id = exitFromGroup.getPlayerId();

						for (Player player : listActivityPlayer)
							if (player.getId() == id) {
								listActivityPlayer.remove(player);
								break;
							}

						for (Player player : listDesirePlayer)
							if (player.getId() == id) {
								listDesirePlayer.remove(player);
								break;
							}

						for (Player player : listWantToPlayPlayer)
							if (player.getId() == id) {
								listWantToPlayPlayer.remove(player);
								break;
							}
					}
					adapterForActivityList.notifyDataSetChanged();
					adapterForDesireList.notifyDataSetChanged();
					adapterForWantPlayList.notifyDataSetChanged();
					break;
				case CWANTTOPLAY:
                    Protocol.CWantToPlay wantToPlay = (Protocol.CWantToPlay) msg.obj;
					int opponentId = wantToPlay.getOpponentId();
					Player opponent = null;
                    for (Player player : listActivityPlayer)
						if (player.getId() == opponentId){
						    	listWantToPlayPlayer.add(player);
                                 opponent = player;
                        }
					adapterForWantPlayList.notifyDataSetChanged();
                    Toast toast = Toast.makeText(context,"Player " + opponent.getName() + " want to play with you",40);
                    toast.show();
                    break;
				case CSTARTGAME:
					Protocol.CStartGame startGame = (Protocol.CStartGame) msg.obj;
					startGame(startGame.getOpponentId());

					break;
				}

			}
		};
		onlineGameWorker = Controler.getOnl();
        onlineGameWorker.setHanlerd(handler);
        onlineGameWorker.sendPacket(Protocol.SEnterToGroup.newBuilder()
                .setGroupId(groupId).build());

        onlineGameWorker.sendPacket(Protocol.SGetUpdate.newBuilder()
				.setId(Controler.getPlayer().getId()).setGroupId(groupId).build());

	}

	private void startGame(int opponnetId) {
		Player opponent = null;
		for (Player player : listWantToPlayPlayer) {
			if (player.getId() == opponnetId) {
				opponent = player;
				break;
			}

		}
		OnlineGameHandler onlineGameHandler = new OnlineGameHandler(
				onlineGameWorker, myPlayer, opponent);

		Controler.setGameHandler(onlineGameHandler);
		Intent intent = new Intent(this, GameFieldActivity.class);
		startActivity(intent);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button_update_activitylist:
			onlineGameWorker.sendPacket(Protocol.SGetUpdate.newBuilder()
                    .setId(Controler.getPlayer().getId()).setGroupId(groupId).build());
			break;

		case R.id.btn_invite_to_play:
			int opponentId = adapterForActivityList.getIdLast();
			Loger.printLog("switch " + opponentId);
			Player player = null;
			/*
			 * for (Player pl : listActivityPlayer) { if (pl.getId() ==
			 * opponentId) player = pl;
			 * 
			 * }
			 */
			player = listActivityPlayer.get(opponentId);
			if (player != null) {
				onlineGameWorker.sendPacket(Protocol.SWantToPlay.newBuilder()
						.setOpponentId(player.getId())
						.setPlayerId(myPlayer.getId()).build());
				listDesirePlayer.add(player);
				adapterForDesireList.notifyDataSetChanged();
			}

			break;
		case R.id.btn_cancel_player:
			break;
		case R.id.btn_start_game:
			int oppId = adapterForWantPlayList.getIdLast();
			if (oppId == Integer.MIN_VALUE)
				return;
			Player p = listActivityPlayer.get(oppId);
			if (p != null)
				onlineGameWorker.sendPacket(Protocol.SWantToPlay.newBuilder()
						.setOpponentId(p.getId()).setPlayerId(myPlayer.getId())
						.build());
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.setBackgroundResource(R.drawable.button_crinkle);
			break;
		case MotionEvent.ACTION_UP:
			v.setBackgroundResource(R.drawable.button_yelow);
			break;

		default:
			break;
		}
		return false;
	}

}
