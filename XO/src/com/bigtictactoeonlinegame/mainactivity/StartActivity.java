package com.bigtictactoeonlinegame.mainactivity;

import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;

import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.popup.*;
import com.google.android.gms.ads.*;
import com.google.android.gms.games.Games;

public class StartActivity extends GeneralAdWithPlayServiceActivity implements OnClickListener {
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

            default:
                break;
        }

    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }


//    @Override
//    public void onBackPressed() {
//        XOAlertDialog xoAlertDialog = new XOAlertDialog();
//        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_game));
//        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_game_question));
//        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
//        xoAlertDialog.setNegativeButtonText(getResources().getString(R.string.no));
//        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
//            }
//        });
//        xoAlertDialog.show(getSupportFragmentManager(), "");
//
//    }
}
