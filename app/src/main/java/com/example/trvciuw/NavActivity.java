package com.example.trvciuw;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.trvciuw.ui.lecturaIUW.LecturaViewModel;
import com.google.android.material.navigation.NavigationView;


import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trvciuw.databinding.ActivityNavBinding;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class NavActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavBinding binding;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] readingTagFilters;
    private List<String> lstinformacionNFC = new ArrayList<>();
    private GifImageView alertGif;
    String lineaSerie = null;
    private boolean isGifAnimating = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    private LecturaViewModel lecturaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarNav.toolbar);
        setupDrawer();
        setupNFC();

        alertGif = findViewById(R.id.alertGif);
        alertGif.setClickable(false);
        lecturaViewModel = new ViewModelProvider(this).get(LecturaViewModel.class);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        if (navController.getCurrentDestination().getId() == R.id.nav_lectura) {
            gifFrezzer();
        }
    }

    private void setupDrawer() {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_lectura, R.id.nav_fichatecnica, R.id.nav_homologacion, R.id.nav_referencia, R.id.nav_informacion, R.id.nav_historico)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

   private void setupNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfc_not_supported), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("NavActivity", "Configurando PendingIntent");
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        readingTagFilters = new IntentFilter[]{tagDetected};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, readingTagFilters, null);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            //Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            readFromTag(intent); // Continúa con la lectura como ya lo tienes
            alertGif.setClickable(true);
        }
    }

    private void readFromTag(Intent intent) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        if (navController.getCurrentDestination().getId() == R.id.nav_lectura) {
            lstinformacionNFC.clear();
            NdefMessage[] msgs = getNdefMessages(intent);
            if (msgs != null && msgs.length > 0) {
                processNdefMessages(msgs);
            } else {
                displayNfcContent(getString(R.string.no_ndef_messages));
            }
        }
        else if(navController.getCurrentDestination().getId() == R.id.nav_informacion){
            lstinformacionNFC.clear();
            NdefMessage[] msgs = getNdefMessages(intent);
            if (msgs != null && msgs.length > 0) {
                processNdefMessages(msgs);
            } else {
                displayNfcContent(getString(R.string.no_ndef_messages));
            }
        }else {
            Toast.makeText(this, "No se puede leer NFC en esta pantalla", Toast.LENGTH_SHORT).show();
        }
    }
    private NdefMessage[] getNdefMessages(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null && rawMessages.length > 0) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }

    private void processNdefMessages(NdefMessage[] msgs) {
        NdefRecord record = msgs[0].getRecords()[0];
        try {
            String text = parseTextRecord(record);
            if (text != null) {
                processText(text);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("NavActivity", "Unsupported Encoding", e);
        }
    }
    private String parseTextRecord(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }
    private void processText(String text) {
        String[] lines = text.split("\\n");
        lineaSerie = lines[1];

        String serial = lineaSerie.substring(lineaSerie.indexOf(':') + 1).trim();
        displayNfcContent(serial);

        StringBuilder filteredLines = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("Vol")) {
                String lineWithoutVol = line.substring(line.indexOf(':') + 1).trim();
                filteredLines.append(lineWithoutVol).append(" ");
            }
            if (line.startsWith("Tube")) { //Tube empty
                isGifAnimating = true;
                animateAlertGif();
            }
            lstinformacionNFC.add(line);
            displayInformacionNFC(lstinformacionNFC.toArray(new String[0]));
        }
        displayNfcContent(filteredLines.toString());
        // Actualiza el ViewModel con los nuevos datos
        lecturaViewModel.setSerie(serial);
        lecturaViewModel.setVolumen(filteredLines.toString());

    }
    private void displayNfcContent(String content) {
        String currentDateTime = dateFormat.format(new Date());

        TextView txtSerie = findViewById(R.id.lecturaSerie);
        TextView txtVolumen = findViewById(R.id.lecturaVol);
        TextView txtfechaActual = findViewById(R.id.fechaActual);

        txtVolumen.setText(content);
        txtSerie.setText(content);
        txtfechaActual.setText(currentDateTime);

        lecturaViewModel.setFecha(currentDateTime);
    }

    private void displayInformacionNFC(String[] content) {
        TextView txtInformacionNFC = findViewById(R.id.informacionNfc);
        txtInformacionNFC.setText(String.join("\n", content));
        lecturaViewModel.setInformacionNFC(String.join("\n", content));
    }

    private void  gifFrezzer(){
        GifDrawable gifDrawable = (GifDrawable) alertGif.getDrawable();
        if (isGifAnimating) {
            gifDrawable.start();
        } else {
            gifDrawable.stop();
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            alertGif.setColorFilter(filter);
        }
    }
    private void animateAlertGif() {
        runOnUiThread(() -> {
            GifDrawable gifDrawable = (GifDrawable) alertGif.getDrawable();
            gifDrawable.start();
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(1);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            alertGif.setColorFilter(filter);
        });
    }
}