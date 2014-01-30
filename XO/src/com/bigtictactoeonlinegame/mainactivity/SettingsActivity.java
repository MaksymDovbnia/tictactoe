package com.bigtictactoeonlinegame.mainactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.entity.Player;
import com.bigtictactoeonlinegame.activity.R;
import com.bigtictactoeonlinegame.chat.ChatMessage;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;
import com.bigtictactoeonlinegame.gamefield.GameFieldAdapter;
import com.bigtictactoeonlinegame.gamefield.handler.FriendGameHandler;

/**
 * Created by Maksym on 06.01.14.
 */
public class SettingsActivity extends Activity implements GameFieldActivityAction {
    private GridView gridView;
    int scale;
    private GameFieldAdapter gameFieldAdapter;

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity_layout);
        super.onCreate(savedInstanceState);
        com.entity.Player player = new Player();
        player.setName("");
        com.entity.Player opponent1 = new Player();
        opponent1.setName("");
        FriendGameHandler friendGameHandler = new FriendGameHandler(player, opponent1, this);

        gridView = (GridView) findViewById(R.id.grid_view_game_field_2);
        gridView.setAdapter(gameFieldAdapter);

        findViewById(R.id.zoom_plus).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 gridView.setScaleX(++scale);
                 gridView.setScaleY(++scale);
            }
        });

        findViewById(R.id.zoom_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setScaleX(--scale);
                gridView.setScaleY(--scale);
            }
        });
    }


    @Override
    public void showWonPopup(String wonPlayerName) {

    }

    @Override
    public void opponentExitFromGame() {

    }

    @Override
    public void connectionToServerLost() {

    }

    @Override
    public void receivedChatMessage(ChatMessage chatMessage) {

    }
}