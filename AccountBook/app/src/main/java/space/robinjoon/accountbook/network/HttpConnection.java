package space.robinjoon.accountbook.network;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import space.robinjoon.accountbook.dto.Account;
import space.robinjoon.accountbook.dto.Asset;
import space.robinjoon.accountbook.dto.Category;
import space.robinjoon.accountbook.dto.ConversionAccount;

public class HttpConnection {
    public static final String url = "url";

    public static Request getBook(String yearMonth){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/book/"+yearMonth)
                .build();
        return request;
    }


    public static Request postBook(String yearMonth){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/book/"+yearMonth)
                .post(RequestBody.create(MediaType.parse("application/json"),""))
                .build();
        return request;
    }

    public static Request getAccountsByDate(String date){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts/"+date)
                .build();
        return request;
    }
    public static Request postAccount(Account account) throws JsonProcessingException {
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts")
                .post(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(account)))
                .build();
        Log.i("request",new ObjectMapper().writeValueAsString(account));
        return request;
    }
    public static Request putAccount(Account account) throws JsonProcessingException{
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts/"+account.getAid())
                .put(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(account)))
                .build();
        Log.i("request",new ObjectMapper().writeValueAsString(account));
        return request;
    }
    public static Request deleteAccount(Account account) throws JsonProcessingException{
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts/"+account.getAid())
                .delete(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(account)))
                .build();
        Log.i("request",new ObjectMapper().writeValueAsString(account));
        return request;
    }
    public static Request getAssetList(){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/assets")
                .build();
        return request;
    }
    public static Request getCategoryList(String type){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/categories/"+type)
                .build();
        return request;
    }
    public static Request postAsset(Asset asset) throws JsonProcessingException{
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/assets/"+asset.getAssetName())
                .post(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(asset)))
                .build();
        return request;
    }
    public static Request postCategory(Category category) throws JsonProcessingException{
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/categories/"+category.getType()+"/"+category.getName())
                .post(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(category)))
                .build();
        return request;
    }
    public static Request postConversionAccount(ConversionAccount account) throws JsonProcessingException{
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts/conversion")
                .post(RequestBody.create(MediaType.parse("application/json"),new ObjectMapper().writeValueAsString(account)))
                .build();
        return request;
    }
    public static Request getConversionAccountList(String yearMonth){
        Request request = new Request.Builder()
                .addHeader("AuthToken","3c1348640d5c42abd0dac16be37ce263362527c278a0521d8da7031eab4256bb")
                .url(url+"/accounts/conversion/"+yearMonth)
                .build();
        return request;
    }
}
