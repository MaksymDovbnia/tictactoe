package com.bigtictactoeonlinegame.onlinerooms;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.activity.*;
import com.entity.*;

import java.util.*;

/**
 * Date: 07.11.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class Top100Fragment extends Fragment implements Top100Action {

    private ListViewAdapter mListViewAdapter;
    private List<Player> mPlayersList;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_100_fragment_layout, null);
        mPlayersList = new ArrayList<Player>();
        mListViewAdapter = new ListViewAdapter(mPlayersList, getActivity());
        ListView listView = (ListView) view.findViewById(R.id.list_view_top_100);
        listView.setAdapter(mListViewAdapter);
        return view;
    }

    @Override
    public void receivedListTop100(List<Player> players) {
        getView().findViewById(R.id.ll_updating_top100).setVisibility(View.GONE);
        this.mPlayersList.clear();
        this.mPlayersList.addAll(players);
        mListViewAdapter.notifyDataSetChanged();
    }

    private void generateTestData() {
        for (int i = 0; i < 100; i++) {
            mPlayersList.add(new Player(1, "Player " + i, i * 10));
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    private static class ListViewAdapter extends ArrayAdapter<Player> {
        private List<Player> players;
        private LayoutInflater layoutInflater;

        private ListViewAdapter(List<Player> players, Context context) {
            super(context, R.layout.top_100_list_item_view, players);
            this.players = players;
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.top_100_list_item_view, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.playerName = (TextView) view.findViewById(R.id.tv_player_name);
                viewHolder.playerPosition = (TextView) view.findViewById(R.id.tv_position);
                viewHolder.playerRating = (TextView) view.findViewById(R.id.tv_player_rating);
                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (players.get(position) != null) {
                Player player = players.get(position);
                viewHolder.playerName.setText(player.getName());
                viewHolder.playerRating.setText("" + player.getRating());
                viewHolder.playerPosition.setText("" + (position + 1));
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView playerPosition;
        TextView playerName;
        TextView playerRating;
    }
}
