package space.robinjoon.accountbook.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import space.robinjoon.accountbook.R;
import space.robinjoon.accountbook.dto.Account;
import space.robinjoon.accountbook.dto.AccountBook;
import space.robinjoon.accountbook.dto.Asset;
import space.robinjoon.accountbook.dto.ConversionAccount;
import space.robinjoon.accountbook.dto.DtoWithHttpCode;
import space.robinjoon.accountbook.network.CustomOkHttpClient;
import space.robinjoon.accountbook.network.HttpConnection;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab,assetCategoryFab;
    private TableLayout calendar;
    private TextView incomeTextView;
    private TextView expenditureTextView;
    private TextView balanceTextView;
    private ColorStateList mainTextColor;
    private ColorStateList mainBackGround;
    private LocalDate selectDate;
    private List<TextView> tvList;
    private TextView main;
    private static Handler handler;
    private ObjectMapper mapper = new ObjectMapper();
    private Button statisticsBtn;
    private AccountBook accountBook;
    private OkHttpClient client = CustomOkHttpClient.client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        assetCategoryFab = findViewById(R.id.assetCategoryFab);
        calendar = findViewById(R.id.calendar);
        incomeTextView = findViewById(R.id.incomeTextView);
        expenditureTextView = findViewById(R.id.expenditureTextView);
        balanceTextView = findViewById(R.id.balanceTextView);
        statisticsBtn = findViewById(R.id.statisticsBtn);
        main = findViewById(R.id.mon);
        tvList = new ArrayList<>();
        mainTextColor = main.getTextColors();
        mainBackGround = main.getBackgroundTintList();
        statisticsBtn.setTextColor(mainTextColor);
        selectDate = LocalDate.now();
        monthSelectorInit();
        initHandler();

    }
    public void statistics(View view){
        Intent intent = new Intent(getApplicationContext(),StatisticsActivity.class);
        try {
            intent.putExtra("accountbook",mapper.writeValueAsString(accountBook));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initCalendar();
        createCalendar(selectDate);
        for(int i=0;i<tvList.size();i++){
            if(Integer.parseInt(tvList.get(i).getText().toString())!=Integer.parseInt(selectDate.format(DateTimeFormatter.ofPattern("dd")))){
                tvList.get(i).setBackground(getDrawable(R.drawable.right_bottom_border));
            }else{
                tvList.get(i).setBackgroundColor(getColor(R.color.primary));
            }
        }
        getAccountsByDate(selectDate);
        postBook(selectDate); //To get the household account book, the household account book must exist, so a request to create the household account book is sent. 그 달의 가계부가 존재해야 가져올 수 있으므로, 가계부 생성요청을 보냄.
        getBook(selectDate);
    }
    private void initCalendar(){
        View view = calendar.getChildAt(0);
        calendar.removeAllViews();
        calendar.addView(view);
    }
    private void createCalendar(LocalDate date){
        LocalDate first = LocalDate.of(date.getYear(),date.getMonth(),1);
        int firstDayOfWeek = first.getDayOfWeek().getValue();
        if(firstDayOfWeek==7)firstDayOfWeek=0;
        else firstDayOfWeek++;
        Log.i("firstDayOfWeek",firstDayOfWeek+"");
        int dayCount = first.lengthOfMonth();
        int day =1;
        for(int i=0;i<6;i++){ // There is a maximum of 6 weeks per month. 한달은 최대 6개의 주로 이루어져있다.
            TableRow tr = new TableRow(getApplicationContext());
            for(int j=1;j<=7;j++){ // 1 week is 7 days. 일주일은 7일이다.
                TextView tv = new TextView(getApplicationContext());
                ViewGroup.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
                tv.setGravity(Gravity.CENTER);
                tv.setGravity(Gravity.TOP);
                tv.setText("a");
                tv.setTextColor(mainTextColor);
                tv.setBackground(getDrawable(R.drawable.right_bottom_border));
                tv.setLayoutParams(params);
                params = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,3);
                tr.setLayoutParams(params);
                if((i==0 && j >=firstDayOfWeek) || (i>0 && day<=dayCount)){
                    tv.setText(day+"");
                    day++;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView v = (TextView)view;
                            view.setBackgroundColor(getColor(R.color.primary));
                            selectDate = LocalDate.of(date.getYear(),date.getMonth(),Integer.parseInt(v.getText().toString()));
                            Log.i("selectDate",selectDate.toString());
                            for(int i=0;i<tvList.size();i++){
                                if(tvList.get(i)!=view){
                                    tvList.get(i).setBackground(getDrawable(R.drawable.right_bottom_border));
                                }
                            }
                            getAccountsByDate(selectDate);
                        }
                    });
                }else{
                    tv.setText("-1");
                    tv.setTextColor(getColor(R.color.primaryBackground));
                }
                tvList.add(tv);

                tr.addView(tv);
            }
            calendar.addView(tr);
        }
    }
    private void monthSelectorInit(){
        TextView leftButton = findViewById(R.id.leftButton);
        TextView yearMonth = findViewById(R.id.yearMonth);
        TextView rightButton = findViewById(R.id.rightButton);
        float h = main.getTextSize()/1.3f;
        leftButton.setTextSize(h);
        yearMonth.setTextSize(h);
        yearMonth.setText(selectDate.getMonthValue()+"월");
        rightButton.setTextSize(h);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout accounts = findViewById(R.id.accounts);
                accounts.removeAllViews();
                selectDate = selectDate.minusMonths(1);
                yearMonth.setText(selectDate.getMonthValue()+"월");
                initCalendar();
                createCalendar(selectDate);
                postBook(selectDate);
                getBook(selectDate);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout accounts = findViewById(R.id.accounts);
                accounts.removeAllViews();
                selectDate = selectDate.plusMonths(1);
                yearMonth.setText(selectDate.getMonthValue()+"월");
                initCalendar();
                createCalendar(selectDate);
                postBook(selectDate);
                getBook(selectDate);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
                intent.putExtra("date",selectDate.toString());
                intent.putExtra("time", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                startActivity(intent);
            }
        });
        assetCategoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AssetCategoryActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initHandler(){
        handler = new Handler(Looper.getMainLooper()){
            private Message msg;

            @Override
            public void handleMessage(Message msg){
                this.msg = msg;
                if (msg.what == 1) { // getBook
                    DtoWithHttpCode<AccountBook> dto = (DtoWithHttpCode<AccountBook>) msg.obj;
                    if (dto.getHttpCode() == 200) {
                        accountBook = dto.getData();
                        long income = accountBook.getTotalIncome();
                        long ex = accountBook.getTotlaExpenditure();
                        List<Asset> assetList = accountBook.getAssetList();
                        long balance = 0;
                        StringBuffer assets = new StringBuffer();
                        for(Asset asset : assetList){
                            balance+=asset.getValue();
                            assets.append(asset.getAssetName()+" : "+asset.getValue()+"원 \n");
                        }
                        DecimalFormat format = new DecimalFormat("###,###₩");
                        incomeTextView.setText(getString(R.string.income) + "\n" + format.format(income));
                        expenditureTextView.setText(getString(R.string.expenditure) + "\n" + format.format(ex));
                        balanceTextView.setText(getString(R.string.balance) + "\n" + format.format(balance));

                        balanceTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getApplicationContext(),assets.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        incomeTextView.setText(getString(R.string.income) + "\n" + 0);
                        expenditureTextView.setText(getString(R.string.expenditure) + "\n" + 0);
                        balanceTextView.setText(getString(R.string.balance) + "\n" + 0);
                    }
                } else if (msg.what == 2) {
                    LinearLayout accounts = findViewById(R.id.accounts);
                    accounts.removeAllViews();
                    List dtolist = (List) msg.obj;
                    DtoWithHttpCode<List<Account>> dto = (DtoWithHttpCode<List<Account>>) dtolist.get(0);
                    DtoWithHttpCode<List<ConversionAccount>> conversionDto = (DtoWithHttpCode<List<ConversionAccount>>)dtolist.get(1);
                    if(dto.getHttpCode()==200){
                        List<Account> accountList = dto.getData();
                        List<ConversionAccount> conversionAccountList = conversionDto.getData();
                        List<String> accountTextList = new ArrayList<>();
                        Map<Long,Account> accountMap = new HashMap<>();
                        Map<Long,ConversionAccount> conversionMap = new HashMap<>();
                        for(int i=0;i<accountList.size();i++) {
                            Account account = accountList.get(i);
                            accountTextList.add(account.toString() + "@account@" + account.getAid());
                            accountMap.put(account.getAid(),account);
                        }
                        for(int i=0;i<conversionAccountList.size();i++) {
                            ConversionAccount account = conversionAccountList.get(i);
                            accountTextList.add(account.toString() + "@conversion@" + account.getAid());
                            conversionMap.put(account.getAid(),account);
                        }
                        Collections.sort(accountTextList);
                        for(int i=0;i<accountTextList.size();i++){
                            String accountText = accountTextList.get(i);
                            Log.i("accountText",accountText);
                            String[] splitAccountText = accountText.split("@");
                            if(splitAccountText[1].contentEquals("account")){
                                Account account = accountMap.get(Long.parseLong(splitAccountText[2]));
                                TextView textView = new TextView(getApplicationContext());
                                textView.setText(splitAccountText[0]);
                                if(account.getAccountType().equals("Income"))textView.setTextColor(getColor(R.color.blue));
                                else textView.setTextColor(getColor(R.color.red));
                                textView.setBackground(getDrawable(R.drawable.bottom_border));
                                float h = main.getTextSize()/1.5f;
                                textView.setTextSize(h);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(),AccountViewActivity.class);
                                        try {
                                            intent.putExtra("account",mapper.writeValueAsString(account));
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                    }
                                });
                                textView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    Request request = HttpConnection.deleteAccount(account);
                                                    Response response = client.newCall(request).execute();
                                                    String json = response.body().string();
                                                    Log.i("delete_json",json);
                                                    DtoWithHttpCode<Account> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<AccountBook>>(){});
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        if(dto.getHttpCode()==200) {
                                            Log.i("delete_json",dto.getHttpCode()+"");
                                            Toast.makeText(getApplicationContext(), "삭제성공", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.i("delete_json",dto.getHttpCode()+"");
                                            Toast.makeText(getApplicationContext(), "삭제실패", Toast.LENGTH_SHORT).show();
                                        }
                                        textView.setVisibility(View.INVISIBLE);
                                        return true;
                                    }
                                });
                                accounts.addView(textView);
                            }else{
                                TextView textView = new TextView(getApplicationContext());
                                textView.setText(splitAccountText[0]);
                                textView.setTextColor(getColor(R.color.green));
                                textView.setBackground(getDrawable(R.drawable.bottom_border));
                                float h = main.getTextSize()/1.5f;
                                textView.setTextSize(h);
                                accounts.addView(textView);
                            }
                        }
                    }
                }
            }
        };
    }
    private void postBook(LocalDate localDate){
        String yearMonth = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Log.i("yearMonth",yearMonth);
        new Thread(new Runnable() {
            @Override
            public void run() {
                 
                Request request = HttpConnection.postBook(yearMonth);
                try{
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    Log.i("json",json);
                    DtoWithHttpCode<AccountBook> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<AccountBook>>(){});
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getBook(LocalDate localDate){
        String yearMonth = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Log.i("yearMonth",yearMonth);
        new Thread(new Runnable() {
            @Override
            public void run() {
                 
                Request request = HttpConnection.getBook(yearMonth);
                try{
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    Log.i("json",json);
                    DtoWithHttpCode<AccountBook> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<AccountBook>>(){});
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
    private void getAccountsByDate(LocalDate localDate){
        new  Thread(new Runnable() {
            @Override
            public void run() {
                 
                Request request = HttpConnection.getAccountsByDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                Request conversionAccountRequest = HttpConnection.getConversionAccountList(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                try {
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    Log.i("getAccountsByDatejson",json);
                    DtoWithHttpCode<List<Account>> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<List<Account>>>(){});
                    Response conversionAccountResponse = client.newCall(conversionAccountRequest).execute();
                    json = conversionAccountResponse.body().string();
                    DtoWithHttpCode<List<ConversionAccount>> conversionDto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<List<ConversionAccount>>>(){});
                    List<ConversionAccount> conversionAccountList = conversionDto.getData();
                    List<ConversionAccount> newConversionAccountList = new ArrayList<>();
                    for(int i=0;i<conversionAccountList.size();i++){
                        ConversionAccount account = conversionAccountList.get(i);
                        LocalDateTime dateTime = LocalDateTime.parse(account.getTime());
                        LocalDate date = dateTime.toLocalDate();
                        if(date.isEqual(localDate)){
                            newConversionAccountList.add(account);
                        }
                    }
                    conversionDto = new DtoWithHttpCode<List<ConversionAccount>>(conversionDto.getHttpCode(),newConversionAccountList,new String[0]);
                    List dtolist = new ArrayList();
                    dtolist.add(dto);
                    dtolist.add(conversionDto);
                    Message message = handler.obtainMessage();
                    message.what=2;
                    message.obj = dtolist;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}