package com.example.trvciuw.ui.lecturaIUW;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LecturaViewModel extends ViewModel {
    private final MutableLiveData<String> serie = new MutableLiveData<>();
    private final MutableLiveData<String> volumen = new MutableLiveData<>();
    private final MutableLiveData<String> fecha = new MutableLiveData<>();
    private final MutableLiveData<String> informacionNFC = new MutableLiveData<>();

    public LiveData<String> getInformacionNFC() {
        return informacionNFC;
    }
    public void setInformacionNFC(String informacionNFC) {
        this.informacionNFC.setValue(informacionNFC);
    }
    public LiveData<String> getSerie() {
        return serie;
    }
    public void setSerie(String serie) {
        this.serie.setValue(serie);
    }
    public LiveData<String> getVolumen() {
        return volumen;
    }
    public void setVolumen(String volumen) {
        this.volumen.setValue(volumen);
    }
    public LiveData<String> getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha.setValue(fecha);
    }
}
