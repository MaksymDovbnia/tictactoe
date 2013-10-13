package com.game.activity;

import com.game.fragments.OnlineGroupsFragment;
import com.game.popup.XOAlertDialog;

import android.content.DialogInterface;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

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
