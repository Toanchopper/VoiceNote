package com.example.voicenote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class AddNote extends AppCompatActivity {

    Toolbar toolbar;
    EditText edttitle,edtcontent;
    ImageView btnvoice1,btnvoice2,btnlang;
    TextView tvlang;
    Button btnok,btncancel;
    String nation="Locate.US";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        toolbar=findViewById(R.id.toolbar);
        edttitle=findViewById(R.id.edttitle);
        edtcontent=findViewById(R.id.edtcontent);
        btnvoice1=findViewById(R.id.btnvoice1);
        btnvoice2=findViewById(R.id.btnvoice2);
        btnok=findViewById(R.id.btnok);
        btncancel=findViewById(R.id.btncancel);
        btnlang=findViewById(R.id.btnlang);
        tvlang=findViewById(R.id.tvlang);

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        btnvoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(1);
            }
        });
        btnvoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(2);
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takenote();
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nation.equals("Locate.US"))
                {
                    nation="vi_VN";
                    tvlang.setText("Ngôn ngữ: Tiếng Việt");
                }
                else{
                    nation="Locate.US";
                    tvlang.setText("Ngôn ngữ: Tiếng Anh");
                }
            }
        });
    }
    public void record(int select)
    {
        if(select==1)
        {
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,nation);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Nói để ghi chú");
            startActivityForResult(intent,1);
        }
        else{
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, nation);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Nói để ghi chú");
            startActivityForResult(intent,2);
        }
    }

    public void takenote()
    {
              if(!edttitle.getText().toString().equals("")&&!edtcontent.getText().toString().equals(""))
              {
                  Random rd=new Random();
                  int id=rd.nextInt(100);
                  Intent intent=new Intent(AddNote.this,MainActivity.class);
                  intent.putExtra("id",id);
                  intent.putExtra("title",edttitle.getText().toString());
                  intent.putExtra("content",edtcontent.getText().toString());
                  setResult(RESULT_OK,intent);
                  finish();
              }
              else{
                  Toast.makeText(this, "Nhập đầy đủ thông tin đã", Toast.LENGTH_SHORT).show();
              }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            ArrayList<String> listspeech=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edttitle.append(listspeech.get(0).toString()+" ");
        }
        else if(requestCode==2&&resultCode==RESULT_OK)
        {
            ArrayList<String> listspeech=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edtcontent.append(listspeech.get(0).toString()+" ");
        }
    }
}