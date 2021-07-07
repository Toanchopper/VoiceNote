package com.example.voicenote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.StatusBarManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE =100 ;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageView imgbtn;
    FloatingActionButton fab;
    ListView listnote;
    static List<Note> notes;
    static DBManager db;
    static NoteAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.navdrawer);
        toolbar=findViewById(R.id.toolbar);
        imgbtn=findViewById(R.id.btnshow);
        fab=findViewById(R.id.fabadd);
        listnote=findViewById(R.id.listnote);
        listnote.setDivider(null);
        listnote.setDividerHeight(20);
        View view=findViewById(R.id.tvempty);
        listnote.setEmptyView(view);

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");

        notes=new ArrayList<>();
        db=new DBManager(this);
        notes.clear();
        notes.addAll(db.getnote());
        setAdapter();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog);
                dialog.show();
                Button btnnhap=dialog.findViewById(R.id.btnnhap);
                Button btnmoi=dialog.findViewById(R.id.btnmoi);
                btnmoi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,AddNote.class);
                        startActivityForResult(intent,1);
                        dialog.dismiss();
                    }
                });
                btnnhap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nhapghichu();
                        dialog.dismiss();
                    }
                });
            }
        });
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }
    public void setAdapter()
    {
        if(adapter==null)
        {
            adapter=new NoteAdapter(this,R.layout.itemnote,notes);
            listnote.setAdapter(adapter);
        }
        else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            int id=data.getIntExtra("id",0);
            String title=data.getStringExtra("title");
            String content=data.getStringExtra("content");
            Note note=new Note(id,title,content);
            db.add(note);
            notes.clear();
            notes.addAll(db.getnote());
            setAdapter();
            Toast.makeText(this, "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode==3)
        {
            try {
                Uri uri = data.getData();
                String temp=getFileName(uri);
                String name=temp.substring(0,temp.length()-4);
                List<String> list = new ArrayList<>();
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    for (String line; (line = r.readLine()) != null; ) {
                        list.add(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String code = list.get(0);
                String[] newcode = code.split(" ");
                String alpha = list.get(1);
                String codewe = list.get(2);
                String[] newwe = codewe.split(" ");
                char[] arrayalpha = alpha.toCharArray();
                ShannoFano sf = new ShannoFano();
                String content = sf.kqgm(newcode, arrayalpha, newwe);

                Random rd=new Random();
                int id=rd.nextInt(100);
                Note note=new Note(id,name,content);
                db.add(note);
                notes.clear();
                notes.addAll(db.getnote());
                setAdapter();
                Toast.makeText(this, "Đã nhập ghi chú", Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(this, "File ghi chú không đúng định dạng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void nhapghichu()
    {
          Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("text/plain");
          startActivityForResult(intent,3);
    }
    @Override
    protected void onResume() {
        super.onResume();
        notes.clear();
        notes.addAll(db.getnote());
        setAdapter();
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}