package com.game.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.game.activity.R;

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
    private Activity activity;
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

        this.activity = activity;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.chat_fragment_layout, null);
        btnSentMessage = (Button) chat.findViewById(R.id.btn_chat_sent_message);
        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputText.getText().toString().length() > 0) {
                    sendNewMessage(new ChatMessage(inputText.getText().toString(), actionNotification.getPlayerName()));

                }

            }
        });
        chatListView = (ListView) chat.findViewById(R.id.chat_list_view);
        inputText = (EditText) chat.findViewById(R.id.chat_input_edit_text);
        chatMessageList = new ArrayList<ChatMessage>();
        chatListViewAdapter = new ChatListViewAdapter(activity, chatMessageList);
        chatListView.setAdapter(chatListViewAdapter);
       // generateTesData();
        chatListView.setSelection(chatMessageList.size());
        return chat;
    }

    private void sendNewMessage(ChatMessage chatMessage) {
        actionNotification.actionSendMessage(chatMessage);
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
    }

    private void generateTesData() {

        for (int i = 0; i < 30; i++) {
            chatMessageList.add(new ChatMessage("Message " + i, "Sender " + i / 2));
            chatListViewAdapter.notifyDataSetChanged();
        }
    }


}
