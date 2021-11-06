package com.example.necromanic.Interfaces;


import com.example.necromanic.Models.RecordingItem;

public interface OnDatabaseChangedListener {

     void onDatabaseEntryAdded(RecordingItem recordingItem);
}
