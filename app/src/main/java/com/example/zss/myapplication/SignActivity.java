package com.example.zss.myapplication;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class SignActivity extends AppCompatActivity {

    private List<APPInfo> list = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(myAdapter = new MyAdapter());

        getPackageNam();
    }

    private void getPackageNam() {
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchables = getPackageManager().queryIntentActivities(main, PackageManager.MATCH_ALL);


        list.clear();
        for (ResolveInfo resolveInfo : launchables) {
            String packageName = resolveInfo.activityInfo.packageName;

            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    //第三方应用
                    String name = resolveInfo.loadLabel(getPackageManager()).toString();
                    Drawable drawable = resolveInfo.loadIcon(getPackageManager());
                    list.add(new APPInfo(drawable, name, packageName));
                } else {
                    //系统应用

                    //第三方应用
                    String name = resolveInfo.loadLabel(getPackageManager()).toString();
                    Drawable drawable = resolveInfo.loadIcon(getPackageManager());
                    list.add(new APPInfo(drawable, name, packageName));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (list != null) {
            for (APPInfo appInfo : list) {
                getSign(appInfo);
            }
        }
        myAdapter.setNewData(list);
    }


    private Signature[] getRawSignature(String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            return null;
        }
        PackageManager localPackageManager = getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return null;
        }
        return localPackageInfo.signatures;
    }


    /**
     * 开始获得签名 * @param packageName 报名 * @return
     *
     * @param appInfo
     */
    private void getSign(APPInfo appInfo) {
        Signature[] arrayOfSignature = getRawSignature(appInfo.packageName);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
            return;
        }
        String messageDigest = getMessageDigest(arrayOfSignature[0].toByteArray());
        appInfo.setSign(messageDigest);

    }


    public String getMessageDigest(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i) return new String(arrayOfChar2);
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
        }
        return null;
    }

    public byte[] getRawDigest(byte[] paramArrayOfByte) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            return arrayOfByte;
        } catch (Exception localException) {
        }
        return null;
    }

    class APPInfo {
        private Drawable icon;
        private String name;
        private String packageName;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        private String sign;

        public APPInfo(Drawable icon, String name, String packageName) {
            this.icon = icon;
            this.name = name;
            this.packageName = packageName;
        }
    }

    class MyAdapter extends BaseQuickAdapter<APPInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.rvitem_appinfo);
        }

        @Override
        protected void convert(BaseViewHolder helper, final APPInfo item) {
            try {
                try {
                    ImageView icon = helper.getView(R.id.icon);
                    icon.setImageDrawable(item.icon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                helper.setText(R.id.tv_appName, item.name + "\n" + item.packageName + "\n" + item.sign);
                helper.setOnClickListener(R.id.btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(item.name + "\n" + item.packageName + "\n" + item.sign);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
