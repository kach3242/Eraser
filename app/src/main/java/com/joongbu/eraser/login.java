package com.joongbu.eraser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        hidebottombar.hide(getWindow().getDecorView(), getWindow().getDecorView().getSystemUiVisibility());

        Button login_btn = findViewById(R.id.login);
        Button signup_btn = findViewById(R.id.sign_up);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void request() {
        EditText edit_id = findViewById(R.id.id);
        EditText edit_pwd = findViewById(R.id.pwd);
        final String id = edit_id.getText().toString();
        final String pwd = edit_pwd.getText().toString();

        String url = "https://eraser2020.herokuapp.com/users/authenticate";
        JSONObject json = new JSONObject();
        try {
            json.put("userid", id);
            json.put("userpassword", pwd);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String result = jsonObject.getString("success");
                        if (result.equals("true")) {
                            jsonObject = new JSONObject(jsonObject.getString("userNoPW"));
                            String name = jsonObject.getString("username");
                            Toast.makeText(getApplicationContext(), name + "님이 로그인 되었습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), login_o.class);
                            intent.putExtra("ID", id);
                            startActivity(intent);
                            finish();
                        } else {
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("아이디 없음"))
                                Toast.makeText(getApplicationContext(), "존재하지않은 ID입니다.", Toast.LENGTH_LONG).show();
                            else if (msg.equals("패스워드 다름"))
                                Toast.makeText(getApplicationContext(), "PASSWORD를 확인하세요.", Toast.LENGTH_LONG).show();
                            else if (msg.equals("이메일 인증 안함")) {
                                Toast.makeText(getApplicationContext(), "이메일을 인증해주세요.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), register_email.class);
                                intent.putExtra("ID", id);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
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
}
