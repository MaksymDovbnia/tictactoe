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
 * Created by Maksym on 6/19/13.
 */
public class ChatFragment extends Fragment implements ChatAction {

    private Button btnSentMessage;
    private ListView chatListView;
    private EditText inputText;
    private List<ChatMessage> chatMessageList;

    private ChatListViewAdapter chatListViewAdapter;
    private ChatActionNotification actionNotification;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actionNotification = (ChatActionNotification) activity;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Each activity witch " +
                    "use ChatFragment must implement  ChatActionNotification " + e);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat_fragment_layout, null);
        btnSentMessage = (Button) chat.findViewById(R.id.btn_chat_sent_message);
        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputAndSent();
            }
        });
        chatListView = (ListView) chat.findViewById(R.id.chat_list_view);
        inputText = (EditText) chat.findViewById(R.id.chat_input_edit_text);
        inputText.setOnKeyListener(new View.OnKeyListener() {
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
        //    inputText.setImeActionLabel(getString(R.string.send), KeyEvent.KEYCODE_ENTER);
        chatMessageList = new ArrayList<ChatMessage>();
        chatListViewAdapter = new ChatListViewAdapter(getActivity(), chatMessageList);
        chatListView.setAdapter(chatListViewAdapter);
        // generateTesData();
        chatListView.setSelection(chatMessageList.size());
        return chat;
    }


    private void checkInputAndSent() {
        if (inputText.getText().toString().length() > 0) {
            sendNewMessage(new ChatMessage(inputText.getText().toString(), actionNotification.getPlayerName()));
        }
    }

    private void sendNewMessage(ChatMessage chatMessage) {
        actionNotification.actionSendChatMessage(chatMessage);
        chatMessageList.add(chatMessage);
        chatListViewAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatMessageList.size());
        inputText.setText("");
    }

    @Override
    public void receivedMessage(ChatMessage message) {
        chatListView.setSelection(chatMessageList.size());
        chatMessageList.add(message);
        chatListViewAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatMessageList.size());
    }



    private void generateTesData() {

        for (int i = 0; i < 30; i++) {
            chatMessageList.add(new ChatMessage("Message " + i, "Sender " + i / 2));
            chatListViewAdapter.notifyDataSetChanged();
        }
    }


}
