package com.example.iops_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    File[] filesAndFolders;
    public MyAdapter(Context context, File[] filesAndFolders)
    {
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()) {
            holder.fileImageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }
        else {
            holder.fileImageView.setImageResource(R.drawable.ic_baseline_file_24);
        }



        holder.deleteIcon.setImageResource(R.drawable.ic_baseline_delete_outline_24);

        holder.textView.setOnClickListener(view -> {

            if(selectedFile.isDirectory()) {

                Intent intent = new Intent(context, FileManager.class);
                String path = selectedFile.getAbsolutePath();
                intent.putExtra("path",path);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            else{
                try{
                    Intent intent= new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    String type = "text/csv";
                    intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()),type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                catch(Exception e){
                    System.out.print(e);
                }
            }
        });

        holder.deleteIcon.setOnClickListener(view -> {

            boolean deleted = selectedFile.delete();
            if (deleted) {
                Toast.makeText(context.getApplicationContext(), "DELETED", Toast.LENGTH_SHORT).show();
                view.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(context.getApplicationContext(), "Cannot delete, folder is not empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        //System.out.println(filesAndFolders.length);
        return filesAndFolders.length;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        TextView textView;
        ImageView fileImageView;
        ImageView deleteIcon;

        public ViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.file_name_text_view);
            fileImageView = itemView.findViewById(R.id.icon_view);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }
}

