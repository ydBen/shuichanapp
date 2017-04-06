package com.jit.shuichan.ui.diary;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static com.jit.shuichan.http.NetUtils.getCurrentTime;
import static com.jit.shuichan.http.NetUtils.getPatrolDataTypeRequest;
import static com.jit.shuichan.http.NetUtils.getPatrolTypeRequset;
import static com.jit.shuichan.http.NetUtils.getPondInfoRequest;
import static com.jit.shuichan.http.NetUtils.getTime;
import static com.jit.shuichan.http.NetUtils.loginRequest;
import static com.jit.shuichan.http.NetUtils.patrolSubmitURL;
import static com.jit.shuichan.http.NetUtils.postImage;

/**
 * 巡视检查页面
 * Created by japser on 2017/3/17.
 */

public class PatrolActivity extends Activity implements View.OnClickListener {

    private Spinner spArea;
    private Button backbutton;

    private Spinner sp_object, sp_type, sp_state1, sp_state2;

    private Spinner sp_data_type;
    private EditText sp_data_value;
    private TextView sp_normal_value;

    private Button btn_uploading;
    private TextView et_throwtime;
    private Button btn_throwlog, btn_submit;


    private RadioButton yes, no;
    private RadioGroup group;
    private LinearLayout linearLayout;

    private ArrayList<String> inputType;
    private ArrayList<String> inputTypeName;

    private ArrayList<String> inputDataType;
    private ArrayList<String> inputDataTypeName;
    private ArrayList<String> inputDataTypeRange;
    private ArrayAdapter<String> dataTypeAdapter;
    private String dataTypeStr;
    private String dataTypeRange;


    private ArrayList<String> pondId;
    private ArrayList<String> pondName;
    private ArrayAdapter<String> area_adapter;

    private String pondIdStr;
    private String patrolObjectStr;

    private String[] patrolObject;
    private ArrayAdapter<String> patrolObjectAdapter;

    private ArrayList<String> patrolTypeData1;
    private ArrayList<String> patrolTypeID1;

    private ArrayList<String> patrolTypeData2;
    private ArrayList<String> patrolTypeID2;
    private ArrayAdapter<String> patrolTypeAdapter;
    private String patrolTypeStr;

    private String[] patrolState1Data1;
    private String[] patrolState1Data2;
    private String[] patrolState1Data3;
    private String[] patrolState1Data4;
    private String[] patrolState1Data5;
    private String[] patrolState1Data6;
    private String[] patrolState1Data7;
    private String[] patrolState1Data8;

    private String[] patrolState2;
    private ArrayAdapter<String> patrolState1Adapter;
    private ArrayAdapter<String> patrolState2Adapter;

    private String patrolState1Str;
    private String patrolState2Str;

    private String ischecked = "yes";


    private String imagePath;
    private String imageType;

    protected static final int SHOW_SPINNER = 100;
    protected static final int SHOW_SPINNER_POND = 101;
    protected static final int SUBMIT_SUCCESS = 102;

    protected static final int SHOW_DATA_TYPE = 103;
    protected static final int SHOW_DATA_TYPE_SUCCESS = 104;
    protected static final int SHOW_PATROL_OBJECT = 105;


    protected static final int POND_SPINNER = 106;
    protected static final int OBJECT_SPINNER = 107;

    protected static final int TYPE_SPINNER1 = 108;
    protected static final int TYPE_SPINNER2 = 109;


    protected static final int STATE1_SPINNER1 = 110;
    protected static final int STATE1_SPINNER2 = 111;
    protected static final int STATE1_SPINNER3 = 112;
    protected static final int STATE1_SPINNER4 = 113;
    protected static final int STATE1_SPINNER5 = 114;
    protected static final int STATE1_SPINNER6 = 115;
    protected static final int STATE1_SPINNER7 = 116;
    protected static final int STATE1_SPINNER8 = 117;

    protected static final int STATE2_SPINNER = 120;
    protected static final int DATATYPE_SPINNER = 121;

