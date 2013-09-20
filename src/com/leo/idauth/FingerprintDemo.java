package com.leo.idauth;

import com.leo.idauth.provider.DatabaseUtils;
import com.leo.idauth.provider.DatabaseUtils.Person;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.idauth.R;
import cn.com.aratek.fp.FingerprintScanner;

public class FingerprintDemo extends Activity {
    private Button mBtnReadFinger;
    private Button mBtnCompareFinger;
    private Button mBtnShowFinger;
    private Button mBtnContinuousTest;
    private Button mBtnDatabaseTest;
    private TextView mCaptureTime;
    private TextView mExtractTime;
    private TextView mGeneralizeTime;
    private TextView mVerifyTime;
    private TextView mContinuousTestInfo;
    private ImageView mFingerprintImage;
    private FingerprintScanner mScanner;
    private byte[] mFpTemp;// 指纹模板数据，用户可使用其作为之后比对的参数之一。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        mScanner = FingerprintScanner.getInstance();

        mCaptureTime = (TextView) findViewById(R.id.captureTime);
        mExtractTime = (TextView) findViewById(R.id.extractTime);
        mGeneralizeTime = (TextView) findViewById(R.id.generalizeTime);
        mVerifyTime = (TextView) findViewById(R.id.verifyTime);
        mContinuousTestInfo = (TextView) findViewById(R.id.continuousTestInfo);
        mFingerprintImage = (ImageView) findViewById(R.id.fingerimage);
        
