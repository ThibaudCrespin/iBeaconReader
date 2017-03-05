package com.iut.lr.ibeaconreader;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    IntentFilter[] filters;
    String[][] techs;
    PendingIntent pendingIntent;
    NfcAdapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter mifare = new IntentFilter((NfcAdapter.ACTION_TECH_DISCOVERED));
        filters = new IntentFilter[] { mifare };
        techs = new String[][] { new String[] {NfcA.class.getName() } };
        adapter = NfcAdapter.getDefaultAdapter(this);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

                imageView.startAnimation(rotation);
            }
        });
    }

    public void onResume() {
        super.onResume();
        Animation Animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        imageView.startAnimation(Animation);

        adapter.enableForegroundDispatch(this, pendingIntent, filters, techs);
    }

    public void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    public void onNewIntent (Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        ByteBuffer wrapped = ByteBuffer.wrap(id);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        int signedInt = wrapped.getInt();
        long number = signedInt & 0xfffffff1;
        Evt(number);
    }

    public void Evt(long number) {
        Animation Animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        imageView.startAnimation(Animation);

        Intent goToApp = new Intent(MainActivity.this, BeaconActivity.class);

        // Create the AlertDialog for failed authentication
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.fail)
                .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        Dialog auth_error = builder.create();

        // Nous avons fait un System.out.println du nombre récupéré avec la carte puis nous
        // l'avons inséré dans la condition if.
        if (number == -1971634176 || number == -2106576896) {
            startActivity(goToApp);
        } else {
            auth_error.show();
        }

    }
}
