package com.game.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.game.activity.R;

/**
 * Created by Maksym on 7/14/13.
 */
public class XOAlertDialog extends DialogFragment {
    private Dialog dialog;
    private String tile;
    private String mainText;
    private String positiveButtonText;
    private String negativeButtonText;

    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;


    public XOAlertDialog() {
        tile = "XOAlertDialog";
        mainText = "";

        positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };
        negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };

    }

    public void dismiss(){
     dialog.dismiss();
        dialog.cancel();
        dialog.hide();

    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public void setPositiveListener(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogInterface.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (positiveButtonText == null) positiveButtonText = getResources().getString(R.string.ok);
        if (negativeButtonText == null) negativeButtonText = getResources().getString(R.string.cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tile);
        builder.setMessage(mainText);
        builder.setPositiveButton(positiveButtonText, positiveListener);
        builder.setNegativeButton(negativeButtonText, negativeListener);
        dialog = builder.create();
        return dialog;
    }


    public class Builder {
        private String tile;
        protected PositiveButtonListener positiveButtonListener;
        protected NegativeButtonListener negativeButtonListener;

        public void setTile(String tile) {
            this.tile = tile;
        }

        public void setPositiveListener(PositiveButtonListener positiveListener) {
            positiveButtonListener = positiveListener;

        }

        public void setNegativeListener(NegativeButtonListener negativeListener) {
            negativeButtonListener = negativeListener;
        }

        public void build() {


        }

        ;


    }

}
