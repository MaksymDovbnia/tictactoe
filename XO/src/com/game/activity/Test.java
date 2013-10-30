package com.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.game.gamefield.GameFieldAdapter;
import com.game.gamefield.GameFieldItem;

/**
 * Created by Maksym on 10/6/13.
 */
public class Test extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        GridView gridView = (GridView) findViewById(R.id.grid_view_game_field);
        gridView.setAdapter(new GameFieldAdapter(this, null));



    }
}