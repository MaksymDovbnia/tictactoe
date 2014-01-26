package com.bigtictactoeonlinegame.onlinerooms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.entity.Group;
import com.bigtictactoeonlinegame.activity.R;
import com.bigtictactoeonlinegame.openedroom.OnlineOpenedRoomActivity;

import com.utils.Loger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OnlineRoomsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Group> groups;
    private int idLast;

    List<View> views = new ArrayList<View>();

    public OnlineRoomsAdapter(Context context, List<Group> groups) {
        this.groups = groups;
        this.context = context;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return groups.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    public int getIdLast() {
        return idLast;
    }

    // TODO 07.06.13 01.25-
    @Override
    public View getView(int position, View arg1, ViewGroup parent) {
        View view = arg1;
        if (view == null)
            view = layoutInflater.inflate(R.layout.online_group_list_item, parent, false);

        TextView name = (TextView) view.findViewById(R.id.tv_online_group_item_name);
        TextView count = (TextView) view.findViewById(R.id.tv_count_of_online_players_in_group);

        Group group = groups.get(position);
//        for (Group group1 : groups) {
//            if (group1.getId() == position) {
//                group = group1;
//            }
//        }
        if (group != null) {
            name.setText(group.getId() + "");
            count.setText(group.getCountOfOnlinePlayer() + "/" + group.getCountOfMaxPlayer());
        }
        views.add(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idLast = ((Integer) v.getTag());
                Intent intent = new Intent(context, OnlineOpenedRoomActivity.class);
                intent.putExtra(OnlineRoomsFragment.NUMBER_OF_GROUP, idLast);
                context.startActivity(intent);
                Loger.printLog("CLICK " + idLast);


            }
        });


        view.setTag(group.getId());

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(groups,new GroupComparator());
        super.notifyDataSetChanged();
    }

    public class GroupComparator implements Comparator<Group> {
        @Override
        public int compare(Group group, Group group2) {
            int idGroup1 = group.getId();
            int idGroup2 = group2.getId();
            if (idGroup1 < idGroup2) return  -1;
            else if(idGroup1 == idGroup2){ return 0;}
            return 1;
        }
    }

}
