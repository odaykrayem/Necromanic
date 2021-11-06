package com.example.necromanic.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.necromanic.Models.RecordingItem;
import com.example.necromanic.R;
import com.github.sshadkany.CircleButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlaybackFragment extends DialogFragment {

    private RecordingItem item ;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    long minutes = 0;
    long seconds = 0;

    SeekBar seekBarPlayer;
    TextView fileNamePlayer;
    TextView fileLengthPlayer;
    TextView currentProgressTV;
    CircleButton fabPlay;
    ImageView playButtonIcon;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (RecordingItem)getArguments().getSerializable("item");
        minutes = TimeUnit.MILLISECONDS.toMinutes(item.getLength());
        seconds = TimeUnit.MILLISECONDS.toSeconds(item.getLength()) - TimeUnit.MINUTES.toSeconds(minutes);

    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_playback,null);
        seekBarPlayer = view.findViewById(R.id.seek_bar);
        fileNamePlayer =  view.findViewById(R.id.file_name_player);
        fileLengthPlayer =  view.findViewById(R.id.file_length_player);
        currentProgressTV = view.findViewById(R.id.current_progress_tv);
        fabPlay =  view.findViewById(R.id.fab_play);
        playButtonIcon = view.findViewById(R.id.play_button_icon);

        setSeekbarValues();

        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onPlay(isPlaying);
                    setSeekbarValues();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = !isPlaying;
            }
        });
        fileNamePlayer.setText(item.getName());
        fileLengthPlayer.setText(String.format("%02d:%02d",minutes,seconds));
        builder.setView(view);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    private void onPlay(boolean isPlaying) throws IOException {
        if(!isPlaying){
                startPlaying();
        }
        else{
            pausePlaying();
        }

    }

    private void pausePlaying() {
        playButtonIcon.setImageResource(R.drawable.ic_baseline_play_arrow_12);
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    private void startPlaying() throws  IOException {
        playButtonIcon.setImageResource(R.drawable.ic_media_pause);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(item.getPath());
        mediaPlayer.prepare();
        seekBarPlayer.setProgress(mediaPlayer.getCurrentPosition());
        seekBarPlayer.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        updateSeekbar();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setSeekbarValues() {

        seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(mediaPlayer!= null && fromUser){
                  mediaPlayer.seekTo(progress);
                  handler.removeCallbacks(mRunnable);

                  long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                  long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())- TimeUnit.MINUTES.toSeconds(minutes);

                  currentProgressTV.setText(String.format("%02d:%02d",minutes,seconds));
                  updateSeekbar();
                }
                else if (mediaPlayer == null && fromUser){
                    try {
                        prepareMediaplayerFromPoint(progress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateSeekbar();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void prepareMediaplayerFromPoint(int progress) throws IOException{
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(item.getPath());
        mediaPlayer.prepare();

        seekBarPlayer.setMax(mediaPlayer.getDuration());
        mediaPlayer.seekTo(progress);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

    }

    private void stopPlaying() {
        playButtonIcon.setImageResource(R.drawable.ic_baseline_play_arrow_12);
        handler.removeCallbacks(mRunnable);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        seekBarPlayer.setProgress(seekBarPlayer.getMax());
        isPlaying = !isPlaying;
        currentProgressTV.setText(fileLengthPlayer.getText());
        seekBarPlayer.setProgress(seekBarPlayer.getMax());
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
             if(mediaPlayer != null){
                 int mCurrentPosition = mediaPlayer.getCurrentPosition();
                 seekBarPlayer.setProgress(mCurrentPosition);
                 long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                 long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)- TimeUnit.MINUTES.toSeconds(minutes);
                 currentProgressTV.setText(String.format("%02d:%02d",minutes,seconds));
                 updateSeekbar();
             }

        }
    };

    private void updateSeekbar() {
        handler.postDelayed(mRunnable,1000);
    }

}
