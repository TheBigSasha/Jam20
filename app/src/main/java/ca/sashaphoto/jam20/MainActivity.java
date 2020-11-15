package ca.sashaphoto.jam20;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SeekBar howYouFeltSlider;
    JamBackend backend;
    Button button_imbored;
    LottieAnimationView animationView;
    ImageView background;
    Button button_happy;
    EditText editTextFeedback;
    TextView whatToDo;
    static SuggestedItemRepository suggestedItemRepository;

    //AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "database-name").build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        backend = JamBackend.build();
        suggestedItemRepository = new SuggestedItemRepository(getApplication());
        howYouFeltSlider = findViewById(R.id.howYouFeltSlider);
        button_imbored = findViewById(R.id.button_imbored);         //TODO: Swap with a positive and negative response button!
        editTextFeedback = findViewById(R.id.editTextFeedback);
        whatToDo = findViewById(R.id.textViewWhatToDo);
        button_happy = findViewById(R.id.button_lovedIt);
        background = findViewById(R.id.imageView);
        animationView = findViewById(R.id.animation_view);
        button_imbored.setOnClickListener(e -> getSuggestion(false));
        button_happy.setOnClickListener(e -> getSuggestion(true));
        whatToDo.setOnClickListener(e -> actionFromSuggestion(String.valueOf(whatToDo.getText())));
        getSuggestion(false);
    }

    private void actionFromSuggestion(String currentSuggestion) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, currentSuggestion); // query contains search string
        startActivity(intent);
    }

    private void getSuggestion(boolean wasGood) {

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
            protected void onPreExecute(){
                animationView.setVisibility(View.VISIBLE);
                /*background.setVisibility(View.GONE);*/
                //animationView.playAnimation();
                whatToDo.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(Void... voids) {
                backend.fetchNewSuggestion(wasGood);
                try {
                    Thread.sleep(1940);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//TODO: Set image from API!
return backend.getCurrentSuggestion();
            }

            @Override
            protected void onPostExecute(String result) {
                whatToDo.setText(result);
                animationView.setVisibility(View.GONE);
                //background.setVisibility(View.VISIBLE);
                whatToDo.setVisibility(View.VISIBLE);
            }

        };

        updateWasGood.execute();
        //Toast.makeText(getBaseContext(),backend.getCurrentSuggestion(),Toast.LENGTH_LONG).show();

    }


}