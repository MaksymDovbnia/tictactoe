package com.game.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.config.BundleKeys;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.game.Controller;
import com.game.GameType;
import com.game.gamefield.GameFieldActivity;
import com.game.onlinegroups.OnlineGroupsActivity;
import com.game.activity.R;
import com.game.adapters.OnlinePlayersAdapter;
import com.net.online.WorkerOnlineConnection;
import com.utils.Loger;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 6/19/13.
 */
public class OnlineOpenedRoomFragment extends Fragment implements View.OnClickListener, IOnlineOpenedRoomAction {
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
        startGame = (Button) v.findViewById(R.id.btn_start_game);
        startGame.setOnClickListener(this);
        cancelPlayer = (Button) v.findViewById(R.id.btn_cancel_player);
        cancelPlayer.setOnClickListener(this);
        updateList = (Button) v.findViewById(R.id.button_update_activitylist);
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
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.online_list_view_header_layout, lvActivityPlayer, false);
        lvActivityPlayer.addHeaderView(header);
        lvActivityPlayer.setAdapter(adapterForActivityList);
        lvDesirePlayer = (ListView) v.findViewById(R.id.list_disered_players);
        lvDesirePlayer.setAdapter(adapterForInvitedList);
        lvWantPlayPlayer = (ListView) v.findViewById(R.id.list_player_witch_want_to_play);
        lvWantPlayPlayer.setAdapter(adapterForWantPlayList);
        Intent intent = activity.getIntent();
        groupId = intent.getIntExtra(OnlineGroupsActivity.NUMBER_OF_GROUP, -10);
        myPlayer = Controller.getInstance().getPlayer();
        workerOnlineConnection = Controller.getInstance().getOnlineWorker();
        if (workerOnlineConnection != null) {
            workerOnlineConnection.registerHandler(handler);
            workerOnlineConnection.sendPacket(Protocol.SEnterToGroup.newBuilder()
                    .setGroupId(groupId).build());
            workerOnlineConnection.sendPacket(Protocol.SGetUpdate.newBuilder()
                    .setId(Controller.getInstance().getPlayer().getId()).setGroupId(groupId).build());
        }
        return v;
    }


    private void startOnlineGame(Protocol.CStartGame cStartGame) {
        Player opponent = null;
        for (Player player : listWantToPlayPlayer) {
            if (player.getId() == cStartGame.getOpponentId()) {
                opponent = player;
                opponent.setNumOfAllWonGame(cStartGame.getNumberOfLostGame());
                break;
            }
        }
        Intent intent = new Intent(context, GameFieldActivity.class);
        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.ONLINE);
        intent.putExtra(BundleKeys.OPPONENT, opponent);
        intent.putExtra(BundleKeys.TYPE_OF_MOVE, (cStartGame.getTypeMove() == Protocol.TypeMove.X) ? TypeOfMove.X : TypeOfMove.O);
        startActivity(intent);
        activity.finish();
    }

    public void clearAllListView() {
        listActivityPlayer.clear();
        listWantToPlayPlayer.clear();
        listInvitedPlayers.clear();
        adapterForActivityList.notifyDataSetChanged();
        adapterForInvitedList.notifyDataSetChanged();
        adapterForWantPlayList.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if (workerOnlineConnection != null)
            workerOnlineConnection.sendPacket(Protocol.SGetUpdate.newBuilder()
                    .setId(Controller.getInstance().getPlayer().getId()).setGroupId(groupId).build());
        super.onResume();
    }


    @Override
    public void onDestroy() {
        clearAllListView();
        if (workerOnlineConnection != null) workerOnlineConnection.unRegisterHandler(handler);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update_activitylist:
                workerOnlineConnection.sendPacket(Protocol.SGetUpdate.newBuilder()
                        .setId(Controller.getInstance().getPlayer().getId()).setGroupId(groupId).build());
                break;
            case R.id.btn_invite_to_play:
                int opponentId = adapterForActivityList.getIdLast();
                Loger.printLog("switch " + opponentId);
                Player player = null;
                if (listActivityPlayer.size() > 0 && opponentId >= 0) {
                    player = listActivityPlayer.get(opponentId);
                }
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
                Player player1 = null;
                if (listInvitedPlayers.size() > 0 && cancelId >= 0) {
                    player1 = listInvitedPlayers.get(cancelId);
                }
                if (player1 != null) {
                    listInvitedPlayers.remove(player1);
                    adapterForInvitedList.notifyDataSetChanged();
                    workerOnlineConnection.sendPacket(Protocol.SCancelDesirePlay.newBuilder().setPlayerId(myPlayer.getId()).setOpponentId(player1.getId()).build());
                }
                break;
            case R.id.btn_start_game:
                int oppId = adapterForWantPlayList.getIdLast();
                if (oppId == Integer.MIN_VALUE) {
                    return;
                }
                Player p = null;
                if (listWantToPlayPlayer.size() > 0) {
                    p = listWantToPlayPlayer.get(oppId);
                }
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
    public void updateAboutActivityPlayer(Message msg) {
        Protocol.CUpdateAboutActivityPlayer cActivityPlayer =
                (Protocol.CUpdateAboutActivityPlayer) msg.obj;
        for (Protocol.Player player : cActivityPlayer
                .getNewPlayerList()) {
            Loger.printLog("get updt + " + player.getName());
            if (player.getId() != -1)
                listActivityPlayer.add(new Player(player.getId(),
                        player.getName(), player.getRating()));
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
    }

    @Override
    public void wantToPlay(Message msg) {
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
    }

    @Override
    public void startGame(Message msg) {
        Protocol.CStartGame startGamePacket = (Protocol.CStartGame) msg.obj;
        myPlayer.setNumOfAllWonGame(startGamePacket.getNumberOfWonGame());
        startOnlineGame(startGamePacket);
    }

    @Override
    public void cancelPlayDesire(Message msg) {
        Protocol.CCancelDesirePlay cCancelDesirePlay = (Protocol.CCancelDesirePlay) msg.obj;
        int id = cCancelDesirePlay.getOpponentId();
        Player canceledPlayer = null;
        for (Player player : listWantToPlayPlayer) {
            if (player.getId() == id) {
                canceledPlayer = player;
                listWantToPlayPlayer.remove(player);
                adapterForWantPlayList.notifyDataSetChanged();
            }
        }
        Toast toastAboutCancel = Toast.makeText(context, "Player " + canceledPlayer.getName() + " canceled invite", 40);
        toastAboutCancel.show();
    }

    @Override
    public List<Player> getListActivePlayer() {
        return listActivityPlayer;
    }
}