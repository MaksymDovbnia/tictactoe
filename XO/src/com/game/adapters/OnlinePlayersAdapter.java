package com.game.adapters;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.Player;
import com.game.activity.R;
import com.utils.Loger;

public class OnlinePlayersAdapter extends BaseAdapter {
	private Context context;
	private List<Player> players;
	private LayoutInflater layoutInflater;
	private int idLast = Integer.MIN_VALUE;
	List<TextView> listView = new ArrayList<TextView>();
	List<View> views = new ArrayList<View>();

	public OnlinePlayersAdapter(Context context, List<Player> players) {

		this.context = context;
		this.players = players;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return players.size();
	}

	public int getIdLast() {
		return idLast;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return players.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.listactivityplayers_item,
					parent, false);
			Loger.printLog(" NEW !getWiev ");
			views.add(view);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					idLast = ((Integer) v.getTag());
					for (View view : views) {
						view.setBackgroundColor(color.primary_text_light);
					}
					v.setBackgroundColor(Color.BLUE);
					Loger.printLog("CLICK " + idLast);

				}
			});

		}
		Loger.printLog("getWiev " + position);
		TextView text = ((TextView) view.findViewById(R.id.playerName));
		text.setText(players.get(position).getName());
		view.setBackgroundColor(color.primary_text_light);
		view.setTag(position);
		return view;
	}

}