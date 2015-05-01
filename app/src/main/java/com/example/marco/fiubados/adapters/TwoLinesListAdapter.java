package com.example.marco.fiubados.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.example.marco.fiubados.model.DualField;

import java.util.List;

/**
 * Created by Marco on 22/04/2015.
 */
public class TwoLinesListAdapter extends BaseAdapter {

    private Context context;
    private List<DualField> fields;

    public TwoLinesListAdapter(Context context, List<DualField> fields) {
        this.context = context;
        this.fields = fields;
    }

    @Override
    public int getCount() {
        return this.fields.size();
    }

    @Override
    public Object getItem(int position) {
        return this.fields.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TwoLineListItem twoLineListItem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }
        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        DualField dualField = this.fields.get(position);
        text1.setText(dualField.getField1().getValue());
        text2.setText(dualField.getField2().getValue());

        // REVIEW: Podría crear un layout y definir el color desde ahi también
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.DKGRAY);

        return twoLineListItem;
    }
}
