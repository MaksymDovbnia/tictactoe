package com.bigtictactoeonlinegame.gamefield;

//import android.R;

import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.gamefield.handler.*;
import com.entity.*;
import com.utils.*;

import java.util.*;

public class GameFieldAdapter extends BaseAdapter {
    private GameFieldItem[] fields = new GameFieldItem[225];
    private GameFieldItem[][] fieldsGrid = new GameFieldItem[15][15];
    private IGameHandler gameHandler;
    private LayoutInflater layoutInflater;
    private boolean isEnableGameField;
    private GameFieldItem lastGameFieldItem;
    private HorizontalScrollView mHorizontalScrollView;
    private ScrollView mScrollView;



    public GameFieldAdapter(Context context, IGameHandler gameFiledSource,
                            HorizontalScrollView horizontalScrollView, ScrollView scrollView) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHorizontalScrollView = horizontalScrollView;
        mScrollView = scrollView;
        if (context instanceof GameFieldActivityAction)

            this.gameHandler = gameFiledSource;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameFieldItem gameFieldItem = (GameFieldItem) convertView;
        if (gameFieldItem == null) {
            gameFieldItem = (GameFieldItem) layoutInflater.inflate(R.layout.game_field_item, parent,
                    false);

            if (fields[position] == null) {
                fields[position] = gameFieldItem;
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
                gameFieldItem.setI(y);
                gameFieldItem.setJ(x);
                fieldsGrid[y][x] = gameFieldItem;

            }
            gameFieldItem.setTag(position);
        }

        GameFieldItem field = (GameFieldItem) gameFieldItem.findViewById(R.id.tv_field_item);
        field.setOnClickListener(new GameFieldClickListener());


        field.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                GameFieldItem field = (GameFieldItem) view;
                List<GameFieldItem> itemList = new ArrayList<GameFieldItem>();
                int i = field.getI();
                int j = field.getJ();
                for (int z = 0; z < 15; z++) {
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
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    for (GameFieldItem item : itemList) {
                        item.setMarkAboutInSight(false);
                    }
                }


                return false;
            }
        });

        return field;
    }

    public void showOneMove(OneMove oneMove) {
        //   int id = (15 * performedOneMove.i) + performedOneMove.j;
        GameFieldItem fieldItem = fieldsGrid[oneMove.i][oneMove.j];
        fieldItem.setFieldTypeAndDraw((oneMove.type.equals(TypeOfMove.X)) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O);
        fieldItem.setMarkAboutLastMove(true);
        if (lastGameFieldItem != null) lastGameFieldItem.setMarkAboutLastMove(false);
        lastGameFieldItem = fieldItem;
        fieldItem.setEnabled(false);
    }

    public void showOneMove(OneMove oneMove, boolean isNeedScroll) {
        showOneMove(oneMove);
        if (isNeedScroll) {
            scrollingIfItNecassery(oneMove);
        }
    }

    private void scrollingIfItNecassery(OneMove oneMove) {
        if (oneMove.j < 5) {
            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        }
        if (oneMove.j > 10) {
            mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }

        if (oneMove.i < 5) {
            mScrollView.fullScroll(ScrollView.FOCUS_UP);
        }
        if (oneMove.i > 10) {
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }


    public void drawWinLine(List<OneMove> listWinField) {
        for (OneMove oneMove : listWinField) {
            int id = (15 * oneMove.i) + oneMove.j;
            GameFieldItem fieldItem = fields[id];
            if (oneMove.typeLine.equals(LineType.LEFT))
                fieldItem.setWinLineType(GameFieldItem.WinLineType.LEFT);
            else if (oneMove.typeLine.equals(LineType.RIGHT))
                fieldItem.setWinLineType(GameFieldItem.WinLineType.RIGHT);
            else if (oneMove.typeLine.equals(LineType.HORIZONTAL))
                fieldItem.setWinLineType(GameFieldItem.WinLineType.HORIZONTAL);
            else if (oneMove.typeLine.equals(LineType.VERTICAL))
                fieldItem.setWinLineType(GameFieldItem.WinLineType.VERTICAl);
        }

        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(false);
        }
        isEnableGameField = false;


    }

    public void setEnableAllUnusedGameField(boolean isEnable) {
        isEnableGameField = isEnable;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null && fields[i].getFieldType() == null)
                fields[i].setEnabled(isEnable);
        }

    }

    public void startNewGame() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(true);
            fields[i].clear();
        }
        isEnableGameField = true;
    }

    public boolean isEnableGameField() {
        return isEnableGameField;
    }

    public void setEnableGameField(boolean enableGameField) {
        isEnableGameField = enableGameField;
    }

    private class GameFieldClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {

            GameFieldItem field = (GameFieldItem) view;
//            if (lastGameFieldItem != null) lastGameFieldItem.setMarkAboutLastMove(false);
//            lastGameFieldItem = field;
//            lastGameFieldItem.setMarkAboutLastMove(true);
//            field.setEnabled(false);

            int j = field.getJ(), i = field.getI();
            //field.setFieldTypeAndDraw();
            gameHandler.occurredMove(i, j);

        }
    }
}