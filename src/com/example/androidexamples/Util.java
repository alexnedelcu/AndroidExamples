package com.example.androidexamples;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;



public class Util {

	public static void showAlert(Context context, int bodyMessage, int positiveButtonMessage) {
		// showing error dialog if the class does not exist
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(bodyMessage)
			.setTitle(positiveButtonMessage)
			.setPositiveButton("OK", new OnClickListener () {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// the dialog disappears - nothing to do
				}});
		builder.show();
	}
}
