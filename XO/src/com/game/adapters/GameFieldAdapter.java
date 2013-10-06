package com.game.adapters;

//import android.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.OneMove;
import com.entity.TypeFieldElement;
import com.game.activity.GameFieldActivityAction;
import com.game.handler.GameHandler;
import com.game.GameType;
import com.game.TypeLine;
import com.game.activity.R;
import com.utils.Loger;

public class GameFieldAdapter extends BaseAdapter {

    private TextView[] fields = new TextView[225];
    private Context mContext;
    private int indicator = 1;
    private int i = 0;
    private GameHandler gameFiledSource;
    private LayoutInflater layoutInflater;
    private boolean getOpponentMove = true;
    private TextView player1;
    private TextView player2;
    private GameFieldActivityAction activityAction;

    public TextView getPlayer1() {
        return player1;
    }

    public TextView getPlayer2() {
        return player2;
    }

    public void setPlayer1(TextView player1) {
        this.player1 = player1;
    }

    public void setPlayer2(TextView player2) {
        this.player2 = player2;
    }

    public GameFieldAdapter(Context context, GameHandler gameFiledSource) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (context instanceof GameFieldActivityAction)
            activityAction = (GameFieldActivityAction) context;
        this.mContext = context;
        this.gameFiledSource = gameFiledSource;

    }

    @Override
    public int getCount() {
        return fields.length;
    }

    @Override
    public Object getItem(int position) {
        return fields[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void opponentDidOneMove(OneMove oneMove) {
        int id = (15 * oneMove.i) + oneMove.j;
        Loger.printLog("ID ELEMENT  " + id);
        TextView textView = fields[id];
        textView.setBackgroundResource((oneMove.type.equals(TypeFieldElement.X)) ? R.drawable.x
                : R.drawable.o);
        textView.setEnabled(false);
        getOpponentMove = true;
        changeIndicator();

    }

    public void drawWinLine(List<OneMove> listWinField) {
        for (OneMove oneMove : listWinField) {
            int id = (15 * oneMove.i) + oneMove.j;
            TextView textView = fields[id];
            if (oneMove.type.equals(TypeFieldElement.X)) {
                if (oneMove.typeLine.equals(TypeLine.LEFT))
                    textView.setBackgroundResource(R.drawable.x_withline_l);
                else if (oneMove.typeLine.equals(TypeLine.RIGHT))
                    textView.setBackgroundResource(R.drawable.x_withline_r);
                else if (oneMove.typeLine.equals(TypeLine.HORIZONTAL))
                    textView.setBackgroundResource(R.drawable.x_withline_h);
                else if (oneMove.typeLine.equals(TypeLine.VERTICAL))
                    textView.setBackgroundResource(R.drawable.x_withline_v);

            } else {
                if (oneMove.typeLine.equals(TypeLine.LEFT))
                    textView.setBackgroundResource(R.drawable.o_withline_l);
                else if (oneMove.typeLine.equals(TypeLine.RIGHT))
                    textView.setBackgroundResource(R.drawable.o_withline_r);
                else if (oneMove.typeLine.equals(TypeLine.HORIZONTAL))
                    textView.setBackgroundResource(R.drawable.o_withline_h);
                else if (oneMove.typeLine.equals(TypeLine.VERTICAL))
                    textView.setBackgroundResource(R.drawable.o_withline_v);

            }
            for (int i = 0; i < fields.length; i++) {
                fields[i].setEnabled(false);
            }



        }
        showWonPopup();

    }

    public void startNewGame() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(true);
            fields[i].setBackgroundResource(R.drawable.field);

        }
    }

    private void changeIndicator() {
        if (indicator == 0) {
            player1.setBackgroundResource(R.drawable.ovalbound_red);
            player2.setBackgroundResource(R.drawable.button_white);
            indicator = 1;
        } else {
            player2.setBackgroundResource(R.drawable.ovalbound_red);
            player1.setBackgroundResource(R.drawable.button_white);
            indicator = 0;

        }
    }

    private void showWonPopup() {
        if (activityAction == null) return;
        String name = indicator == 1 ? player1.getText().toString() : player2.getText().toString();
        activityAction.showWonPopup(name);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.my_botton_iteam, parent,
                    false);

            TextView field = (TextView) view
                    .findViewById(R.id.tv_first_player_name);
            if (fields[position] == null)
                fields[position] = field;
            field.setTag(position);
            // Loger.printLog("new view " + position);
        }

        TextView field = (TextView) view.findViewById(R.id.tv_first_player_name);
        field.setBackgroundResource(R.drawable.field);
        field.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getOpponentMove)
                    return;
                else if (!gameFiledSource.getGameType().equals(GameType.FRIEND))
                    getOpponentMove = false;
                TextView field = (TextView) v;
                field.setEnabled(false);
                int x = 0, y = 0;
                int position = (Integer) field.getTag();
                double d = position / 15.0;
                int i = position / 15;
                Loger.printLog(" i " + i + " d " + d);
                if (d - i == 0) {
                    x = 0;
                    y = i;
                } else {
                    y = i;
                    x = position - (15 * i);
                }
                Loger.printLog("click " + x + " " + y);
                if (indicator == 1) {
                    field.setBackgroundResource(R.drawable.x);
                    List<OneMove> list = gameFiledSource.oneMove(new OneMove(y,
                            x, TypeFieldElement.X));
                    if (list != null)
                        drawWinLine(list);
                    changeIndicator();

                } else {
                    field.setBackgroundResource(R.drawable.o);
                    List<OneMove> list = gameFiledSource.oneMove(new OneMove(y,
                            x, TypeFieldElement.O));
                    if (list != null)
                        drawWinLine(list);
                    changeIndicator();
                }

            }
        });

        field.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                return false;
            }
        });

        return view;
    }

}