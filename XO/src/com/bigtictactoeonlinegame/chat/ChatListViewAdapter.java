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
 * Date: 06.09.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class ChatListViewAdapter extends BaseAdapter {
    private List<ChatMessage> mMessagesList;
    private LayoutInflater mLayoutInflater;

    public ChatListViewAdapter(Context context, List<ChatMessage> messages) {
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMessagesList = messages;
    }

    @Override
    public int getCount() {
        return mMessagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.chat_list_item, parent, false);
        }
        TextView sender = (TextView) view.findViewById(R.id.tv_sender_name);
        TextView message = (TextView) view.findViewById(R.id.tv_message);
        ChatMessage chatMessage = mMessagesList.get(position);
        sender.setText(chatMessage.getSender() + ":");
        message.setText(chatMessage.getMessage());
        return view;
    }
}
