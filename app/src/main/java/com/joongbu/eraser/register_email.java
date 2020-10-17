package com.joongbu.eraser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class register_email extends AppCompatActivity {

    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_email);
        hidebottombar.hide(getWindow().getDecorView(), getWindow().getDecorView().getSystemUiVisibility());
        Intent intent = getIntent();
        ID = intent.getStringExtra("id");
        Button back_btn = findViewById(R.id.back);
        Button send_btn = findViewById(R.id.send);
        Button register_btn = findViewById(R.id.register);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
    }

    public void request() {
        EditText edit_email = findViewById(R.id.email);
        EditText edit_cert_num = findViewById(R.id.certnum);
        final String email = edit_email.getText().toString();
        final String cert = edit_cert_num.getText().toString();

        String url = "https://eraser2020.herokuapp.com/certs/emailauth";
        JSONObject json = new JSONObject();
        try {
            json.put("userid", ID);
            json.put("semail", email);
            json.put("cert", cert);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String result = jsonObject.getString("success");
                        String msg = jsonObject.getString("msg");
                        if (result.equals("true")) {
                            if (msg.equals("변경 성공")) {
                                show(1);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            if (msg.equals("전송 실패")) {
                                show(2);
                            } else if (msg.equals("일치하지 않음")) {
                                show(3);
                            } else
                                Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } catch (JSONException e) {
        }
    }

    void show(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        switch (i) {
            case 1:
                builder.setMessage("인증번호가 발송되었습니다.");
                break;
            case 2:
                builder.setMessage("이메일을 다시 확인해주십시오.");
                break;
            case 3:
                builder.setMessage("인증번호를 다시 확인해주십시오.");
                break;
        }
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
}
