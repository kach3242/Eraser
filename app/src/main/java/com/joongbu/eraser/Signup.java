package com.joongbu.eraser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup);

        hidebottombar.hide(getWindow().getDecorView(), getWindow().getDecorView().getSystemUiVisibility());

        Button signup_btn = findViewById(R.id.sign_up);
        Button back_btn = findViewById(R.id.back);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void request(){
        EditText edit_id = findViewById(R.id.id);
        EditText edit_pwd = findViewById(R.id.pwd);
        EditText edit_pwd2 = findViewById(R.id.pwd2);
        EditText edit_name = findViewById(R.id.name);
        EditText edit_email = findViewById(R.id.phone);
        final String id = edit_id.getText().toString();
        final String pwd = edit_pwd.getText().toString();
        final String pwd2 = edit_pwd2.getText().toString();
        final String name = edit_name.getText().toString();
        final String phone = edit_email.getText().toString();

        String url = "https://eraser2020.herokuapp.com/users/register";
        JSONObject testjson = new JSONObject();
        try {
            testjson.put("userid", id);
            testjson.put("userpassword", pwd);
            testjson.put("userpassword2", pwd2);
            testjson.put("username", name);
            testjson.put("userphone", phone);
            String jsonString = testjson.toString();
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, testjson, new Response.Listener<JSONObject>() {

                //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String result = jsonObject.getString("success");
                        if(result.equals("true")){
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), register_email.class);
                            startActivity(intent);
                            finish();
                        }else{
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("이미 아이디 있음"))
                                Toast.makeText(getApplicationContext(), "존재하는 ID입니다.", Toast.LENGTH_LONG).show();
                            else if (msg.equals("빈칸 있음, 양식 틀림"))
                                Toast.makeText(getApplicationContext(), "빈칸 또는 양식이 틀립니다.", Toast.LENGTH_LONG).show();
                            else if (msg.equals("비밀번호 다름"))
                                Toast.makeText(getApplicationContext(), "비밀번호가 서로 다릅니다.", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "가입에 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
        }
    }
}