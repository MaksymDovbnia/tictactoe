package com.game.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.entity.Group;
import com.game.activity.R;
import com.utils.Loger;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OnlineGroupAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Group> groups;
    private int idLast;
    private Button openedGroup;
    List<View> views = new ArrayList<View>();

    public OnlineGroupAdapter(Context context, List<Group> groups) {
        this.groups = groups;
        this.context = context;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public void setOpenedGroupButton(Button openedGroup){
        this.openedGroup = openedGroup;
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
                if (openedGroup != null) openedGroup.setEnabled(true);
                idLast = ((Integer) v.getTag());
                for (View view : views) {
                    view.setBackgroundColor(color.primary_text_light);
                }
                v.setBackgroundColor(Color.BLUE);
                Loger.printLog("CLICK " + idLast);
                // TODO Auto-generated method stub
            }
        });

        view.setBackgroundColor(color.primary_text_light);
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
