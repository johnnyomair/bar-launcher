package com.jo.barlauncher.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.App;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigureRowAdapter extends ArrayAdapter<App> {
    private final Context mContext;
    private final ArrayList<App> mApps;
    private ArrayList<App> mResults;
    private CharSequence mConstraint;

    public ConfigureRowAdapter(Context context, ArrayList<App> apps) {
        super(context, R.layout.list_item_add_app, apps);
        mContext = context;
        mApps = apps;
        mResults = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_add_app, parent, false);
            holder = new ViewHolder();
            holder.mLabelTextView = (TextView) convertView.findViewById(R.id.text_label);
            holder.mIconImageView = (ImageView) convertView.findViewById(R.id.image_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        App app = mResults.get(position);

        if (mConstraint != null) {
            Spannable spannable = (Spannable) Html.fromHtml(app.label);
            Pattern pattern = Pattern.compile("\\b" + mConstraint.toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(app.label);

            while (matcher.find()) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            holder.mLabelTextView.setText(spannable);
        } else {
            holder.mLabelTextView.setText(app.label);
        }

        holder.mIconImageView.setImageBitmap(app.icon);

        return convertView;
    }

    @Override
    public int getCount() {
        if(mResults != null) {
            return mResults.size();
        }

        return 0;
    }

    @Override
    public App getItem(int position) {
        if(mResults != null && mResults.size() > position){
            return mResults.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // noinspection unchecked
                mResults = (ArrayList<App>) results.values;

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<App> filteredApps = new ArrayList<>();

                mConstraint = constraint;

                if (constraint == null || constraint.length() == 0) {
                    results.values = mApps;
                    results.count = mApps.size();
                } else {
                    Pattern pattern = Pattern.compile("\\b" + constraint.toString(), Pattern.CASE_INSENSITIVE);

                    for (App app : mApps) {
                        Matcher matcher = pattern.matcher(app.label);

                        if (matcher.find()) {
                            filteredApps.add(app);
                        }
                    }

                    results.values = filteredApps;
                    results.count = filteredApps.size();
                }

                return results;
            }
        };
    }

    private class ViewHolder {
        public TextView mLabelTextView;

        public ImageView mIconImageView;
    }
}
