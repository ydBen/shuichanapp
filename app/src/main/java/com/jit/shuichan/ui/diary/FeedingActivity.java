package com.jit.shuichan.ui.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;

import static com.jit.shuichan.http.NetUtils.getBuyTypeRequest;
import static com.jit.shuichan.http.NetUtils.getCurrentTime;
import static com.jit.shuichan.http.NetUtils.getPondInfoRequest;
import static com.jit.shuichan.http.NetUtils.getSearchRequest;
import static com.jit.shuichan.http.NetUtils.getTime;
import static com.jit.shuichan.http.NetUtils.loginRequest;
import static com.jit.shuichan.http.NetUtils.postImage;
import static com.jit.shuichan.http.NetUtils.throwSubmitURL;

/**
 * Created by yuanyuan on 2017/2/23.
 */

public class FeedingActivity extends Activity implements View.OnClickListener {
    private Spinner spArea;
    private ArrayAdapter<String> area_adapter;
    private String[] areaStrs;

    private Spinner spThrow;
    private ArrayAdapter<String> throw_adapter;
    private String[] throwStrs;

    private Spinner spThrowDetail;
    private ArrayAdapter<String> throw_adapterDetail;
    private String[] throwDetailStrs;

//    private Spinner spAmount;
//    private ArrayAdapter<String> amount_adapter;
//    private String[] amountStrs;

    private String jinOrcheStr;
    private String throwStr;
    private String throwDetailStr;
    private String pondIdStr;
    private String imagePath;
    private String imageType;

    private TextView productNameText;
//    private DropEditText drop;

    private EditText throwPrice;
    private EditText throwName;
    private TextView throwTime;
    private LinearLayout llAddName;

    private ArrayList<String> inputType;
    private ArrayList<String> inputTypeName;
    private ArrayList<String> pondId;
    private ArrayList<String> pondName;
    private ArrayList<String> productArray;
    private ArrayList<String> productDetailArray;
    private ArrayList<String> buyThingsName;

    protected static final int SHOW_SPINNER_PRODUCT = 100;
    protected static final int SHOW_SPINNER_POND = 101;
    protected static final int SUBMIT_SUCCESS = 102;
//    protected static final int SHOW_DROPDOWN_INFO = 103;


    private ImageView ivLog;
    private Bitmap urlImage;


    //开启 Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SPINNER_PRODUCT:
                    showSpinnerProduct();
                    break;
                case SHOW_SPINNER_POND:
                    showSpinnerPond();
                    break;
//                case SHOW_DROPDOWN_INFO:
//                    showDropDownInfo();
//                    break;
                case SUBMIT_SUCCESS:
                    Toast.makeText(getApplicationContext(), "投放数据已保存", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);
        initView();
        initData();
    }

    private void initData() {


//        amountStrs = new String[]{"斤", "车"};
//        amount_adapter = new ArrayAdapter<String>(this, R.layout.custom_spiner_text_item, amountStrs);
//        amount_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//        spAmount.setAdapter(amount_adapter);
//        spAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                jinOrcheStr = amountStrs[i];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        getDataFromServer();
    }

    /*
    * 从服务器获取数据
    * */
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
                    Log.e("buyTypeRequest",buyTypeRequest);
                    try {
                        JSONObject jo = new JSONObject(buyTypeRequest);
                        JSONArray dailyInputTypeArray = jo.getJSONArray("dailyInputType");

                        inputTypeName = new ArrayList<String>();
                        inputType = new ArrayList<String>();
                        for (int i = 0;i < dailyInputTypeArray.length();i++){
                            JSONObject jo1 = (JSONObject) dailyInputTypeArray.opt(i);
                            inputType.add(jo1.getString("inputtype"));
                            inputTypeName.add(jo1.getString("inputtypename"));
                        }

                        //for循环结束展示数据
                        message.what = SHOW_SPINNER_PRODUCT;

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
        //水域Spinner
        spArea = (Spinner) findViewById(R.id.sp_area);

        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //投放物类别Spinner
        spThrow = (Spinner) findViewById(R.id.sp_throw);
        spThrowDetail = (Spinner) findViewById(R.id.sp_throwdetail);

        //物品名称Spinner
//        drop = (DropEditText) findViewById(R.id.drop_edit);

        //斤or车
//        spAmount = (Spinner) findViewById(R.id.sp_amount);
        throwName = (EditText) findViewById(R.id.et_pname);
        throwPrice = (EditText) findViewById(R.id.et_throwprice);
        throwTime = (TextView) findViewById(R.id.et_throwtime);
        throwTime.setText(getCurrentTime());


        throwTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pickerView = new TimePickerView.Builder(FeedingActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        throwTime.setText(getTime(date));
                    }
                }).build();

                pickerView.setDate(Calendar.getInstance());
                pickerView.show();
            }
        });


        llAddName = (LinearLayout) findViewById(R.id.ll_etname);
        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        Button uploadImgBtn = (Button) findViewById(R.id.btn_uploading);
        Button ThrowLogBtn = (Button) findViewById(R.id.btn_throwlog);
        Button addBtn = (Button) findViewById(R.id.btn_addname);

        addBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        uploadImgBtn.setOnClickListener(this);
        ThrowLogBtn.setOnClickListener(this);



        ivLog = (ImageView) findViewById(R.id.iv_log);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
