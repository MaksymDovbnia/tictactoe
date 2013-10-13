package com.game.activity;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.game.Controller;
import com.game.fragments.GroupChatFragment;
import com.game.fragments.OnlineOpenedGroupFragment;
import com.game.popup.XOAlertDialog;

import net.protocol.Protocol;

/**
 * Created by Maksym on 6/19/13.
 */
public class OnlineOpenedGroupActivity extends FragmentActivity implements View.OnClickListener {

    private Fragment openedGroupFragment;
    private GroupChatFragment chatFragment;
    private FragmentTransaction fragmentTransaction;
    private Button openGroup;
    private Button openChat;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opened_activity_layout);
        openChat  = (Button) findViewById(R.id.btn_group_chat);
        openGroup = (Button) findViewById(R.id.btn_opened_online_group);
        openChat.setOnClickListener(this);
        openGroup.setOnClickListener(this);
        openedGroupFragment = new OnlineOpenedGroupFragment();
        chatFragment = new GroupChatFragment();
        int groupId =Controller.getInstance().getPlayer().getGroupId();
        openGroup.setText(getString(R.string.opened_group) + " " + groupId);
        setGroupFragment();


    }

    private void setGroupFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, openedGroupFragment);
        fragmentTransaction.commit();
    }

    private void replaceToGroupFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.center_for_fragment, openedGroupFragment);
        fragmentTransaction.commit();
    }


    private void replaceToChatFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.center_for_fragment, chatFragment);
        fragmentTransaction.commit();


    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_group));
        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_group_question));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.exit));
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_opened_online_group:
                replaceToGroupFragment();
                break;
            case R.id.btn_group_chat:
                replaceToChatFragment();
                break;

        }

    }
}