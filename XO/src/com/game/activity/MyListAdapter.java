package com.game.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.Player;

public class MyListAdapter extends BaseAdapter {
	private List<Player> players;
	private Context context;
	LayoutInflater lInflater;

	public MyListAdapter(Context context, List<Player> players) {
		this.players = players;
		this.context = context;

		lInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void changeListPlayers(List<Player> players) {

	}

	@Override
	public int getCount() {
		return players.size();
	}

	@Override
	public Object getItem(int arg0) {
		return players.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {

			view = lInflater.inflate(R.layout.my_list_item, parent, false);
		}
		Player player = players.get(position);
		// ( (TextView) view.findViewById(R.id.tvPrice)
		// ).setText(players.get(position).getName());

		return view;
	}

}
