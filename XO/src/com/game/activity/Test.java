package com.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.game.gamefield.GameFieldItem;

/**
 * Created by Maksym on 10/6/13.
 */
public class Test extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        final GameFieldItem gameFieldItem  = (GameFieldItem) findViewById(R.id.test);

        gameFieldItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameFieldItem.setFieldTypeAndDraw(GameFieldItem.FieldType.X);
          gameFieldItem.setMarkAboutLastMove(true);
            }
        });

    }
}