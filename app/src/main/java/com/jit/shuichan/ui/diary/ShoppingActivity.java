package com.jit.shuichan.ui.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.jit.shuichan.R;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;

import static com.jit.shuichan.http.NetUtils.buySubmitURL;
import static com.jit.shuichan.http.NetUtils.getBuyTypeRequest;
import static com.jit.shuichan.http.NetUtils.getCurrentTime;
import static com.jit.shuichan.http.NetUtils.getTime;
import static com.jit.shuichan.http.NetUtils.loginRequest;
import static com.jit.shuichan.http.NetUtils.postImage;

/**
 * Created by yuanyuan on 2017/2/23.
 */

public class ShoppingActivity extends Activity implements View.OnClickListener{
    private Spinner spProduct;
    private ArrayAdapter<String> product_adapter;

    private Spinner spProductDetail;
    private ArrayAdapter<String> product_detail_adapter;

    private String productStr;
    private String productDetailStr;
    private String jinOrcheStr;
    private String imagePath;
    private String imageType;

    private ArrayList<String> inputType;
    private ArrayList<String> inputTypeName;
    private ArrayList<String> productArray;
    private ArrayList<String> productDetailArray;



    private Spinner spAmount;
    private ArrayAdapter<String> amount_adapter;
    private String[] amountStrs;

    private EditText productName;
    private EditText productAmount;
    private EditText productPrice;
    private TextView productTime;


    /**
     * 获取采购数据
     */
    protected static final int SHOW_SPINNER = 100;
    protected static final int SUBMIT_SUCCESS = 101;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_SPINNER:
                    showSpinner();
                    break;
                case SUBMIT_SUCCESS:
                    Toast.makeText(getApplicationContext(),"采购数据已保存",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void showSpinner() {

        productArray = new ArrayList<String>();
        productDetailArray = new ArrayList<String>();

        //使用数组作为数据源
        int jay = 0;
        for (int i = 0;i < inputTypeName.size(); i++){
            if (inputTypeName.get(i).equals("肥料")){
                jay = i;
                break;
            }
        }


        for (int i = 0;i < inputTypeName.size() - 1;i++){
            if (i < jay){
                productArray.add(inputTypeName.get(i));
            }else if(i >= jay){
                productDetailArray.add(inputTypeName.get(i));
            }
        }

        productArray.add("水质解调剂");
        productArray.add("其它");

        Log.e("return",String.valueOf(jay));

        product_adapter = new ArrayAdapter<String>(this,R.layout.custom_spiner_text_item,productArray);
        product_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spProduct.setAdapter(product_adapter);
        spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spProductDetail.setVisibility(View.GONE);
                if (productArray.get(i).equals("水质解调剂")){
                    spProductDetail.setVisibility(View.VISIBLE);
                    product_detail_adapter = new ArrayAdapter<String>(ShoppingActivity.this,R.layout.custom_spiner_text_item,productDetailArray);
                    product_detail_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                    spProductDetail.setAdapter(product_detail_adapter);
                    spProductDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            productStr = "-1";
                            productDetailStr = String.valueOf(productArray.size()-2 + i +1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    if (productArray.get(i).equals("其它")){
                        productStr = "8";
                        productDetailStr = "1";
                    }else {
                        productStr = inputType.get(i);
                        productDetailStr = "1";
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        initView();
        initData();
    }

    private void initData() {
        getDataFromServer();


//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }





        amountStrs = new String[]{"斤","车"};
        amount_adapter = new ArrayAdapter<String>(this,R.layout.custom_spiner_text_item,amountStrs);
        amount_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spAmount.setAdapter(amount_adapter);
        spAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                jinOrcheStr = amountStrs[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getDataFromServer() {

        Log.e("server","server");
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = Message.obtain();

                String s = loginRequest();

                if (s.equals("0")){//登录成功 获取采购物信息
                    Log.e("服务器请求","服务器请求成功");
                    String buyTypeRequest = getBuyTypeRequest();
                    try {
                        JSONObject jo = new JSONObject(buyTypeRequest);
                        JSONArray dailyInputTypeArray = jo.getJSONArray("dailyInputType");

                        inputTypeName = new ArrayList<String>();
                        inputType = new ArrayList<String>();
                        for (int i = 0;i < dailyInputTypeArray.length();i++){
                            Log.e("list","登录成功");
                            JSONObject jo1 = (JSONObject) dailyInputTypeArray.opt(i);
                            inputType.add(jo1.getString("inputtype"));
                            inputTypeName.add(jo1.getString("inputtypename"));
                            Log.e("list",inputTypeName.get(i) + inputType.get(i));
//                            Log.e("list",inputTypeName.get(i));
                        }

                        //for循环结束展示数据
                        message.what = SHOW_SPINNER;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("解析异常",e.toString());
                    }finally {
                        mHandler.sendMessage(message);
                    }

                }



            }
        }).start();





    }

    private void initView() {
        spProduct = (Spinner) findViewById(R.id.sp_product);
        spProductDetail = (Spinner) findViewById(R.id.sp_productdetail);

        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spAmount = (Spinner) findViewById(R.id.sp_amount);


        productName = (EditText) findViewById(R.id.et_productname);
        productAmount = (EditText) findViewById(R.id.et_productamount);
        productPrice = (EditText) findViewById(R.id.et_productprice);
        productTime = (TextView) findViewById(R.id.et_currenttime);
        productTime.setText(getCurrentTime());


        productTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pickerView = new TimePickerView.Builder(ShoppingActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        productTime.setText(getTime(date));
                    }
                }).build();

                pickerView.setDate(Calendar.getInstance());
                pickerView.show();
            }
        });




        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        Button uploadImgBtn = (Button) findViewById(R.id.btn_uploadimg);
        Button buyLogBtn = (Button) findViewById(R.id.btn_buylog);

        submitBtn.setOnClickListener(this);
        uploadImgBtn.setOnClickListener(this);
        buyLogBtn.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
