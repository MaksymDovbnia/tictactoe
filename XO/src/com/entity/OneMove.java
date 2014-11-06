package com.entity;

import com.bigtictactoeonlinegame.LineType;

public class OneMove implements Comparable <OneMove> {
	public int j;
	public int i;
	public TypeOfMove type;
	public LineType typeLine;

	public OneMove(int i, int j, TypeOfMove typeFieldElement) {
		this.type = typeFieldElement;
		this.j = j;
		this.i = i;
	}

	public OneMove(int i, int j, TypeOfMove typeFieldElement,
			LineType typeLine) {
		this.typeLine = typeLine;
		this.type = typeFieldElement;
		this.j = j;
		this.i = i;
	}

    @Override
    public int compareTo(OneMove another) {
        return 0;
    }
}
