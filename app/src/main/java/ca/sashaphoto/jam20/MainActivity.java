package ca.sashaphoto.jam20;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    SeekBar howYouFeltSlider;
    JamBackend backend;
    Button button_imbored;
    EditText editTextFeedback;

    //AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "database-name").build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backend = JamBackend.build();
        howYouFeltSlider = findViewById(R.id.howYouFeltSlider);
        button_imbored = findViewById(R.id.button_imbored);
        editTextFeedback = findViewById(R.id.editTextFeedback);
        button_imbored.setOnClickListener(e -> getSuggestion());
    }

    private void getSuggestion() {
        Toast.makeText(getBaseContext(),backend.getCurrentSuggestion(),Toast.LENGTH_LONG).show();

    }
}