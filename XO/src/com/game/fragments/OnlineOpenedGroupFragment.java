package com.game.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.entity.Player;
import com.game.Controler;
import com.game.activity.GameFieldActivity;
import com.game.activity.OnlineGroupsActivity;
import com.game.activity.R;
import com.game.adapters.OnlinePlayersAdapter;
import com.game.handler.OnlineGameHandler;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 6/19/13.
 */
public class OnlineOpenedGroupFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private ListView lvActivityPlayer;
    private ListView lvDesirePlayer;
    private ListView lvWantPlayPlayer;

    private List<Player> listActivityPlayer = new ArrayList<Player>();
    private List<Player> listInvitedPlayers = new ArrayList<Player>();
    private List<Player> listWantToPlayPlayer = new ArrayList<Player>();

    private OnlinePlayersAdapter adapterForActivityList;
    private OnlinePlayersAdapter adapterForInvitedList;
    private OnlinePlayersAdapter adapterForWantPlayList;

    private Handler handler;
    private WorkerOnlineConnection workerOnlineConnection;
    private Context context;
    private Button invite_to_play;
    private Button cancelPlayer;
    private Button startGame;
    private Button updateList;
    private Player myPlayer;
    private int groupId;
    private Activity activity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.online_open_group_fragment, null);
        invite_to_play = (Button) v.findViewById(R.id.btn_invite_to_play);
        invite_to_play.setOnClickListener(this);
        invite_to_play.setOnTouchListener(this);
        startGame = (Button) v.findViewById(R.id.btn_start_game);
        startGame.setOnClickListener(this);
        startGame.setOnTouchListener(this);
        cancelPlayer = (Button) v.findViewById(R.id.btn_cancel_player);
        cancelPlayer.setOnClickListener(this);
        cancelPlayer.setOnTouchListener(this);
        updateList = (Button) v.findViewById(R.id.button_update_activitylist);
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
        adapterForActivityList = new OnlinePlayersAdapter(context, listActivityPlayer);
        adapterForInvitedList = new OnlinePlayersAdapter(context, listInvitedPlayers);
        adapterForWantPlayList = new OnlinePlayersAdapter(context, listWantToPlayPlayer);

        // define lists and add appropriate adapter
        lvActivityPlayer = (ListView) v.findViewById(R.id.list_activity_players);
        lvActivityPlayer.setAdapter(adapterForActivityList);
        lvDesirePlayer = (ListView) v.findViewById(R.id.list_disered_players);
        lvDesirePlayer.setAdapter(adapterForInvitedList);
        lvWantPlayPlayer = (ListView) v.findViewById(R.id.list_player_witch_want_to_play);
        lvWantPlayPlayer.setAdapter(adapterForWantPlayList);

        Intent intent = activity.getIntent();
        groupId = intent.getIntExtra(OnlineGroupsActivity.NUMBER_OF_GROUP, -10);
        myPlayer = Controler.getPlayer();


        lvActivityPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                            for (Player player : listInvitedPlayers)
                                if (player.getId() == id) {
                                    listInvitedPlayers.remove(player);
                                    break;
                                }

                            for (Player player : listWantToPlayPlayer)
                                if (player.getId() == id) {
                                    listWantToPlayPlayer.remove(player);
                                    break;
                                }
                        }
                        adapterForActivityList.notifyDataSetChanged();
                        adapterForInvitedList.notifyDataSetChanged();
                        adapterForWantPlayList.notifyDataSetChanged();
                        break;
                    case CWANTTOPLAY:
                        Protocol.CWantToPlay wantToPlay = (Protocol.CWantToPlay) msg.obj;
                        int opponentId = wantToPlay.getOpponentId();
                        Player opponent = null;
                        for (Player player : listActivityPlayer)
                            if (player.getId() == opponentId) {
                                listWantToPlayPlayer.add(player);
                                opponent = player;
                            }
                        adapterForWantPlayList.notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "Player " + opponent.getName() + " want to play with you", 40);
                        toast.show();
                        break;
                    case CSTARTGAME:
                        Protocol.CStartGame startGame = (Protocol.CStartGame) msg.obj;
                        startGame(startGame.getOpponentId());

                        break;
                }

            }
        };
        workerOnlineConnection = Controler.getOnl();
        if (workerOnlineConnection != null) {
            workerOnlineConnection.registerHandler(handler);
            workerOnlineConnection.sendPacket(Protocol.SEnterToGroup.newBuilder()
                    .setGroupId(groupId).build());

            workerOnlineConnection.sendPacket(Protocol.SGetUpdate.newBuilder()
                    .setId(Controler.getPlayer().getId()).setGroupId(groupId).build());
        }

        return v;
    }


    private void startGame(int opponentId) {
        Player opponent = null;
        for (Player player : listWantToPlayPlayer) {
            if (player.getId() == opponentId) {
                opponent = player;
                break;
            }

        }
        OnlineGameHandler onlineGameHandler = new OnlineGameHandler(
                workerOnlineConnection, myPlayer, opponent);

        Controler.setGameHandler(onlineGameHandler);
        Intent intent = new Intent(context, GameFieldActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_update_activitylist:
                workerOnlineConnection.sendPacket(Protocol.SGetUpdate.newBuilder()
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
                if (player != null && !listInvitedPlayers.contains(player)) {
                    workerOnlineConnection.sendPacket(Protocol.SWantToPlay.newBuilder()
                            .setOpponentId(player.getId())
                            .setPlayerId(myPlayer.getId()).build());
                    listInvitedPlayers.add(player);
                    adapterForInvitedList.notifyDataSetChanged();
                }

                break;
            case R.id.btn_cancel_player:
                int cancelId = adapterForInvitedList.getIdLast();
                Player player1 = listInvitedPlayers.get(cancelId);

                if (player1 != null) {
                    listInvitedPlayers.remove(player1);
                    adapterForInvitedList.notifyDataSetChanged();
                    workerOnlineConnection.sendPacket(Protocol.SCancelDesirePlay.newBuilder().setPlayerId(myPlayer.getId()).setOpponentId(cancelId).build());
                }
                break;
            case R.id.btn_start_game:
                int oppId = adapterForWantPlayList.getIdLast();
                if (oppId == Integer.MIN_VALUE)
                    return;
                Player p = listActivityPlayer.get(oppId);
                if (p != null)
                    workerOnlineConnection.sendPacket(Protocol.SWantToPlay.newBuilder()
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