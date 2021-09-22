package space.robinjoon.accountbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import space.robinjoon.accountbook.R;
import space.robinjoon.accountbook.dto.Account;
import space.robinjoon.accountbook.dto.AccountBook;
import space.robinjoon.accountbook.dto.Asset;
import space.robinjoon.accountbook.dto.Category;
import space.robinjoon.accountbook.dto.DtoWithHttpCode;
import space.robinjoon.accountbook.network.CustomOkHttpClient;
import space.robinjoon.accountbook.network.HttpConnection;

public class AccountActivity extends AppCompatActivity {

    private MaterialButton incomeButton,expenditureButton,conversionButton;
    private TextView date,time,asset,category;
    private EditText value,memo;
    private TableLayout table;
    private String timeString;
    private String dateString;
    private static Handler handler;
    private ObjectMapper mapper = new ObjectMapper();
    private String type="";
    private OkHttpClient client = CustomOkHttpClient.client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initViews();
        initHandler();
    }
    public void save(View view){
        if(incomeButton.isEnabled()){
            type = "Expenditure";
        }
        if(expenditureButton.isEnabled()){
            type = "Income";
        }
        if(expenditureButton.isEnabled() && incomeButton.isEnabled()){
            Toast.makeText(getApplicationContext(),"income 이나 expenditure 버튼을 눌러주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        Account.Builder builder = new Account.Builder();
        Account account = builder.setValue(Long.parseLong(value.getText().toString()))
                .setMemo(memo.getText().toString())
                .setYearMonth(LocalDate.parse(dateString).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .setCategory(category.getText().toString())
                .setAssetName(asset.getText().toString())
                .setTime(date.getText().toString()+"T"+time.getText().toString())
                .setAccountType(type)
                .build();
        StringBuffer json = new StringBuffer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Request request = HttpConnection.postAccount(account);
                    Response response = client.newCall(request).execute();
                    json.append(response.body().string());
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
        while(json.toString().isEmpty()){

        }
        finish();
    }
    public void cancel(View view){
        finish();
    }
    private void initViews(){
        TextView tv = findViewById(R.id.dateTv);
        ColorStateList textColor = tv.getTextColors();
        incomeButton = findViewById(R.id.IncomeButton);
        expenditureButton =findViewById(R.id.ExpenditureButton);
        conversionButton = findViewById(R.id.ConversionButton);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        asset = findViewById(R.id.asset);
        category = findViewById(R.id.category);
        value = findViewById(R.id.value);
        memo = findViewById(R.id.memo);
        Intent intent = getIntent();
        dateString = intent.getStringExtra("date");
        timeString = intent.getStringExtra("time");
        date.setText(dateString);
        time.setText(timeString);
        date.setTextColor(textColor);
        time.setTextColor(textColor);
        asset.setTextColor(textColor);
        category.setTextColor(textColor);
        value.setTextColor(textColor);
        memo.setTextColor(textColor);
        table = findViewById(R.id.table);
        incomeButton.setTextColor(category.getTextColors());
        expenditureButton.setTextColor(category.getTextColors());
        conversionButton.setTextColor(category.getTextColors());
        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "income";
                incomeButton.setEnabled(false);
                expenditureButton.setEnabled(true);
                conversionButton.setEnabled(true);
                incomeButton.setTextColor(getColor(R.color.blue));
                expenditureButton.setTextColor(category.getTextColors());
                conversionButton.setTextColor(category.getTextColors());
            }
        });
        expenditureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Expenditure";
                expenditureButton.setEnabled(false);
                incomeButton.setEnabled(true);
                conversionButton.setEnabled(true);
                expenditureButton.setTextColor(getColor(R.color.red));
                incomeButton.setTextColor(category.getTextColors());
                conversionButton.setTextColor(category.getTextColors());
            }
        });
        conversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conversionButton.setEnabled(false);
                incomeButton.setEnabled(true);
                expenditureButton.setEnabled(true);
                conversionButton.setTextColor(getColor(R.color.green));
                incomeButton.setTextColor(category.getTextColors());
                expenditureButton.setTextColor(category.getTextColors());
                finish();
                Intent conversionIntent = new Intent(getApplicationContext(),ConversionAccountActivity.class);
                conversionIntent.putExtra("date",dateString);
                conversionIntent.putExtra("time",timeString);
                startActivity(conversionIntent);
            }
        });

        asset.setOnClickListener(new View.OnClickListener() {
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
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = HttpConnection.getCategoryList(type);
                        try{
                            Response response = client.newCall(request).execute();
                            String json = response.body().string();
                            Log.i("json",json);
                            DtoWithHttpCode<List<Category>> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<List<Category>>>(){});
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
                                tv.setTextColor(category.getTextColors());
                                tv.setLayoutParams(params);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        asset.setText(tv.getText().toString());
                                    }
                                });
                                tr.addView(tv);
                            }
                        }
                        table.addView(tr);
                    }
                } else if (msg.what == 2) {
                    table.removeAllViews();
                    DtoWithHttpCode<List<Category>> dto = (DtoWithHttpCode<List<Category>>) msg.obj;
                    List<Category> categoryList = dto.getData();
                    int rowcount = categoryList.size() / 4 + 1;
                    for (int i = 0; i < rowcount; i++) {
                        TableRow tr = new TableRow(getApplicationContext());
                        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        tr.setLayoutParams(params);
                        for (int j = 0; j < 4; j++) {
                            if ((i * 4 + j) < categoryList.size()) {
                                if (!categoryList.get(i * 4 + j).getType().equalsIgnoreCase(type)) continue;
                                TextView tv = new TextView(getApplicationContext());
                                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                tv.setText(categoryList.get((i * 4 + j)).getName());
                                tv.setGravity(Gravity.CENTER);
                                tv.setBackground(getDrawable(R.drawable.border));
                                tv.setTextColor(category.getTextColors());
                                tv.setLayoutParams(params);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        category.setText(tv.getText().toString());
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
}