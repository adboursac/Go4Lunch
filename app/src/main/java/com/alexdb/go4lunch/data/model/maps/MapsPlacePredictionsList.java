package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsPlacePredictionsList {

    @SerializedName("status")
    @Expose
    private final String status;

    @SerializedName("predictions")
    @Expose
    private final List<MapsPlacePrediction> predictions;

    public MapsPlacePredictionsList(String status, List<MapsPlacePrediction> predictions) {
        this.status = status;
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public List<MapsPlacePrediction> getPredictions() {
        return predictions;
    }
}
