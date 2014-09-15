package me.timnew.shared;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AdvBaseAdapter<T, TView extends View> extends BaseAdapter {
    private List<T> items;

    protected AdvBaseAdapter(List<T> items) {
        this.items = items;
    }

    protected AdvBaseAdapter() {
        this(new ArrayList<T>());
    }

    protected List<T> getItems() {
        return items;
    }

    protected void setItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getItems().size();
    }

    @Override
    public T getItem(int position) {
        return getItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TView itemView;

        if (convertView == null) {
            itemView = createView(parent);
        } else {
            itemView = (TView) convertView;
        }

        T item = getItem(position);

        return updateView(itemView, item);
    }

    protected abstract TView updateView(TView itemView, T item);

    protected abstract TView createView(ViewGroup parent);
}
