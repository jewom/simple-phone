package exemple.com.simple_phone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.widget.EditText;
import java.util.ArrayList;


import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;


public class DialerActivity extends AppCompatActivity {
    EditText phoneNumberInput;
    private static final int REQUEST_PERMISSION = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        if (getIntent().getData() != null)
            phoneNumberInput.setText(getIntent().getData().getSchemeSpecificPart());
    }

    @Override
    public void onStart() {
        super.onStart();
        offerReplacingDefaultDialer();

        phoneNumberInput.setOnEditorActionListener((v, actionId, event) -> {
            makeCall();
            return true;
        });
    }

    @SuppressLint("MissingPermission")
    private void makeCall() {
        // If permission to call is granted
        if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
            // Create the Uri from phoneNumberInput
            Uri uri = Uri.parse("tel:"+phoneNumberInput.getText());
            // Start call to the number in input
            startActivity(new Intent(Intent.ACTION_CALL, uri));
        } else {
            // Request permission to call
            ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<Integer> grantRes = new ArrayList<>();
        // Add evry result to the array
        for (Integer grantResult: grantResults) grantRes.add(grantResult);

        if (requestCode == REQUEST_PERMISSION && grantRes.contains(PERMISSION_GRANTED)) {
            makeCall();
        }
    }

    private void offerReplacingDefaultDialer() {
        TelecomManager systemService = this.getSystemService(TelecomManager.class);
        if (systemService != null && !systemService.getDefaultDialerPackage().equals(this.getPackageName())) {
            startActivity((new Intent(ACTION_CHANGE_DEFAULT_DIALER)).putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.getPackageName()));
        }
    }
}
