package com.xiaoxian.dianhuaben.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xiaoxian.dianhuaben.Application.MyApplication;
import com.xiaoxian.dianhuaben.R;
import com.xiaoxian.dianhuaben.SqlList.UtilDao;

public class AddDataActivity extends AppCompatActivity {
    private EditText edit_name,edit_phone;
    private Button but;
    private UtilDao dao;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        //初始化组件
        initWidget();
        DbUtil();

        /**
         * 点击编辑按钮传过来的值
         * 用于显示当前编辑项的数据信息
         * */
        intent = getIntent();
        String user_name = intent.getStringExtra("edit_name");
        String user_phone = intent.getStringExtra("edit_phone");
        edit_name.setText(user_name);
        edit_phone.setText(user_phone);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取到两个输入框的值
                String name = edit_name.getText().toString();
                String phone = edit_phone.getText().toString();
                if(!name.equals("") && !phone.equals("")){
                    /**
                     * 数据库操作需要用到的数据
                     * 详情请查看 UtilDao 类下的 addData() 方法
                     * */
                    String[] key = {"userName","userPhone"};
                    String[] values = {name,phone};
                    intent = new Intent();
                    //点击添加按钮则返回 key 和 values 数组
                    intent.putExtra("key",key);
                    intent.putExtra("values",values);
                    //点击编辑按钮则返回 name 和 phone 字符串
                    intent.putExtra("name",name);
                    intent.putExtra("phone",phone);

                    setResult(RESULT_OK,intent);
                    finish();
                } else if(name.equals("") || phone.equals("")){
                    finish();
                }
            }
        });
    }

    /**
     * 初始化控件
     * */
    private void initWidget(){
        edit_name = findViewById(R.id.add_edit_name);
        edit_phone = findViewById(R.id.add_edit_phone);
        but = findViewById(R.id.add_but);
    }

    public void DbUtil(){
        dao = ((MyApplication)this.getApplication()).getDao();
    }

}
