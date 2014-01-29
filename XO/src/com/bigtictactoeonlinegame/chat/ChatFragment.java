package com.bigtictactoeonlinegame.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.bigtictactoeonlinegame.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 06.09.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class ChatFragment extends Fragment implements ChatAction {

    private static final String EMPTY_STRING = "";
    private ListView mChatListView;
    private EditText mInputText;
    private List<ChatMessage> mChatMessageList;
    private ChatListViewAdapter mChatListViewAdapter;
    private IChatActionNotification mActionNotification;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActionNotification = (IChatActionNotification) activity;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Each activity witch " +
                    "use ChatFragment must implement  IChatActionNotification " + e);
        }


    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat_fragment_layout, null);
        initViews(chat);
        mChatMessageList = new ArrayList<ChatMessage>();
        mChatListViewAdapter = new ChatListViewAdapter(getActivity(), mChatMessageList);
        mChatListView.setAdapter(mChatListViewAdapter);
        // generateTesData();
        mChatListView.setSelection(mChatMessageList.size());
        return chat;
    }

    private void initViews(View chatView) {
        Button btnSentMessage = (Button) chatView.findViewById(R.id.btn_chat_sent_message);
        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputAndSent();
            }
        });
        mChatListView = (ListView) chatView.findViewById(R.id.chat_list_view);
        mInputText = (EditText) chatView.findViewById(R.id.chat_input_edit_text);
        mInputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    checkInputAndSent();
                    return true;
                }
                return false;
            }
        });
    }


    @SuppressWarnings("ConstantConditions")
    private void checkInputAndSent() {
        if (mInputText.getText().toString().length() > 0) {
            sendNewMessage(new ChatMessage(mInputText.getText().toString(), mActionNotification.getPlayerName()));
        }
    }

    private void sendNewMessage(ChatMessage chatMessage) {
        mActionNotification.actionSendChatMessage(chatMessage);
        mChatMessageList.add(chatMessage);
        mChatListViewAdapter.notifyDataSetChanged();
        mChatListView.setSelection(mChatMessageList.size());
        mInputText.setText(EMPTY_STRING);
    }

    @Override
    public void receivedMessage(ChatMessage message) {
        mChatListView.setSelection(mChatMessageList.size());
        mChatMessageList.add(message);
        mChatListViewAdapter.notifyDataSetChanged();
        mChatListView.setSelection(mChatMessageList.size());
    }


    private void generateTesData() {
        for (int i = 0; i < 30; i++) {
            mChatMessageList.add(new ChatMessage("Message " + i, "Sender " + i / 2));
            mChatListViewAdapter.notifyDataSetChanged();
        }
    }


}
