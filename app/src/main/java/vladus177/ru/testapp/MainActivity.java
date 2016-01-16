package vladus177.ru.testapp;


import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText name;
    private EditText mail;
    private EditText pass;
    private EditText passRepeat;
    private TextView terms;
    private Button sign;

    LocationService LC;
    GMailSender sender;

    double latitude = 0.0;
    double longitude = 0.0;

    String address = null;
    String firstW;
    String secondW;
    String thirdW;
    String fourthW;
    String ename = null;
    String email = null;
    String epass = null;
    String epassRepeat = null;
    String esender = "itsatestmail3@gmail.com";
    String esubject = "Высылаю беспилотный модуль";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        final EditText name = (EditText) findViewById(R.id.editTextName);
        final EditText mail = (EditText) findViewById(R.id.editTextMail);
        final EditText pass = (EditText) findViewById(R.id.editTextPass);
        final EditText passRepeat = (EditText) findViewById(R.id.editTextPassRepeat);
        TextView terms = (TextView) findViewById(R.id.textViewTerms);
        Button BtnSign = (Button) findViewById(R.id.buttonSignUp);


        firstW = getString(R.string.terms1);
        secondW = "<u><font color='#3699DB'>Terms of Use</font></u>";
        thirdW = "and";
        fourthW = "<u><font color='#3699DB'>Privacy Policy</font></u>";
        terms.setText(Html.fromHtml(firstW + " " + secondW + " " + thirdW + " " + fourthW));

        sender = new GMailSender("itsatestmail3@gmail.com", "iddqdiddqd");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Coordinates
        LC = new LocationService(this);
        if (LC.canGetLocation()) {

            latitude = LC.getLatitude();
            longitude = LC.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            LC.showSettingsAlert();
        }
        //address
        address = GetAddress(latitude, longitude);
        if (address == null) {
            address = GetAddress(latitude, longitude);
        }

        //click
        BtnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validation name
                if (name.getText().toString().length() >= 1) {
                    ename = name.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter name", Toast.LENGTH_SHORT).show();
                }
                //validation mail
                if (isValidEmail(mail.getText().toString())) {
                    email = mail.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter valid mail **@**.**", Toast.LENGTH_SHORT).show();
                }
                //validation pass
                if (pass.getText().toString().length() >= 1) {
                    epass = pass.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter password", Toast.LENGTH_SHORT).show();
                }
                //validation pass repeat
                if (passRepeat.getText().toString().equals(epass)) {
                    epassRepeat = passRepeat.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please repeat password", Toast.LENGTH_SHORT).show();
                }
                //sending mail
                if (ename != null && email != null && epass != null && epassRepeat != null && epassRepeat.equals(epass)) {
                    try {
                        new MyAsyncClass().execute();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please check text fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public String GetAddress(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                ret = strReturnedAddress.toString();
                //Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_SHORT).show();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = null;
        }
        return ret;
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail(esubject, address, esender, email);

            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(getApplicationContext(), "Email send", Toast.LENGTH_LONG).show();
        }
    }

}
