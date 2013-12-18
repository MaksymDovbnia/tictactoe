package com.game.activity;

import com.entity.Font;
import com.game.popup.XOAlertDialog;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class StartActivity extends FragmentActivity implements OnClickListener {
    View start;
    View exit;
    View settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_layout);


        start = findViewById(R.id.btn_start_activity_start_game);
        start.setOnClickListener(this);

        exit =  findViewById(R.id.btn_start_activity_exit);
        exit.setOnClickListener(this);

        settings =  findViewById(R.id.btn_start_activity_settings);
        settings.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start_activity_start_game:
                // view.setBackgroundResource(R.drawable.button_white_withblueline);
                Intent intent = new Intent(this, SelectTypeOfGameActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_start_activity_exit:
                finish();
                break;

            case R.id.btn_start_activity_settings:

                break;
            default:

                break;
        }

    }


    @Override
    public void onBackPressed() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_game));
        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_game_question));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.exit_from_game));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");

    }
}
