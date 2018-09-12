
package example.howen_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<String> {

    Context context;

    String[] items = new String[] {};

    public SpinnerAdapter(final Context context,

            final int textViewResourceId, final String[] objects) {

        super(context, textViewResourceId, objects);

        this.items = objects;

        this.context = context;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        // tv.setTextColor(Color.BLUE);
        tv.setTextSize(25);

        return convertView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(items[position]);
        // tv.setTextColor(Color.BLUE);
        tv.setTextSize(25);

        return convertView;

    }

}
