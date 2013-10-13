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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.entity.Font;
import com.entity.Player;
import com.game.Controller;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineTypeRegistrationActivity extends Activity implements
        OnClickListener {

    private  static  final String TAG = "";
    private TextView textVievAnonymous, textVievTTT;
    private final int ANNONDIALOG = 1;
    private final int XOACCOUNTDIALOG = 2;
    private final int FORGOTPASSWORDDIALOG = 3;


    private ProgressDialog pd;
    private Player player;
    private EditText loginAnon;
    private WorkerOnlineConnection onlineGameWorker;
    Handler handler;
    Dialog anonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_type_registraion);
        textVievAnonymous = (TextView) findViewById(R.id.textView_registration_anon);
        textVievAnonymous.setOnClickListener(this);
        textVievTTT = (TextView) findViewById(R.id.textView_registration_byTTT);
        textVievTTT.setOnClickListener(this);
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
        LinearLayout l = (LinearLayout) findViewById(R.id.LL_notebook_registration);
        Font.setAppFont((ViewGroup) l, mFont);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                if (msg.what == 100) {
                    pd.cancel();
                    createToastWithText("Sorry, server not avaible, plase try later");
                    return;
                }
                Log.d(TAG, "handler received message" + protoType );
                switch (protoType) {
                    case CLOGINTOGAME:
                        Protocol.CLoginToGame cLoginToGame = (Protocol.CLoginToGame) msg.obj;
                        int id = cLoginToGame.getId();
                        player.setId(id);
                        Controller.getInstance().setPlayer(player);
                        Loger.printLog("Conected to server with id " + id);
                        pd.cancel();
                        anonLogin.cancel();
                        loginToGame();
                        break;
                }
            }
        };

    }

    private void loginToGame() {
        Intent intent = new Intent(this, OnlineGroupsActivity.class);
        startActivity(intent);
    }

    private void createToastWithText(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public Dialog onCreateDialog(int id) {
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        switch (id) {
            case ANNONDIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.anonymous_popup_layout, null);
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                view.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
                view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
                builder.setView(view);
                Button login = (Button) view.findViewById(R.id.btn_anonymous_login);
                login.setOnClickListener(this);

                TextView tw = (TextView) view.findViewById(R.id.textView_anondailog);
                tw.setTypeface(font);
                login.setTypeface(font);
                loginAnon = (EditText) view.findViewById(R.id.edt_anonymous_player_name);
                loginAnon.setTypeface(font);
                builder.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        textVievAnonymous
                                .setBackgroundResource(R.drawable.button_white);

                    }
                });
                anonLogin = builder.create();
                return anonLogin;

            case XOACCOUNTDIALOG:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                LayoutInflater inflater2 = getLayoutInflater();
                View viewXO = inflater2.inflate(R.layout.xo_account_loginview, null);

                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                viewXO.setMinimumWidth((int) (displayRectangle.width() * 0.6f));
                viewXO.setMinimumHeight((int) (displayRectangle.height() * 0.6f));
                builder2.setView(viewXO);
                Button loginXO = (Button) viewXO.findViewById(R.id.button_login_withxoaccount);
                Button registr = (Button) viewXO.findViewById(R.id.button_open_regxoacount_activity);
                registr.setOnClickListener(this);
                loginXO.setOnClickListener(this);
                Button f = (Button) viewXO.findViewById(R.id.Button_forgot_password);
                f.setOnClickListener(this);

                TextView tvname = (TextView) viewXO.findViewById(R.id.textView_name_xologin);
                tvname.setTypeface(font);
                loginXO.setTypeface(font);
                loginAnon = (EditText) viewXO.findViewById(R.id.editText_name_xoaccount);
                loginAnon.setTypeface(font);
                builder2.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        textVievTTT
                                .setBackgroundResource(R.drawable.button_white);

                    }
                });
                anonLogin = builder2.create();
                return anonLogin;

            case FORGOTPASSWORDDIALOG:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                LayoutInflater inflater3 = getLayoutInflater();
                View forgot = inflater3.inflate(R.layout.forgot_password_view, null);

                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                forgot.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
                forgot.setMinimumHeight((int) (displayRectangle.height() * 0.5f));


                builder3.setView(forgot);
                anonLogin = builder3.create();
                return anonLogin;

        }
        return null;

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

                showDialog(ANNONDIALOG);
                //Dialog d = new Dialog(this);
                //d.setContentView(R.layout.anonymousloginview);
                //d.show();
                // Loger.printLog(anonConect+":");
                // frame.addView(anonConect, FrameLayout.FOCUS_LEFT);

                break;
            case R.id.textView_registration_byTTT:
                v.setBackgroundResource(R.drawable.stroke_red);
                showDialog(XOACCOUNTDIALOG);
                break;
            case R.id.button_open_regxoacount_activity:
                Intent intent = new Intent(this, RegistrationXOAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.Button_forgot_password:
                //Loger.printLog("click FORGOT PASSROWD");
                anonLogin.cancel();
                showDialog(FORGOTPASSWORDDIALOG);
                break;
            case R.id.btn_anonymous_login:

                pd = new ProgressDialog(this);
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    Loger.printLog("click" + v.getId());
                    player = new Player();
                    player.setName(loginAnon.getText().toString());
                    player.setRegistrationType(Protocol.RegistrationType.annonymous);
                    onlineGameWorker = new WorkerOnlineConnection(handler,
                            player, pd);
                    Controller.getInstance().setOnlineWorker(onlineGameWorker);
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
            onlineGameWorker.disconnect();

        super.onDestroy();
    }

}
