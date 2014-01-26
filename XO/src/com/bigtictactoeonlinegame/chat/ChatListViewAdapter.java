package com.bigtictactoeonlinegame.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigtictactoeonlinegame.activity.R;

import java.util.List;

/**
 * Created by Maksym on 09.11.13.
 */
public class ChatListViewAdapter extends BaseAdapter {


    private List<ChatMessage> messages;
    private LayoutInflater layoutInflater;

    public ChatListViewAdapter(Context context, List<ChatMessage> messages) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.chat_list_item, parent, false);
        }
        TextView sender = (TextView) view.findViewById(R.id.tv_sender_name);
        TextView message = (TextView) view.findViewById(R.id.tv_message);
        ChatMessage chatMessage = messages.get(position);
        sender.setText(chatMessage.getSender() +":");
        message.setText(chatMessage.getMessage());
        return view;
    }
}
