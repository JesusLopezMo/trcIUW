package com.example.trvciuw.ui.lecturaIUW;

import androidx.lifecycle.ViewModelProvider;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.trvciuw.R;
import com.example.trvciuw.databinding.FragmentLecturaBinding;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LecturaFragment extends Fragment {

    private FragmentLecturaBinding binding;
    private NfcAdapter nfcAdapter;
    private LecturaViewModel lecturaViewModel;
    private GifImageView alertGif;
    private boolean isGifAnimating = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lecturaViewModel = new ViewModelProvider(requireActivity()).get(LecturaViewModel.class);


        binding = FragmentLecturaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        alertGif = binding.alertGif;
        setupObservers();
        setupListeners();

        setupNfc();
        return root;
    }

    private void setupObservers() {
        lecturaViewModel.getSerie().observe(getViewLifecycleOwner(), s -> binding.lecturaSerie.setText(s));
        lecturaViewModel.getVolumen().observe(getViewLifecycleOwner(), s -> binding.lecturaVol.setText(s));
        lecturaViewModel.getFecha().observe(getViewLifecycleOwner(), s -> binding.fechaActual.setText(s));
        lecturaViewModel.getInformacionNFC().observe(getViewLifecycleOwner(), s -> binding.informacionNfc.setText(s));
    }

    private void setupListeners() {
        binding.linkTriveca.setOnClickListener(v -> openWebPage("https://linktr.ee/triveca"));
        binding.alertGif.setClickable(false);
        gifFrezzer();

        lecturaViewModel.getSerie().observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.isEmpty()) {
                isGifAnimating = true;
                binding.alertGif.setClickable(true);
                animateAlertGif();
                binding.alertGif.setOnClickListener(this::showAlertDialog);
            }
        });
    }

    private void openWebPage(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void showAlertDialog(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.alerta_tuberia_title)
                .setMessage(R.string.alerta_tuberia_message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void gifFrezzer() {
        if (alertGif != null) {
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
    }

    private void animateAlertGif() {

        if (alertGif != null) {
            getActivity().runOnUiThread(() -> {
                GifDrawable gifDrawable = (GifDrawable) alertGif.getDrawable();
                gifDrawable.start();
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(1);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                alertGif.setColorFilter(filter);
            });
        }
    }
    private void setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        if (nfcAdapter == null) {
            showToast(R.string.nfc_not_supported);
        } else if (!nfcAdapter.isEnabled()) {
            showToast(R.string.nfc_disabled);
        } else {
            showToast(R.string.nfc_enabled);
        }
    }

    private void showToast(int resId) {
        Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}