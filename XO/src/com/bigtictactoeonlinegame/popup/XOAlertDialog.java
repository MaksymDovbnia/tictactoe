package com.bigtictactoeonlinegame.popup;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;
import android.view.*;

import com.bigtictactoeonlinegame.activity.*;


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

    public enum ALERT_TYPE {ONE_BUTTON, TWO_BUTTON}

    private ALERT_TYPE alert_type;

    private int sleepTimeBeforeShowPopup = 0;


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

    public int getSleepTimeBeforeShowPopup() {
        return sleepTimeBeforeShowPopup;
    }

    public void setSleepTimeBeforeShowPopup(int sleepTimeBeforeShowPopup) {
        if (sleepTimeBeforeShowPopup < 0 ) throw  new IllegalStateException("time can't be above 0");
        this.sleepTimeBeforeShowPopup = sleepTimeBeforeShowPopup;
    }

    public void setAlert_type(ALERT_TYPE alert_type) {
        this.alert_type = alert_type;
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
            if (alert_type == null || alert_type == ALERT_TYPE.TWO_BUTTON) {
                builder.setPositiveButton(positiveButtonText, positiveListener);
                builder.setNegativeButton(negativeButtonText, negativeListener);
            } else if (alert_type == ALERT_TYPE.ONE_BUTTON) {
                builder.setPositiveButton(positiveButtonText, positiveListener);
            }


        } else {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(contentId, null);
            if (contentInitialization != null) contentInitialization.onContentItialization(view);
            builder.setView(view);
        }
        dialog = builder.create();
        if (sleepTimeBeforeShowPopup != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return dialog;
    }



}