        // database test
        mBtnDatabaseTest = (Button) findViewById(R.id.bt_databaseTest);
        mBtnDatabaseTest.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Person person = Person.getTestInstance();
                DatabaseUtils.addOrUpdatePerson(FingerprintDemo.this, person);
                // Gets the current system time in milliseconds
                Long now = Long.valueOf(System.currentTimeMillis());
                Log.e("a1797z", "now: " + now);
                Log.e("a1797z", "Integer: " + Integer.MAX_VALUE);
                
            }
        });

        // 录入指纹
        mBtnReadFinger = (Button) findViewById(R.id.bt_readfinger);
        mBtnReadFinger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] fpRaw = mScanner.captureRAW();// 采集图像
                if (fpRaw == null) {
                    Toast.makeText(FingerprintDemo.this,
                            "采集指纹图像失败！错误码：" + mScanner.getLastError(),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                byte[] fpBmp = mScanner.raw2Bmp(fpRaw);
                if (fpBmp == null) {
                    Toast.makeText(FingerprintDemo.this,
                            "Raw转Bmp失败！错误码：" + mScanner.getLastError(),
                            Toast.LENGTH_LONG).show();
                } else {
                    mFingerprintImage.setImageBitmap(BitmapFactory
                            .decodeByteArray(fpBmp, 0, fpBmp.length));
                }
                byte[] fpChar = mScanner.extractFromRaw(fpRaw);// 从Raw数据提取特征
                if (fpChar != null) {
                    // 生成模板，并暂存在对象中，用户可将其存入数据库等持久型存储器中，作为一个指纹的身份信息
                    mFpTemp = mScanner.generalize(fpChar, fpChar, fpChar);
                    if (mFpTemp != null && mFpTemp.length != 0) {
                        Toast.makeText(FingerprintDemo.this, "录入成功！",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FingerprintDemo.this,
                                "录入失败，生成模板出错！错误码：" + mScanner.getLastError(),
                                Toast.LENGTH_LONG).show();
                    }
                    updateSingerTestText(mScanner.mCaptureTime,
                            mScanner.mExtractTime, mScanner.mGeneralizeTime, -1);
                } else {
                    Toast.makeText(FingerprintDemo.this,
                            "录入失败，提取特征出错！错误码：" + mScanner.getLastError(),
                            Toast.LENGTH_LONG).show();
                    updateSingerTestText(mScanner.mCaptureTime,
                            mScanner.mExtractTime, -1, -1);
                }
            }
        });

        // 比对指纹
        mBtnCompareFinger = (Button) findViewById(R.id.bt_comparefinger);
        mBtnCompareFinger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mFpTemp == null || mFpTemp.length == 0) {
                    Toast.makeText(FingerprintDemo.this, "模板为空！",
                            Toast.LENGTH_LONG).show();
                    updateSingerTestText(-1, -1, -1, -1);
                    return;
                }
                byte[] fpChar = mScanner.extract();// 录入指纹并提取特征
                if (fpChar != null) {
                    int score = mScanner.verify(fpChar, mFpTemp);
                    if (score > 0) {
                        Toast.makeText(FingerprintDemo.this,
                                "比对成功！比对分值：" + score, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FingerprintDemo.this,
                                "比对失败！错误码：" + mScanner.getLastError(),
                                Toast.LENGTH_LONG).show();
                    }
                    updateSingerTestText(mScanner.mCaptureTime,
                            mScanner.mExtractTime, -1, mScanner.mVerifyTime);
                } else {
                    Toast.makeText(FingerprintDemo.this,
                            "录入失败，提取特征出错！错误码：" + mScanner.getLastError(),
                            Toast.LENGTH_LONG).show();
                    updateSingerTestText(mScanner.mCaptureTime,
                            mScanner.mExtractTime, -1, -1);
                }
            }
        });

        // 显示指纹
        mBtnShowFinger = (Button) findViewById(R.id.bt_showfinger);
        mBtnShowFinger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 获取指纹图像
                Bitmap img = mScanner.capture();
                updateSingerTestText(mScanner.mCaptureTime, -1, -1, -1);
                if (img != null) {
                    Toast.makeText(FingerprintDemo.this, "指纹图像获取成功！",
                            Toast.LENGTH_LONG).show();
                    mFingerprintImage.setImageBitmap(img);
                } else {
                    Toast.makeText(FingerprintDemo.this,
                            "指纹图像获取失败！错误码：" + mScanner.getLastError(),
                            Toast.LENGTH_LONG).show();
                    BitmapDrawable draw = (BitmapDrawable) getResources()
                            .getDrawable(R.drawable.nofinger);
                    mFingerprintImage.setImageBitmap(draw.getBitmap());
                }
            }
        });

        // 连续测试
        mBtnContinuousTest = (Button) findViewById(R.id.bt_continuousTest);
        mBtnContinuousTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int TEST_COUNT = 30;
                int count = 0;
                long captureTotal = 0, extractTotal = 0, generalizeTotal = 0, verifyTotal = 0;
                long captureMin = Long.MAX_VALUE, extractMin = Long.MAX_VALUE, generalizeMin = Long.MAX_VALUE, verifyMin = Long.MAX_VALUE;
                long captureMax = 0, extractMax = 0, generalizeMax = 0, verifyMax = 0;
                List<byte[]> charList = new ArrayList<byte[]>();
                byte[] temp = null;
                // 取图+生成特征
                while (count < TEST_COUNT) {
                    byte[] fpRaw = mScanner.captureRAW();
                    if (fpRaw == null || fpRaw.length <= 0) {
                        continue;
                    }
                    byte[] fpChar = mScanner.extractFromRaw(fpRaw);
                    if (fpChar == null || fpChar.length <= 0) {
                        continue;
                    }
                    charList.add(fpChar);
                    captureTotal += mScanner.mCaptureTime;
                    captureMin = (captureMin > mScanner.mCaptureTime) ? mScanner.mCaptureTime
                            : captureMin;
                    captureMax = (captureMax > mScanner.mCaptureTime) ? captureMax
                            : mScanner.mCaptureTime;
                    extractTotal += mScanner.mExtractTime;
                    extractMin = (extractMin > mScanner.mExtractTime) ? mScanner.mExtractTime
                            : extractMin;
                    extractMax = (extractMax > mScanner.mExtractTime) ? extractMax
                            : mScanner.mExtractTime;
                    count++;
                }
                // 随机抽取特征合成模板
                count = 0;
                while (count < TEST_COUNT) {
                    int random1 = (int) (Math.random() * TEST_COUNT);
                    int random2 = (int) (Math.random() * TEST_COUNT);
                    int random3 = (int) (Math.random() * TEST_COUNT);

                    temp = mScanner.generalize(charList.get(random1),
                            charList.get(random2), charList.get(random3));
                    if (temp == null || temp.length <= 0) {
                        continue;
                    }
                    generalizeTotal += mScanner.mGeneralizeTime;
                    generalizeMin = (generalizeMin > mScanner.mGeneralizeTime) ? mScanner.mGeneralizeTime
                            : generalizeMin;
                    generalizeMax = (generalizeMax > mScanner.mGeneralizeTime) ? generalizeMax
                            : mScanner.mGeneralizeTime;
                    count++;
                }
                // 随机抽取特征与最后一个模板进行比对
                count = 0;
                while (count < TEST_COUNT) {
                    int random = (int) (Math.random() * TEST_COUNT);

                    mScanner.verify(charList.get(random), temp, 3);

                    verifyTotal += mScanner.mVerifyTime;
                    verifyMin = (verifyMin > mScanner.mVerifyTime) ? mScanner.mVerifyTime
                            : verifyMin;
                    verifyMax = (verifyMax > mScanner.mVerifyTime) ? verifyMax
                            : mScanner.mVerifyTime;
                    count++;
                }

                mContinuousTestInfo.setText("平均取图时间："
                        + captureTotal / TEST_COUNT + "ms，最长：" + captureMax
                        + "ms， 最短：" + captureMin + "ms。\n" + "平均生成特征时间："
                        + extractTotal / TEST_COUNT + "ms，最长：" + extractMax
                        + "ms， 最短：" + extractMin + "ms。\n" + "平均生成模板时间："
                        + generalizeTotal / TEST_COUNT + "ms，最长："
                        + generalizeMax + "ms， 最短：" + generalizeMin + "ms。\n"
                        + "平均一对一比对时间：" + verifyTotal / TEST_COUNT + "ms，最长："
                        + verifyMax + "ms， 最短：" + verifyMin + "ms。");
            }
        });

        mBtnReadFinger.setEnabled(false);
        mBtnCompareFinger.setEnabled(false);
        mBtnShowFinger.setEnabled(false);
        mBtnContinuousTest.setEnabled(false);

        updateSingerTestText(-1, -1, -1, -1);
    }

    protected void onResume() {
        super.onResume();

        // 完成上电、初始化操作
        if (!mScanner.powerOn()) {
            Toast.makeText(this, "指纹仪上电失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mScanner.open()) {
            Toast.makeText(this, "指纹仪打开失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "指纹仪打开成功！", Toast.LENGTH_SHORT).show();
            mBtnReadFinger.setEnabled(true);
            mBtnCompareFinger.setEnabled(true);
            mBtnShowFinger.setEnabled(true);
            mBtnContinuousTest.setEnabled(true);
        }
    }

    protected void onPause() {
        // 完成清理、断电操作
        if (!mScanner.close()) {
            Toast.makeText(this, "指纹仪关闭失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mScanner.powerOff()) {
            Toast.makeText(this, "指纹仪断电失败！" + mScanner.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "指纹仪关闭成功！", Toast.LENGTH_SHORT).show();
            mBtnReadFinger.setEnabled(false);
            mBtnCompareFinger.setEnabled(false);
            mBtnShowFinger.setEnabled(false);
            mBtnContinuousTest.setEnabled(false);
        }

        super.onPause();
    }

    private void updateSingerTestText(long captureTime, long extractTime,
            long generalizeTime, long verifyTime) {
        if (captureTime < 0) {
            mCaptureTime.setText("取图：未进行");
        } else if (captureTime < 1) {
            mCaptureTime.setText("取图：< 1ms");
        } else {
            mCaptureTime.setText("取图：" + captureTime + "ms");
        }

        if (extractTime < 0) {
            mExtractTime.setText("生成特征：未进行");
        } else if (extractTime < 1) {
            mExtractTime.setText("生成特征：< 1ms");
        } else {
            mExtractTime.setText("生成特征：" + extractTime + "ms");
        }

        if (generalizeTime < 0) {
            mGeneralizeTime.setText("生成模板：未进行");
        } else if (generalizeTime < 1) {
            mGeneralizeTime.setText("生成模板：< 1ms");
        } else {
            mGeneralizeTime.setText("生成模板：" + generalizeTime + "ms");
        }

        if (verifyTime < 0) {
            mVerifyTime.setText("一对一比对：未进行");
        } else if (verifyTime < 1) {
            mVerifyTime.setText("一对一比对：< 1ms");
        } else {
            mVerifyTime.setText("一对一比对：" + verifyTime + "ms");
        }
    }

}
