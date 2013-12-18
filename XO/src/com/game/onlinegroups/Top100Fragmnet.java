package com.game.onlinegroups;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Player;
import com.game.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maksym on 17.11.13.
 */
public class Top100Fragmnet extends Fragment implements Top100Action {

    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private List<Player> players;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_100_fragment_layout, null);
        players = new ArrayList<Player>();
        listViewAdapter = new ListViewAdapter(players, getActivity());
        listView = (ListView) view.findViewById(R.id.list_view_top_100);
        View listViewHeader = inflater.inflate(R.layout.top_100_list_view_header, null);
        listView.addHeaderView(listViewHeader);
        listView.setAdapter(listViewAdapter);


//        generateTestData();
        return view;
    }

    @Override
    public void receivedListTop100(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        listViewAdapter.notifyDataSetChanged();
    }

    private void generateTestData() {
        for (int i = 0; i < 100; i++) {
            players.add(new Player(1, "Player " + i, i * 10));
        }
        listViewAdapter.notifyDataSetChanged();
    }

    private static class ListViewAdapter extends BaseAdapter {
        private List<Player> players;
        private LayoutInflater layoutInflater;

        private ListViewAdapter(List<Player> players, Context context) {
            this.players = players;
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.top_100_list_item_view, parent, false);
            }
            TextView playerPosition, name, rating;
            playerPosition = (TextView) view.findViewById(R.id.tv_position);
            name = (TextView) view.findViewById(R.id.tv_player_name);
            rating = (TextView) view.findViewById(R.id.tv_player_rating);
            if (players.get(position) != null) {
                Player player = players.get(position);
                name.setText(player.getName() + "");
                rating.setText(player.getRating() + "");
                playerPosition.setText((position + 1) + "");

            }


            return view;
        }
    }
}
