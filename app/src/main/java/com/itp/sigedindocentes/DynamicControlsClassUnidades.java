package com.itp.sigedindocentes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DynamicControlsClassUnidades {
    private TextView t;
    private ImageButton ib;
    private ListView lv;

    public DynamicControlsClassUnidades(Context c, String pro) {
        this.t = new TextView(c);
        this.t.setGravity(Gravity.CENTER_VERTICAL);
        this.t.setText(pro);
        this.t.setPadding(10,10,5,15);
        this.t.setTextColor(Color.BLACK);
        this.t.setTextSize(15);
        this.t.setTypeface(null, Typeface.BOLD);
        this.ib = new ImageButton(c);
        this.ib.setBackgroundResource(R.drawable.transparentbutton);
        this.ib.setPadding(1,0,1,0);
        this.lv = new ListView(c);
    }

    public TextView getT() {
        return t;
    }
    public ListView getL() {
        return lv;
    }
    public ImageButton getIb() {
        return ib;
    }
}