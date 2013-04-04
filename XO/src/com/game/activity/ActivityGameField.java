package com.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.game.Controler;
import com.game.GameFiledSource;
import com.game.adapters.GameFieldAdapter;

public class ActivityGameField extends Activity implements OnClickListener,
		OnTouchListener {
	private GridView gridView;
	private GameFiledSource gameFiledSource;
	private Handler handler;
	private GameFieldAdapter gameFieldAdapter;
	private Button newGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_field);
		gridView = (GridView) findViewById(R.id.gridView1);

		gameFiledSource = Controler.getGameFiledSource();
		handler = gameFiledSource.getHandler();
		gameFieldAdapter = new GameFieldAdapter(this, gameFiledSource);
		gameFieldAdapter
				.setPlayer1((TextView) findViewById(R.id.textViewFirstPlayer));
		gameFieldAdapter
				.setPlayer2((TextView) findViewById(R.id.textViewSecondPlayer));

		gameFiledSource.setAdapter(gameFieldAdapter);
		gridView.setAdapter(gameFieldAdapter);

		newGame = (Button) findViewById(R.id.button_gamefield_newgame);
		newGame.setOnClickListener(this);

		// newGame.setOnTouchListener(this);

	}

	@Override
	public void onClick(View v) {
		gameFieldAdapter.startNewGame();
		gameFiledSource.startNewGame();

	}
	private void crateWinDialog (String name, int numOfMoves) {
		
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.setBackgroundResource(R.drawable.button_crinkle);

			break;
		case MotionEvent.ACTION_UP:
			// v.setBackgroundResource(R.drawable.knopka_prosta);
			break;

		default:
			break;
		}

		return true;
	}

}
