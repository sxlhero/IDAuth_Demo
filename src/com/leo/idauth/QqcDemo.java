package com.leo.idauth;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.idauth.R;
import cn.com.aratek.qrc.CodeScanner;

public class QqcDemo extends Activity {
    private CodeScanner mScanner;
    private Button mBtnScan;
    private EditText mScanInfo;
    private TextView mDevSN;
    private TextView mDevVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrc);

        // 获得二维码扫描器控制实例
        mScanner = CodeScanner.getInstance();

        mDevSN = (TextView) findViewById(R.id.devSN);
        mDevVersion = (TextView) findViewById(R.id.devVersion);
        mScanInfo = (EditText) findViewById(R.id.et_scaninfo);
        mScanInfo.setEnabled(false);
        mBtnScan = (Button) findViewById(R.id.bt_scan);
        mBtnScan.setEnabled(false);
        mBtnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBtnScan.setEnabled(false);
                mScanInfo.setText("");
                byte[] data = mScanner.read();
                if (data != null && data.length > 0) {
                    // 进行数据处理，这里仅作演示，不做正确性检查
                    try {
                        mScanInfo.setText(new String(data, "gb2312").trim());
                        Toast.makeText(QqcDemo.this, "二维码扫描器读取数据成功！",
                                Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Toast.makeText(QqcDemo.this, "二维码扫描器读取数据失败！",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QqcDemo.this, "二维码扫描器读取数据失败！",
                            Toast.LENGTH_SHORT).show();
                }
                mBtnScan.setEnabled(true);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // 完成上电、初始化操作
        if (!mScanner.powerOn()) {
            Toast.makeText(this, "二维码扫描器上电失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mScanner.open()) {
            Toast.makeText(this, "二维码扫描器打开失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            // 初始化完成，扫描按钮可用
            Toast.makeText(this, "二维码扫描器初始化成功！", Toast.LENGTH_SHORT).show();
            mBtnScan.setEnabled(true);
            mDevSN.setText("设备序列号：" + mScanner.getDeviceSN());
            mDevVersion.setText("设备固件版本：" + mScanner.getDeviceVersion());
        }
    }

    protected void onPause() {
        // 完成清理、断电操作
        if (!mScanner.close()) {
            Toast.makeText(this, "二维码扫描器关闭失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mScanner.powerOff()) {
            Toast.makeText(this, "二维码扫描器断电失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "二维码扫描器关闭成功！", Toast.LENGTH_SHORT).show();
        }

        mBtnScan.setEnabled(false);
        super.onPause();
    }

}
