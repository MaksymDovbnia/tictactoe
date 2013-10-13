package com.game.gamefield;

//import android.R;

import java.util.ArrayList;
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
import com.game.gamefield.handler.GameHandler;
import com.game.GameType;
import com.game.TypeLine;
import com.game.activity.R;
import com.utils.Loger;

public class GameFieldAdapter extends BaseAdapter {

    private GameFieldItem[] fields = new GameFieldItem[225];
    private GameFieldItem[][] fieldsGrid = new GameFieldItem[15][15];

    private int indicator = 1;
    private GameHandler gameFiledSource;
    private LayoutInflater layoutInflater;
    private boolean getOpponentMove = true;
    private TextView player1;
    private TextView player2;
    private TextView player1Score;
    private TextView player2Score;
    private GameFieldActivityAction activityAction;
    private GameFieldItem lastGameFieldItem;
    private int player1ScoreNum = 0;
    private int player2ScoreNum = 0;

    public TextView getPlayer1() {
        return player1;
    }

    public void setPlayer1Score(TextView player1Score) {
        this.player1Score = player1Score;
    }

    public void setPlayer2Score(TextView player2Score) {
        this.player2Score = player2Score;
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
        GameFieldItem fieldItem = fields[id];
        fieldItem.setFieldType((oneMove.type.equals(TypeFieldElement.X)) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O);
        fieldItem.setMarkAboutLastMove(true);
        if (lastGameFieldItem != null) lastGameFieldItem.setMarkAboutLastMove(false);
        lastGameFieldItem = fieldItem;
        getOpponentMove = true;
        changeIndicator();

    }

    public void drawWinLine(List<OneMove> listWinField) {
        for (OneMove oneMove : listWinField) {
            int id = (15 * oneMove.i) + oneMove.j;
            GameFieldItem fieldItem = fields[id];
                if (oneMove.typeLine.equals(TypeLine.LEFT))
                    fieldItem.setWinLineType(GameFieldItem.WinLineType.LEFT);
                else if (oneMove.typeLine.equals(TypeLine.RIGHT))
                    fieldItem.setWinLineType(GameFieldItem.WinLineType.RIGHT);
                else if (oneMove.typeLine.equals(TypeLine.HORIZONTAL))
                    fieldItem.setWinLineType(GameFieldItem.WinLineType.HORIZONTAL);
                else if (oneMove.typeLine.equals(TypeLine.VERTICAL))
                    fieldItem.setWinLineType(GameFieldItem.WinLineType.VERTICAl);
        }

        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(false);
        }


        showWonPopup();

    }

    public void startNewGame(boolean isFirstPlayerMoveByX) {
        if (isFirstPlayerMoveByX) {
            player1.setBackgroundResource(R.drawable.ovalbound_red);
            player2.setBackgroundResource(R.drawable.button_white);
            indicator = 1;
        } else {
            player2.setBackgroundResource(R.drawable.ovalbound_red);
            player1.setBackgroundResource(R.drawable.button_white);
            indicator = 0;

        }
            startNewGame();
        }

    public void startNewGame() {

        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(true);
            fields[i].clear();
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
            view = layoutInflater.inflate(R.layout.game_field_item, parent,
                    false);


            GameFieldItem field = (GameFieldItem) view.findViewById(R.id.tv_field_item);
            if (fields[position] == null){
                fields[position] = field;
                int x = 0, y = 0;
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
                field.setI(y);
                field.setJ(x);
                fieldsGrid[y][x] = field;

            }
            field.setTag(position);
            // Loger.printLog("new view " + position);
        }

        final GameFieldItem field = (GameFieldItem) view.findViewById(R.id.tv_field_item);
        field.setBackgroundResource(R.drawable.field);
        field.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getOpponentMove)
                    return;
                else if (!gameFiledSource.getGameType().equals(GameType.FRIEND))
                    getOpponentMove = false;
                GameFieldItem field = (GameFieldItem) v;
                if (lastGameFieldItem != null) lastGameFieldItem.setMarkAboutLastMove(false);
                lastGameFieldItem = field;
                lastGameFieldItem.setMarkAboutLastMove(true);
                field.setEnabled(false);
                int j = field.getJ(), i = field.getI();
                if (indicator == 1) {
                    field.setFieldType(GameFieldItem.FieldType.X);
                    List<OneMove> list = gameFiledSource.oneMove(new OneMove(i,
                            j, TypeFieldElement.X));
                    if (list != null){
                       player1ScoreNum++;
                        player1Score.setText(player1ScoreNum+"");
                        drawWinLine(list);

                    }
                    changeIndicator();


                } else {
                    field.setFieldType(GameFieldItem.FieldType.O);
                    List<OneMove> list = gameFiledSource.oneMove(new OneMove(i,
                            j, TypeFieldElement.O));
                    if (list != null){
                        player2ScoreNum++;
                        player2Score.setText(player2ScoreNum+"");
                        drawWinLine(list);

                    }
                    changeIndicator();
                }

            }
        });

        field.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                GameFieldItem field = (GameFieldItem) view;
                List<GameFieldItem> itemList = new ArrayList<GameFieldItem>();
                int i = field.getI();
                int j= field.getJ();
                for (int z = 0; z <15;z++ ){
                    GameFieldItem oneGameFieldItem = fieldsGrid[i][z];
                    if (oneGameFieldItem != null) itemList.add(oneGameFieldItem);
                    GameFieldItem xGameFieldItem = fieldsGrid[z][j];
                    if (xGameFieldItem != null) itemList.add(xGameFieldItem);

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    for (GameFieldItem item : itemList) {
                        item.setMarkAboutInSight(true);
                    }

                } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    for (GameFieldItem item : itemList) {
                        item.setMarkAboutInSight(false);
                    }
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    for (GameFieldItem item : itemList) {
                        item.setMarkAboutInSight(false);
                    }
                }

                return false;
            }
        });

        return view;
    }

}