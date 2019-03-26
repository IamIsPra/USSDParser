package info.isuru.ussdparser;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Listener{

    EditText edtUssdCode;
    Button btnDial;
    TextView txtResult;
    private String strUssdCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        IntentFilter filter = new IntentFilter();
        filter.addAction("info.isuru.ussdparser.action.REFRESH");

        UssdBroadcastReceiver ussdBroadcastReceiver = new UssdBroadcastReceiver(this);
        registerReceiver(ussdBroadcastReceiver, filter);

        edtUssdCode = findViewById(R.id.edtUssdCode);
        btnDial = findViewById(R.id.btnDial);
        txtResult = findViewById(R.id.txtResult);

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strUssdCode = edtUssdCode.getText().toString();
                if(!strUssdCode.isEmpty() && isPermissionGranted()){
                    dialCode(strUssdCode);
                }
            }
        });

    }

    private void dialCode(String code){
        String ussd = Uri.encode(code);
        startService(new Intent(this, UssdReadService.class));
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussd)));
    }

    public void updateResult(String message) {
        txtResult.setText(message);
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialCode(edtUssdCode.getText().toString());
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
