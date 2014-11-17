package com.bigtictactoeonlinegame.popup;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bigtictactoeonlinegame.activity.R;

/**
 * @author Maksym Dovbnia (maksim.dovbnya@mthechlab.net) on 10/8/2014.
 */
public class GeneralDialog extends Dialog {

    private View.OnClickListener positiveButtonListener;
    private View.OnClickListener negativeButtonListener;
    private int contentId;
    private int positiveButtonTextId;
    private int negativeButtonTextId;
    private int titleTextId;
    private String titleText;
    private CharSequence contentText;
    private IContentInitialization contentInitialization;
    private Context context;
    private boolean isAutoDismissPopup = true;


    private GeneralDialog(Builder builder) {
        super(builder.context);
        context = builder.context;
        contentId = builder.contentId;
        positiveButtonTextId = builder.positiveButtonTextId;
        negativeButtonTextId = builder.negativeButtonTextId;
        titleTextId = builder.titleTextId;
        contentText = builder.contentText;
        contentInitialization = builder.contentInitialization;
        positiveButtonListener = builder.positiveButtonListener;
        negativeButtonListener = builder.negativeButtonListener;
        isAutoDismissPopup = builder.isAutoDismissPopup;
        titleText = builder.titleText;

        initLayout();
    }


    private void initLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.general_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView contentTextView = (TextView) findViewById(R.id.dialog_text);
        if (contentId != 0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(contentId, null);
            if (contentInitialization != null) {
                contentInitialization.onContentInitialization(view);
            }
            ((FrameLayout) findViewById(R.id.content_container)).addView(view);
            contentTextView.setVisibility(View.GONE);
        } else {
            contentTextView.setText(contentText);
            findViewById(R.id.content_container).setVisibility(View.GONE);
        }

        ImageView cancelBtn = (ImageView) findViewById(R.id.dialog_cancel_btn);

        ImageView okBtn = (ImageView) findViewById(R.id.dialog_ok_btn);


        TextView titleText = (TextView) findViewById(R.id.header);
//
//        if (positiveButtonTextId != 0) {
//            okBtn.setText((getContext().getString(positiveButtonTextId)));
//        }
//        if (negativeButtonTextId != 0) {
//            cancelBtn.setText((getContext().getString(positiveButtonTextId)));
//        }

        if (!TextUtils.isEmpty(this.titleText)) {
            titleText.setText(this.titleText);
        }
        else if (titleTextId != 0) {

            titleText.setText(getContext().getString(titleTextId));
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (negativeButtonListener != null) {
                    negativeButtonListener.onClick(v);
                }
                dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonListener != null) {
                    positiveButtonListener.onClick(v);
                }
                if (isAutoDismissPopup) {
                    dismiss();
                }
            }
        });
    }


    public interface IContentInitialization {
        public void onContentInitialization(View view);
    }

    public static class Builder {
        private View.OnClickListener positiveButtonListener;
        private View.OnClickListener negativeButtonListener;
        private int contentId;
        private int positiveButtonTextId;
        private int negativeButtonTextId;
        private int titleTextId;
        private String titleText;

        private CharSequence contentText;
        private Context context;
        private IContentInitialization contentInitialization;
        private boolean isAutoDismissPopup = true;


        public Builder(Context context) {
            this.context = context;
        }


        public Builder setContentInitialization(IContentInitialization contentInitialization) {
            this.contentInitialization = contentInitialization;
            return this;
        }

        public Builder setPositiveButtonListener(View.OnClickListener positiveButtonListener) {
            this.positiveButtonListener = positiveButtonListener;
            return this;
        }

        public Builder setNegativeButtonListener(View.OnClickListener negativeButtonListener) {
            this.negativeButtonListener = negativeButtonListener;
            return this;
        }

        public Builder setContentId(int contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder setPositiveButtonTextId(int positiveButtonTextId) {
            this.positiveButtonTextId = positiveButtonTextId;
            return this;
        }

        public Builder setNegativeButtonTextId(int negativeButtonTextId) {
            this.negativeButtonTextId = negativeButtonTextId;
            return this;
        }

        public Builder setTitleTextId(int titleTextId) {
            this.titleTextId = titleTextId;
            return this;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }


        public Builder setContentText(CharSequence contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setAutoDissmisDialog(boolean isAutoDismissPopup) {
            this.isAutoDismissPopup = isAutoDismissPopup;
            return this;
        }

        public GeneralDialog build() {
            return new GeneralDialog(this);
        }
    }
}
