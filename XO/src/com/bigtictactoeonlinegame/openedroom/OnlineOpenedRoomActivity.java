package com.bigtictactoeonlinegame.openedroom;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bigtictactoeonlinegame.chat.ChatActionNotification;
import com.bigtictactoeonlinegame.chat.ChatMessage;
import com.bigtictactoeonlinegame.onlinerooms.OnlineRoomsFragment;
import com.bigtictactoeonlinegame.popup.XOAlertDialog;
import com.entity.Player;
import com.bigtictactoeonlinegame.Controller;
import com.bigtictactoeonlinegame.activity.R;
import com.bigtictactoeonlinegame.chat.ChatAction;
import com.bigtictactoeonlinegame.chat.ChatFragment;
import com.bigtictactoeonlinegame.mainactivity.GeneralAdActivity;
import com.google.android.gms.ads.AdView;
import com.net.online.protobuf.ProtoType;

import net.protocol.Protocol;

/**
 * @author Maksym Dovbnia on 6/19/13.
 */
public class OnlineOpenedRoomActivity extends GeneralAdActivity implements View.OnClickListener, ChatActionNotification {
    private Fragment openedGroupFragment;
    private ChatFragment chatFragment;
    private FragmentTransaction fragmentTransaction;
    private Button openGroup;
    private Button openChat;
    private Handler handler;
    private IOnlineOpenedRoomAction openedGroupAction;
    private ChatAction chatAction;

    private enum TAB {OPENED_GROUP, CHAT}

    private TAB currentTab;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opened_activity_layout);
        openChat = (Button) findViewById(R.id.btn_group_chat);
        openGroup = (Button) findViewById(R.id.btn_opened_online_group);
        openChat.setOnClickListener(this);
        openGroup.setOnClickListener(this);
        int groupId = getIntent().getIntExtra(OnlineRoomsFragment.NUMBER_OF_GROUP, 0);
        Controller.getInstance().getPlayer().setGroupId(groupId);
        openGroup.setText(getString(R.string.room) + " " + groupId);
        setGroupFragment();
        initFragment();
        initHandler();
    }

    @Override
    public AdView getAdView() {
        return openedGroupAction.getAdView();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CUPDATEAOBOUTACTIVITYPLAYER:
                        openedGroupAction.updateAboutActivityPlayer(msg);
                        break;
                    case CWANTTOPLAY:
                        openedGroupAction.wantToPlay(msg);
                        break;
                    case CSTARTGAME:
                        openedGroupAction.startGame(msg);
                        break;
                    case CCANCELDESIREPLAY:
                        openedGroupAction.cancelPlayDesire(msg);
                        break;
                    case CGROUPCHATMESSAGE:
                        Protocol.CGroupChatMessage cGroupChatMessage = (Protocol.CGroupChatMessage) msg.obj;
                        int senderID = cGroupChatMessage.getPlayerId();
                        String senderName = "anonymous";
                        for (Player player : openedGroupAction.getListActivePlayer()) {
                            if (player.getId() == senderID) {
                                senderName = player.getName();
                                break;
                            }
                        }
                        if (cGroupChatMessage != null) {
                            chatAction.receivedMessage(new ChatMessage(cGroupChatMessage.getMessage(), senderName));
                        }
                        if (currentTab == TAB.OPENED_GROUP) {
                            openChat.setText(R.string.message);
                            openChat.setTextColor(getResources().getColor(R.color.blue));
                        }
                        break;
                    case CONNECTION_TO_SERVER_LOST:
                        connectionToServerLost();
                }
            }
        };
    }

    private void setGroupFragment() {
    }

    private void initFragment() {
        openedGroupFragment = new OnlineOpenedRoomFragment();
        openedGroupAction = (IOnlineOpenedRoomAction) openedGroupFragment;
        chatFragment = new ChatFragment();
        chatAction = chatFragment;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, openedGroupFragment);
        fragmentTransaction.add(R.id.center_for_fragment, chatFragment);
        fragmentTransaction.hide(chatFragment);
        fragmentTransaction.show(openedGroupFragment);
        fragmentTransaction.commit();
        currentTab = TAB.OPENED_GROUP;
    }

    private void switchToFragment(TAB tab) {
        currentTab = tab;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (tab) {
            case OPENED_GROUP:
                fragmentTransaction.hide(chatFragment);
                fragmentTransaction.show(openedGroupFragment);
                break;
            case CHAT:
                fragmentTransaction.hide(openedGroupFragment);
                fragmentTransaction.show(chatFragment);
                break;
        }
        fragmentTransaction.commit();
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
    protected void onResume() {
        Controller.getInstance().getOnlineWorker().registerHandler(handler);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Controller.getInstance().getOnlineWorker().unRegisterHandler(handler);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (currentTab == TAB.CHAT) {
            switchToFragment(TAB.OPENED_GROUP);
        } else {
            XOAlertDialog xoAlertDialog = new XOAlertDialog();
            xoAlertDialog.setTile(getResources().getString(R.string.exit_from_room));
            xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_room_question));
            xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
            xoAlertDialog.setNegativeButtonText((getResources().getString(R.string.no)));
            xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Controller.getInstance().getOnlineWorker().
                            sendPacket(Protocol.SExitFromGroup.newBuilder().setPlayerId(Controller.getInstance().getPlayer().getId()).setGroupId(Controller.getInstance().getPlayer().getGroupId()).build());
                    finish();
                }
            });
            xoAlertDialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_opened_online_group:
                switchToFragment(TAB.OPENED_GROUP);
                break;
            case R.id.btn_group_chat:
                switchToFragment(TAB.CHAT);
                openChat.setText(R.string.chat);
                openChat.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    @Override
    public void actionSendChatMessage(ChatMessage chatMessage) {
        Controller.getInstance().getOnlineWorker().sendPacket(Protocol.SGroupChatMessage
                .newBuilder().setMessage(chatMessage.getMessage())
                .setGroupId(Controller.getInstance().getPlayer().getGroupId())
                .setPlayerId(Controller.getInstance().getPlayer().getId())
                .build());
    }

    @Override
    public String getPlayerName() {
        return Controller.getInstance().getPlayer().getName();
    }
}