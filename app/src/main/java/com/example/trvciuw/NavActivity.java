package com.example.trvciuw;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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


public class NavActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavBinding binding;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] readingTagFilters;

    // Lista para almacenar palabras que empiecen con "Vol"
    private List<String> volWordsList;
    private TextView lecturaVol;
    String firstLine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNav.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_lectura, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Configurar el PendingIntent para la actividad actual
        Log.d("MainActivity", "Configurando PendingIntent");
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Configurar el filtro para el descubrimiento de etiquetas NFC
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        readingTagFilters = new IntentFilter[]{tagDetected};

        lecturaVol = findViewById(R.id.lecturaVol);
        volWordsList = new ArrayList<>(); //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Log.d("NavActivity", "Nueva intención NFC recibida");
            readFromTag(intent);
        }
    }
    private void readFromTag(Intent intent) {
        // Limpiar la lista volWordsList
        volWordsList.clear();

        try {
            NdefMessage[] msgs = getNdefMessages(intent);
            if (msgs != null && msgs.length > 0) {
                NdefRecord record = msgs[0].getRecords()[0];
                String text = parseTextRecord(record);
                if (text != null) {
                    String[] lines = text.split("\n"); // Dividir el texto en líneas
                    firstLine = lines[1]; // Capturar la primera línea del texto
                    String Segundalinea = firstLine.substring(firstLine.indexOf(':') + 1).trim();
                    for (String line : lines) {
                        if (line.startsWith("Vol")) {
                            volWordsList.add(line); // Agregar línea a la lista si contiene "Vol"
                        }
                    }
                    // Manejar la primera línea con un try-catch individual
                    try {
                        // Procesar la primera línea aquí
                        // Por ejemplo, mostrarla en un TextView
                        displayFirstLine(Segundalinea);
                    } catch (Exception e) {
                        Log.e("ReadTag", "Error al procesar la primera línea: " + e.getMessage(), e);
                    }
                    // Obtener el texto sin "Vol:"
                    List<String> filteredLines = new ArrayList<>();
                    for (String volLine : volWordsList) {
                        // Obtener el texto después de "Vol:"
                        String lineWithoutVol = volLine.substring(volLine.indexOf(':') + 1).trim();
                        filteredLines.add(lineWithoutVol);
                    }
                    Log.d("ReadTag", "Contenido de la línea: " + filteredLines); // Mostrar contenido de la línea en el registro
                    displayNfcContent(filteredLines.toString().replaceAll("[\\[\\]]", ""));
                }
            } else {
                displayNfcContent("No se encontraron mensajes NDEF en la etiqueta NFC");
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("ReadTag", "Error al leer la etiqueta NFC Linea: " + e.getMessage(), e);
                displayNfcContent("Error al leer la etiqueta NFC: " + e.getMessage());
            } else {
                Log.e("ReadTag", "Error al leer la etiqueta NFC", e);
                displayNfcContent("Error al leer la etiqueta NFC");
            }
        }
    }

    private void displayFirstLine(String firstLine) {
        // Mostrar la primera línea en un TextView
        // Por ejemplo, si tienes un TextView con id 'lecturaPrimeraLinea'
        TextView lecturaSerie = findViewById(R.id.lecturaSerie);
        lecturaSerie.setText(firstLine);

    }
    private void displayNfcContent(String content) {
        // Obtener la fecha y hora actuales
        String currentDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Mostrar la fecha y hora actuales en un TextView
        // Asegúrate de tener un TextView con id 'fechaActual' en tu layout
        TextView fechaActualTextView = findViewById(R.id.fechaActual);
        fechaActualTextView.setText(currentDateTime);
        lecturaVol.setText(content);
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

    private String parseTextRecord(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }



}