package pl.pw.mini.mininavigator.views.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.pw.mini.mininavigator.R;


public class MenuEntryAdapter extends BaseAdapter {

    private static class ViewHolderItem {
        public TextView textView;
        public ImageView iconImageView;
    }

    private Activity mActivity;
    private List<MenuEntry> mMenuItemList;

    public MenuEntryAdapter AddEntry(int stringId, int drawableId) {
        MenuEntry entry = new MenuEntry(stringId, drawableId);
        mMenuItemList.add(entry);
        return this;
    }

    public MenuEntryAdapter(Activity activity) {
        mActivity = activity;
        mMenuItemList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMenuItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listitem_icon_text, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.menuTextView);
            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.menuIconImageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        MenuEntry menuEntry = mMenuItemList.get(position);

        if (menuEntry != null){
            String text = mActivity.getString(menuEntry.stringId);
            viewHolder.textView.setText(text);
            viewHolder.iconImageView.setImageResource(menuEntry.drawableId);
        }

        return convertView;
    }
}
