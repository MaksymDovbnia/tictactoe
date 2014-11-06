package com.bigtictactoeonlinegame.gamefield.handler;

import com.entity.OneMove;

/**
 * Date: 26.10.2014
 * Time: 10:52
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public interface IOpponentActionListener {
    public void opponentMoveTimeEnd();

    public void onOpponentPerformMove(OneMove oneMove, boolean isLast);
}
