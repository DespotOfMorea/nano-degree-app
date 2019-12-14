package org.vnuk.nanodegreeapp.model;

import com.google.android.gms.maps.model.LatLng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonAddress {
    private String street;
    private String city;
    private String state;
    private String postcode;
    private LatLng coordinates;
}
