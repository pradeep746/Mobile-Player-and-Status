package com.pradeep.mobileacess.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.pradeep.mobileacess.R;
import com.pradeep.mobileacess.databinding.ActivityHomeBinding;
import com.pradeep.mobileacess.databinding.ActivityTextSpeechBinding;

public class TextSpeechActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech mTextToSpeech;
    private String mInput, mOutput;
    private ActivityTextSpeechBinding binding;
    private List<String> mInputLanguage, mOutputLanguage;
    private Context mContext;
    private ArrayAdapter<String> mInputAdapter, mOutputAdaptor;

    private Translator englishHindiTranslator;
    private int mInputLanPosition, mOutputPosition;
    private List<String> mAllLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextSpeechBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        mInput = null;
        mOutput = null;
        mTextToSpeech = new TextToSpeech(this, this);
        mAllLanguage = new ArrayList<>();
        binding.appBar.textView.setText("Convert other language");
        binding.appBar.backRegister.setOnClickListener(View-> {
            finish();
        });
        binding.speeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });
        binding.selectWriteLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mInput = null;
                    Locale locale = new Locale("hi");  // Hindi locale
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                    return;
                }
                mInput = parent.getItemAtPosition(position).toString();
                mInputLanPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.selectLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mOutput = null;
                    return;
                }
                mOutputPosition = position;
                mOutput = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mInputLanguage = new ArrayList<>();
        mOutputLanguage = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Locale[] availableLocales = Locale.getAvailableLocales();
                mInputLanguage.add("Please select text input language");
                mOutputLanguage.add("Please select output language");
                List<String> checking = new ArrayList();
                checking.addAll(TranslateLanguage.getAllLanguages());
                for (int i = 0; i < availableLocales.length; i++) {
                    Locale locale = availableLocales[i];
                    if (locale.getCountry().equalsIgnoreCase("IN")) {
                        Log.e("value", "value:" + locale.getLanguage());
                        if (checking.contains(locale.getLanguage())) {
                            mAllLanguage.add(locale.getLanguage());
                            mInputLanguage.add(locale.getDisplayName());
                            mOutputLanguage.add(locale.getDisplayName());
                        }
                    }
                }

                ((TextSpeechActivity) mContext).runOnUiThread(new Runnable() {
                    public void run() {
                        mInputAdapter.notifyDataSetChanged();
                        binding.selectWriteLanguage.setSelection(0);
                        mOutputAdaptor.notifyDataSetChanged();
                        binding.selectLanguage.setSelection(0);
                    }
                });
            }
        }.start();
        binding.enterTextData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if (mInput == null) {
                    Toast.makeText(mContext, "Please select input language", Toast.LENGTH_SHORT).show();
                    binding.enterTextData.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mInputAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mInputLanguage);
        mInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectWriteLanguage.setAdapter(mInputAdapter);

        mOutputAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mOutputLanguage);
        mOutputAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectLanguage.setAdapter(mOutputAdaptor);

        binding.outputbutton.setOnClickListener(View -> {
            String text = binding.enterTextData.getText().toString();
            if (text != null && text.length() > 0) {
                String input = mAllLanguage.get(mInputLanPosition-1), output = mAllLanguage.get(mOutputPosition-1);
                showProgressBar();
                outputText(text, input, output, -1);
            } else {
                Toast.makeText(mContext, "Please enter text to convert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                binding.speeckButton.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void outputText(String text, String input, String output, int outputSpeak) {
        Log.e("TAG", "output lan; " + output);
        Log.e("TAG", "input lan; " + input);
        Log.e("TAG", "" + mInput + "," + mOutput);
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(input)
                .setTargetLanguage(output)
                .build();
        englishHindiTranslator = Translation.getClient(options);
        englishHindiTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {
                    englishHindiTranslator.translate(text)
                            .addOnSuccessListener(translatedText -> {
                                binding.outputText.setText(translatedText);
                                englishHindiTranslator.close();
                                englishHindiTranslator = null;
                                hideProgressBar();
                                if(outputSpeak > -1) {
                                    final Locale[] availableLocales = Locale.getAvailableLocales();
                                    for(int i = 0; i < availableLocales.length;i++) {
                                        if(availableLocales[i].getLanguage().equalsIgnoreCase(output)) {
                                            mTextToSpeech.setLanguage(availableLocales[outputSpeak]);
                                            mTextToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null,null);
                                            break;
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle error
                                Log.e("Translation", "Error translating", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Model Download", "Error downloading model", e);
                });
    }

    private void speakOut() {
        if (mInput == null) {
            Toast.makeText(mContext, "Please select input text language", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mOutput == null) {
            Toast.makeText(mContext, "Please select output language", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = binding.enterTextData.getText().toString();
        if (text != null && text.length() > 0) {
            String input = mAllLanguage.get(mInputLanPosition-1), output = mAllLanguage.get(mOutputPosition-1);
            showProgressBar();
            outputText(text, input, output, 1);
        } else {
            Toast.makeText(mContext, "Please enter text to convert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showProgressBar() {
        ((TextSpeechActivity) mContext).runOnUiThread(new Runnable() {
            public void run() {
                binding.layoutLoader.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        ((TextSpeechActivity) mContext).runOnUiThread(new Runnable() {
            public void run() {
                binding.layoutLoader.setVisibility(View.GONE);
            }
        });
    }

}
