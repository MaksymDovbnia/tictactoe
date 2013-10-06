package com.game.activity;

import java.util.ArrayList;
import java.util.List;

import net.protocol.Protocol;
import net.protocol.Protocol.CGetGroupList;

import com.entity.Group;
import com.entity.Player;
import com.game.Controler;
import com.game.adapters.OnlineGroupAdapter;
import com.game.fragments.OnlineGroupsFragment;
import com.game.popup.XOAlertDialog;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;

import android.content.DialogInterface;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineGroupsActivity extends FragmentActivity implements View.OnClickListener {
   private static final String TAG = OnlineGroupsActivity.class.getCanonicalName();
   public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
   private Fragment onlineGroupsFragment, top100Fgragment;
    private FragmentTransaction fragmentTransaction;
   private enum TAB{GROUP_LIST, TOP100};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_group_activity_layout);
        onlineGroupsFragment = new OnlineGroupsFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, onlineGroupsFragment);
        fragmentTransaction.commit();
    }



   private void switchToTab(TAB tab){
       fragmentTransaction = getSupportFragmentManager().beginTransaction();
       switch (tab){
           case GROUP_LIST:
               fragmentTransaction.replace(R.id.center_for_fragment, onlineGroupsFragment);
               break;
           case TOP100:
               fragmentTransaction.replace(R.id.center_for_fragment, onlineGroupsFragment);
               break;
       }
       fragmentTransaction.commit();
   }
    @Override
    public void onBackPressed() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_online_game));
        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_game_online_question));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.exit));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }
    @Override
    public void onClick(View view) {

    }


    }
