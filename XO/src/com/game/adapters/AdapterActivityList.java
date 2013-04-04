package com.game.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.entity.Player;
import com.game.activity.R;

public class AdapterActivityList extends ArrayAdapter<Player> {
	private List<Player> players;
	private Context context;
	private LayoutInflater lInflater;

	public AdapterActivityList(Context context, List<Player> players) {
		super(context, R.layout.my_list_item, players);
		this.players = players;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.my_list_item, parent, false);
		TextView textView = (TextView) rowView
				.findViewById(R.id.textViewMainList);
		// textView.setText("111");
		textView.setText(players.get(position).getName());
		// textView.setBackgroundColor(Color.BLUE);

		return rowView;
	}

}
