package com.jit.shuichan.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddCameraGuideDialog extends Dialog implements android.view.View.OnClickListener {
    /** 变量/常量说明 */
    private Button mGoBackBtn = null;

    /** 变量/常量说明 */
    private Button mToScanBtn = null;

    private TextView mHowTextView = null;

    private QuitNow mQuitNow = null;

    /**
     * @param context
     * @param theme
     * @param type
     */
    public AddCameraGuideDialog(Context context, int theme) {
        super(context, theme);
        //mj setContentView(R.layout.add_camera_guide_dialog);
        findViews();
    }

    /**
     * 这里对方法做描述
     * 
     * @see
     * @since V1.0
     */
    private void findViews() {
        /*mj mGoBackBtn = (Button) findViewById(R.id.to_back_btn);
        mGoBackBtn.setOnClickListener(this);

        mToScanBtn = (Button) findViewById(R.id.to_scan_btn);
        mToScanBtn.setOnClickListener(this);

        mHowTextView = (TextView) findViewById(R.id.howToConnect);
        mHowTextView.setOnClickListener(this);

        if (Config.IS_INTL) {
            findViewById(R.id.howToConnect).setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onClick(View view) {/* mj
        switch (view.getId()) {
            case R.id.to_scan_btn:
                mQuitNow.scanNow();
                if (getWindow() != null) {
                    dismiss();
                }

                break;
            case R.id.to_back_btn:
                mQuitNow.quitNow();
                if (getWindow() != null) {
                    dismiss();
                }
                break;
            case R.id.howToConnect:
                mQuitNow.howToConnect();
                if (getWindow() != null) {
                    dismiss();
                }
                break;
            default:
                break;
        }
    */}

    public void setQuitListener(QuitNow quitNow) {
        this.mQuitNow = quitNow;
    }

    public interface QuitNow {
        public void quitNow();

        public void scanNow();

        public void howToConnect();
    }
}
