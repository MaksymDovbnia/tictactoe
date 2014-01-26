package com.bigtictactoeonlinegame.openedroom;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigtictactoeonlinegame.gamefield.GameFieldActivity;
import com.bigtictactoeonlinegame.onlinerooms.OnlineRoomsActivity;
import com.config.BundleKeys;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.bigtictactoeonlinegame.Controller;
import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.activity.R;
import com.google.android.gms.ads.AdView;
import com.net.online.WorkerOnlineConnection;
import com.utils.Loger;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 6/19/13.
 */
public class OnlineOpenedRoomFragment extends Fragment implements IOnlineOpenedRoomAction {
    private ListView lvActivityPlayer;
    private ListView lvWantPlayPlayer;
    private List<Player> listActivityPlayer = new ArrayList<Player>();
    private List<Player> listWantToPlayPlayer = new ArrayList<Player>();
    private OnlinePlayersAdapter adapterForActivityList;
    private ArrayAdapter adapterForWantPlayList;
    private Handler handler;
    private WorkerOnlineConnection workerOnlineConnection;
    private Context context;
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
        View view = inflater.inflate(R.layout.online_opened_room_fragment, null);
        // updateList.setOnTouchListener(this);
//        for (int i = 1; i <= 50; i++) {
//            Player player = new Player();
//            player.setName("player " + i);
//            listWantToPlayPlayer.add(player);
//        }
        // create adapter for all list
        adapterForActivityList = new OnlinePlayersAdapter(context, listActivityPlayer);
        adapterForWantPlayList = new WantedToPlayAdapter(context, R.layout.wanted_buttle_list_item,
                listWantToPlayPlayer);
        // define lists and add appropriate adapter
        lvActivityPlayer = (ListView) view.findViewById(R.id.list_activity_players);
        lvActivityPlayer.setAdapter(adapterForActivityList);
        lvWantPlayPlayer = (ListView) view.findViewById(R.id.list_player_witch_want_to_play);
        lvWantPlayPlayer.setAdapter(adapterForWantPlayList);
        lvWantPlayPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Player p = listWantToPlayPlayer.get(i);
                if (p != null)
                    workerOnlineConnection.sendPacket(Protocol.SWantToPlay.newBuilder()
                            .setOpponentId(p.getId()).setPlayerId(myPlayer.getId())
                            .build());
            }
        });
        Intent intent = activity.getIntent();
        groupId = intent.getIntExtra(OnlineRoomsActivity.NUMBER_OF_GROUP, -10);
        myPlayer = Controller.getInstance().getPlayer();
        workerOnlineConnection = Controller.getInstance().getOnlineWorker();
        if (workerOnlineConnection != null) {
            workerOnlineConnection.registerHandler(handler);
            workerOnlineConnection.sendPacket(Protocol.SEnterToGroup.newBuilder()
                    .setGroupId(groupId).build());

        }
        return view;
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
        adapterForActivityList.notifyDataSetChanged();
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
    public void updateAboutActivityPlayer(Message msg) {
        Protocol.CUpdateAboutActivityPlayer cActivityPlayer =
                (Protocol.CUpdateAboutActivityPlayer) msg.obj;
        for (Protocol.Player player : cActivityPlayer
                .getNewPlayerList()) {
            Loger.printLog("updateAboutActivityPlayer NewPlayerList()+ " + player.getName());
            if (player.getId() != -1)
                listActivityPlayer.add(new Player(player.getId(),
                        player.getName(), player.getRating()));
        }
        for (Protocol.CExitFromGroup exitFromGroup : cActivityPlayer
                .getExitPlayerList()) {
            Loger.printLog("updateAboutActivityPlayer exitFromGroup  " + exitFromGroup.getPlayerId());
            int id = exitFromGroup.getPlayerId();
            for (Player player : listActivityPlayer)
                if (player.getId() == id) {
                    adapterForActivityList.removePlayer(player);
                    break;
                }
            for (Player player : listWantToPlayPlayer)
                if (player.getId() == id) {
                    listWantToPlayPlayer.remove(player);
                    break;
                }
        }
        adapterForActivityList.notifyDataSetChanged();
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
        Toast.makeText(context, getResources().getString(R.string.player)
                + " " + opponent.getName() + " " +
                getResources().getString(R.string.want_to_battle_with_you), Toast.LENGTH_SHORT).show();

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
        Toast.makeText(context, getString(R.string.player) + " "
                + canceledPlayer.getName() + " "
                + getResources().getString(R.string.canceled_inviting), Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<Player> getListActivePlayer() {
        return listActivityPlayer;
    }

    @Override
    public AdView getAdView() {
        return (AdView) getView().findViewById(R.id.ad_view);
    }

    private static class WantedToPlayAdapter extends ArrayAdapter<Player> {
        private LayoutInflater layoutInflater;
        private int resosurcesForInflate;
        private List<Player> players;

        private WantedToPlayAdapter(Context context, int textViewResourceId, List<Player> listPlayers) {
            super(context, textViewResourceId, listPlayers);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.players = listPlayers;
            this.resosurcesForInflate = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(resosurcesForInflate, parent, false);
            }
            TextView textView = (TextView) convertView;
            if (textView != null) {
                textView.setText(players.get(position).getName() + " " + players.get(position).getRating());
            }
            return convertView;
        }
    }
}