//                Toast.makeText(getApplicationContext(),"提交",Toast.LENGTH_SHORT).show();
                submitToServer();
                break;
            case R.id.btn_uploadimg:
                Toast.makeText(getApplicationContext(),"上传图片",Toast.LENGTH_SHORT).show();
                PhotoPicker.builder().setPhotoCount(1).setShowCamera(true).setPreviewEnabled(false).start(ShoppingActivity.this,PhotoPicker.REQUEST_CODE);
                break;
            case R.id.btn_buylog:
                openLogPage();
                break;
        }
    }

    private void openLogPage() {
        Intent intent = new Intent(ShoppingActivity.this, LogActivity.class);
        startActivity(intent);
    }


    private void submitToServer() {

        if (productName.getText().toString().trim().equals("") || productAmount.getText().toString().trim().equals("") || productPrice.getText().toString().trim().equals("") || productTime.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"请用户填写完整信息",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Message message = Message.obtain();

//                ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
//                values.add(new BasicNameValuePair("buy_dailyinputtype",productStr));
//                values.add(new BasicNameValuePair("buy_sub_dailyinputtype",""));
//                values.add(new BasicNameValuePair("buy_name",productName.getText().toString()));
//                values.add(new BasicNameValuePair("buy_count",productAmount.getText().toString()));
//                values.add(new BasicNameValuePair("buy_unit",jinOrcheStr));
//                values.add(new BasicNameValuePair("buy_value",productPrice.getText().toString()));
//                values.add(new BasicNameValuePair("buy_image",""));
//                values.add(new BasicNameValuePair("buy_activetime",productTime.getText().toString()));


                    MultipartEntity multipartEntity = new MultipartEntity();
                    try {
                        multipartEntity.addPart("buy_dailyinputtype",new StringBody(productStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("buy_sub_dailyinputtype",new StringBody(productDetailStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("buy_name",new StringBody(productName.getText().toString(), Charset.forName("UTF-8")));
                        multipartEntity.addPart("buy_count",new StringBody(productAmount.getText().toString(), Charset.forName("UTF-8")));
                        multipartEntity.addPart("buy_unit",new StringBody(jinOrcheStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("buy_value",new StringBody(productPrice.getText().toString(), Charset.forName("UTF-8")));
                        if (imagePath == null || imagePath.trim().equals("")){
                            multipartEntity.addPart("buy_image",new FileBody(new File("/sdcard/test/file.txt")));
                        }else{
                            multipartEntity.addPart("buy_image",new FileBody(new File(imagePath)));
                        }
                        multipartEntity.addPart("buy_activetime",new StringBody(productTime.getText().toString(), Charset.forName("UTF-8")));



                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String s = postImage(buySubmitURL,multipartEntity);
                    if (s.contains("养殖日志")){
                        message.what = SUBMIT_SUCCESS;
                        mHandler.sendMessage(message);
                    }
                    Log.e("提交","哈哈" + s);
                }
            }).start();
        }



//        HttpUtils httpUtils = new HttpUtils(60 * 1000);
//        RequestParams requestParams = new RequestParams();
//        requestParams.setHeader("Cookie","JSESSIONID=" + JSESSIONID);
//        requestParams.addBodyParameter("buy_dailyinputtype",productStr);
//        requestParams.addBodyParameter("buy_sub_dailyinputtype","");
//        requestParams.addBodyParameter("buy_name",productName.getText().toString());
//        requestParams.addBodyParameter("buy_count",productAmount.getText().toString());
//        requestParams.addBodyParameter("buy_unit",jinOrcheStr);
//        requestParams.addBodyParameter("buy_value",productPrice.getText().toString());
//        requestParams.addBodyParameter("buy_image",new File(imagePath),imageType);
//        requestParams.addBodyParameter("buy_activetime",productTime.getText().toString());
//
//        httpUtils.send(HttpRequest.HttpMethod.POST, buySubmitURL, requestParams, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                Log.e("成功",responseInfo.result);
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                Log.e("失败",s);
//            }
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                Log.e("photoPath",photos.get(0));
                imagePath = photos.get(0);
                imageType = imagePath.substring(imagePath.lastIndexOf(".")+1);
                imageType = "image/" + imageType;
                Log.e("图片类型",imageType);
            }
        }
    }


}