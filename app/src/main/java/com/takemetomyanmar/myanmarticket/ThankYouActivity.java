package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.takemetomyanmar.myanmarticket.Authentication.RegisterAccountActivity;

/**
 * Created by AlexAung on 2/22/2015.
 */
public class ThankYouActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        setTitle("Thank you!");
        Bundle extras = getIntent().getExtras();
        Double amount = extras.getDouble("amount");

        TextView txtThankYou = (TextView) findViewById(R.id.txtThankYou);
        txtThankYou.setText("Your payment of $"+ amount +" has been sent. Thank you for paying with PayPal.");

        Button mBtnDone = (Button) findViewById(R.id.btnNext);
        mBtnDone.setOnClickListener(doneClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_login, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    View.OnClickListener doneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        }
    };
}