//                Toast.makeText(getApplicationContext(),"提交" + drop.mEditText.getText().toString(),Toast.LENGTH_SHORT).show();
                submitToServer();

                break;
            case R.id.btn_uploading:
                Toast.makeText(getApplicationContext(),"上传图片",Toast.LENGTH_SHORT).show();
                PhotoPicker.builder().setPhotoCount(1).setShowCamera(true).setPreviewEnabled(false).start(FeedingActivity.this,PhotoPicker.REQUEST_CODE);
                break;
            case R.id.btn_throwlog:
                TestHistory();
                break;
            case R.id.btn_addname:
                final EditText etDia = new EditText(this);
                if (throwName.getText().toString().equals("")){
                    etDia.setText(throwName.getText().toString());
                }else {
                    etDia.setText(throwName.getText().toString() + ";");
                }

                Editable etext = etDia.getText();
                Selection.setSelection(etext, etext.length());


                new AlertDialog.Builder(this).setTitle("物品名称")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(etDia)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (etDia.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "内容为空！", Toast.LENGTH_LONG).show();
                                }else {
                                    if (etDia.getText().toString().substring(etDia.getText().toString().length() - 1).equals(";")){
                                        throwName.setText(etDia.getText().toString().substring(0,etDia.getText().toString().length() - 1));
                                    }else {
                                        throwName.setText(etDia.getText().toString());
                                    }

                                }
                                Editable etext = throwName.getText();
                                Selection.setSelection(etext, etext.length());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    private void TestHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = getSearchRequest("2", "1", "1", "do2_1", "2017-03-30", "2017-03-30", "01:00:00", "08:00:00");
                Log.e("历史数据",s);
//                MultipartEntity multipartEntity = new MultipartEntity();
//                try {
//                    multipartEntity.addPart("buy_dailyinputtype",);
//                    multipartEntity.addPart("buy_sub_dailyinputtype",new StringBody(productDetailStr, Charset.forName("UTF-8")));
//                    multipartEntity.addPart("buy_name",new StringBody(productName.getText().toString(), Charset.forName("UTF-8")));
//                    multipartEntity.addPart("buy_count",new StringBody(productAmount.getText().toString(), Charset.forName("UTF-8")));
//                    multipartEntity.addPart("buy_unit",new StringBody(jinOrcheStr, Charset.forName("UTF-8")));
//                    multipartEntity.addPart("buy_value",new StringBody(productPrice.getText().toString(), Charset.forName("UTF-8")));
//                    if (imagePath == null || imagePath.trim().equals("")){
//                        multipartEntity.addPart("buy_image",new FileBody(new File("/sdcard/test/file.txt")));
//                    }else{
//                        multipartEntity.addPart("buy_image",new FileBody(new File(imagePath)));
//                    }
//                    multipartEntity.addPart("buy_activetime",new StringBody(productTime.getText().toString(), Charset.forName("UTF-8")));
//
//
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                String s = postImage(searchURL,multipartEntity);




