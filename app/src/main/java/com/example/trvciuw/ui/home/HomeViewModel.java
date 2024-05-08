package com.example.trvciuw.ui.home;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NdefRecord;
import android.nfc.NdefMessage;

import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trvciuw.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> currentDate = new MutableLiveData<>();

    public void setCurrentDate(String date) {
        currentDate.setValue(date);
    }

    public LiveData<String> getCurrentDate() {
        return currentDate;
    }

}