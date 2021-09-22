package space.robinjoon.accountbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import space.robinjoon.accountbook.R;
import space.robinjoon.accountbook.dto.Account;
import space.robinjoon.accountbook.dto.Asset;
import space.robinjoon.accountbook.dto.Category;
import space.robinjoon.accountbook.dto.ConversionAccount;
import space.robinjoon.accountbook.dto.DtoWithHttpCode;
import space.robinjoon.accountbook.network.CustomOkHttpClient;
import space.robinjoon.accountbook.network.HttpConnection;

public class ConversionAccountActivity extends AppCompatActivity {
    private String date,time;
    private TextView dateView,timeView,fromView,toView;
    private EditText valueView,memoView;
    private TableLayout table;
    private OkHttpClient client = CustomOkHttpClient.client;
    private static Handler handler;
    private ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_account);
        initViews();
        initHandler();
    }
    private void initViews(){
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);
        fromView = findViewById(R.id.from);
        toView = findViewById(R.id.to);
        valueView = findViewById(R.id.value);
        valueView.setTextColor(dateView.getTextColors());
        memoView = findViewById(R.id.memo);
        memoView.setTextColor(dateView.getTextColors());
        table = findViewById(R.id.table);
        dateView.setText(date);
        timeView.setText(time);
        fromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = HttpConnection.getAssetList();
                        try{
                            Response response = client.newCall(request).execute();
                            String json = response.body().string();
                            Log.i("json",json);
                            DtoWithHttpCode<List<Asset>> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<List<Asset>>>(){});
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.obj = dto;
                            handler.sendMessage(message);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        toView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = HttpConnection.getAssetList();
                        try{
                            Response response = client.newCall(request).execute();
                            String json = response.body().string();
                            Log.i("json",json);
                            DtoWithHttpCode<List<Asset>> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<List<Asset>>>(){});
                            Message message = handler.obtainMessage();
                            message.what = 2;
                            message.obj = dto;
                            handler.sendMessage(message);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    public void save(View view){
        ConversionAccount.Builder builder = new ConversionAccount.Builder();
        ConversionAccount account =
                builder.setFrom(fromView.getText().toString())
                        .setTo(toView.getText().toString())
                        .setValue(Long.parseLong(valueView.getText().toString()))
                        .setMemo(memoView.getText().toString())
                        .setTime(dateView.getText().toString()+"T"+timeView.getText().toString())
                        .setYearMonth(date.substring(0,7))
                        .build();
        StringBuffer json = new StringBuffer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Request request = HttpConnection.postConversionAccount(account);
                    Log.i("requestjson",account.getYearMonth());
                    Response response = client.newCall(request).execute();
                    Log.i("json",json.toString());
                    DtoWithHttpCode<Account> dto = mapper.readValue(json.toString(),new TypeReference<DtoWithHttpCode<Account>>(){});
                    if(dto.getHttpCode()!=200 || dto.getData().getAid()==0){
                        Log.i("error",json.toString());
                    }else{
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        finish();
    }
    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            private Message msg;

            @Override
            public void handleMessage(Message msg) {
                this.msg = msg;
                if (msg.what == 1) { // getBook
                    table.removeAllViews();
                    DtoWithHttpCode<List<Asset>> dto = (DtoWithHttpCode<List<Asset>>) msg.obj;
                    List<Asset> assetList = dto.getData();
                    int rowcount = assetList.size() / 4 + 1;
                    for (int i = 0; i < rowcount; i++) {
                        TableRow tr = new TableRow(getApplicationContext());
                        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        tr.setLayoutParams(params);
                        for (int j = 0; j < 4; j++) {
                            if ((i * 4 + j) < assetList.size()) {
                                TextView tv = new TextView(getApplicationContext());
                                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                tv.setText(assetList.get((i * 4 + j)).getAssetName());
                                tv.setGravity(Gravity.CENTER);
                                tv.setBackground(getDrawable(R.drawable.border));
                                tv.setTextColor(dateView.getTextColors());
                                tv.setLayoutParams(params);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fromView.setText(tv.getText().toString());
                                    }
                                });
                                tr.addView(tv);
                            }
                        }
                        table.addView(tr);
                    }
                } else if (msg.what == 2) {
                    table.removeAllViews();
                    DtoWithHttpCode<List<Asset>> dto = (DtoWithHttpCode<List<Asset>>) msg.obj;
                    List<Asset> assetList = dto.getData();
                    int rowcount = assetList.size() / 4 + 1;
                    for (int i = 0; i < rowcount; i++) {
                        TableRow tr = new TableRow(getApplicationContext());
                        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        tr.setLayoutParams(params);
                        for (int j = 0; j < 4; j++) {
                            if ((i * 4 + j) < assetList.size()) {
                                TextView tv = new TextView(getApplicationContext());
                                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                tv.setText(assetList.get((i * 4 + j)).getAssetName());
                                tv.setGravity(Gravity.CENTER);
                                tv.setBackground(getDrawable(R.drawable.border));
                                tv.setTextColor(dateView.getTextColors());
                                tv.setLayoutParams(params);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        toView.setText(tv.getText().toString());
                                    }
                                });
                                tr.addView(tv);
                            }
                        }
                        table.addView(tr);
                    }
                }
            }
        };
    }
    public void cancel(View view){
        finish();
    }
    public void changeToAccountActivity(View view){
        Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        startActivity(intent);
        finish();
    }
}