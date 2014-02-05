package com.bigtictactoeonlinegame.openedroom;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.entity.*;

import net.protocol.*;

import java.util.*;

public class OnlinePlayersAdapter extends BaseAdapter {
    private static final String LOG_TAG = OnlinePlayersAdapter.class.getName();
    private Context context;
    private Player myPlayer;
    private List<Player> players;
    private LayoutInflater layoutInflater;
    protected int pos = Integer.MIN_VALUE;
    private Map<Integer, Boolean> booleanMap;

    public OnlinePlayersAdapter(Context context, List<Player> players) {
        this.context = context;
        this.players = players;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myPlayer = Controller.getInstance().getPlayer();
        booleanMap = new HashMap<Integer, Boolean>();
    }

    @Override
    public int getCount() {
        return players.size();
    }

    public int getIdLast() {
        return pos;
    }


    public void removePlayer(Player player) {
        int index = players.indexOf(player);
        booleanMap.remove(index);
        players.remove(player);
        //  notifyDataSetChanged();
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
        // Log.d(LOG_TAG, "getView pos: " + position + " " + convertView);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.online_players_list_item, parent, false);
            viewHolder.name = ((TextView) convertView.findViewById(R.id.tv_player_name));
            viewHolder.rating = ((TextView) convertView.findViewById(R.id.tv_player_rating));
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box_invite_to_play);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new ItemClickListener(position));
        viewHolder.name.setText(players.get(position).getName());
        viewHolder.rating.setText("" + players.get(position).getRating());
        viewHolder.position = position;
        if (booleanMap.containsKey(position)) {
            viewHolder.checkBox.setChecked(booleanMap.get(position));
        } else {
            viewHolder.checkBox.setChecked(false);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        int position;
        TextView rating;
        CheckBox checkBox;
        boolean isChecked;
    }

    private class ItemClickListener implements OnClickListener {
        private int position;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            pos = ((ViewHolder) v.getTag()).position;
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.check_box_invite_to_play);
            checkBox.setChecked(!checkBox.isChecked());
            Player player = null;
            if (players.size() > 0 && pos >= 0) {
                player = players.get(pos);
            }
            // Log.d(LOG_TAG,"position "  + position);
            booleanMap.put(position, checkBox.isChecked());
            if (checkBox.isChecked()) {
                Toast.makeText(context, context.getString(R.string.player)
                        + " " + player.getName() + " " +
                        context.getString(R.string.want_to_battle_with_you), Toast.LENGTH_SHORT).show();
                Controller.getInstance().getOnlineWorker()
                        .sendPacket(Protocol.SWantToPlay.newBuilder()
                                .setOpponentId(player.getId())
                                .setPlayerId(myPlayer.getId()).build());
            } else {
                Controller.getInstance().getOnlineWorker().
                        sendPacket(Protocol.SCancelDesirePlay.newBuilder().
                                setPlayerId(myPlayer.getId())
                                .setOpponentId(player.getId()).build());
            }
        }
    }
}

