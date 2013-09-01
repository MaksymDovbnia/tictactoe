package com.game.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.game.activity.R;
import com.game.adapters.GameFieldAdapter;
import com.game.handler.GameHandler;

/**
 * Created by Maksym on 6/19/13.
 */
public class GameChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat_fragment_layout,null);
        return chat;
    }
}
