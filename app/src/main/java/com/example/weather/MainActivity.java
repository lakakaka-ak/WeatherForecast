package com.example.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button refresh,find_city,add,first,huancun1,huancun2,huancun3,huancun4;
    private TextView city,weather,temperature,direction,power,humidity,time,updatetime,province;
    private DatabaseHelper dbHelper;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search=findViewById(R.id.search);
        refresh=findViewById(R.id.refresh);//刷新按钮
        find_city=findViewById(R.id.find_city);//查找按钮
        add=findViewById(R.id.add);//添加
        first=findViewById(R.id.first);
        province=findViewById(R.id.province);
        city=findViewById(R.id.city);//市
        weather=findViewById(R.id.weather);//天气现象
        temperature=findViewById(R.id.temperature);//气温
        direction=findViewById(R.id.direction);//风向
        power=findViewById(R.id.power);//风力
        humidity=findViewById(R.id.humidity);//湿度
        time=findViewById(R.id.time);
        updatetime=findViewById(R.id.updatetime);
        huancun1=findViewById(R.id.huancun1);
        huancun2=findViewById(R.id.huancun2);
        huancun3=findViewById(R.id.huancun3);
        huancun4=findViewById(R.id.huancun4);

        //日期设置
        SimpleDateFormat formatter =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String time= formatter.format(curDate);
        updatetime.setText(time);

        //数据库 addcity获取关注城市
        dbHelper = new DatabaseHelper(MainActivity.this, "Weather.db",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList citylist=new ArrayList();
        Cursor cursor=db.query("addcity",new String[]{"mycityname"},null,null,null,null,"id desc",null);
        if (cursor.moveToFirst()){
            do{

                String cityname = cursor.getString(cursor.getColumnIndex("mycityname"));
                citylist.add(cityname);
            }while(cursor.moveToNext());
        }
        cursor.close();
        first.setText(citylist.get(0).toString());
        db.close();
        String citypage=first.getText().toString();
        getWeatherData(citypage);


        dbHelper = new DatabaseHelper(MainActivity.this, "Weather.db",null,1);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
        //获取缓存记录
        ArrayList searchlist=new ArrayList();
        Cursor cursor1=db1.query("searchcity",new String[]{"searchcityname"},null,null,null,null,"id desc",null);
        if (cursor1.moveToFirst()){
            do{

                String searchname = cursor1.getString(cursor1.getColumnIndex("searchcityname"));
                searchlist.add(searchname);
            }while(cursor1.moveToNext());
        }
        cursor1.close();
        huancun1.setText(searchlist.get(0).toString());
        huancun2.setText(searchlist.get(1).toString());
        huancun3.setText(searchlist.get(2).toString());
        huancun4.setText(searchlist.get(3).toString());
        db1.close();


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String citypage=city.getText().toString();
                getWeatherData(citypage);
                SimpleDateFormat formatter =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                String time= formatter.format(curDate);
                updatetime.setText(time);
                Toast.makeText(MainActivity.this,"刷新成功",
                        Toast.LENGTH_SHORT).show();
            }
        });

        find_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchcity=search.getText().toString();
                if(TextUtils.isEmpty(searchcity)){
                    Toast.makeText(MainActivity.this,"请输入内容",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    getWeatherData(searchcity);
                    dbHelper = new DatabaseHelper(MainActivity.this, "Weather.db",null,1);
                    SQLiteDatabase db= dbHelper.getReadableDatabase();
                    String curcity=city.getText().toString();
                    ContentValues values = new ContentValues();
                    values.put("searchcityname",curcity);
                    db.insert("searchcity",null,values);
                    //获取缓存记录
                    ArrayList searchlist=new ArrayList();
                    Cursor cursor=db.query("searchcity",new String[]{"searchcityname"},null,null,null,null,"id desc",null);
                    if (cursor.moveToFirst()){
                        do{

                            String searchname = cursor.getString(cursor.getColumnIndex("searchcityname"));
                            searchlist.add(searchname);
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                    huancun1.setText(searchlist.get(0).toString());
                    huancun2.setText(searchlist.get(1).toString());
                    huancun3.setText(searchlist.get(2).toString());
                    huancun4.setText(searchlist.get(3).toString());
                    db.close();

                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DatabaseHelper(MainActivity.this, "Weather.db",null,1);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                ContentValues values = new ContentValues();
                String citypage=city.getText().toString();
                values.put("mycityname",citypage);
                db.insert("addcity",null,values);
                ArrayList citylist=new ArrayList();
                Cursor cursor=db.query("addcity",new String[]{"mycityname"},null,null,null,null,"id desc",null);
                if (cursor.moveToFirst()){
                    do{

                        String cityname = cursor.getString(cursor.getColumnIndex("mycityname"));
                        citylist.add(cityname);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                first.setText(citylist.get(0).toString());
                db.close();

            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curadd=first.getText().toString();//获取关心城市
                getWeatherData(curadd);
            }
        });
        huancun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String huancunone=huancun1.getText().toString();//获取关心城市
                getWeatherData(huancunone);
            }
        });
        huancun2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String huancuntwo=huancun2.getText().toString();//获取关心城市
                getWeatherData(huancuntwo);
            }
        });
        huancun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String huancunthree=huancun3.getText().toString();//获取关心城市
                getWeatherData(huancunthree);
            }
        });
        huancun4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String huancunfourth=huancun4.getText().toString();//获取关心城市
                getWeatherData(huancunfourth);
            }
        });

    }

    //获取天气信息
    private void getWeatherData(String city)
    {
        final String address = "https://restapi.amap.com/v3/weather/weatherInfo?city="+city+"&key=563aba69b0d3208deebb98c207799fa9&extensions=base";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(address);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while((str=reader.readLine())!=null)
                    {
                        sb.append(str);
                        Log.d("data from url",str);
                    }
                    String response = sb.toString();
                    Log.d("response",response);
                    parseJSONWithGSON(response);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithGSON(String response) {
        Gson g=new Gson();
        gson gson=g.fromJson(response,gson.class);
        List<gson.LivesBean> lives=gson.getLives();
        Log.d("MainActivity",lives.get(0).getHumidity());
        Log.d("MainActivity",lives.get(0).getReporttime());
        humidity.setText(lives.get(0).getHumidity());
        time.setText(lives.get(0).getReporttime());
        province.setText(lives.get(0).getProvince());
        city.setText(lives.get(0).getCity());
        weather.setText(lives.get(0).getWeather());
        temperature.setText(lives.get(0).getTemperature());
        direction.setText(lives.get(0).getWinddirection());
        power.setText(lives.get(0).getWindpower());

    }

}

