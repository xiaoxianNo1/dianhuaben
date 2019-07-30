package com.xiaoxian.dianhuaben.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoxian.dianhuaben.Adapter.MyAdapter;
import com.xiaoxian.dianhuaben.Application.MyApplication;
import com.xiaoxian.dianhuaben.MainActivity;
import com.xiaoxian.dianhuaben.R;
import com.xiaoxian.dianhuaben.SqlList.UtilDao;
import com.xiaoxian.dianhuaben.model.User;
import com.xiaoxian.dianhuaben.util.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textNum;
    private Button button;
    private ListView listView;
    private List<User> list,newList;
    private UtilDao dao;
    private MyAdapter adapter;
    private int listNum = 0;

    List<User> contactsList=new ArrayList<>();

    private PermissionsUtil permissionsUtil;
    //储存所有权限
    String[] allpermissions=new String[]{
            Manifest.permission.READ_CONTACTS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applypermission();
        setContentView(R.layout.activity_index);


        //初始化控件
        initWidget();

        //实例dao
        DbUtil();

        //判断是否开启读取通讯录的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            readContacts();
        }

        //显示ListView
        showListView();
        //显示listView的条目数量
        linkmanNum();


    }

    /**
     * 初始化控件
     * */
    private void initWidget(){
        button = findViewById(R.id.main_but);
        listView = findViewById(R.id.main_list_view);
        textNum = findViewById(R.id.main_num);
        newList = new ArrayList<>();
        list = new ArrayList<>();
    }

    /**
     * 显示ListView
     * */
    public void showListView(){
        //查询数据
        /**
         * 添加数据到链表中
         * **/
        list = dao.inquireData();

        /**
         * 创建并绑定适配器
         * */
        adapter = new MyAdapter(this,R.layout.item_phone,list);
        listView.setAdapter(adapter);

        /**
         * ListView事件监听
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogList();
                listNum = i;
            }
        });

        button.setOnClickListener(this);
    }

    /**
     * 普通对话框
     * */
    public void dialogNormal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener dialogOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                User userDel = list.get(listNum);
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        dao.delData("userName=?",new String[]{userDel.getName()});
                        refresh();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:break;
                }
            }
        };
        builder.setTitle("删除联系人");
        builder.setMessage("确定要删除吗？");
        builder.setPositiveButton("确定", dialogOnClick);
        builder.setNegativeButton("取消",dialogOnClick);
        builder.create().show();
    }

    /**
     * 选项列表
     * */
    public void dialogList(){
        final String[] items = {"拨打电话","发送短信","编辑","删除"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //拿到当前选中项的 User 对象
                User userNum = list.get(listNum);
                Intent intent;
                switch (i){
                    //拨打电话
                    case 0: intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + userNum.getPhone()));
                        startActivity(intent);
                        break;
                    //发送短信
                    case 1: intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + userNum.getPhone()));
                        startActivity(intent);
                        break;
                    case 2: intent = new Intent(IndexActivity.this,AddDataActivity.class);
                        //传入当前选中项的姓名和电话以在编辑页面中显示在输入框中
                        intent.putExtra("edit_name",userNum.getName().toString());
                        intent.putExtra("edit_phone",userNum.getPhone().toString());
                        startActivityForResult(intent,2);
                        break;
                    //弹出对话框提示是否删除
                    case 3: dialogNormal();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    //刷新
    public void refresh(){
        //最后查询数据刷新列表
        getNotifyData();
    }

    //页面顶部显示ListView条目数
    public void linkmanNum(){
        textNum.setText("("+list.size()+")");
    }

    //点击添加按钮
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_but:
                //跳转到 AddData Activity 传入请求码 1
                Intent intent = new Intent(IndexActivity.this,AddDataActivity.class);
                startActivityForResult(intent,1);
                break;
            default:break;
        }
    }

    public void DbUtil(){
        dao = ((MyApplication)this.getApplication()).getDao();
    }

    /**
     * 当页面回到此活动时，调用此方法，刷新ListView
     * */
    @Override
    protected void onResume() {
        super.onResume();
        getNotifyData();
    }

    /**
     * 这个是用来动态刷新 * */
    public void getNotifyData(){
        //使用新的容器获得最新查询出来的数据
        newList = dao.inquireData();
        //清除原容器里的所有数据
        list.clear();
        //将新容器里的数据添加到原来容器里
        list.addAll(newList);
        //更新页面顶部括号里显示数据
        linkmanNum();
        //刷新适配器
        adapter.notifyDataSetChanged();
    }

    /**
     * 上一个页面传回来的值
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //请求码为1，表示点击了添加按钮
            case 1:
                //执行添加方法
                if(resultCode == RESULT_OK){
                    String[] key = data.getStringArrayExtra("key");
                    String[] values = data.getStringArrayExtra("values");
                    dao.addData("UserInfo",key,values);
                }
                break;
            //请求码为2，表示点击了编辑按钮
            case 2:
                //执行修改方法
                if(resultCode == RESULT_OK){
                    User user = list.get(listNum);
                    String name = data.getStringExtra("name");
                    String phone = data.getStringExtra("phone");
                    String[] values = {name,phone,user.getName()};
                    dao.update(values);
                }
                break;
        }
    }


    //调用并获取联系人信息
    private void readContacts() {
        Cursor cursor=null;
        try {
            //查询联系人数据,使用了getContentResolver().query方法来查询系统的联系人的数据
            // CONTENT_URI就是一个封装好的Uri，是已经解析过得常量
             cursor=getContentResolver().query(
             ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null,null,null
             );
             //对cursor进行遍历，取出姓名和电话号码
             if (cursor!=null){

                 //String[] key = null;//data.getStringArrayExtra("key");
                 //String[] values ;//= data.getStringArrayExtra("values");
                 while (cursor.moveToNext()){
                     //获取联系人姓名

                     String displayName=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                     ));

                     //获取联系人手机号
                     String number=cursor.getString(cursor.getColumnIndex(
                           ContactsContract.CommonDataKinds.Phone.NUMBER
                           ));


                     System.out.println("1联系人姓名："+displayName+",手机号："+number);
                     //System.out.println("list"+list.toString());


                     if(!displayName.equals("") && !number.equals("")){
                         //System.out.println("2联系人姓名："+displayName+",手机号："+number);
                         String[] key = {"userName","userPhone"};
                         String[] values = {displayName,number};
                         System.out.println("key："+key[0].toString()+",values："+values[0].toString());
                         System.out.println("key："+key[1].toString()+",values："+values[1].toString());
                         dao.addData("UserInfo",key,values);
                     }


                 }

             }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //记得关掉cursor

            if (cursor!=null){
                cursor.close();
            }
        }
    }
    /*private void readContacts() {
        Cursor cursor = null;
        try {
            //cursor指针 query询问 contract协议 kinds种类
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list.add(displayName + '\n' + number);
                }
                //notify公布
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/


    /**
     * 动态申请权限
     */
    public void applypermission(){
        permissionsUtil = new PermissionsUtil(this);
        permissionsUtil.shouldShowPermissionRationale(111,allpermissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsUtil.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
