package com.example.trvciuw.ui.homologacion;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trvciuw.R;
import com.example.trvciuw.ui.fichatecnica.FichatecnicaFragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HomologacionFragment extends Fragment {

    private PDFView pdfhomologacion;
    private HomologacionViewModel mViewModel;

    public static HomologacionFragment newInstance() {
        return new HomologacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homologacion, container, false);
        pdfhomologacion = view.findViewById(R.id.pdfhomologacion);
        FloatingActionButton fabDownload = view.findViewById(R.id.fab_download);

        pdfhomologacion.fromAsset("DM_HLFL-002-2021.pdf").load();

        fabDownload.setOnClickListener(v -> {
            copiarArchivoLocal("DM_HLFL-002-2021.pdf", "Homogolacion.pdf");
        });

        return view;
    }

    private void copiarArchivoLocal(String nombreArchivoAssets, String nombreArchivoDestino) {
        File archivoDestino = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nombreArchivoDestino);
        try (InputStream in = getContext().getAssets().open(nombreArchivoAssets);
             OutputStream out = new FileOutputStream(archivoDestino)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            Toast.makeText(getContext(), "Archivo descargado con Exito", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error al descargar el archivo", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}