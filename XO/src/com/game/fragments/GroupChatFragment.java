package com.game.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.activity.R;

/**
 * Created by Maksym on 6/19/13.
 */
public class GroupChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat_fragment_layout,null);
        return chat;
    }
}
