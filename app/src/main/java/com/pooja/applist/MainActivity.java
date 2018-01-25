package com.pooja.applist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText mainText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        setCallbacks();
    }

    private void setViews() {
        mainText = findViewById(R.id.mailText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
    }

    private void setCallbacks() {
        mainText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.isEmpty()) {
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setEnabled(isEmailValid(text));
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mainText.getText().toString();
                if (!email.isEmpty()) {
                    List<String> list = getAppList();
                    sendList(list);
                } else {
                    Toast.makeText(MainActivity.this, "You can not leave the email empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendList(List<String> list) {
        if (list.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mainText.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "All Apps List");
        intent.putExtra(Intent.EXTRA_TEXT, createText(list));

        try {
            startActivity(Intent.createChooser(intent, "Send via Email"));
        } catch (Exception e) {

        }
    }

    private String createText(List<String> list) {
        StringBuilder text = new StringBuilder();
        for (int i=0; i< list.size(); i++) {
            text.append(i + ". " + list.get(i) + "\n");
        }
        return text.toString();
    }

    private List<String> getAppList() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        List<String> finalList = new ArrayList<>();

        for (ResolveInfo info : appList) {
            finalList.add(info.loadLabel(packageManager).toString());
        }

        return finalList;
    }

    private boolean isEmailValid(String mail) {
        if (mail.isEmpty()) return false;
        else return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }
}
