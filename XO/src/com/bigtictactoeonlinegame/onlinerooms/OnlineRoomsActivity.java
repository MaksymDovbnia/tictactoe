package com.bigtictactoeonlinegame.onlinerooms;

import com.bigtictactoeonlinegame.popup.XOAlertDialog;
import com.entity.Player;
import com.bigtictactoeonlinegame.Controller;
import com.bigtictactoeonlinegame.activity.R;
import com.bigtictactoeonlinegame.mainactivity.GeneralAdActivity;
import com.google.android.gms.ads.AdView;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

public class OnlineRoomsActivity extends GeneralAdActivity implements IOnlineRoomsAction {
    private static final String TAG = OnlineRoomsActivity.class.getCanonicalName();
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private Handler handler;
    private WorkerOnlineConnection conectionGameWorker;
    private Fragment onlineGroupsFragment, top100Fgragment;
    private FragmentTransaction fragmentTransaction;
    private Top100Action top100Action;
    private final Player player = Controller.getInstance().getPlayer();
    private OnlineRoomsFragmentAction onlineRoomsFragmentAction;

    @Override
    public void getListOfGroup() {
        Protocol.SGetGroupList sGetGroupList = Protocol.SGetGroupList.newBuilder().setId(player.getId()).build();
        conectionGameWorker.sendPacket(sGetGroupList);
        Log.d(TAG, "sent packet " + sGetGroupList);
    }

    private enum TAB {GROUP_LIST, TOP100}

    ;
    private TAB currentTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_rooms_activity_layout);
        onlineGroupsFragment = new OnlineRoomsFragment();
        top100Fgragment = new Top100Fragmnet();
        top100Action = (Top100Action) top100Fgragment;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, onlineGroupsFragment);
        fragmentTransaction.add(R.id.center_for_fragment, top100Fgragment);
        fragmentTransaction.hide(top100Fgragment);
        onlineRoomsFragmentAction = (OnlineRoomsFragmentAction) onlineGroupsFragment;
        fragmentTransaction.commit();
        currentTab = TAB.GROUP_LIST;
        findViewById(R.id.btn_top_100).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTab(TAB.TOP100);
            }
        });
        findViewById(R.id.btn_online_groups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTab(TAB.GROUP_LIST);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CGETGROUPLIST:
                        onlineRoomsFragmentAction.getGroupList(msg.obj);
                        break;
                    case CTOP100:
                        Protocol.CTop100Player cTop100Player = (Protocol.CTop100Player) msg.obj;
                        List<Player> playerList = new ArrayList<Player>();
                        for (Protocol.Player protocolPlayer : cTop100Player.getPlayerList()) {
                            playerList.add(new Player(protocolPlayer.getId(), protocolPlayer.getName(), protocolPlayer.getRating()));
                        }
                        top100Action.receivedListTop100(playerList);
                        break;
                    case CONNECTION_TO_SERVER_LOST:
                        connectionToServerLost();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        conectionGameWorker = Controller.getInstance().getOnlineWorker();
    }


    @Override
    public AdView getAdView() {
        return onlineRoomsFragmentAction.getAdView();
    }

    private void switchToTab(TAB tab) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        currentTab = tab;
        switch (tab) {
            case GROUP_LIST:
                fragmentTransaction.hide(top100Fgragment);
                fragmentTransaction.show(onlineGroupsFragment);
                break;
            case TOP100:
                fragmentTransaction.hide(onlineGroupsFragment);
                fragmentTransaction.show(top100Fgragment);
                sendPacketGetTOP100List();
                break;
        }
        fragmentTransaction.commit();
    }

    private void sendPacketGetTOP100List() {
        conectionGameWorker
                .sendPacket(Protocol
                        .STop100Player
                        .newBuilder()
                        .setPlayerId(player.getId())
                        .build());
    }

    @Override
    public void onBackPressed() {
        if (currentTab == TAB.TOP100) {
            switchToTab(TAB.GROUP_LIST);
        } else {
            XOAlertDialog xoAlertDialog = new XOAlertDialog();
            xoAlertDialog.setTile(getResources().getString(R.string.exit_from_online_game));
            xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_game_online_question));
            xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
            xoAlertDialog.setNegativeButtonText(getResources().getString(R.string.no));
            xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            xoAlertDialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!conectionGameWorker.isSockedInLive()) {
            finish();
        }
        conectionGameWorker.registerHandler(handler);
        if (conectionGameWorker != null) {
            getListOfGroup();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (conectionGameWorker != null) {
            conectionGameWorker.unRegisterHandler(handler);
        }
    }

    public void connectionToServerLost() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setAlert_type(XOAlertDialog.ALERT_TYPE.ONE_BUTTON);
        xoAlertDialog.setTile(getResources().getString(R.string.connection_to_server_lost));
        String mainText = getString(R.string.please_try_to_connect_once_more);
        xoAlertDialog.setMainText(mainText);
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.ok));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onDestroy() {
        if (conectionGameWorker == null) return;
        conectionGameWorker.sendPacket(Protocol.SExitFromGlobalGame.newBuilder().setPlayerId(player.getId()).build());
        conectionGameWorker.unRegisterHandler(handler);
        conectionGameWorker.disconnect();
        super.onDestroy();
    }
}
