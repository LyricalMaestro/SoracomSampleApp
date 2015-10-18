package com.lyricaloriginal.soracomsampleapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * エラーを通知するためDialogです。
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String ARG_PARAM = "MSG";

    public static AlertDialogFragment newInstance(String msg) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString(ARG_PARAM, ""));
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }
}
