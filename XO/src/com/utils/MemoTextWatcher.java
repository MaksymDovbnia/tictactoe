package com.utils;

/**
 * Date: 19.12.13
 * Time: 22:43
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com) */


import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author o.desiatko
 */
public abstract class MemoTextWatcher implements TextWatcher {
    /**
     * Constant, that sets max amount characters of text
     */
    public static final int MAX_CHARACTER_COUNT = 15;
    private int max_character;

    String oldText = "";

    /**
     * Crete MemoTextWatcher with default size
     */
    protected MemoTextWatcher() {
        max_character = MAX_CHARACTER_COUNT;
    }

    /**
     * Crete MemoTextWatcher with special size
     *
     * @param maxSize max size for  TextWatcher
     */
    protected MemoTextWatcher(int maxSize) {
        max_character = maxSize;
    }


    /**
     * Save text before changes. If changed tex length more then MAX_CHARACTER_COUNT then set flag to
     * remove last changes. This method is called to notify you that, within <code>s</code>, the
     * <code>count</code> characters beginning at <code>start</code> are about to be replaced by new text
     * with length <code>after</code>.
     */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (s.length() <= max_character)
            oldText = s.toString();
    }

    /**
     * do nothing until override
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Revert old text if length of new text more then MAX_CHARACTER_COUNT
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > max_character) {
            s.replace(0, s.length(), oldText, 0, oldText.length());
            showMaxAlert();
        } else {
            inputtedTextLengthChanged(s.length());
        }
    }

    /**
     * do nothing until override
     *
     * @param length - new length of text
     */
    protected void inputtedTextLengthChanged(int length) {
    }

    /**
     * Abstract method. Show alert when changes have been removed.
     */
    protected abstract void showMaxAlert();
}
