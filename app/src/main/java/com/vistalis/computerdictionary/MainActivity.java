package com.vistalis.computerdictionary;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.vistalis.computerdictionary.Adapters.WordAdapter;
import com.vistalis.computerdictionary.DatabaseModules.DB;
import com.vistalis.computerdictionary.DatabaseModules.Models.Word;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import hotchemi.android.rate.AppRate;

import static com.vistalis.computerdictionary.Helpers.SharedPref.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordAdapter wordAdapter;
    LinearLayoutManager layoutManager;
    List<Word> word_list;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSharedPreferenceBoolean(this,"is_splash_open",true);

//        this.setActivityToFullScreen();

        findViewById(R.id.mainLayout).requestFocus();

        this.wordForToday();


        this.buildRecyclerView();

       EditText searchField = findViewById(R.id.searchField);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        this.displayAppRatingDialog();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.action_translate:
                    intent = new Intent(this,TranslateActivity.class);
                    startActivity(intent);
                    break;


                case R.id.action_favorites:
                    intent = new Intent(this,FavoritesActivity.class);
                    startActivity(intent);
                    break;

                case R.id.action_phrases:
                    intent = new Intent(this,PhrasesActivity.class);
                    startActivity(intent);
                    break;

            }
            return true;
        });

    }

    private void wordForToday() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastDay = getSharedPreferenceInt(getApplicationContext(),"day",0);
        int noOfWords = DB.getInstance(this).wordsDao().noOfWords();
        Random r = new Random();
        int randomId = r.nextInt(noOfWords);

        if( lastDay != currentDay ) {
            setSharedPreferenceInt(getApplicationContext(),"day",currentDay);

            Word word = DB.getInstance(this).wordsDao().pickWord(randomId);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Word for today");
            builder.setCancelable(false);
            builder.setMessage(word.getEnglish_word() + " - " + word.getTranslation()).setPositiveButton("Thank you", (dialog, which) -> {
                // If the user click the Thank you button what would you want to execute place here...
            }).create().show();
        }
    }

    private void filter(String text) {
        ArrayList<Word> filteredList = new ArrayList<>();

        for (Word item : word_list) {
            if (item.getEnglish_word().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        wordAdapter.filterList(filteredList);
    }

    private void buildRecyclerView() {

        word_list = DB.getInstance(this).wordsDao().getAllWords();

        wordAdapter = new WordAdapter(word_list);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.addItemDecoration(
                new DividerItemDecoration(this,
                        LinearLayoutManager.VERTICAL)
        );


        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(wordAdapter);

    }

    private void displayAppRatingDialog() {
        // callback listener.
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Integer.toString(which)))
                .monitor();


        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }


    private void setActivityToFullScreen()
    {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
