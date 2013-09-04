package com.game.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
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
    private IContentInitialization contentInitialization;
    private int contentId = 0;
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

    public void setContentInitialization(IContentInitialization contentInitialization) {
        this.contentInitialization = contentInitialization;
    }

    public interface IContentInitialization {
        public void onContentItialization(View view);
    }


    public void dismiss() {
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

    public void setContent(int layoutId) {
        contentId = layoutId;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (contentId == 0) {
            if (positiveButtonText == null)
                positiveButtonText = getResources().getString(R.string.ok);
            if (negativeButtonText == null)
                negativeButtonText = getResources().getString(R.string.cancel);

            builder.setTitle(tile);
            builder.setMessage(mainText);
            builder.setPositiveButton(positiveButtonText, positiveListener);
            builder.setNegativeButton(negativeButtonText, negativeListener);
        } else {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(contentId, null);
            if (contentInitialization != null) contentInitialization.onContentItialization(view);
            builder.setView(view);
        }
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
