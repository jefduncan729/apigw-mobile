package com.axway.apigw.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.axway.apigw.android.BaseApp;
import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.event.PassphraseEnteredEvent;

public class DomainPassphraseDialog extends DialogFragment {

	private View dlgView;
	private TextView txtMsg;
	private EditText edtDomainPP;
	private EditText edtKeyPP;
	private int sslOption;

	public static DomainPassphraseDialog newInstance(String title, String msg, int sslOpt) {
		Bundle args = new Bundle();
		args.putString(Intent.EXTRA_TITLE, title);
		switch (sslOpt) {
			case R.id.edit_external_ca:
				msg = "Enter passphrase for temporary key files";
				break;
			case R.id.edit_system_ca:
			case R.id.edit_user_ca:
				msg = "Enter domain passphrase and passphrase for temporary key files";
				break;
		}
		args.putString(Intent.EXTRA_TEXT, msg);
		DomainPassphraseDialog rv = new DomainPassphraseDialog();
		rv.sslOption = sslOpt;
		rv.setArguments(args);
		return rv;
	}

	private DialogInterface.OnClickListener onYes = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			final String dpp = edtDomainPP.getText().toString();
			final String kpp = edtKeyPP.getText().toString();
			BaseApp.post(new PassphraseEnteredEvent(sslOption, dpp, kpp));
		}
	};
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Bundle args = getArguments();
    	if (args == null) {
			args = new Bundle();
    	}
    	LayoutInflater inflater = LayoutInflater.from(getActivity());
    	dlgView = inflater.inflate(R.layout.p12_dlg, null);
		txtMsg = (TextView)dlgView.findViewById(R.id.text01);
    	edtDomainPP = (EditText)dlgView.findViewById(R.id.text02);
		edtKeyPP = (EditText)dlgView.findViewById(R.id.text03);
		switch (sslOption) {
			case R.id.edit_external_ca:
				edtDomainPP.setVisibility(View.GONE);
				edtKeyPP.setVisibility(View.VISIBLE);
				break;
			case R.id.edit_system_ca:
			case R.id.edit_user_ca:
				edtDomainPP.setVisibility(View.VISIBLE);
				edtKeyPP.setVisibility(View.VISIBLE);
				break;
		}
		txtMsg.setText(args.getString(Intent.EXTRA_TEXT));
        return new AlertDialog.Builder(getActivity())
                .setTitle(args.getString(Intent.EXTRA_TITLE))
				.setIcon(R.mipmap.ic_action_warning_holo_light)
                .setView(dlgView)
                .setPositiveButton(android.R.string.yes, onYes)
                .setNegativeButton(android.R.string.no, Constants.NOOP_LISTENER)
                .create();
    }
}