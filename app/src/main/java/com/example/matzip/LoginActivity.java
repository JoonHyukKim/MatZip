package com.example.matzip;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText idText, passwordText;
    private Button loginButton, registerButton;
    private CheckBox idCheckBox;
    private Context mContext;


    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        mContext = this;


        idText = findViewById( R.id.idText );
        passwordText = findViewById( R.id.passwordText );
        idCheckBox=findViewById(R.id.idCheckBox);
        registerButton = findViewById( R.id.registerButton );
        loginButton = findViewById( R.id.loginButton );


        boolean boo = PreferenceManager.getBoolean(mContext,"check"); //로그인 정보 기억하기 체크 유무 확인
        if(boo){ // 체크가 되어있다면 아래 코드를 수행
            //저장된 아이디와 암호를 가져와 셋팅한다.
            idText.setText(PreferenceManager.getString(mContext, "id"));

            idCheckBox.setChecked(true); //체크박스는 여전히 체크 표시 하도록 셋팅
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity( intent );
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserID = idText.getText().toString();
                String UserPwd = passwordText.getText().toString();

                PreferenceManager.setString(mContext, "id", idText.getText().toString()); //id라는 키값으로 저장
                String checkId = PreferenceManager.getString(mContext, "id");


                if (UserID.equals("") || UserPwd.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    dialog = builder.setMessage("아이디, 비밀번호를 입력해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {//로그인 성공시

                                String UserID = jsonObject.getString( "UserID" );
                                String UserPwd = jsonObject.getString( "UserPwd" );
                                String UserPN = jsonObject.getString( "UserPN" );

                                Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", UserPN), Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( LoginActivity.this, MapsActivity.class );

                                intent.putExtra( "UserID", UserID );
                                intent.putExtra( "UserPwd", UserPwd );
                                intent.putExtra( "UserPN", UserPN );
                                intent.putExtra("id",checkId);

                                startActivity( intent );
                                finish();


                            } else {//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("아이디, 비밀번호를 확인해주세요").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest( UserID, UserPwd, responseListener );
                RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
                queue.add( loginRequest );

            }
        });
        idCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) { // 체크박스 체크 되어 있으면
                    //editText에서 아이디 가져와 PreferenceManager에 저장한다.
                    PreferenceManager.setString(mContext, "id", idText.getText().toString()); //id 키값으로 저장

                    PreferenceManager.setBoolean(mContext, "check", idCheckBox.isChecked()); //현재 체크박스 상태 값 저장
                } else { //체크박스가 해제되어있으면
                    PreferenceManager.setBoolean(mContext, "check", idCheckBox.isChecked()); //현재 체크박스 상태 값 저장
                    PreferenceManager.clear(mContext); //로그인 정보를 모두 날림
                }
            }
        });





    }
}