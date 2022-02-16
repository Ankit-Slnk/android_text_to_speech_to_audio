package com.ankit.texttospeechtoaudio.adaper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ankit.texttospeechtoaudio.models.CustomLocaleClass;

import java.util.ArrayList;

public class LanguagesAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private ArrayList<CustomLocaleClass> listData;

    public LanguagesAdapter(Context context, ArrayList<CustomLocaleClass> listData) {
        this.listData = listData;
        this.context = context;
    }

    public int getCount() {
        return listData.size();
    }

    public Object getItem(int i) {
        return listData.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.LEFT);
        txt.setText(listData.get(position).displayName);
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
//        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_down, 0);
        txt.setText(listData.get(i).displayName);
        txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }
}
