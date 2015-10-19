package com.lyricaloriginal.soracomsampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by LyricalMaestro on 15/10/20.
 */
public class SingleChoiceDialogFragment extends DialogFragment {

    private Listener _listener = null;

    /**
     * @param title
     * @param values
     * @param selection
     * @return
     */
    public static DialogFragment newInstance(String title, String[] values, int selection) {
        int sel = selection;
        if (values.length <= selection || selection < 0) {
            sel = -1;
        }

        Bundle args = new Bundle();
        args.putStringArray("VALUES", values);
        args.putString("TITLE", title);
        args.putInt("SELECTION", sel);

        SingleChoiceDialogFragment d = new SingleChoiceDialogFragment();
        d.setArguments(args);
        return d;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            _listener = (Listener) activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("TITLE"));
        builder.setSingleChoiceItems(
                getArguments().getStringArray("VALUES"),
                getArguments().getInt("SELECTION"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        String[] values = getArguments().getStringArray("VALUES");
                        if (_listener != null) {
                            _listener.onSelectItemListener(getTag(), values[which]);
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public interface Listener {
        void onSelectItemListener(String tag, String selectedValue);
    }
}
