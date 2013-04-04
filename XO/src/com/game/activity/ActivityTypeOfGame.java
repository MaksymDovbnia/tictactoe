package com.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.entity.Font;
import com.game.Controler;
import com.game.GameFiledSource;
import com.game.handler.FriendGameHandler;

public class ActivityTypeOfGame extends Activity implements OnClickListener,
		OnTouchListener {
	private Button friend;
	private Button online;
	private Button android;
	private Button bluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type_of_game);
		
		Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
		LinearLayout l = (LinearLayout) findViewById(R.id.LL_typeofgamenu);
		Font.setAppFont((ViewGroup)l, mFont);

		friend = (Button) findViewById(R.id.button_gametypes_friend);
		friend.setOnClickListener(this);
		friend.setOnTouchListener(this);

		online = (Button) findViewById(R.id.button_gametypes_online);
		online.setOnClickListener(this);
		online.setOnTouchListener(this);

		android = (Button) findViewById(R.id.button_gametypes_android);
		android.setOnClickListener(this);
	//	android.setOnTouchListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_gametypes_android:
			

			break;
		case R.id.button_gametypes_friend:
			FriendGameHandler f = new FriendGameHandler();
			Controler.setGameFiledSource(f);
			Intent intent = new Intent(this, ActivityGameField.class);
			startActivity(intent);
			break;
		case R.id.button_gametypes_online:
			/*Intent intentOnline = new Intent(this,
					OnlineGameConnectionActivity.class);
			startActivity(intentOnline);*/
			Intent reg = new Intent(this, ActivityTypeRegistraion.class);
			startActivity(reg);
			break;
		case R.id.button_gametypes_bluetooth:
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
