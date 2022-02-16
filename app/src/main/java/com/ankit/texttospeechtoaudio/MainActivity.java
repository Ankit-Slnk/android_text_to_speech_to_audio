package com.ankit.texttospeechtoaudio;

import android.Manifest;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ankit.texttospeechtoaudio.adaper.LanguagesAdapter;
import com.ankit.texttospeechtoaudio.models.CustomLocaleClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    EditText etText;
    TextToSpeech textToSpeech;
    String tempDestFile = "";
    LinearLayout llProgressBar;
    Spinner spLanguage;
    CustomLocaleClass selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.etText);
        llProgressBar = findViewById(R.id.llProgressBar);
        spLanguage = findViewById(R.id.spLanguage);

        String exStoragePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File appTmpPath = new File(exStoragePath);
        appTmpPath.mkdirs();
        String tempFilename = "ankit_tts.wav";
        tempDestFile = appTmpPath.getAbsolutePath() + "/" + tempFilename;

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.e("tts status", status + "");
                if (status == TextToSpeech.SUCCESS) {

                    ArrayList<CustomLocaleClass> languagesList = new ArrayList<CustomLocaleClass>();

                    Set<Locale> languages = textToSpeech.getAvailableLanguages();
                    Log.e("languages", languages.size() + "");
                    for (Locale locale : languages) {
                        languagesList.add(new CustomLocaleClass(locale.getDisplayName() + "", locale.getLanguage() + ""));
                    }

                    LanguagesAdapter languagesAdapter = new LanguagesAdapter(MainActivity.this, languagesList);

                    spLanguage.setAdapter(languagesAdapter);
                    spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedLanguage = languagesList.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (selectedLanguage != null) {
                        for (int i = 0; i < languagesList.size(); i++) {
                            if (languagesList.get(i).language.equals(selectedLanguage.language) && languagesList.get(i).displayName.equals(selectedLanguage.displayName)) {
                                spLanguage.setSelection(i);
                            }
                        }
                    }

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(MainActivity.this, Uri.fromFile(new File(tempDestFile)));

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    llProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Text to speech converted to audio successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });
                }
            }
        });
    }

    public void onPlay(View view) {
        if (etText.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
        } else if (selectedLanguage == null) {
            Toast.makeText(MainActivity.this, "Please select language", Toast.LENGTH_SHORT).show();
        } else {
            int res = textToSpeech.setLanguage(Locale.forLanguageTag(selectedLanguage.language));
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(MainActivity.this, "Language not Supported", Toast.LENGTH_SHORT).show();
            } else {
                llProgressBar.setVisibility(View.VISIBLE);

                HashMap<String, String> myHashRender = new HashMap<String, String>();
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, System.currentTimeMillis() + "");

                textToSpeech.synthesizeToFile(etText.getText().toString(), myHashRender, tempDestFile);

                textToSpeech.speak(etText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
    }
}