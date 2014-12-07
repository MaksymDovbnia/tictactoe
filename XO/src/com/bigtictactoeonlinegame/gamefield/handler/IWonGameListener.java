package com.bigtictactoeonlinegame.gamefield.handler;

import com.entity.OneMove;

import java.util.List;

/**
 * Date: 25.10.2014
 * Time: 18:49
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public interface IWonGameListener {
    public void onGameWin(List<OneMove> line);
    public void onBothPlayerWantContinue();
}
