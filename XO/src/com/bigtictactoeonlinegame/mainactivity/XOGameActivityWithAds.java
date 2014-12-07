package com.bigtictactoeonlinegame.mainactivity;

import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import com.bigtictactoeonlinegame.LoadImageTask;
import com.bigtictactoeonlinegame.XOSharedPreferenceHelper;
import com.bigtictactoeonlinegame.activity.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Date: 02.06.14
 * Time: 20:17
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public abstract class XOGameActivityWithAds extends GeneralAdWithPlayServiceActivity implements View.OnClickListener {
    private static final int REQUEST_ACHIEVEMENTS = 1000;
    private static final int REQUEST_LEADERBOARD = 1001;

    private boolean mWasPrevClickLeaderBoardButton = false;
    private boolean mWasPrevClickAchievemntsButton = false;
    private ImageView userImage;
    private TextView userName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGameHelper().setConnectOnStart(XOSharedPreferenceHelper.getInstance().isUserLoginSocial());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        findViewById(R.id.sign_in_button_container).setOnClickListener(this);
        findViewById(R.id.btn_leaderboards).setOnClickListener(this);
        findViewById(R.id.btn_achievments).setOnClickListener(this);

        userImage = (ImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);

    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_text).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_text).setVisibility(View.GONE);
    }

    @Override
    public void onSignInSucceeded() {

        if (mWasPrevClickAchievemntsButton) {
            startAchievementsActivity();
            mWasPrevClickAchievemntsButton = false;
        }
        if (mWasPrevClickLeaderBoardButton) {
            startLeaderBoardActivity();
            mWasPrevClickLeaderBoardButton = false;
        }


        findViewById(R.id.sign_in_text).setVisibility(View.GONE);
        findViewById(R.id.sign_out_text).setVisibility(View.VISIBLE);

        XOSharedPreferenceHelper.getInstance().userLoginSocial(true);
        if (Plus.PeopleApi.getCurrentPerson(getApiClient()) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(getApiClient());
            String personName = currentPerson.getDisplayName();
            XOSharedPreferenceHelper.getInstance().saveUserName(personName);

            ((TextView) findViewById(R.id.user_name)).setText(personName);
            final Person.Image personPhoto = currentPerson.getImage();

            if (personPhoto.hasUrl()) {
                LoadImageTask loadImageTask = new LoadImageTask(userImage);
                loadImageTask.execute(personPhoto.getUrl());
            }

        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sign_in_button_container:
                if (isSignedIn()) {
                    signOut();
                    findViewById(R.id.sign_in_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_out_text).setVisibility(View.GONE);
                    XOSharedPreferenceHelper.getInstance().userLoginSocial(false);
                    XOSharedPreferenceHelper.getInstance().saveUserName("");
                    XOSharedPreferenceHelper.getInstance().saveUserImageName("");
                    userImage.setImageBitmap(null);
                    userImage.setBackgroundResource(R.drawable.foto);
                    userName.setText(R.string.player);
                } else {
                    beginUserInitiatedSignIn();
                }
                break;
            case R.id.btn_leaderboards:
                if (!isSignedIn()) {
                    mWasPrevClickLeaderBoardButton = true;
                    beginUserInitiatedSignIn();
                } else {
                    startLeaderBoardActivity();
                }
                break;
            case R.id.btn_achievments:
                if (!isSignedIn()) {
                    beginUserInitiatedSignIn();
                    mWasPrevClickAchievemntsButton = true;
                } else {
                    startAchievementsActivity();
                }
            default:
                break;
        }

    }

    private void startLeaderBoardActivity() {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
                getString(R.string.leaderboard_the_best_online_players)), REQUEST_LEADERBOARD);
    }

    private void startAchievementsActivity() {
        startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), REQUEST_ACHIEVEMENTS);
    }

}