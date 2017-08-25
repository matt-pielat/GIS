package pl.pw.mini.mininavigator.views.wifiData;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.pw.mini.mininavigator.R;

public class DoubleTextAdapter extends BaseAdapter {

    private static class ViewHolderItem {
        public TextView upperTextView;
        public TextView lowerTextView;
    }

    private Context mContext;
    private List<Pair<String, String>> mData;

    public DoubleTextAdapter(Context context, List<Pair<String, String>> data){
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listitem_double_textview, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.upperTextView = (TextView) convertView.findViewById(R.id.upperTextView);
            viewHolder.lowerTextView = (TextView) convertView.findViewById(R.id.lowerTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Pair<String, String> item = mData.get(position);

        if (item != null) {
            viewHolder.upperTextView.setText(item.first);
            viewHolder.lowerTextView.setText(item.second);
        }

        return convertView;
    }
}
