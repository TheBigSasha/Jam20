package ca.sashaphoto.jam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.WidgetContainer;
import androidx.core.app.ActivityCompat;

import io.radar.sdk.Radar;
import io.radar.sdk.RadarTrackingOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SeekBar howYouFeltSlider;
    JamBackend backend;
    Button button_imbored;
    EditText editTextFeedback;
    TextView whatToDo;

    //AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "database-name").build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Radar.initialize(this, "prj_live_pk_a3e096fbbc21b35c19d43b0abcf8f375f38bd1d5");
        Radar.startTracking(RadarTrackingOptions.EFFICIENT);
        //not sure if this next line goes here, but i think it does
        //ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION }, requestCode)

        Objects.requireNonNull(this.getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        backend = JamBackend.build();
        howYouFeltSlider = findViewById(R.id.howYouFeltSlider);
        button_imbored = findViewById(R.id.button_imbored);         //TODO: Swap with a positive and negative response button!
        editTextFeedback = findViewById(R.id.editTextFeedback);
        whatToDo = findViewById(R.id.textViewWhatToDo);
        button_imbored.setOnClickListener(e -> getSuggestion());
        getSuggestion();
    }

    private void getSuggestion() {
        boolean wasGood = false;    //TODO: Get this from button

        Intent intent = new Intent(this, JamWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), JamWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);


        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> updateWasGood = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                backend.fetchNewSuggestion(wasGood);
                return backend.getCurrentSuggestion();
            }

            @Override
            protected void onPostExecute(String result) {
                whatToDo.setText(result);
            }

        };

        updateWasGood.execute();
        //Toast.makeText(getBaseContext(),backend.getCurrentSuggestion(),Toast.LENGTH_LONG).show();

    }


}