package com.example.trvciuw.ui.home;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trvciuw.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private NfcAdapter nfcAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        if(nfcAdapter == null){
            Toast.makeText(getContext(), "Este dispositivo no soporta NFC", Toast.LENGTH_SHORT).show();

        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(getContext(), "NFC está desactivado", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getContext(), "NFC está activado", Toast.LENGTH_SHORT).show();
        }
        binding.linkTriveca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://linktr.ee/triveca"));
                startActivity(browserIntent);
            }
        });
        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }












}