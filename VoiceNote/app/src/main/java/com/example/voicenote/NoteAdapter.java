package com.example.voicenote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {
    Context context;
    int resource;
    List<Note> notes;
    public NoteAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.notes=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.itemnote,parent,false);
            viewHolder.btndel=convertView.findViewById(R.id.btndelete);
            viewHolder.btnex=convertView.findViewById(R.id.btnexport);
            viewHolder.tvtitle=convertView.findViewById(R.id.tvtitle);
            viewHolder.tvcontent=convertView.findViewById(R.id.tvcontent);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Note note=notes.get(position);
        viewHolder.tvtitle.setText(note.getTitle());
        viewHolder.tvcontent.setText(note.getContent());
        viewHolder.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });
        viewHolder.btnex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export(note.getTitle(),note.getContent());
            }
        });
        return convertView;
    }

    public class ViewHolder{
        ImageView btndel,btnex;
        TextView tvtitle,tvcontent;
    }

    public void delete(int pos)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo").setMessage("Bạn chắc chắn muốn xóa ghi chú này?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.db.delete(notes.get(pos));
                MainActivity.notes.clear();
                MainActivity.notes.addAll(MainActivity.db.getnote());
                MainActivity.adapter.notifyDataSetChanged();
                Toast.makeText(context, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void export(String name,String text){
        ShannoFano sf=new ShannoFano();
        List<String[]> rs=sf.kqmh(text);
        String[] kq=rs.get(0);
        String[] code=rs.get(1);
        String[] alphabet=rs.get(2);
        String[] codewe=rs.get(3);
        String average=kq[0];
        String entropy=kq[1];
        String banma=kq[3];
        String code1="",alpha1="",codewe1="";
        for(int i=0;i<code.length;i++)
        {
            code1+=code[i]+" ";
        }
        for(int i=0;i<alphabet.length;i++)
        {
            alpha1+=alphabet[i];
        }
        for(int i=0;i<codewe.length;i++)
        {
            codewe1+=codewe[i]+" ";
        }
        try {
            File output = new File(getContext().getExternalFilesDir(null),name.trim()+".txt");
            FileOutputStream fileout = new FileOutputStream(output.getAbsolutePath());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            outputStreamWriter.write(code1+'\n'+alpha1+'\n'+codewe1+'\n'+banma+'\n'+"Entropy: "+entropy+'\n'+"Average Bit: "+average+'\n');
            outputStreamWriter.close();
            Toast.makeText(context, "Đã ghi ra file", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
