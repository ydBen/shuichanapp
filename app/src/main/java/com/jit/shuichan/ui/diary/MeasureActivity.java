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

import me.iwf.photopicker.PhotoPicker;

import static com.jit.shuichan.http.NetUtils.getCurrentTime;
import static com.jit.shuichan.http.NetUtils.getMeasureTypeRequest;
import static com.jit.shuichan.http.NetUtils.getPondInfoRequest;
import static com.jit.shuichan.http.NetUtils.loginRequest;
import static com.jit.shuichan.http.NetUtils.measureSubmitURL;
import static com.jit.shuichan.http.NetUtils.postImage;

/**生产措施页面
 * Created by japser on 2017/3/17.
 */

public class MeasureActivity extends Activity implements View.OnClickListener {

    private Button btn_back,btn_measure_uploading,btn_measure_throwlog,btn_measure_submit;
    private Spinner sp_area,sp_measure_type,sp_measure_change;
    private EditText sp_measure_value;
    private TextView et_throwtime;


    private ArrayList<String> inputTypeName;
    private ArrayList<String> inputType;

    private ArrayList<String> pondId;
    private ArrayList<String> pondName;

    private ArrayAdapter<String> area_adapter;
    private ArrayAdapter<String> measure_change_adapter;
    private ArrayAdapter<String> action_type_adapter;

    private ArrayList<String> action_type;

    private String imagePath;
    private String imageType;

    private String pondIdStr;
    private String measure_change;
    private String[] measureChange;

    private String measureStr;
    private String measureStateStr;

    protected static final int SHOW_SPINNER = 100;
    protected static final int SHOW_SPINNER_POND = 101;
    protected static final int SUBMIT_SUCCESS = 102;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_SPINNER_POND:
                    showSpinnerPond();
                    break;
                case SHOW_SPINNER:
                    showSpinner();
                    break;
                case SUBMIT_SUCCESS:
                    Toast.makeText(getApplicationContext(),"数据已保存",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void showSpinner() {
        //加载塘口数据
        area_adapter = new ArrayAdapter<String>(this, R.layout.custom_spiner_text_item,pondName);
        area_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_area.setAdapter(area_adapter);
        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pondIdStr = pondId.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //加载措施类型的数据
        action_type = new ArrayList<String>();

        for (int i=0;i<inputTypeName.size();i++)
        {
            action_type.add(inputTypeName.get(i));
        }

        action_type_adapter = new ArrayAdapter<String>(this,R.layout.custom_spiner_text_item,action_type);
        action_type_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        sp_measure_type.setAdapter(action_type_adapter);
        sp_measure_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (action_type.get(position).equals("水草")||action_type.get(position).equals("其他")){
                    sp_measure_change.setVisibility(View.GONE);
                    measure_change = "-1";
                }else {
                    sp_measure_change.setVisibility(View.VISIBLE);

                }
                measureStr = inputType.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showSpinnerPond() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String pondInfoRequest = getPondInfoRequest();

                try {
                    JSONObject jo1 = new JSONObject(pondInfoRequest);
                    JSONArray pondInfoArray = jo1.getJSONArray("pondinfo");

                    pondId = new ArrayList<String>();
                    pondName = new ArrayList<String>();

                    for (int i=0;i<pondInfoArray.length();i++)
                    {
                        JSONObject jo = (JSONObject)pondInfoArray.opt(i);
                        pondId.add(jo.getString("id"));
                        pondName.add(jo.getString("pondName"));
                        Log.e("措施",pondId.get(i)+"   "+pondName.get(i));
                    }
                    message.what=SHOW_SPINNER;
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        initUI();
        initData();
    }

    private void initData() {
        getDataFromServer();

        //加载措施内容的如何调整
        addMeasureChange();
    }

    private void addMeasureChange() {
        measureChange = new String[]{"低","高"};
        measure_change_adapter = new ArrayAdapter<String>(this,R.layout.custom_spiner_text_item,measureChange);
        measure_change_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        sp_measure_change.setAdapter(measure_change_adapter);
        sp_measure_change.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measure_change = measureChange[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String s = loginRequest();

                if(s.equals("0")){
                    String measureTypeRequest = getMeasureTypeRequest();
                    try {
                        JSONObject jo = new JSONObject(measureTypeRequest);
                        JSONArray dailyInputTypeArray = jo.getJSONArray("measureType");

                        inputType = new ArrayList<String>();
                        inputTypeName = new ArrayList<String>();

                        for (int i=0;i<dailyInputTypeArray.length();i++)
                        {
                            JSONObject jo1 = (JSONObject)dailyInputTypeArray.opt(i);
                            inputType.add(jo1.getString("measuretype"));
                            inputTypeName.add(jo1.getString("name"));

                            Log.e("措施",inputType.get(i)+"  "+inputTypeName.get(i));
                        }

                        message.what = SHOW_SPINNER_POND;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        mHandler.sendMessage(message);
                    }
                }
            }
        }).start();
    }


