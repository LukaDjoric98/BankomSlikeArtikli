package com.example.bankomslikeartikala.customSpinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bankomslikeartikala.models.Artikli;

// Nasa klasa za spinner mora da nasledjuje ArrayAdapter<T>
public class CustomSpinnerAdapterArtikli extends ArrayAdapter<Artikli> {
    private Context context;
    // Stavljamo niz objekata koji zelimo da prikazemo u spinneru
    private Artikli[] artikli;

    public CustomSpinnerAdapterArtikli(Context context, int textViewResourceId, Artikli[] artikli) {
        super(context, textViewResourceId, artikli);
        this.context = context;
        this.artikli = artikli;
    }

    @Override // Vraca duzinu niza
    public int getCount() {
        return artikli.length;
    }

    @Nullable
    @Override // Vraca poziciju odabranog elementa
    public Artikli getItem(int position) {
        return artikli[position];
    }

    @Override // Vraca Id odabranog elementa
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override // Vraca pregled elemenata
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        return label;
    }

    @Override // Vraca pregled elemenata u DropDown listi
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        return label;
    }
}
