package com.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	// public final static String EXTRA_MESSAGE =
	// "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void loadButton(View view) {
		Intent intent = new Intent(this, StartActivity.class);
		startActivity(intent);

	}
	/*
	 * public void sendMessage(View view){ Intent intent = new Intent(this,
	 * DisplayMessageActivity.class); // EditText editText = (EditText)
	 * findViewById(R.id.edit_message); //String message =
	 * editText.getText().toString(); //intent.putExtra(EXTRA_MESSAGE, message);
	 * //startActivity(intent);
	 * 
	 * }
	 */

}
