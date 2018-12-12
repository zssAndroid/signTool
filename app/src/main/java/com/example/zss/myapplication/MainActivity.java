package com.example.zss.myapplication;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动服务
//                Intent i = new Intent();                                  //ai.aitek.baseui.service.DialogService
////                i.setComponent(new ComponentName("ai.aitek.va", "ai.aitek.va.service.DialogService"));
//                i.setComponent(new ComponentName("ai.aitek.va", "ai.aitek.baseui.service.DialogService"));
//                i.putExtra("action", "action_aitek_show_dialog");
//                startService(i);

                try {
                    startSplit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送广播
//                sendBroadcast(new Intent("action_show_dialog_out_app"));
//                startActivity(new Intent(MainActivity.this, SignActivity.class));
            }
        });

    }

    private void startSplit() throws IOException {
        InputStream is = getAssets().open("111.txt");

        int size = is.available();
        // Read the entire asset into a local byte buffer.
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        // Convert the buffer into a string.
        String text = new String(buffer, "utf-8");
        // Finally stick the string into the text view.

        String[] split = text.split("target=\"_blank\">");
        ArrayList<String> list = new ArrayList<>();
        for (String s : split) {
            String replace = s.replace("target=\"_blank\">", "");
            list.add(replace);
        }
        ArrayList<String> list2 = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : list) {
            int indexOf = s.indexOf("</li>");
            if (indexOf > 2) {
                String substring = s.substring(0, indexOf);
                String replace = substring.replace("</a>(", "\":\"");
                String replace1 = replace.replace(")", "");
                String replace2 = replace1.replace("/", "");
                replace2 = "\""+replace2 + "\"";
                list2.add(replace2);
                stringBuffer.append(replace2 + ",");
                Log.e("========", replace2);
            }
        }

        String s = stringBuffer.toString();
        Log.e("========", s);

    }




    public void test() {
        PackageManager packageManager = getPackageManager();
        //查看系统中是否有可用的相机应用
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            String packageName = resolveInfo.activityInfo.packageName;
            String name1 = resolveInfo.activityInfo.name;
            try {
                //打开相机
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent == null) {
                    intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.setPackage(packageName);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName name = new ComponentName(packageName, name1);
                    intent.setComponent(name);
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        String data = "";
        JSONObject jsonObject = new JSONObject();
    }
}
