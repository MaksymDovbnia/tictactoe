package com.game.activity;

import java.security.acl.Group;

import com.entity.Font;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BigTicTacToeOnline extends Activity implements OnClickListener,
		OnTouchListener {
	Button start;
	Button exit;
	

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
			Intent intent = new Intent(this, ActivityTypeOfGame.class);
			startActivity(intent);
			break;

		case R.id.Button_startactivity_exit: finish();
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

}
