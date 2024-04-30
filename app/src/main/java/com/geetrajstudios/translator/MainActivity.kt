package com.geetrajstudios.translator

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.databinding.DataBindingUtil
import com.geetrajstudios.translator.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.lang.reflect.Array
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech : TextToSpeech

    private lateinit var binding: ActivityMainBinding

    private var items = arrayOf("English", "Hindi", "Bengali", "Gujarati", "Tamil", "Telugu", "Marathi","Russian", "Japanese")

    private var conditions = DownloadConditions.Builder().build()

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        textToSpeech = TextToSpeech(this@MainActivity,this@MainActivity)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val itemAdapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,items)

        binding.languageFrom.setAdapter(itemAdapter)

        binding.languageTo.setAdapter(itemAdapter)

        binding.languageFrom.dropDownHeight = 600 // Set the height of the dropdown in pixels
        binding.languageTo.dropDownHeight = 600

        binding.buttonTextToSp.isEnabled = false
        binding.translate.setOnClickListener{
            val options = TranslatorOptions.Builder().setSourceLanguage(selectFrom())
                .setTargetLanguage(selectTo())
                .build()

            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(binding.input.text.toString())
                        .addOnSuccessListener {translatedText->

                        binding.output.text = translatedText

                    }
                        .addOnFailureListener{exception->
                            Toast.makeText(this, exception.message,Toast.LENGTH_SHORT).show()
                        }


            }
                .addOnFailureListener{
                    Toast.makeText(this, it.message,Toast.LENGTH_SHORT).show()

                }



        }

        binding.buttonSpeak.setOnClickListener{
            val intent =  Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something...")

            try{
                activityResultLauncher.launch(intent)
            }catch (exp: ActivityNotFoundException){
                Toast.makeText(applicationContext, "Device not supported for this app", Toast.LENGTH_SHORT).show()
            }


        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result!!.resultCode == RESULT_OK && result!!.data!=null)
            {
                val spokentext = result!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<Editable>
                binding.input.text =spokentext[0]
            }
        }

        binding.buttonTextToSp.setOnClickListener {
            speakOut()
        }

    }

    private fun speakOut() {
        val textForSpeak = binding.output.text.toString()

        // Determine the language to set for TextToSpeech
        val language = selectTo()

        // Set the language for TextToSpeech
        val res = textToSpeech.setLanguage(Locale(language))

        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Language not supported
            Toast.makeText(this, "Language $language is not supported", Toast.LENGTH_SHORT).show()
        } else {
            // Speak the text
            textToSpeech.speak(textForSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }



    private fun selectFrom(): String {
        return when(binding.languageFrom.text.toString()){
            ""->TranslateLanguage.ENGLISH
            "English" -> TranslateLanguage.ENGLISH
            "Hindi" ->TranslateLanguage.HINDI
            "Bengali" ->TranslateLanguage.BENGALI
            "Gujarati" ->TranslateLanguage.GUJARATI
            "Tamil" ->TranslateLanguage.TAMIL
            "Telugu" ->TranslateLanguage.TELUGU
            "Marathi" -> TranslateLanguage.MARATHI
            "Russian" ->TranslateLanguage.RUSSIAN
            "Japanese" ->TranslateLanguage.JAPANESE

            else->TranslateLanguage.ENGLISH
        }
    }

    private fun selectTo(): String {
        return when(binding.languageTo.text.toString()){
            ""->TranslateLanguage.HINDI
            "English" -> TranslateLanguage.ENGLISH
            "Hindi" ->"hi"
            "Bengali" ->TranslateLanguage.BENGALI
            "Gujarati" ->TranslateLanguage.GUJARATI
            "Tamil" ->TranslateLanguage.TAMIL
            "Telugu" ->TranslateLanguage.TELUGU
            "Marathi" -> TranslateLanguage.MARATHI
            "Russian" ->TranslateLanguage.RUSSIAN
            "Japanese" ->TranslateLanguage.JAPANESE
            else->TranslateLanguage.HINDI
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
        {
            val res = textToSpeech.setLanguage(Locale.getDefault())
            if(res==TextToSpeech.LANG_MISSING_DATA || res==TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this,"Language Not Supported", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.buttonTextToSp.isEnabled = true

            }
        }
        else{
            Toast.makeText(this,"Failed To Initialized", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {

        textToSpeech.stop()
        textToSpeech.shutdown()

        super.onDestroy()
    }
}