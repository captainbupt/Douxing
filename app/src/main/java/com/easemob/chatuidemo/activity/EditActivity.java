package com.easemob.chatuidemo.activity;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseBackActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends BaseBackActionBarActivity {
    private EditText editText;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_edit);
        setActionbarTitle(R.string.Change_the_group_name);
        editText = (EditText) findViewById(R.id.edittext);
        String data = getIntent().getStringExtra("data");
        if (data != null)
            editText.setText(data);
        editText.setSelection(editText.length());
        setRightText(R.string.save, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
    }


    public void save(View view) {
        setResult(RESULT_OK, new Intent().putExtra("data", editText.getText().toString()));
        finish();
    }
}
