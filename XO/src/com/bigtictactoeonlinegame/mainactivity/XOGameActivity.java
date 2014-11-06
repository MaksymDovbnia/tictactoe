package com.bigtictactoeonlinegame.mainactivity;

import android.os.*;
import android.view.*;

import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.activity.R;
import com.google.example.games.basegameutils.*;

/**
 * Date: 02.06.14
 * Time: 20:17
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class XOGameActivity extends BaseGameActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
       // findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    @Override
    public void onSignInSucceeded() {
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
      //  findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
    }
}