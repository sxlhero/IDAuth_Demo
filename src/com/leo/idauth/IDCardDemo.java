package com.leo.idauth;

import com.leo.idauth.provider.DatabaseUtils;
import com.leo.idauth.provider.DatabaseUtils.Person;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.idauth.R;
import cn.com.aratek.dev.AraError;
import cn.com.aratek.idcard.CardReader;
import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCard.Nationality;
import cn.com.aratek.idcard.IDCard.Sex;

public class IDCardDemo extends Activity {
    private TextView tvIdInfo1;
    private TextView tvIdInfo2;
    private Button mBtnReadIdCard;
    private Button mBtnReadDatabase;
    private CardReader mReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取身份证识读器控制实例
        mReader = CardReader.getInstance();

        setContentView(R.layout.activity_idcard);

        tvIdInfo1 = (TextView) this.findViewById(R.id.tv_idcardinfo1);
        tvIdInfo2 = (TextView) this.findViewById(R.id.tv_idcardinfo2);
        
        mBtnReadDatabase = (Button) findViewById(R.id.bt_readdatabase);
        mBtnReadDatabase.setEnabled(false);
        mBtnReadDatabase.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                IDCard card = null;
                Person person = DatabaseUtils.getFirstPerson(IDCardDemo.this);
                showPeopleInfo(person);
                mBtnReadDatabase.setEnabled(!DatabaseUtils.isEmptyPerson(IDCardDemo.this));
            }


        });
        
        mBtnReadIdCard = (Button) findViewById(R.id.bt_readidcard);
        mBtnReadIdCard.setEnabled(false);
        mBtnReadIdCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvIdInfo1.setText(null);
                tvIdInfo2.setText(null);
                ImageView image1 = (ImageView) findViewById(R.id.iv_idcardimage);
                image1.setImageBitmap(null);
                mBtnReadIdCard.setEnabled(false);
                IDCard card = null;
                // 读身份证，大约需要1s
                card = mReader.read();
                if (card != null) {
                    Toast.makeText(IDCardDemo.this, "读卡成功！", Toast.LENGTH_SHORT)
                            .show();
                    showPeopleInfo(card);
                    
                    //save IDCard
                    Person person = Person.valueOf(card);
                    DatabaseUtils.addOrUpdatePerson(IDCardDemo.this, person);
                    
                } else if (mReader.getLastError() == AraError.CARD_NO_CARD) {
                    Toast.makeText(IDCardDemo.this, "请重新放卡或者确认卡片是否存在！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IDCardDemo.this,
                            "读卡失败！错误码：" + mReader.getLastError(),
                            Toast.LENGTH_SHORT).show();
                }
                mBtnReadIdCard.setEnabled(true);
                mBtnReadDatabase.setEnabled(!DatabaseUtils.isEmptyPerson(IDCardDemo.this));
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // 完成上电、初始化操作
        if (!mReader.powerOn()) {// 上电大概需要2s时间，此处可使用提示或线程
            Toast.makeText(this, "身份证识读器上电失败！" + mReader.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mReader.open()) {
            Toast.makeText(this, "身份证识读器打开失败！" + mReader.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            // 初始化完成，读卡按钮可用
            mBtnReadIdCard.setEnabled(true);
            Toast.makeText(this, "身份证识读器初始化成功！", Toast.LENGTH_SHORT).show();
        }
        
        this.mBtnReadDatabase.setEnabled(!DatabaseUtils.isEmptyPerson(this));
    }

    protected void onPause() {
        // 完成清理、断电操作
        if (!mReader.close()) {
            Toast.makeText(this, "身份证识读器关闭失败！" + mReader.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else if (!mReader.powerOff()) {
            Toast.makeText(this, "身份证识读器断电失败！" + mReader.getLastError(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "身份证识读器关闭成功！", Toast.LENGTH_SHORT).show();
        }

        super.onPause();
    }
    private void showPeopleInfo(Person person) {
        String mIdInfo = new String();

        // 姓名：30
        mIdInfo = "姓名：" + person.mName;

        // 性别：2
        mIdInfo = mIdInfo + "\n性别：" + Sex.valueOf(person.mSex).toString();

        // 民族：4
        mIdInfo = mIdInfo + "\n民族：" + Nationality.valueOf(person.mNationality).toString();

        // 出生：16
        mIdInfo = mIdInfo + "\n出生：" + person.mBirthday;
        tvIdInfo1.setText(mIdInfo);

        // 住址：70
        mIdInfo = "\n住址：" + person.mAddress;

        // 公民身份号码：36
        mIdInfo = mIdInfo + "\n公民身份号码：" + person.mNumber;

        // 签发机关：36
        mIdInfo = mIdInfo + "\n签发机关：" + person.mAuthority;

        // 有效期起始日期：16
        mIdInfo = mIdInfo + "\n有效期起始日期：" + person.mValidFrom;

        // 有效期终止日期：16
        mIdInfo = mIdInfo + "\n有效期终止日期："
                + (person.mValidTo == null ? "长期" : person.mValidTo);

        // 最新住址：36
        mIdInfo = mIdInfo + "\n最新住址：" + person.mLatestAddress;

        if (person.mPhoto_idcard != null) {
            ImageView image1 = (ImageView) findViewById(R.id.iv_idcardimage);
            image1.setImageBitmap(person.mPhoto_idcard);
        }

        tvIdInfo2.setText(mIdInfo);
        
    }
    private void showPeopleInfo(IDCard card) {
        String mIdInfo = new String();

        // 姓名：30
        mIdInfo = "姓名：" + card.getName();

        // 性别：2
        mIdInfo = mIdInfo + "\n性别：" + Sex.FEMALE.toString();

        // 民族：4
        mIdInfo = mIdInfo + "\n民族：" + card.getNationality().toString();

        // 出生：16
        mIdInfo = mIdInfo + "\n出生：" + card.getBirthday();
        tvIdInfo1.setText(mIdInfo);

        // 住址：70
        mIdInfo = "\n住址：" + card.getAddress();

        // 公民身份号码：36
        mIdInfo = mIdInfo + "\n公民身份号码：" + card.getNumber();

        // 签发机关：36
        mIdInfo = mIdInfo + "\n签发机关：" + card.getAuthority();

        // 有效期起始日期：16
        mIdInfo = mIdInfo + "\n有效期起始日期：" + card.getValidFrom();

        // 有效期终止日期：16
        mIdInfo = mIdInfo + "\n有效期终止日期："
                + (card.getValidTo() == null ? "长期" : card.getValidTo());

        // 最新住址：36
        mIdInfo = mIdInfo + "\n最新住址：" + card.getLatestAddress();

        if (card.getPhoto() != null) {
            ImageView image1 = (ImageView) findViewById(R.id.iv_idcardimage);
            image1.setImageBitmap(card.getPhoto());
        }

        tvIdInfo2.setText(mIdInfo);
    }
}
