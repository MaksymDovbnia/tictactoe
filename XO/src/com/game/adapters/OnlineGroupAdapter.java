package com.game.adapters;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;
import com.game.activity.R;

public class OnlineGroupAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater layoutInflater;
	private List <Group> groups;
    private int idLast;
    List<View> views = new ArrayList<View>();
	public OnlineGroupAdapter(Context context, List <Group> groups) {
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
		if (view==null)
	    view = layoutInflater.inflate(R.layout.online_group_list_item, parent, false);
		
		TextView name = (TextView) view.findViewById(R.id.textView_online_group_item_name);
		TextView count = (TextView) view.findViewById(R.id.textViewCountOfOnlinePlayersInGroup);

        Group group = null;
        for (Group group1: groups){
            if (group1.getId() == position){
                group = group1;
            }

        }
		if (group!=null) name.setText(group.getId()+"");
	    count.setText(group.getCountOfOnlinePlayer() + "/" + group.getCountOfMaxPlayer());
		views.add(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        view.setTag(position);
		
		return view;
	}

}
