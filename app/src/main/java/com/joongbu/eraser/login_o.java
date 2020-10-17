package com.joongbu.eraser;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login_o extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private String str;
    private String filename;
    private String ID;
    private static String url = "https://eraser2020.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mainpagelogino);
        hidebottombar.hide(getWindow().getDecorView(), getWindow().getDecorView().getSystemUiVisibility());
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Log.e("ID", ID);
        Button eraser = findViewById(R.id.eraser);
        Button logout = findViewById(R.id.logout_btn);
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    str = getRealPathFromURI(uri);
                    Log.e("file path", str);
                    String[] tmp = str.split("/");
                    filename = tmp[tmp.length - 1];
                    Log.e("file name", filename);
                    upload();
                    Toast.makeText(this, "삭제중 입니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void delete() throws IOException {
        File file = new File(str);
        showfilelog(file);
        Long len = file.length();
        byte[] b = new byte[len.intValue()];
        changedata(b, file);
        showfilelog(file);
        Arrays.fill(b, (byte) 1 );
        changedata(b, file);
        showfilelog(file);
        new Random().nextBytes(b);
        changedata(b, file);
        showfilelog(file);
        byte[] b1 = {0};
        changedata(b1, file);
        showfilelog(file);

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = {str};
        getContentResolver().delete(contentUri, selection, selectionArgs);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), "삭제가 완료되었습니다.", Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    private void changedata(byte[] b, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.close();
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
    }
    private void upload() {
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", ID)
                .addFormDataPart("filename", filename)
                .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE_PNG, new File(str)))
                .build();
        Request request = new Request.Builder()
                .url(url + "logs/logupload")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "onResponse: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", response.body().string());
                delete();
            }
        });
    }

    private void showfilelog(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        int i=0;
        StringBuilder sb1 = new StringBuilder();
        while (is.available() > 0 && i < 50){
            int value = (int) is.read();
            sb1.append(String.format("%02X ", value));
            i++;
        }
        is.close();
        Log.i("File Data ", sb1.toString());
    }
}
