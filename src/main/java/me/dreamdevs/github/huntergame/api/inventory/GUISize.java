package me.dreamdevs.github.huntergame.api.inventory;

import lombok.Getter;

public enum GUISize {

    ONE_ROW(9), TWO_ROWS(18), THREE_ROWS(27), FOUR_ROWS(36), FIVE_ROWS(45), SIX_ROWS(54);

    private @Getter int size;

    GUISize(int size) {
        this.size = size;
    }

    public static GUISize sizeOf(int size) {
        if(size >= 0 && size <= 9)
            return ONE_ROW;
        else if (size >= 10 && size <= 18)
            return TWO_ROWS;
        else if (size >= 19 && size <= 27)
            return THREE_ROWS;
        else if (size >= 28 && size <= 36)
            return FOUR_ROWS;
        else if (size >= 37 && size <= 45)
            return FIVE_ROWS;
        else if (size >= 46)
            return SIX_ROWS;
        return null;
    }

}