    private void initUI() {

        //返回按钮初始化，并添加点击事件
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化3个spinner
        sp_area = (Spinner)findViewById(R.id.sp_area);
        sp_measure_type = (Spinner)findViewById(R.id.sp_measure_type);
        sp_measure_change =(Spinner)findViewById(R.id.sp_measure_change);

        //初始化措施名称
        sp_measure_value = (EditText)findViewById(R.id.sp_measure_value);

        //初始化时间,并获取当前时间
        et_throwtime = (TextView)findViewById(R.id.et_throwtime);
        et_throwtime.setText(getCurrentTime());

        //初始化提交按钮，投放日志按钮，选择文件按钮（选择图片）
        btn_measure_submit = (Button)findViewById(R.id.btn_measure_submit);
        btn_measure_throwlog = (Button)findViewById(R.id.btn_measure_throwlog);
        btn_measure_uploading = (Button)findViewById(R.id.btn_measure_uploading);

        //为3个按钮添加点击事件
        btn_measure_submit.setOnClickListener(this);
        btn_measure_throwlog.setOnClickListener(this);
        btn_measure_uploading.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_measure_submit:
                submitToServer();
                break;

            case R.id.btn_measure_throwlog:
                Toast.makeText(getApplicationContext(),"点击了投放日志按钮",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_measure_uploading:
                Toast.makeText(getApplicationContext(),"点击了选择文件按钮",Toast.LENGTH_SHORT).show();
                PhotoPicker.builder().setPhotoCount(1).setShowCamera(true).setPreviewEnabled(false)
                        .start(MeasureActivity.this,PhotoPicker.REQUEST_CODE);
                break;
        }
    }

    private void submitToServer() {
        if(sp_measure_value.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"请用户输入措施名称",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = Message.obtain();

                    MultipartEntity multipartEntity = new MultipartEntity();
                    try {
                        multipartEntity.addPart("measure_pondId",new StringBody(pondIdStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("measure_normaltype",new StringBody(measureStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("measureStatus",new StringBody(measure_change, Charset.forName("UTF-8")));
                        multipartEntity.addPart("measureName",new StringBody(sp_measure_value.getText().toString(), Charset.forName("UTF-8")));
                        if (imagePath == null || imagePath.trim().equals("")){
                            multipartEntity.addPart("measure_image",new FileBody(new File("/sdcard/test/file.txt")));
                        }else{
                            multipartEntity.addPart("measure_image",new FileBody(new File(imagePath)));
                        }
                        multipartEntity.addPart("measure_activetime",new StringBody(et_throwtime.getText().toString(), Charset.forName("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.e("错误",pondIdStr+"    "+measureStr+"    "+measure_change+"    "+sp_measure_value.getText().toString()+"    ");
                    String s = postImage(measureSubmitURL,multipartEntity);
                    if (s.contains("养殖日志")){
                        message.what = SUBMIT_SUCCESS;
                        mHandler.sendMessage(message);
                    }
                    Log.e("提交", s);

                }
            }).start();
        }
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
