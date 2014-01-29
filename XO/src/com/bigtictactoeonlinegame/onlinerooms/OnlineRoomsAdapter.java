package com.bigtictactoeonlinegame.onlinerooms;

import android.content.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.openedroom.*;
import com.entity.*;
import com.utils.*;

import java.util.*;

public class OnlineRoomsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Group> mGroups;
    private int mIdLast;

    public OnlineRoomsAdapter(Context context, List<Group> groups) {
        mGroups = groups;
        mContext = context;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mGroups.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }


    // TODO 07.06.13 01.25-
    @SuppressWarnings("ConstantConditions")
    @Override
    public View getView(int position, View arg1, ViewGroup parent) {
        View view = arg1;
        if (view == null)
            view = mLayoutInflater.inflate(R.layout.online_group_list_item, parent, false);

        TextView name = (TextView) view.findViewById(R.id.tv_online_group_item_name);
        TextView count = (TextView) view.findViewById(R.id.tv_count_of_online_players_in_group);

        Group group = mGroups.get(position);
        if (group != null) {
            name.setText(String.valueOf(group.getId()));
            count.setText(group.getCountOfOnlinePlayer() + "/" + group.getCountOfMaxPlayer());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIdLast = ((Integer) v.getTag());
                Intent intent = new Intent(mContext, OnlineOpenedRoomActivity.class);
                intent.putExtra(OnlineRoomsFragment.NUMBER_OF_GROUP, mIdLast);
                mContext.startActivity(intent);
                Loger.printLog("CLICK " + mIdLast);


            }
        });


        view.setTag(group.getId());

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(mGroups, new GroupComparator());
        super.notifyDataSetChanged();
    }

    private static class GroupComparator implements Comparator<Group> {
        @Override
        public int compare(Group group, Group group2) {
            int idGroup1 = group.getId();
            int idGroup2 = group2.getId();
            if (idGroup1 < idGroup2) return -1;
            else if (idGroup1 == idGroup2) {
                return 0;
            }
            return 1;
        }
    }

}
