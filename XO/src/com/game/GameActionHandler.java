package com.game;

import java.util.ArrayList;
import java.util.List;

import com.entity.OneMove;
import com.entity.TypeFieldElement;
import com.utils.Loger;

public class GameActionHandler {
	int[][] array = new int[15][15];

	public GameActionHandler() {
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < array.length; j++)
				array[i][j] = 100;
	}

	private void setValue(OneMove oneMove) {
		array[oneMove.i][oneMove.j] = (oneMove.type.equals(TypeFieldElement.X)) ? 1
				: 0;
	}

	private int getValue(int i, int j) {
		if (i > array.length - 1 || j > array.length - 1 || i < 0 || j < 0)
			return 100;
		else {
			// Loger.printLog("get VAlue " +" " +i + " " +j+ " " +array[i][j]);
			return array[i][j];

		}
	}

	public List<OneMove> oneMove(OneMove oneMove) {
		setValue(oneMove);
		Loger.printLog("one move" + oneMove.i + " " + oneMove.j + " "
				+ oneMove.type);
		return searchWinLine(oneMove);
	}

	public void newGame() {
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < array.length; j++)
				array[i][j] = 100;
	}

	private List<OneMove> searchWinLine(OneMove oneMove) {
		List<OneMove> outList = new ArrayList<OneMove>();

		int value = (oneMove.type.equals(TypeFieldElement.X)) ? 1 : 0;

		// calculate line - "\"
		if (getValue(oneMove.i + 1, oneMove.j + 1) == value
				|| getValue(oneMove.i - 1, oneMove.j - 1) == value) {
			int j = oneMove.j;
			int i = oneMove.i;
			while (getValue(++i, ++j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.LEFT));
			}
			j = oneMove.j;
			i = oneMove.i;
			while (getValue(--i, --j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.LEFT));
			}
			oneMove.typeLine = TypeLine.LEFT;
			outList.add(oneMove);
			Loger.printLog("size /| " + outList.size());
			if (outList.size() >= 5) {
				return outList;
			} else
				outList.clear();
		}
		// calculate line - "|"
		if (getValue(oneMove.i - 1, oneMove.j) == value
				|| getValue(oneMove.i + 1, oneMove.j) == value) {
			int j = oneMove.j;
			int i = oneMove.i;
			while (getValue(++i, j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.VERTICAL));
			}
			j = oneMove.j;
			i = oneMove.i;
			while (getValue(--i, j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.VERTICAL));
			}
			oneMove.typeLine = TypeLine.VERTICAL;
			outList.add(oneMove);
			Loger.printLog("size | " + outList.size());
			if (outList.size() >= 5) {
				return outList;
			} else
				outList.clear();
		}
		// calculate line - "/"
		if (getValue(oneMove.i - 1, oneMove.j + 1) == value
				|| getValue(oneMove.i + 1, oneMove.j - 1) == value) {
			int j = oneMove.j;
			int i = oneMove.i;
			while (getValue(--i, ++j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.RIGHT));
			}
			j = oneMove.j;
			i = oneMove.i;
			while (getValue(++i, --j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.RIGHT));
			}
			oneMove.typeLine = TypeLine.RIGHT;
			outList.add(oneMove);
			Loger.printLog("size /" + outList.size());
			if (outList.size() >= 5) {
				return outList;
			} else
				outList.clear();
		}
		// calculate line - "-"
		if (getValue(oneMove.i, oneMove.j - 1) == value
				|| getValue(oneMove.i, oneMove.j + 1) == value) {
			int j = oneMove.j;
			int i = oneMove.i;
			while (getValue(i, ++j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.HORIZONTAL));
			}
			j = oneMove.j;
			i = oneMove.i;
			while (getValue(i, --j) == value) {
				outList.add(new OneMove(i, j, oneMove.type, TypeLine.HORIZONTAL));
			}
			oneMove.typeLine = TypeLine.HORIZONTAL;
			outList.add(oneMove);
			Loger.printLog("size  - " + outList.size());
			if (outList.size() >= 5) {
				return outList;
			} else
				outList.clear();
		}

		return null;
	}

}
