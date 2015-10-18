package com.lyricaloriginal.soracomsampleapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

/**
 * Created by LyricalMaestro on 15/10/17.
 */
class SubScriberListAdapter extends BaseAdapter {

    private final LayoutInflater _inflater;
    private final SubScriber[] _subscribers;

    /**
     * コンストラクタ
     *
     * @param activity
     * @param subscribers
     */
    SubScriberListAdapter(Activity activity, SubScriber[] subscribers) {
        if (activity == null) {
            throw new IllegalArgumentException("第一引数activityがnullです。");
        }
        _inflater = activity.getLayoutInflater();
        _subscribers = subscribers != null ? subscribers : new SubScriber[0];
    }

    @Override
    public int getCount() {
        return _subscribers.length;
    }

    @Override
    public Object getItem(int position) {
        return _subscribers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = _inflater.inflate(R.layout.subscriber_list_item, null);
        }

        SubScriber subScriber = _subscribers[position];
        TextView imsiTextView = (TextView) convertView.findViewById(R.id.imsi_text_view);
        imsiTextView.setText("IMSI : " + subScriber.imsi);
        TextView msisdnTextView = (TextView) convertView.findViewById(R.id.msisdn_text_view);
        msisdnTextView.setText("MSISDN : " + subScriber.msisdn);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(subScriber.tags.name);
        return convertView;
    }
}
