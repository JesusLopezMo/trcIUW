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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    /*
    private final MutableLiveData<String> mText;
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }*/


    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] readingTagFilters;



    // Lista para almacenar palabras que empiecen con "Vol"
    private List<String> volWordsList;
    String firstLine = null;








}