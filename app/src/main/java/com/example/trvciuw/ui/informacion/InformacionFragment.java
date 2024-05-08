package com.example.trvciuw.ui.informacion;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trvciuw.R;
import com.example.trvciuw.databinding.FragmentInformacionBinding;
import com.example.trvciuw.ui.lecturaIUW.LecturaViewModel;

public class InformacionFragment extends Fragment {

    private LecturaViewModel lecturaViewModel;
    private FragmentInformacionBinding binding;

    public static InformacionFragment newInstance() {
        return new InformacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        lecturaViewModel = new ViewModelProvider(requireActivity()).get(LecturaViewModel.class);

        binding = FragmentInformacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lecturaViewModel.getInformacionNFC().observe(getViewLifecycleOwner(), s -> binding.informacionNfc.setText(s));
        return root;
    }



}