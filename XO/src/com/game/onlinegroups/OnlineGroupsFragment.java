package com.game.onlinegroups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.game.Controller;
import com.game.activity.OnlineOpenedRoomActivity;
import com.game.activity.R;
import com.game.adapters.OnlineGroupAdapter;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 9/3/13.
 */
public class OnlineGroupsFragment extends Fragment implements View.OnClickListener, OnlineGrpoupsFragmentAction {
    public static final String TAG = OnlineGroupsFragment.class.getCanonicalName();

    private ListView listViewOnlineGroup;
    private List<Group> groups;
    private OnlineGroupAdapter adapter;
    private final Player player = Controller.getInstance().getPlayer();
    private Button openGroup;
    private Button updateGroupList;
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private TextView allPlayers;
    private int numberOfAllPlayers = 0;
    private Activity activity;
    private IOnlineGroupsAction iOnlineGroupsAction;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        iOnlineGroupsAction = (IOnlineGroupsAction) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_group_fragment_layout, null);

        listViewOnlineGroup = (ListView) view.findViewById(R.id.listOnlineGroup);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.online_groups_list_view_header_layout, listViewOnlineGroup, false);
        listViewOnlineGroup.addHeaderView(header);
        openGroup = (Button) view.findViewById(R.id.b_open_group);
        openGroup.setOnClickListener(this);
        openGroup.setEnabled(false);
        updateGroupList = (Button) view.findViewById(R.id.btn_update_group_list);
        updateGroupList.setOnClickListener(this);
        groups = new ArrayList<Group>();
        allPlayers = (TextView) view.findViewById(R.id.tv_all_online_players);

        adapter = new OnlineGroupAdapter(activity, groups);
        adapter.setOpenedGroupButton(openGroup);

        listViewOnlineGroup.setAdapter(adapter);


        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.b_open_group:
                Intent intent = new Intent(activity, OnlineOpenedRoomActivity.class);
                intent.putExtra(NUMBER_OF_GROUP, adapter.getIdLast());
                startActivity(intent);
                break;
            case R.id.btn_update_group_list:
                getListOfGroup();
                break;
        }
    }


    private void getListOfGroup() {
        iOnlineGroupsAction.getListOfGroup();
    }


    @Override
    public void getGroupList(Object o) {
        numberOfAllPlayers = 0;
        groups.clear();
        Protocol.CGetGroupList getGroupList = (Protocol.CGetGroupList) o;
        for (Protocol.Group group : getGroupList.getGroupList()) {
            numberOfAllPlayers += group.getNumOnlinePlayers();
            groups.add(new Group(group.getGroupId(), group.getNumOnlinePlayers(), 100));
        }
//                        for (int i = 0; i < 30; i++) {
//                            Group group = new Group(i, i * 2, i * 20);
//                            groups.add(group);
//                            adapter.notifyDataSetChanged();
//
//                        }
        Log.d(TAG, "get  " + groups.size() + "  groups");
        numberOfAllPlayers += 1;
        allPlayers.setText(getResources().getString(R.string.all_online_players) + " " + numberOfAllPlayers);
        adapter.notifyDataSetChanged();
    }
}
