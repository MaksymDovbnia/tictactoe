package com.game.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Group;
import com.entity.Player;
import com.game.Controler;
import com.game.activity.OnlineOpenedGroupActivity;
import com.game.activity.R;
import com.game.adapters.OnlineGroupAdapter;
import com.game.popup.XOAlertDialog;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 9/3/13.
 */
public class OnlineGroupsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = OnlineGroupsFragment.class.getCanonicalName();
    private Handler handler;
    private WorkerOnlineConnection conectionGameWorker;
    private ListView listViewOnlineGroup;
    private List<Group> groups;
    private OnlineGroupAdapter adapter;
    private final Player player = Controler.getPlayer();
    private Button openGroup;
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private TextView allPlayers;
    private int numberOfAllPlayers = 0;
    private Activity activity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_group_fragment_layout, null);

        listViewOnlineGroup = (ListView) view.findViewById(R.id.listOnlineGroup);
        openGroup = (Button) view.findViewById(R.id.b_open_group);
        openGroup.setOnClickListener(this);
        groups = new ArrayList<Group>();
        allPlayers = (TextView) view.findViewById(R.id.tv_all_online_players);


        adapter = new OnlineGroupAdapter(activity, groups);

        listViewOnlineGroup.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CGETGROUPLIST:
                        numberOfAllPlayers = 0;
                        groups.clear();
                        Protocol.CGetGroupList getGroupList = (Protocol.CGetGroupList) msg.obj;
                        for (Protocol.Group group : getGroupList.getGroupList()) {
                            numberOfAllPlayers += group.getNumOnlinePlayers();
                            groups.add(new Group(group.getGroupId(), group.getNumOnlinePlayers(), 100));
                        }
                        for (int i = 0; i < 30; i++) {
                            Group group = new Group(i, i * 2, i * 20);
                            groups.add(group);
                            adapter.notifyDataSetChanged();

                        }
                        Log.d(TAG, "get  " + groups.size() + "  groups");
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

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.b_open_group:
                Intent intent = new Intent(activity, OnlineOpenedGroupActivity.class);
                intent.putExtra(NUMBER_OF_GROUP, adapter.getIdLast());
                startActivity(intent);
                break;
        }


    }


    @Override
    public void onResume() {
        if (conectionGameWorker != null) {
            conectionGameWorker.registerHandler(handler);
            getListOfGroup();
        }
        super.onResume();
    }

    private void getListOfGroup() {

        Protocol.SGetGroupList sGetGroupList = Protocol.SGetGroupList.newBuilder().setId(player.getId()).build();
        conectionGameWorker.sendPacket(sGetGroupList);
        Log.d(TAG, "sent packet " + sGetGroupList);
    }


    @Override
    public void onPause() {
        if (conectionGameWorker != null) conectionGameWorker.unRegisterHandler(handler);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (conectionGameWorker == null) return;;
            conectionGameWorker.sendPacket(Protocol.SExitFromGlobalGame.newBuilder().setPlayerId(player.getId()).build());
       conectionGameWorker.unRegisterHandler(handler);
        super.onDestroy();
    }
}
