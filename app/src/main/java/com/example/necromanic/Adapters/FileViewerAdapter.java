package com.example.necromanic.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.necromanic.Database.DBHelper;
import com.example.necromanic.Fragments.PlaybackFragment;
import com.example.necromanic.Interfaces.OnDatabaseChangedListener;
import com.example.necromanic.Models.RecordingItem;
import com.example.necromanic.R;
import com.github.sshadkany.CircleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileViewerAdapter extends RecyclerView.Adapter <FileViewerAdapter.FileViewrViewHolder> implements OnDatabaseChangedListener {

    Context context;
    ArrayList<RecordingItem> arrayList;
    LinearLayoutManager llm;
    DBHelper dbHelper;

    public FileViewerAdapter(Context context, ArrayList<RecordingItem> arrayList, LinearLayoutManager llm) {
        this.context = context;
        this.arrayList = arrayList;
        this.llm = llm;
        this.dbHelper = new DBHelper(context);
        dbHelper.setOnDatabaseChangedListener(this);
    }

    @NonNull
    @Override
    public FileViewrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
         return new FileViewrViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( FileViewrViewHolder holder, int position) {

        RecordingItem recordingItem = arrayList.get(position);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(recordingItem.getLength());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(recordingItem.getLength()) - TimeUnit.MINUTES.toSeconds(minutes);

        holder.fileNameText.setText(recordingItem.getName());
        holder.fileLengthText.setText(String.format("%02d:%02d",minutes ,seconds));
        holder.fileTimeAdded.setText(DateUtils.formatDateTime(context, recordingItem.getTime_added(),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR ));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onDatabaseEntryAdded(RecordingItem recordingItem) {
           arrayList.add(recordingItem);
           notifyItemInserted(arrayList.size() - 1);
    }


    public class FileViewrViewHolder extends RecyclerView.ViewHolder {

        TextView fileNameText;

        TextView fileLengthText;

        TextView fileTimeAdded;

        RelativeLayout cardiew;

        CircleButton playfab;

        RecordingItem recordingItemGet;

        @SuppressLint("ClickableViewAccessibility")

        public FileViewrViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.file_name_text);
            fileLengthText = itemView.findViewById(R.id.file_length_text);
            fileTimeAdded = itemView.findViewById(R.id.file_time_added);
            playfab = itemView.findViewById(R.id.play_fab_from_list_item);
            cardiew = itemView.findViewById(R.id.card_view);

            playfab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaybackFragment playbackFragment = new PlaybackFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("item", arrayList.get(getAdapterPosition()));
                    playbackFragment.setArguments(b);

                    FragmentTransaction fragmentTransaction = ((FragmentActivity)context).
                            getSupportFragmentManager().beginTransaction();
                    playbackFragment.show(fragmentTransaction,"dialog_playback");

                }
            });

            cardiew.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setTitle("Delete result")
                            .setMessage("Are you sure you want delete this Record?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* This is where deletions should be handled */
                                    int position = getAdapterPosition();
                                    dbHelper = new DBHelper(context);


                                    try {
                                        deleteFile(position);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    recordingItemGet= arrayList.get(position);
                                    dbHelper.deleteEntry(recordingItemGet.getId());
                                    arrayList.remove(position);
                                    notifyItemRemoved(position);
                                    //            notifyDataSetChanged();
                                    //          notifyItemRangeChanged(getAdapterPosition(),arrayList.size());

                                }

                            })
                            .setNegativeButton("No", null)
                            .show();


                    return true;
                }
            });


        }

        public void deleteFile(int id) throws IOException {
            recordingItemGet = arrayList.get(id);
            String path = recordingItemGet.getPath();

            File file = new File(path);
            file.delete();

            File fdelete = new File(path);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Toast.makeText(context, "File Deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "File Deleted", Toast.LENGTH_LONG).show();
                }
            }
        }

    }




}

//    private void removeItem(int position) {
//
//        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder;
//        int newPosition = holder.getAdapterPosition();
//        model.remove(newPosition);
//        notifyItemRemoved(newPosition);
//        notifyItemRangeChanged(newPosition, model.size());
//    }

