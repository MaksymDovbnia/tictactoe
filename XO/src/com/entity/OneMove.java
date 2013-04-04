package com.entity;

import com.game.TypeLine;

public class OneMove {
	public int j;
	public int i;
	public TypeFieldElement type;
	public TypeLine typeLine;

	public OneMove(int i, int j, TypeFieldElement typeFieldElement) {
		this.type = typeFieldElement;
		this.j = j;
		this.i = i;
	}

	public OneMove(int i, int j, TypeFieldElement typeFieldElement,
			TypeLine typeLine) {
		this.typeLine = typeLine;
		this.type = typeFieldElement;
		this.j = j;
		this.i = i;
	}
}
