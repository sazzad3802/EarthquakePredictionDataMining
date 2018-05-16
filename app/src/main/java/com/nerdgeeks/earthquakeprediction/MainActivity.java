package com.nerdgeeks.earthquakeprediction;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {

    private static final String[] regions = {
                                            "Select your desired region",
                                            "Srinagar",
                                            "Jhamnagar",
                                            "Patna",
                                            "Delhi",
                                            "Guwahati",
                                            "Meerut",
                                            "Jammu",
                                            "Amritsar",
                                            "Jalandhar",
                                            "Dehradun",
                                            "Vadodara",
                                            "Surat",
                                            "Rajkot",
                                            "Pune",
                                            "Ahmedabad"
                                            };
    private Location location;
    private View rootview;
    private TextView testTextView;
    private TextView optionalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootview = findViewById(R.id.rootView);

        Button predButton = findViewById(R.id.id_text_predict);
        predButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                predictDoingRegression();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        testTextView = findViewById(R.id.testTV);
        optionalTextView = findViewById(R.id.id_optionalText);

        final MaterialSpinner matSpinner = (MaterialSpinner) findViewById(R.id.id_spinner);
        matSpinner.setItems(regions);
        matSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                switch(item)
                {
                    case "Srinagar":
                        showLatLong(item);
                        break;
                    case "Jhamnagar":
                        showLatLong(item);
                        break;
                    case "Patna":
                        showLatLong(item);
                        break;
                    case "Delhi":
                        showLatLong(item);
                        break;
                    case "Guwahati":
                        showLatLong(item);
                        break;
                    case "Meerut":
                        showLatLong(item);
                        break;
                    case "Jammu":
                        showLatLong(item);
                        optionalTextView.setText("The magnitude of 2013 earthquake was 5.7");
                        break;
                    case "Amritsar":
                        showLatLong(item);
                        break;
                    case "Jhalandar":
                        showLatLong(item);
                        break;
                    case "Dehradun":
                        showLatLong(item);
                        break;
                    case "Vadodara":
                        showLatLong(item);
                        break;
                    case "Surat":
                        showLatLong(item);
                        break;
                    case "Rajkot":
                        showLatLong(item);
                        break;
                    case "Pune":
                        showLatLong(item);
                        break;
                    case "Ahmedabad":
                        showLatLong(item);
                        break;
                }
            }
        });
        matSpinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Location getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Location latLng = null;

        try {
            address = coder.getFromLocationName(strAddress + ", India", 5);
            if (address == null || address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            latLng = new Location("");
            latLng.setLatitude(lat);
            latLng.setLongitude(lng);
            }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return latLng;
    }

    public void showLatLong(String item)
    {
        location = getLocationFromAddress(item);
    }

    private void predictDoingRegression(){
        try {

            InputStream is = this.getAssets().open("quake_new_linearRegression.model");
            ObjectInputStream ois = new ObjectInputStream(is);
            LinearRegression model = (LinearRegression) ois.readObject();

            //create instance
            final Attribute attributeLat = new Attribute("latitude");
            final Attribute attributeLng = new Attribute("longitude");
            final Attribute attributeMag = new Attribute("combustibleelement");
            final Attribute attributePop = new Attribute("densityofpopulation");
            final Attribute attributeDist = new Attribute("distancefromrecentquakezone");
            final Attribute attributeSoil = new Attribute("soiltype");
            final Attribute attributeMagni = new Attribute("magnitude");

            // Instances(...) requires ArrayList<> instead of List<>...
            ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
                {
                    add(attributeLat);
                    add(attributeLng);
                    add(attributeMag);
                    add(attributePop);
                    add(attributeDist);
                    add(attributeSoil);
                    add(attributeMagni);
                }
            };

            // unpredicted data sets (reference to sample structure for new instances)
            Instances dataUnpredicted = new Instances("TestInstances",
                    attributeList, 1);
            // last feature is target variable
            dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

            // create new instance
            DenseInstance newInstance = new DenseInstance(dataUnpredicted.numAttributes()) {
                {
                    setValue(attributeLat, location.getLatitude());
                    setValue(attributeLng, location.getLongitude());
                }
            };

            // reference to dataset
            newInstance.setDataset(dataUnpredicted);
            double predictedValue = model.classifyInstance(newInstance);
            String result = String.format(Locale.getDefault(),"%.2f", predictedValue);
            testTextView.setText(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
