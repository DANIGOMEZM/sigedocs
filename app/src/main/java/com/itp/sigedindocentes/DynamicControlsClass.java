package com.itp.sigedindocentes;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class DynamicControlsClass {
    private TextView t1, t2;
    private EditText e;
    private ImageButton ib1, ib2;

    public DynamicControlsClass(int idt, Context c, String nest, int f, int i) {
        this.t1 = new TextView(c);
        this.t1.setGravity(Gravity.CENTER_VERTICAL);
        this.t1.setText(MethodsClass.textCapWords(nest));
        this.t1.setPadding(0,0,5,0);
        this.t1.setTextColor(Color.BLACK);
        this.t2 = new TextView(c);
        this.t2.setGravity(Gravity.CENTER);
        this.t2.setTextColor(Color.BLACK);
        this.t2.setWidth(70);
        this.t2.setPadding(10,0,0,0);
        this.t2.setText(Html.fromHtml("<b>"+i+"</b>."));
        this.e = new EditText(c);
        this.e.setId(idt);
        this.e.setText(""+f);
        this.e.setWidth(90);
        this.e.setGravity(Gravity.CENTER);
        this.e.setEnabled(false);
        this.e.setTextColor(Color.BLACK);
        this.ib1 = new ImageButton(c);
        this.ib1.setBackgroundResource(R.drawable.transparentbutton);
        this.ib1.setImageResource(R.drawable.ic_down_foreground);
        this.ib1.setPadding(1,0,1,0);
        this.ib2 = new ImageButton(c);
        this.ib2.setBackgroundResource(R.drawable.transparentbutton);
        this.ib2.setImageResource(R.drawable.ic_up_foreground);
        this.ib2.setPadding(1,0,1,0);
    }

    public TextView getT1() {
        return t1;
    }
    public TextView getT2() {
        return t2;
    }
    public EditText getE() {
        return e;
    }
    public ImageButton getIb1() {
        return ib1;
    }
    public ImageButton getIb2() {
        return ib2;
    }
}