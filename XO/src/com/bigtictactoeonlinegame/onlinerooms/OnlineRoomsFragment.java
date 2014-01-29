package com.bigtictactoeonlinegame.onlinerooms;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.activity.*;
import com.entity.*;
import com.google.android.gms.ads.*;

import net.protocol.*;

import java.util.*;

/**
 * Date: 09.03.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class OnlineRoomsFragment extends Fragment implements OnlineRoomsFragmentAction {
    public static final String LOG_TAG = OnlineRoomsFragment.class.getCanonicalName();
    public static final String NUMBER_OF_GROUP = "NUMBER_OF_GROUP";
    private List<Group> mGroups;
    private OnlineRoomsAdapter mAdapter;
    private TextView mTextViewAllPlayers;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_rooms_fragment_layout, null);
        ListView listViewOnlineGroup = (ListView) view.findViewById(R.id.list_online_group);
        mGroups = new ArrayList<Group>();
        mTextViewAllPlayers = (TextView) view.findViewById(R.id.tv_all_online_players);
        mAdapter = new OnlineRoomsAdapter(getActivity(), mGroups);
        listViewOnlineGroup.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void gotGroupList(Object o) {
        getView().findViewById(R.id.ll_updating_rooms_list).setVisibility(View.GONE);
        int numberOfAllPlayers = 0;
        mGroups.clear();
        Protocol.CGetGroupList getGroupList = (Protocol.CGetGroupList) o;
        for (Protocol.Group group : getGroupList.getGroupList()) {
            numberOfAllPlayers += group.getNumOnlinePlayers();
            mGroups.add(new Group(group.getGroupId(), group.getNumOnlinePlayers(), 50));
        }
//                        for (int i = 0; i < 30; i++) {
//                            Group group = new Group(i, i * 2, i * 20);
//                            mGroups.add(group);
//                            mAdapter.notifyDataSetChanged();
//
//                        }
        Log.d(LOG_TAG, "get  " + mGroups.size() + "  mGroups");
        numberOfAllPlayers += 1;
        mTextViewAllPlayers.setText(getResources().getString(R.string.all_online_players) + " " + numberOfAllPlayers);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public AdView getAdView() {
        return (AdView) getView().findViewById(R.id.ad_view);
    }
}