    private Context context;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SPINNER_POND:
                    showSpinnerPond();
                    break;

                case SHOW_SPINNER:
                    showSpinner();
                    break;

                case SHOW_DATA_TYPE:
                    //加载录入数据的数据类型
                    getDataType();
                    break;

                case SHOW_DATA_TYPE_SUCCESS:
                    showDataTypeSpinner();
                    break;

                case SHOW_PATROL_OBJECT:
                    addPatrolObject();
                    break;

                case SUBMIT_SUCCESS:

                    Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //封装方法


    /*
    * ArrayAdapter<String> myAdapter  适配器
     * Spinner mySpinner  下拉框
     * ArrayList<String> myId 从服务器获取的ID
     * String[] myName 从服务器获取的字符串
    * */
    private void addServerDataToSpinner(ArrayAdapter<String> myAdapter, Spinner mySpinner, ArrayList<String> myId, ArrayList<String> myName, final int SpinnerId){

        final ArrayList<String> myID1;
        myID1 = myId;

        myAdapter = new ArrayAdapter<String>(this, R.layout.custom_spiner_text_item,myName);
        myAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (SpinnerId){
                    case POND_SPINNER:
                        pondIdStr = myID1.get(position);
                        Log.e("saveServerStr",pondIdStr);
                        break;

                    case DATATYPE_SPINNER:
                        dataTypeStr = inputDataType.get(position);
                        dataTypeRange = inputDataTypeRange.get(position);
                        sp_normal_value.setText(dataTypeRange);
                        break;

                    case TYPE_SPINNER1:
                        patrolTypeStr = patrolTypeID1.get(position);
                        if (patrolTypeStr.equals("1")) {
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data1,STATE1_SPINNER1);
                        }else if (patrolTypeStr.equals("2") || patrolTypeStr.equals("3")) {
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data2,STATE1_SPINNER2);
                        }else {
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data3,STATE1_SPINNER3);
                        }
                        break;

                    case TYPE_SPINNER2:
                        patrolTypeStr = patrolTypeID2.get(position);
                        if (patrolTypeStr.equals("6")) {
                            sp_state2.setVisibility(View.GONE);
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data3,STATE1_SPINNER3);
                        }else if (patrolTypeStr.equals("7")) {
                            sp_state2.setVisibility(View.GONE);
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data4,STATE1_SPINNER4);
                        }else if (patrolTypeStr.equals("8")) {
                            sp_state2.setVisibility(View.GONE);
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data5,STATE1_SPINNER5);
                        }else if (patrolTypeStr.equals("9")) {
                            sp_state2.setVisibility(View.GONE);
                            if (patrolObjectStr.equals("鱼")) {
                                addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data8,STATE1_SPINNER6);
                            }else {
                                addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data6,STATE1_SPINNER7);
                            }

                            sp_state2.setVisibility(View.VISIBLE);
                            addLocalDataToSpinner(patrolState2Adapter,sp_state2,patrolState2,STATE2_SPINNER);

                        }else if (patrolTypeStr.equals("10")) {
                            sp_state2.setVisibility(View.GONE);
                            addLocalDataToSpinner(patrolState1Adapter,sp_state1,patrolState1Data7,STATE1_SPINNER8);
                        }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    /*
    * ArrayAdapter<String> myAdapter  适配器
     * Spinner mySpinner  下拉框
     * String[] myName 从本地获取的字符串
    * */
    private void addLocalDataToSpinner(ArrayAdapter<String> myAdapter, Spinner mySpinner, String[] myName ,final int SpinnerId){

        myAdapter = new ArrayAdapter<String>(context,R.layout.custom_spiner_text_item,myName);
        myAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (SpinnerId)
                {
                    case  OBJECT_SPINNER:
                        sp_state2.setVisibility(View.GONE);
                        patrolObjectStr = patrolObject[position];
                        if (patrolObjectStr.equals("水质")) {
                            addServerDataToSpinner(patrolTypeAdapter,sp_type,patrolTypeID1,patrolTypeData1,TYPE_SPINNER1);

                        }else {
                            addServerDataToSpinner(patrolTypeAdapter,sp_type,patrolTypeID1,patrolTypeData2,TYPE_SPINNER2);

                        }
                        break;

                    case STATE1_SPINNER1:
                        patrolState1Str = patrolState1Data1[position];
                        break;

                    case STATE1_SPINNER2:
                        patrolState1Str = patrolState1Data2[position];
                        break;

                    case STATE1_SPINNER3:
                        patrolState1Str = patrolState1Data3[position];
                        break;

                    case STATE1_SPINNER4:
                        patrolState1Str = patrolState1Data4[position];
                        break;

                    case STATE1_SPINNER5:
                        patrolState1Str = patrolState1Data5[position];
                        break;

                    case STATE1_SPINNER6:
                        patrolState1Str = patrolState1Data8[position];
                        break;

                    case STATE1_SPINNER7:
                        patrolState1Str = patrolState1Data6[position];
                        break;

                    case STATE1_SPINNER8:
                        patrolState1Str = patrolState1Data7[position];
                        break;

                    case STATE2_SPINNER:
                        patrolState2Str = patrolState2[position];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showDataTypeSpinner() {

        Message message = Message.obtain();

        addServerDataToSpinner(dataTypeAdapter,sp_data_type,inputDataType,inputDataTypeName,DATATYPE_SPINNER);

        message.what = SHOW_PATROL_OBJECT;
        mHandler.sendMessage(message);
    }

    private void showSpinner() {

        Message message = Message.obtain();

        addServerDataToSpinner(area_adapter,spArea,pondId,pondName,POND_SPINNER);

        message.what = SHOW_DATA_TYPE;
        mHandler.sendMessage(message);
    }

    private void showSpinnerPond() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String pondInfoRequest = getPondInfoRequest();
                try {
                    JSONObject jo1 = new JSONObject(pondInfoRequest);
                    JSONArray pondinfoArray = jo1.getJSONArray("pondinfo");
                    pondId = new ArrayList<String>();
                    pondName = new ArrayList<String>();
                    for (int i = 0; i < pondinfoArray.length(); i++) {
                        JSONObject jo = (JSONObject) pondinfoArray.opt(i);
                        pondId.add(jo.getString("id"));
                        pondName.add(jo.getString("pondName"));
                        Log.e("巡视", pondId.get(i) + pondName.get(i));
                    }
                    //for循环结束展示数据
                    message.what = SHOW_SPINNER;

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(message);
                }


            }
        }).start();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol);

        context = PatrolActivity.this;

        initUI();
        initData();
    }

    private void initData() {
        /*
        * 单选按钮事件
        */
        setRadioData();

        //从服务器获取数据
        getDataFromServer();

        //加载巡视内容的对象
        patrolObject = new String[]{"水质", "螃蟹", "虾", "鱼"};

        //加载巡视内容的第一个状态数据
        patrolState1Data1 = new String[]{"发绿", "发黑", "正常", "发黑"};
        patrolState1Data2 = new String[]{"过低", "偏低", "正常", "偏高", "过高"};
        patrolState1Data3 = new String[]{"太少", "偏少", "正常", "偏多", "太多"};
        patrolState1Data4 = new String[]{"很弱", "偏弱", "正常", "偏强", "过强"};
        patrolState1Data5 = new String[]{"常浮上水面", "常浮到池边", "正常", "常在深水区", "常吐泡沫", "持续颤抖", "极少死亡", "少量死亡", "大量死亡"};
        patrolState1Data6 = new String[]{"蟹钳", "附肢", "头壳", "腮", "腹部", "触须"};
        patrolState1Data7 = new String[]{"提前", "正常", "推迟"};
        patrolState1Data8 = new String[]{"鳞", "嘴", "腮", "腹部", "尾部"};

        patrolState2 = new String[]{"腐烂", "斑点", "正常", "发黑", "发白", "发绿", "发红", "水肿", "少量脱落", "大量脱落"};

    }

    private void getDataType() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String dataTypeRequest = getPatrolDataTypeRequest();

                try {
                    JSONObject jo = new JSONObject(dataTypeRequest);
                    JSONArray dataTypeArray = jo.getJSONArray("manualData");

                    inputDataType = new ArrayList<String>();
                    inputDataTypeName = new ArrayList<String>();
                    inputDataTypeRange = new ArrayList<String>();

                    for (int i = 0; i < dataTypeArray.length(); i++) {
                        JSONObject jo1 = (JSONObject) dataTypeArray.opt(i);
                        inputDataType.add(jo1.getString("datatype"));
                        inputDataTypeName.add(jo1.getString("name"));
                        inputDataTypeRange.add(jo1.getString("range"));
                        Log.e("信息", inputDataType.get(i) + "   " + inputDataTypeName.get(i) + "    " + inputDataTypeRange.get(i));

                    }
                    message.what = SHOW_DATA_TYPE_SUCCESS;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void addPatrolObject() {

        //存放巡视内容类别的数据
        patrolTypeData1 = new ArrayList<String>();
        patrolTypeData2 = new ArrayList<String>();
        patrolTypeID1 = new ArrayList<String>();
        patrolTypeID2 = new ArrayList<String>();

        int jay = 0;
        for (int i = 0; i < inputTypeName.size(); i++) {
            if (inputTypeName.get(i).equals("吃料")) {
                jay = i;
                break;
            }
        }
        for (int i = 0; i < inputTypeName.size() && i < inputType.size(); i++) {
            if (i < jay) {
                patrolTypeData1.add(inputTypeName.get(i));
                patrolTypeID1.add(inputType.get(i));
            } else {
                patrolTypeData2.add(inputTypeName.get(i));
                patrolTypeID2.add(inputType.get(i));
            }
        }

        addLocalDataToSpinner(patrolObjectAdapter,sp_object,patrolObject,OBJECT_SPINNER);

    }


    /*
    * 从服务器获取数据
    * */
    private void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String s = loginRequest();
                //登录成功，获取巡视检查信息
                if (s.equals("0")) {
                    String patrolTypeRequest = getPatrolTypeRequset();

                    inputTypeName = new ArrayList<String>();
                    inputType = new ArrayList<String>();

                    try {
                        JSONObject jo = new JSONObject(patrolTypeRequest);
                        JSONArray dailyInputTypeArray = jo.getJSONArray("patrolContentType");

                        for (int i = 0; i < dailyInputTypeArray.length(); i++) {
                            JSONObject jo1 = (JSONObject) dailyInputTypeArray.opt(i);
                            inputType.add(jo1.getString("patroltype"));
                            inputTypeName.add(jo1.getString("name"));
                            Log.e("巡视", inputType.get(i) + "    " + inputTypeName.get(i));
                        }
                        message.what = SHOW_SPINNER_POND;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        mHandler.sendMessage(message);
                    }
                }
            }
        }).start();
    }


    /*点击“是”，显示要录入数据的数据类型，数值，正常值；
     * 点击“否”，隐藏要录入数据的数据类型，数值，正常值
     * */
    private void setRadioData() {
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (yes.getId() == checkedId) {
                    linearLayout.setVisibility(View.VISIBLE);
                    ischecked = "yes";
                } else {
                    linearLayout.setVisibility(View.GONE);
                    ischecked = "no";
                }
            }
        });
    }

    private void initUI() {

        //塘口的spinner和返回按钮
        spArea = (Spinner) findViewById(R.id.sp_area);
        backbutton = (Button) findViewById(R.id.btn_back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //巡视内容的4个spinner
        sp_object = (Spinner) findViewById(R.id.sp_object);
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_state1 = (Spinner) findViewById(R.id.sp_state1);
        sp_state2 = (Spinner) findViewById(R.id.sp_state2);

        //初始化单选按钮“是”“否”
        group = (RadioGroup) findViewById(R.id.sp_radiogroup);
        yes = (RadioButton) findViewById(R.id.sp_radioyes);
        no = (RadioButton) findViewById(R.id.sp_radiono);
        linearLayout = (LinearLayout) findViewById(R.id.sp_radio_isyes);

        //当单选按钮为“是”时，录入数据
        sp_data_type = (Spinner) findViewById(R.id.sp_data_type);
        sp_data_value = (EditText) findViewById(R.id.sp_data_value);
        sp_normal_value = (TextView) findViewById(R.id.sp_normal_value);

        //选择文件按钮（上传图片）
        btn_uploading = (Button) findViewById(R.id.btn_patrol_uploading);
        //显示当前时间
        et_throwtime = (TextView) findViewById(R.id.et_throwtime);
        et_throwtime.setText(getCurrentTime());

        et_throwtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pickerView = new TimePickerView.Builder(PatrolActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        et_throwtime.setText(getTime(date));
                    }
                }).build();

                pickerView.setDate(Calendar.getInstance());
                pickerView.show();
            }
        });

        //投放日志和提交按钮
        btn_throwlog = (Button) findViewById(R.id.btn_patrol_throwlog);
        btn_submit = (Button) findViewById(R.id.btn_patrol_submit);

        btn_uploading.setOnClickListener(this);
        btn_throwlog.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //提交按钮
            case R.id.btn_patrol_submit:
                submitToServer();
                //Toast.makeText(getApplicationContext(),"提交" + drop.mEditText.getText().toString(),Toast.LENGTH_SHORT).show();
                break;

            //选择图片按钮
            case R.id.btn_patrol_uploading:
                Toast.makeText(getApplicationContext(), "上传图片", Toast.LENGTH_SHORT).show();
                PhotoPicker.builder().setPhotoCount(1).setShowCamera(true).setPreviewEnabled(false)
                        .start(PatrolActivity.this, PhotoPicker.REQUEST_CODE);
                break;

            //上传日志按钮
            case R.id.btn_patrol_throwlog:
                Toast.makeText(getApplicationContext(), "投放日志", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /*
    * 向服务器提交内容
    * */
    private void submitToServer() {

        if(ischecked.equals("yes")) {
            if (sp_data_value.getText().toString().trim().equals("")) {
                Toast.makeText(PatrolActivity.this, "请输入数值", Toast.LENGTH_SHORT).show();
            } else {
                if (sp_data_value.getText().toString().length() > 7) {
                    Toast.makeText(PatrolActivity.this, "请输入合理的数值", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = Message.obtain();
                            MultipartEntity multipartEntity = new MultipartEntity();
                            try {

                                multipartEntity.addPart("pondId", new StringBody(pondIdStr, Charset.forName("UTF-8")));

                                multipartEntity.addPart("patrolTarget", new StringBody(patrolObjectStr, Charset.forName("UTF-8")));
                                multipartEntity.addPart("patrolContentType1", new StringBody(patrolTypeStr, Charset.forName("UTF-8")));
                                multipartEntity.addPart("patrolContentType2", new StringBody(patrolTypeStr, Charset.forName("UTF-8")));

                                multipartEntity.addPart("patrolContentStatus", new StringBody(patrolState1Str, Charset.forName("UTF-8")));

                                if (patrolState2Str != null) {
                                    multipartEntity.addPart("patrolTargetPart", new StringBody(patrolState2Str, Charset.forName("UTF-8")));
                                } else {
                                    multipartEntity.addPart("patrolTargetPart", new StringBody("", Charset.forName("UTF-8")));
                                }


                                //录入数据
                                multipartEntity.addPart("islog", new StringBody(ischecked, Charset.forName("UTF-8")));

                                multipartEntity.addPart("ranges", new StringBody(sp_data_value.getText().toString(), Charset.forName("UTF-8")));
                                multipartEntity.addPart("catalog", new StringBody(dataTypeStr, Charset.forName("UTF-8")));

                                if (imagePath == null || imagePath.trim().equals("")) {
                                    multipartEntity.addPart("files", new FileBody(new File("/sdcard/test/file.txt")));
                                } else {
                                    multipartEntity.addPart("files", new FileBody(new File(imagePath)));
                                }
                                multipartEntity.addPart("activeTime", new StringBody(et_throwtime.getText().toString(), Charset.forName("UTF-8")));

                                Log.e("提交",
                                        "塘口  " + pondIdStr +
                                                "   巡视内容  " + patrolObjectStr + "   巡视类别  " + patrolTypeStr +
                                                "   巡视状态1  " + patrolState1Str + "   巡视状态2  " + patrolState2Str +
                                                "   按钮状态  " + ischecked + "   数据类型  " + dataTypeStr + "   数值  " + sp_data_value.getText().toString());

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Log.e("提交", "错误");
                            }

                            String s = postImage(patrolSubmitURL, multipartEntity);
                            if (s.contains("养殖日志")) {
                                message.what = SUBMIT_SUCCESS;
                                mHandler.sendMessage(message);
                            }
                            Log.e("提交", "sdafha" + s);
                        }
                    }).start();
                }
            }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    MultipartEntity multipartEntity = new MultipartEntity();
                    try {
                        multipartEntity.addPart("pondId", new StringBody(pondIdStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("patrolTarget", new StringBody(patrolObjectStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("patrolContentType1", new StringBody(patrolTypeStr, Charset.forName("UTF-8")));
                        multipartEntity.addPart("patrolContentType2", new StringBody(patrolTypeStr, Charset.forName("UTF-8")));

                        multipartEntity.addPart("patrolContentStatus", new StringBody(patrolState1Str, Charset.forName("UTF-8")));

                        if (patrolState2Str != null) {
                            multipartEntity.addPart("patrolTargetPart", new StringBody(patrolState2Str, Charset.forName("UTF-8")));
                        } else {
                            multipartEntity.addPart("patrolTargetPart", new StringBody("", Charset.forName("UTF-8")));
                        }


                        //录入数据
                        multipartEntity.addPart("islog", new StringBody(ischecked, Charset.forName("UTF-8")));


                        multipartEntity.addPart("ranges", new StringBody("", Charset.forName("UTF-8")));
                        multipartEntity.addPart("datalog", new StringBody("", Charset.forName("UTF-8")));

                        if (imagePath == null || imagePath.trim().equals("")) {
                            multipartEntity.addPart("files", new FileBody(new File("/sdcard/test/file.txt")));
                        } else {
                            multipartEntity.addPart("files", new FileBody(new File(imagePath)));
                        }
                        multipartEntity.addPart("activeTime", new StringBody(et_throwtime.getText().toString(), Charset.forName("UTF-8")));

                        Log.e("提交",
                                "塘口  " + pondIdStr +
                                        "   巡视内容  " + patrolObjectStr + "   巡视类别  " + patrolTypeStr +
                                        "   巡视状态1  " + patrolState1Str + "   巡视状态2  " + patrolState2Str +
                                        "   按钮状态  " + ischecked + "   数据类型  " + dataTypeStr + "   数值  " + sp_data_value.getText().toString());

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e("提交", "错误");
                    }

                    String s = postImage(patrolSubmitURL, multipartEntity);
                    if (s.contains("养殖日志")) {
                        message.what = SUBMIT_SUCCESS;
                        mHandler.sendMessage(message);
                    }
                    Log.e("提交", "sdafha" + s);
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

                Log.e("photoPath", photos.get(0));
                imagePath = photos.get(0);
                imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1);
                imageType = "image/" + imageType;
                Log.e("图片类型", imageType);
            }
        }
    }
}
