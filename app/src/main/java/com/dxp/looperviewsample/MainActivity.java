package com.dxp.looperviewsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dxp.looperview.LooperShowView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    LooperShowView loopShowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loopShowView = findViewById(R.id.loopShowView);
        loopShowView.setBackgroundResource(R.drawable.shape_message_bg);
        loopShowView.startWithList(Arrays.asList(new String[]{"复方感冒灵片低至4.6元","20包板蓝根颗粒，速抢","同仁堂清喉利咽玩9.99","过年换新手机,低至1999","保险节能冰箱,低至729元"}));
    }
}
