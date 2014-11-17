package com.bigtictactoeonlinegame.openedroom;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.bigtictactoeonlinegame.onlinerooms.*;
import com.config.*;
import com.entity.*;
import com.google.android.gms.ads.*;
import com.net.online.*;
import com.utils.*;

import net.protocol.*;

import java.util.*;

/**
 * Date: 06.09.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class OnlineOpenedRoomFragment extends Fragment implements IOnlineOpenedRoomAction {
    private List<Player> mListActivityPlayer = new ArrayList<Player>();
    private List<Player> mListWantToPlayPlayer = new ArrayList<Player>();
    private OnlinePlayersAdapter mAdapterForActivityList;
    private ArrayAdapter mAdapterForWantPlayList;
    private OnlineConnectionManager mOnlineConnectionManager;
    private Player mMyPlayer;
    private int mMyGroupId;


    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_opened_room_fragment, null);
        //  generateTestData();
        // create adapter for all list
        initAdapters(view);
        Intent intent = getActivity().getIntent();
        mMyGroupId = intent.getIntExtra(OnlineRoomsActivity.NUMBER_OF_GROUP, -10);
        mMyPlayer = Controller.getInstance().getPlayer();
        mOnlineConnectionManager = Controller.getInstance().getOnlineWorker();
        if (mOnlineConnectionManager != null) {
            mOnlineConnectionManager.sendPacket(Protocol.SEnterToGroup.newBuilder()
                    .setGroupId(mMyGroupId).build());

        }
        return view;
    }

    private void initAdapters(View view) {
        mAdapterForActivityList = new OnlinePlayersAdapter(getActivity(), mListActivityPlayer);
        mAdapterForWantPlayList = new WantedToPlayAdapter(getActivity(), R.layout.wanted_buttle_list_item,
                mListWantToPlayPlayer);
        // define lists and add appropriate adapter
        ListView lvActivityPlayer = (ListView) view.findViewById(R.id.list_activity_players);
        lvActivityPlayer.setAdapter(mAdapterForActivityList);
        ListView lvWantPlayPlayer = (ListView) view.findViewById(R.id.list_player_witch_want_to_play);
        lvWantPlayPlayer.setAdapter(mAdapterForWantPlayList);
        lvWantPlayPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Player p = mListWantToPlayPlayer.get(i);
                if (p != null)
                    mOnlineConnectionManager.sendPacket(Protocol.SWantToPlay.newBuilder()
                            .setOpponentId(p.getId()).setPlayerId(mMyPlayer.getId())
                            .build());
            }
        });
    }

    private void generateTestData() {
        for (int i = 1; i <= 50; i++) {
            Player player = new Player();
            player.setName("player " + i);
            mListWantToPlayPlayer.add(player);
        }
    }

    private void startOnlineGame(Protocol.CStartGame cStartGame) {
        Player opponent = null;
        for (Player player : mListWantToPlayPlayer) {
            if (player.getId() == cStartGame.getOpponentId()) {
                opponent = player;
                opponent.setNumOfAllWonGame(cStartGame.getNumberOfLostGame());
                break;
            }
        }
        Intent intent = new Intent(getActivity(), GameFieldActivity.class);
        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.ONLINE);
        intent.putExtra(BundleKeys.OPPONENT, opponent);
        intent.putExtra(BundleKeys.TYPE_OF_MOVE, (cStartGame.getTypeMove() == Protocol.TypeMove.X)
                ? TypeOfMove.X : TypeOfMove.O);
        startActivity(intent);
        getActivity().finish();
    }

    private void clearAllListView() {
        mListActivityPlayer.clear();
        mListWantToPlayPlayer.clear();
        mAdapterForActivityList.notifyDataSetChanged();
        mAdapterForWantPlayList.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOnlineConnectionManager != null) {
            mOnlineConnectionManager.sendPacket(Protocol.SGetUpdate.newBuilder()
                    .setId(Controller.getInstance().getPlayer().getId()).setGroupId(mMyGroupId).build());
        }
    }

    @Override
    public void onDestroy() {
        clearAllListView();
        super.onDestroy();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void updateAboutActivityPlayer(Message msg) {
        getView().findViewById(R.id.ll_updating_group).setVisibility(View.GONE);
        Protocol.CUpdateAboutActivityPlayer updateAboutActivityPlayers =
                (Protocol.CUpdateAboutActivityPlayer) msg.obj;
        Logger.printLog("Invoked updateAboutActivityPlayer");
        for (Protocol.Player protocolPlayer : updateAboutActivityPlayers
                .getNewPlayerList()) {
            Logger.printLog("updateAboutActivityPlayer NewPlayerList()+ " + protocolPlayer.getName());
            if (protocolPlayer.getId() != -1)
                mListActivityPlayer.add(new Player(protocolPlayer.getId(),
                        protocolPlayer.getName(), protocolPlayer.getRating(), protocolPlayer.getGooglePlayRating()));
        }
        for (Protocol.CExitFromGroup exitFromGroup : updateAboutActivityPlayers
                .getExitPlayerList()) {
            Logger.printLog("updateAboutActivityPlayer exitFromGroup  " + exitFromGroup.getPlayerId());
            int id = exitFromGroup.getPlayerId();
            for (Player player : mListActivityPlayer)
                if (player.getId() == id) {
                    mAdapterForActivityList.removePlayer(player);
                    break;
                }
            for (Player player : mListWantToPlayPlayer)
                if (player.getId() == id) {
                    mListWantToPlayPlayer.remove(player);
                    break;
                }
        }
        mAdapterForActivityList.notifyDataSetChanged();
        mAdapterForWantPlayList.notifyDataSetChanged();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void wantToPlay(Message msg) {
        Protocol.CWantToPlay wantToPlay = (Protocol.CWantToPlay) msg.obj;
        int opponentId = wantToPlay.getOpponentId();
        Player opponent = null;
        for (Player player : mListActivityPlayer)
            if (player.getId() == opponentId) {
                mListWantToPlayPlayer.add(player);
                opponent = player;
            }
        mAdapterForWantPlayList.notifyDataSetChanged();
        Toast.makeText(getActivity(), getResources().getString(R.string.player)
                + " " + opponent.getName() + " " +
                getResources().getString(R.string.want_to_battle_with_you), Toast.LENGTH_SHORT).show();

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void startGame(Message msg) {
        Protocol.CStartGame startGamePacket = (Protocol.CStartGame) msg.obj;
        mMyPlayer.setNumOfAllWonGame(startGamePacket.getNumberOfWonGame());
        startOnlineGame(startGamePacket);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void cancelPlayDesire(Message msg) {
        Protocol.CCancelDesirePlay cCancelDesirePlay = (Protocol.CCancelDesirePlay) msg.obj;
        int id = cCancelDesirePlay.getOpponentId();
        Player canceledPlayer = null;
        for (Player player : mListWantToPlayPlayer) {
            if (player.getId() == id) {
                canceledPlayer = player;
                mListWantToPlayPlayer.remove(player);
                mAdapterForWantPlayList.notifyDataSetChanged();
            }
        }
        Toast.makeText(getActivity(), getString(R.string.player) + " "
                + canceledPlayer.getName() + " "
                + getResources().getString(R.string.canceled_inviting), Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<Player> getListActivePlayer() {
        return mListActivityPlayer;
    }

    @Override
    public AdView getAdView() {
        return null;

    }

    private static class WantedToPlayAdapter extends ArrayAdapter<Player> {
        private LayoutInflater layoutInflater;
        private int resourcesForInflate;
        private List<Player> players;

        private WantedToPlayAdapter(Context context, int textViewResourceId, List<Player> listPlayers) {
            super(context, textViewResourceId, listPlayers);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.players = listPlayers;
            this.resourcesForInflate = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(resourcesForInflate, parent, false);
            }
            TextView textView = (TextView) convertView;
            if (textView != null) {
                textView.setText(players.get(position).getName() + " " + players.get(position).getRating());
            }
            return convertView;
        }
    }
}
