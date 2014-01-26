package com.bigtictactoeonlinegame.mainactivity;

import com.bigtictactoeonlinegame.popup.XOAlertDialog;
import com.bigtictactoeonlinegame.activity.R;
import com.google.android.gms.ads.AdView;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StartActivity extends GeneralAdActivity implements OnClickListener {
    private View start;
    private View exit;
    private View settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_layout);


        start = findViewById(R.id.btn_start_activity_start_game);
        start.setOnClickListener(this);

        exit = findViewById(R.id.btn_start_activity_exit);
        exit.setOnClickListener(this);

        settings = findViewById(R.id.btn_start_activity_settings);
        settings.setOnClickListener(this);
        findViewById(R.id.btn_about_game).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, AboutActivity.class));
            }
        });


    }

    @Override
    public AdView getAdView() {
        return (AdView) StartActivity.this.findViewById(R.id.ad_view);
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
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
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
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
        xoAlertDialog.setNegativeButtonText(getResources().getString(R.string.no));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");

    }
}
