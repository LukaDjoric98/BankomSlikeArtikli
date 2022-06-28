package com.example.bankomslikeartikala.customSpinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bankomslikeartikala.models.User;

public class CustomSpinnerAdapterUsers extends ArrayAdapter<User> {
    private Context context;
    private User[] users;

    public CustomSpinnerAdapterUsers(Context context, int textViewResourceId, User[] users) {
        super(context, textViewResourceId, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.length;
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return users[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        return label;
    }
}
