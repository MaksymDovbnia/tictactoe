package com.bigtictactoeonlinegame.onlinerooms;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.mainactivity.*;
import com.bigtictactoeonlinegame.popup.*;
import com.entity.*;
import com.google.android.gms.ads.*;
import com.net.online.*;
import com.net.online.protobuf.*;

import net.protocol.*;

import java.util.*;

/**
 * Date: 09.03.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */

public class OnlineRoomsActivity extends XOGameActivityWithAds implements IOnlineRoomsAction {
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private static final String LOG_TAG = OnlineRoomsActivity.class.getCanonicalName();
    private Handler mHandler;
    private OnlineConnectionManager mOnlineConnectionManager;
    private Fragment mOnlineGroupsFragment, mTop100Fragment;
    private FragmentTransaction mFragmentTransaction;
    private Top100Action mTop100Action;
    private final Player mPlayer = Controller.getInstance().getPlayer();
    private OnlineRoomsFragmentAction mOnlineRoomsFragmentAction;

    @Override
    public void getListOfGroup() {
        Protocol.SGetGroupList sGetGroupList = Protocol.SGetGroupList.newBuilder().setId(mPlayer.getId()).build();
        mOnlineConnectionManager.sendPacket(sGetGroupList);
        Log.d(LOG_TAG, "sent packet " + sGetGroupList);
    }

    private enum TAB {GROUP_LIST, TOP_100}

    ;
    private TAB mCurrentTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_rooms_activity_layout);


        initViews();
        initHandler();
        mOnlineConnectionManager = Controller.getInstance().getOnlineWorker();
    }

    private void initViews() {
        mOnlineGroupsFragment = new OnlineRoomsFragment();
        mTop100Fragment = new Top100Fragment();
        mTop100Action = (Top100Action) mTop100Fragment;
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.center_for_fragment, mOnlineGroupsFragment);
        mFragmentTransaction.add(R.id.center_for_fragment, mTop100Fragment);
        mFragmentTransaction.hide(mTop100Fragment);
        mOnlineRoomsFragmentAction = (OnlineRoomsFragmentAction) mOnlineGroupsFragment;
        mFragmentTransaction.commit();
        mCurrentTab = TAB.GROUP_LIST;
        findViewById(R.id.btn_back_from_select_type_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackFromOnlineGame();
            }
        });


    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CGETGROUPLIST:
                        mOnlineRoomsFragmentAction.gotGroupList(msg.obj);
                        break;
                    case CTOP100:
                        Protocol.CTop100Player cTop100Player = (Protocol.CTop100Player) msg.obj;
                        List<Player> playerList = new ArrayList<Player>();
                        for (Protocol.Player protocolPlayer : cTop100Player.getPlayerList()) {
                            playerList.add(new Player(protocolPlayer.getId(), protocolPlayer.getName(), protocolPlayer.getRating()));
                        }
                        mTop100Action.receivedListTop100(playerList);
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
    }


    @Override
    public AdView getAdView() {
        return mOnlineRoomsFragmentAction.getAdView();
    }

    private void switchToTab(TAB tab) {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mCurrentTab = tab;
        switch (tab) {
            case GROUP_LIST:

                mFragmentTransaction.hide(mTop100Fragment);
                mFragmentTransaction.show(mOnlineGroupsFragment);
                break;
            case TOP_100:

                mFragmentTransaction.hide(mOnlineGroupsFragment);
                mFragmentTransaction.show(mTop100Fragment);
                sendPacketGetTop100List();
                break;
        }
        mFragmentTransaction.commit();
    }

    private void sendPacketGetTop100List() {
        mOnlineConnectionManager
                .sendPacket(Protocol
                        .STop100Player
                        .newBuilder()
                        .setPlayerId(mPlayer.getId())
                        .build());
    }

    @Override
    public void onBackPressed() {
        if (mCurrentTab == TAB.TOP_100) {
            switchToTab(TAB.GROUP_LIST);
        } else {
            showBackFromOnlineGame();
        }
    }

    private void showBackFromOnlineGame() {
        GeneralDialog generalDialog = new GeneralDialog.Builder(this)
                .setTitleTextId(R.string.exit_from_this_game)
                .setContentText(getString(R.string.exit_from_game_online_question))
                .setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .build();
        generalDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOnlineConnectionManager == null) {
            return;
        }
        if (!mOnlineConnectionManager.isSockedInLive()) {
            finish();
        }
        mOnlineConnectionManager.registerHandler(mHandler);
        if (mOnlineConnectionManager != null) {
            getListOfGroup();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOnlineConnectionManager != null) {
            mOnlineConnectionManager.unRegisterHandler(mHandler);
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
        if (mOnlineConnectionManager == null) return;
        mOnlineConnectionManager.sendPacket(Protocol.SExitFromGlobalGame.newBuilder().setPlayerId(mPlayer.getId()).build());
        mOnlineConnectionManager.unRegisterHandler(mHandler);
        mOnlineConnectionManager.stopManager();
        super.onDestroy();
    }
}
