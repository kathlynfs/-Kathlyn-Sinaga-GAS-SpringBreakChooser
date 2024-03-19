package com.example.springbreakchooser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import java.util.Locale


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var userInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var tts: TextToSpeech
    private lateinit var ttsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userInput = findViewById(R.id.userInput)
        spinner = findViewById(R.id.languageSpinner)
        ttsButton = findViewById(R.id.toggleTTSButton)

        userInput.setInputType(InputType.TYPE_NULL)

        ArrayAdapter.createFromResource(
            this,
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                updateUserInputHint(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // nothing
            }
        }

        tts = TextToSpeech(this, this)


    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }


    private fun updateUserInputHint(language: String) {
        val hintLang = when (language) {
            "English" -> R.string.english_hint
            "Chinese" -> R.string.chinese_hint
            "Indonesian" -> R.string.indo_hint
            else -> R.string.english_hint
        }
        userInput.hint = getString(hintLang)

        val buttonLang = when (language) {
            "English" -> R.string.toggle_tts_button_en
            "Chinese" -> R.string.toggle_tts_button_cn
            "Indonesian" -> R.string.toggle_tts_button_id
            else -> R.string.toggle_tts_button_en
        }
        ttsButton.text = getString(buttonLang)
    }

    private fun speakOut(text: String, language: String) {
        val locale = when (language) {
            "English" -> Locale("en", "US")
            "Chinese" -> Locale.CHINESE
            "Indonesian" -> Locale("in", "ID")
            else -> Locale.US
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language not supported!")
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }
}