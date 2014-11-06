package com.bigtictactoeonlinegame.mainactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.entity.Player;
import com.bigtictactoeonlinegame.activity.R;


/**
 * Created by Maksym on 06.01.14.
 */
public class SettingsActivity extends Activity {
    private GridView gridView;
    int scale;


    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity_layout);
        super.onCreate(savedInstanceState);
        com.entity.Player player = new Player();
        player.setName("");
        com.entity.Player opponent1 = new Player();
        opponent1.setName("");


        gridView = (GridView) findViewById(R.id.grid_view_game_field_2);



    }



}