package com.game.activity;

import java.util.ArrayList;
import java.util.List;

import net.protocol.Protocol;
import net.protocol.Protocol.CGetGroupList;

import com.entity.Group;
import com.entity.Player;
import com.game.Controler;
import com.game.adapters.OnlineGroupAdapter;
import com.game.popup.PositiveButtonListener;
import com.game.popup.XOAlertDialog;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineGroupsActivity extends FragmentActivity implements View.OnClickListener {
   private static final String TAG = OnlineGroupsActivity.class.getCanonicalName();
    private Handler handler;
    private WorkerOnlineConnection conectionGameWorker;
    private ListView listViewOnlineGroup;
    private List<Group> groups;
    private OnlineGroupAdapter adapter;
    private final Player player = Controler.getPlayer();
    private Button openGroup;
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private TextView allPlayers;
    private  int numberOfAllPlayers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_group);
        listViewOnlineGroup = (ListView) findViewById(R.id.listOnlineGroup);
        openGroup = (Button) findViewById(R.id.b_open_group);
        openGroup.setOnClickListener(this);
        groups = new ArrayList<Group>();
        allPlayers = (TextView) findViewById(R.id.tv_all_online_palyers);
//		for (int i=0; i < 30; i++)
//		{
//			Group group = new Group("group " +i ,i*2 , i*20);
//			groups.add(group);
//			
//		}

        adapter = new OnlineGroupAdapter(this, groups);

        listViewOnlineGroup.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CGETGROUPLIST:
                        numberOfAllPlayers =0;
                        groups.clear();
                        CGetGroupList getGroupList = (CGetGroupList) msg.obj;
                        for (Protocol.Group group : getGroupList.getGroupList()) {
                            numberOfAllPlayers += group.getNumOnlinePlayers();
                            groups.add(new Group(group.getGroupId(), group.getNumOnlinePlayers(), 100));
                        }
                        Log.d(TAG,"get  " + groups.size() +"  groups");
                        numberOfAllPlayers += 1;
                        allPlayers.setText(getResources().getString(R.string.all_online_players) + " " + numberOfAllPlayers);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

                // TODO Auto-generated method stub
                super.handleMessage(msg);
            }


        };

        conectionGameWorker = Controler.getOnl();



    }

    @Override
    protected void onResume() {
        if (conectionGameWorker != null) {
            conectionGameWorker.registerHandler(handler);
            getListOfGroup();
        }
        super.onResume();
    }

    private void getListOfGroup() {

        Protocol.SGetGroupList sGetGroupList = Protocol.SGetGroupList.newBuilder().setId(player.getId()).build();
        conectionGameWorker.sendPacket(sGetGroupList);
        Log.d(TAG, "sent packet "  + sGetGroupList);
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
    protected void onPause() {
        if (conectionGameWorker != null) conectionGameWorker.unRegisterHandler(handler);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        conectionGameWorker.sendPacket(Protocol.SExitFromGlobalGame.newBuilder().setPlayerId(player.getId()).build());
        if (conectionGameWorker != null) conectionGameWorker.unRegisterHandler(handler);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.b_open_group:
                Intent intent = new Intent(this, OnlineOpenedGroupActivity.class);
                intent.putExtra(NUMBER_OF_GROUP, adapter.getIdLast());
                startActivity(intent);
                break;
        }


    }
}
