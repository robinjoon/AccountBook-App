package space.robinjoon.accountbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.nio.file.Files;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import space.robinjoon.accountbook.R;
import space.robinjoon.accountbook.dto.Asset;
import space.robinjoon.accountbook.dto.Category;
import space.robinjoon.accountbook.dto.DtoWithHttpCode;
import space.robinjoon.accountbook.network.CustomOkHttpClient;
import space.robinjoon.accountbook.network.HttpConnection;

public class AssetCategoryActivity extends AppCompatActivity {
    private EditText input1;
    private MaterialButton assetBtn, categoryBtn;
    private RadioButton income,expenditure;
    private LinearLayout input2Layout;
    private boolean isEditAsset = false;
    private ObjectMapper mapper = new ObjectMapper();
    private String type =null;
    private OkHttpClient client = CustomOkHttpClient.client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_category);
        input1 = findViewById(R.id.input1);
        income = findViewById(R.id.incomeRadioBtn);
        expenditure = findViewById(R.id.expenditureRadioBtn);
        assetBtn = findViewById(R.id.assetEdit);
        categoryBtn = findViewById(R.id.categoryEdit);
        input2Layout = findViewById(R.id.input2Layout);
        assetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditAsset = true;
                assetBtn.setTextColor(getColor(R.color.primary));
                categoryBtn.setTextColor(getColor(R.color.primaryTextColor));
                assetBtn.setEnabled(false);
                categoryBtn.setEnabled(true);
                input2Layout.setVisibility(View.INVISIBLE);
            }
        });
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditAsset = false;
                categoryBtn.setTextColor(getColor(R.color.primary));
                assetBtn.setTextColor(getColor(R.color.primaryTextColor));
                categoryBtn.setEnabled(false);
                assetBtn.setEnabled(true);
                input2Layout.setVisibility(View.VISIBLE);
            }
        });
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Income";
            }
        });
        expenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Expenditure";
            }
        });
    }

    public void save(View view){
        int httpCode;
        if(isEditAsset){
            httpCode = postAsset();
        }else{
            httpCode = postCategory();
        }
        Toast.makeText(getApplicationContext(),httpCode+"",Toast.LENGTH_SHORT).show();
        finish();
    }
    public void cancel(View view){
        finish();
    }

    private int postCategory(){
        String name = input1.getText().toString();
        if(type==null){
            Toast.makeText(getApplicationContext(),"수입, 지출을 고르세요",Toast.LENGTH_SHORT).show();
        }
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        int ret[] = new int[1];
        ret[0]=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = HttpConnection.postCategory(category);
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    Log.i("json",json);
                    DtoWithHttpCode<Category> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<Category>>(){});
                    ret[0]=dto.getHttpCode();
                }catch (Exception e){
                    e.printStackTrace();
                    ret[0]=-1;
                }
            }
        }).start();
        while(ret[0]==0){}
        return ret[0];
    }
    private int postAsset(){
        String name = input1.getText().toString();
        Asset asset = new Asset();
        asset.setAssetName(name);
        asset.setValue(0);
        int ret[] = new int[1];
        ret[0]=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = HttpConnection.postAsset(asset);
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    Log.i("json",json);
                    DtoWithHttpCode<Asset> dto = mapper.readValue(json,new TypeReference<DtoWithHttpCode<Asset>>(){});
                    ret[0]=dto.getHttpCode();
                }catch (Exception e){
                    e.printStackTrace();
                    ret[0]=-1;
                }
            }
        }).start();
        while(ret[0]==0){}
        return ret[0];
    }
}