package com.game.activity;

import com.entity.Font;
import com.game.popup.XOAlertDialog;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class StartActivity extends FragmentActivity implements OnClickListener,
		OnTouchListener {
	Button start;
	Button exit;
    Button settings;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
		start = (Button) findViewById(R.id.Button_startactivity_start);
		start.setOnClickListener(this);
		start.setOnTouchListener(this);
		exit = (Button) findViewById(R.id.Button_startactivity_exit);
		exit.setOnClickListener(this);
		exit.setOnTouchListener(this);
		settings = (Button) findViewById(R.id.button_startactivity_settings);
        settings.setOnClickListener(this);
		LinearLayout l = (LinearLayout) findViewById(R.id.LL_startmenu);
		Font.setAppFont((ViewGroup)l, mFont);
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.Button_startactivity_start:
			// view.setBackgroundResource(R.drawable.button_white_withblueline);
			Intent intent = new Intent(this, SelectTypeOfGameActivity.class);
			startActivity(intent);
			break;

		case R.id.Button_startactivity_exit: finish();
		break;

            case R.id.button_startactivity_settings:
                Intent intent2 = new Intent(this, Test.class);
                startActivity(intent2);
                break;
		default:
			
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.setBackgroundResource(R.drawable.button_white_withblueline);
			break;
		case MotionEvent.ACTION_UP:
			v.setBackgroundResource(R.drawable.button_white);
			break;

		default:
			break;
		}

		return false;
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
