package com.game.activity;

import net.protocol.Protocol;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.entity.Font;
import com.entity.Player;
import com.game.Controler;
import com.net.online.OnlineConectionGameWorker;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class ActivityTypeRegistraion extends Activity implements
		OnClickListener {
	private TextView textVievAnonymous, textVievTTT;
	private int annonDialog = 1;
	private ProgressDialog pd;
	private Player player;
	private EditText loginAnon;
	private OnlineConectionGameWorker onlineGameWorker;
	Handler handler;
	Dialog anonLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_type_registraion);
		textVievAnonymous = (TextView) findViewById(R.id.textView_registration_anon);
		textVievAnonymous.setOnClickListener(this);
		textVievTTT = (TextView) findViewById(R.id.textView_registration_byTTT);
		
		Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
		LinearLayout l = (LinearLayout) findViewById(R.id.LL_notebook_registration);
		Font.setAppFont((ViewGroup)l, mFont);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ProtoType protoType = ProtoType.fromInt(msg.what);
				if (msg.what == 100) {
					pd.cancel();
					createToastWithText("Sorry, server not avaible, plase try later");
					return;
				}
				switch (protoType) {
				/*
				 * case CUPDATEAOBOUTACTIVITYPLAYER:
				 * Protocol.CUpdateAboutActivityPlayer cActivityPlayer =
				 * (Protocol.CUpdateAboutActivityPlayer) msg.obj; for
				 * (Protocol.Player player: cActivityPlayer.getPlayerList()){
				 * players.add(new Player(player.getId(), player.getName())); }
				 * adapterActivityList.notifyDataSetChanged(); break;
				 */

				case CLOGINTOGAME:
					Protocol.CLoginToGame cLoginToGame = (Protocol.CLoginToGame) msg.obj;
					int id = cLoginToGame.getId();
					// status.setText(status.getText()+ " conected with id " +
					// id);
					// connect.setEnabled(false);
					// status.setEnabled(false);
					player.setId(id);
					Controler.setPlayer(player);
					Loger.printLog("Conected to server with id " + id);
					// openGroupButton.setEnabled(true);
					pd.cancel();
					anonLogin.cancel();
					loginToGame();

					break;

				}

			}
		};

	}

	private void loginToGame() {
		Intent intent = new Intent(this, OnlinePlayersActivity.class);
		startActivity(intent);
	}

	private void createToastWithText(String s) {
		Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.anonymousloginview, null);
		builder.setView(view);
		Button login = (Button) view.findViewById(R.id.button_anonlogin);
		login.setOnClickListener(this);
		loginAnon = (EditText) view.findViewById(R.id.editText_loginanon);
		//loginAnon.setText("Anon player");
		Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/SNAP____.TTF");
		loginAnon.setTypeface(mFont);
		
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				textVievAnonymous
						.setBackgroundResource(R.drawable.button_white);

			}
		});
		anonLogin = builder.create();
		return anonLogin;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textView_registration_anon:
			v.setBackgroundResource(R.drawable.stroke_red);
			FrameLayout frame = (FrameLayout) findViewById(R.id.loginFrameLayout);

			// LayoutInflater inflater = getLayoutInflater();
			// View anonConect = (View)
			// inflater.inflate(R.layout.anonymousloginview, frame);
			
			showDialog(annonDialog);
			

			// Loger.printLog(anonConect+":");
			// frame.addView(anonConect, FrameLayout.FOCUS_LEFT);

			break;
		case R.id.button_anonlogin:

			pd = new ProgressDialog(this);
			ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				Loger.printLog("click" + v.getId());
				player = new Player();
				player.setName(loginAnon.getText().toString());
				onlineGameWorker = new OnlineConectionGameWorker(handler,
						player, pd);
				Controler.setOnl(onlineGameWorker);
				onlineGameWorker.start();
				
				pd.setTitle("Conection");
				pd.setMessage("conecting...");
				pd.show();

				// pd.cancel();
			} else {
				createToastWithText("No connection, please will make connection and reaped");

			}
			break;

		default:
			break;
		}

	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("asd").setTitle("asdasd");
		// builder.setView( anonConect);
		builder.create();

	}

	@Override
	protected void onDestroy() {
		if (onlineGameWorker != null)
			onlineGameWorker.disconect();

		super.onDestroy();
	}

}
