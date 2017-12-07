package com.example.devansh.sampleorai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by devansh on 22/11/17.
 */

public class DialogBuilder {

    public interface DialogListener {
        public void optionSelected(String option);
    }

    public static void buildConfirmDialogueWithMsg(Context context, String title, String msg, String t1, String t2, boolean isCancelable, final DialogListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        listener.optionSelected("YES");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        listener.optionSelected("NO");
                        break;
                }
            }
        };
        builder.setTitle(title).
                setMessage(msg).setPositiveButton(t1, dialogClickListener).
                setNegativeButton(t2, dialogClickListener).
                setCancelable(isCancelable).show();
    }

}
