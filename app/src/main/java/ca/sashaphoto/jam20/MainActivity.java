package ca.sashaphoto.jam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.WidgetContainer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
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

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> updateWasGood = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                backend.fetchNewSuggestion(wasGood);
                return backend.getCurrentSuggestion();
            }

            @Override
            protected void onProgressUpdate(Void... update) {
                whatToDo.setText(backend.getCurrentSuggestion());
            }

        };

        updateWasGood.execute();
        //Toast.makeText(getBaseContext(),backend.getCurrentSuggestion(),Toast.LENGTH_LONG).show();

    }


}