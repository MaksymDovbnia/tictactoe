package com.bigtictactoeonlinegame.onlinerooms;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Group;
import com.entity.Player;
import com.bigtictactoeonlinegame.Controller;
import com.bigtictactoeonlinegame.activity.R;
import com.google.android.gms.ads.AdView;

import net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 9/3/13.
 */
public class OnlineRoomsFragment extends Fragment implements OnlineRoomsFragmentAction {
    public static final String TAG = OnlineRoomsFragment.class.getCanonicalName();
    private ListView listViewOnlineGroup;
    private List<Group> groups;
    private OnlineRoomsAdapter adapter;
    private final Player player = Controller.getInstance().getPlayer();
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private TextView allPlayers;
    private int numberOfAllPlayers = 0;
    private Activity activity;
    private IOnlineRoomsAction iOnlineRoomsAction;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        iOnlineRoomsAction = (IOnlineRoomsAction) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_rooms_fragment_layout, null);
        listViewOnlineGroup = (ListView) view.findViewById(R.id.listOnlineGroup);
        groups = new ArrayList<Group>();
        allPlayers = (TextView) view.findViewById(R.id.tv_all_online_players);
        adapter = new OnlineRoomsAdapter(activity, groups);
        listViewOnlineGroup.setAdapter(adapter);
        return view;
    }

    private void getListOfGroup() {
        iOnlineRoomsAction.getListOfGroup();
    }

    @Override
    public void getGroupList(Object o) {
        numberOfAllPlayers = 0;
        groups.clear();
        Protocol.CGetGroupList getGroupList = (Protocol.CGetGroupList) o;
        for (Protocol.Group group : getGroupList.getGroupList()) {
            numberOfAllPlayers += group.getNumOnlinePlayers();
            groups.add(new Group(group.getGroupId(), group.getNumOnlinePlayers(), 50));
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

    @Override
    public AdView getAdView() {
        return (AdView) getView().findViewById(R.id.ad_view);
    }
}
