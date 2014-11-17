package com.entity;

import com.bigtictactoeonlinegame.LineType;

import java.util.Comparator;

public class OneMove {
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


    public static Comparator<OneMove> firstOneMoveComparator = new Comparator<OneMove>() {
        @Override
        public int compare(OneMove lhs, OneMove rhs) {
            if (lhs.i > rhs.i || lhs.j > rhs.j) {
                return 1;
            }

            if (lhs.i == rhs.i && lhs.j == rhs.j) {
                return 0;
            }

            return -1;
        }
    };


    public static Comparator<OneMove> secondOneMoveComparator = new Comparator<OneMove>() {
        @Override
        public int compare(OneMove lhs, OneMove rhs) {
            if (lhs.i < rhs.i && lhs.j > rhs.j) {
                return 1;
            }

            if (lhs.i == rhs.i && lhs.j == rhs.j) {
                return 0;
            }

            return -1;
        }
    };
}


