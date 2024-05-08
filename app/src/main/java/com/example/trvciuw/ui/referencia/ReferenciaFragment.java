package com.example.trvciuw.ui.referencia;

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
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReferenciaFragment extends Fragment {

    private PDFView pdfreferencia;

    private ReferenciaViewModel mViewModel;

    public static ReferenciaFragment newInstance() {
        return new ReferenciaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referencia, container, false);
        pdfreferencia = view.findViewById(R.id.pdfreferencia);
        FloatingActionButton fabDownload = view.findViewById(R.id.fab_download);

        pdfreferencia.fromAsset("referencia.pdf").load();

        fabDownload.setOnClickListener(v -> {
            copiarArchivoLocal("referencia.pdf", "Referencias.pdf");
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