//                String s = downloadImage("[7dce0988aff847f4bfc732aac4f9b44d.jpeg]");
//                Log.e("图片下载",s);


//                urlImage = getUrlImage("http://172.19.73.27:8080/IntelligentAgriculture/patrolManage/loadImages?names=[7dce0988aff847f4bfc732aac4f9b44d.jpeg]");
//
//                EzvizApplication.runOnUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(EzvizApplication.getContext(),"获取图片",Toast.LENGTH_LONG).show();
//                        ivLog.setImageBitmap(urlImage);
//                    }
//                });

            }
        }).start();
    }

    private void submitToServer() {
        if (throwName.getText().toString().trim().equals("") || throwPrice.getText().toString().trim().equals("") || throwTime.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"请用户填写完整信息",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Message message = Message.obtain();
                    MultipartEntity multipartEntity = new MultipartEntity();
                    try {
                        multipartEntity.addPart("throw_pondId",new StringBody(pondIdStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("throw_dailyinputtype",new StringBody(throwStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("throw_sub_dailyinputtype",new StringBody(throwDetailStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("throw_name_edit",new StringBody(throwName.getText().toString(), Charset.forName("UTF-8")));
//                        multipartEntity.addPart("throw_count",new StringBody(throwAmount.getText().toString(), Charset.forName("UTF-8")));
//                        multipartEntity.addPart("throw_unit",new StringBody(jinOrcheStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("throw_price_all",new StringBody(throwPrice.getText().toString(), Charset.forName("UTF-8")));
                        if (imagePath == null || imagePath.trim().equals("")){
                            multipartEntity.addPart("throw_image",new FileBody(new File("/sdcard/test/file.txt")));
                        }else{
                            multipartEntity.addPart("throw_image",new FileBody(new File(imagePath)));
                        }
                        multipartEntity.addPart("throw_activetime",new StringBody(throwTime.getText().toString(), Charset.forName("UTF-8")));



                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String s = postImage(throwSubmitURL,multipartEntity);
                    if (s.contains("养殖日志")){
                        message.what = SUBMIT_SUCCESS;
                        mHandler.sendMessage(message);
                    }
                    Log.e("提交","哈哈" + s);
                }
            }).start();
        }
    }


    private void showSpinnerPond() {
        area_adapter = new ArrayAdapter<String>(this, R.layout.custom_spiner_text_item,pondName);
        area_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spArea.setAdapter(area_adapter);
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pondIdStr = pondId.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showSpinnerProduct() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = Message.obtain();
        String pondInfoRequest = getPondInfoRequest();
        Log.e("pondInfo",pondInfoRequest);
        try {
            JSONObject jo1 = new JSONObject(pondInfoRequest);
            JSONArray pondinfoArray = jo1.getJSONArray("pondinfo");
            pondId = new ArrayList<String>();
            pondName = new ArrayList<String>();
            for (int i = 0; i<pondinfoArray.length();i++){
                JSONObject jo = (JSONObject) pondinfoArray.opt(i);
                pondId.add(jo.getString("id"));
                pondName.add(jo.getString("pondName"));
                Log.e("list",pondId.get(i) + pondName.get(i));
            }
            //for循环结束展示数据
            message.what = SHOW_SPINNER_POND;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("塘口解析异常",e.toString());
        }finally {
            mHandler.sendMessage(message);
        }


            }
        }).start();

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

        throw_adapter = new ArrayAdapter<String>(this,R.layout.custom_spiner_text_item,productArray);
        throw_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spThrow.setAdapter(throw_adapter);
        spThrow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spThrowDetail.setVisibility(View.GONE);
                if (productArray.get(i).equals("水质解调剂")){
                    spThrowDetail.setVisibility(View.VISIBLE);
                    throw_adapterDetail = new ArrayAdapter<String>(FeedingActivity.this,R.layout.custom_spiner_text_item,productDetailArray);
                    throw_adapterDetail.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                    spThrowDetail.setAdapter(throw_adapterDetail);
                    spThrowDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            throwStr = "-1";
                            throwDetailStr = String.valueOf(productArray.size()-2 + i +1);
//                            showBuyNameInfo(throwDetailStr);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    if (productArray.get(i).equals("其它")){
                        throwStr = "8";
                        throwDetailStr = "100";
//                        showBuyNameInfo(throwStr);
                    }else {
                        throwStr = inputType.get(i);
                        throwDetailStr = "100";
//                        showBuyNameInfo(throwStr);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

//    private void showBuyNameInfo(String sss) {
//        final String  buyThingsId = sss;
//        //获取 物品名称
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Message message = Message.obtain();
//                MultipartEntity multipartEntity = new MultipartEntity();
//                try {
//                    multipartEntity.addPart("throwType", new StringBody(buyThingsId, Charset.forName("UTF-8")));
//                    multipartEntity.addPart("time", new StringBody(getCurrentTime(), Charset.forName("UTF-8")));
//
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                String buyThingRequest = postImage(buyNameURL, multipartEntity);
//                Log.e("buyThingRequest",buyThingRequest);
//                try {
//                    JSONObject jo1 = new JSONObject(buyThingRequest);
//                    JSONArray buythingsArray = jo1.getJSONArray("buythings");
//                    if (buyThingsName != null){
//                        buyThingsName.clear();
//                        Log.e("clear","clear数组");
//                    }
//                    buyThingsName = new ArrayList<String>();
//                    for (int i = 0; i<buythingsArray.length();i++){
//                        JSONObject jo = (JSONObject) buythingsArray.opt(i);
//                        buyThingsName.add(jo.getString("Name"));
//                        Log.e("buythingslist",buyThingsName.get(i));
//                    }
//                    //for循环结束展示数据
//                    message.what = SHOW_DROPDOWN_INFO;
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e("投放物品解析异常",e.toString());
//                }finally {
//                    mHandler.sendMessage(message);
//                }
//
//
//
//            }
//        }).start();
//    }


//    private void showDropDownInfo() {
//        drop.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return buyThingsName.size();
//            }
//
//            @Override
//            public Object getItem(int i) {
//                return buyThingsName.get(i);
//            }
//
//            @Override
//            public long getItemId(int i) {
//                return i;
//            }
//
//            @Override
//            public View getView(int i, View view, ViewGroup viewGroup) {
//                productNameText = new TextView(FeedingActivity.this);
//                productNameText.setText(buyThingsName.get(i));
//                return productNameText;
//            }
//        });
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.e("photoPath",photos.get(0));
                imagePath = photos.get(0);
                imageType = imagePath.substring(imagePath.lastIndexOf(".")+1);
                imageType = "image/" + imageType;
                Log.e("图片类型",imageType);
            }
        }
    }


    //加载图片
    public Bitmap getUrlImage(String url){
        Bitmap img = null;
        try {
            URL picUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) picUrl.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                // 根据流数据创建 一个Bitmap位图对象
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Log.e("post", "访问成功：responseCode=" + responseCode);
                is.close();
                return bitmap;


            } else {
                Log.e("post", "访问失败：responseCode=" + responseCode);
            }


            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("异常","url异常" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("异常","io异常" + e.toString());
        }


        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
}
