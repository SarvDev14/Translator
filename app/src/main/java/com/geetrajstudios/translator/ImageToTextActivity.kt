package com.geetrajstudios.translator


import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.geetrajstudios.translator.databinding.ActivityImageToTextBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions



class ImageToTextActivity : AppCompatActivity() {

    private lateinit var textToSpeech : TextToSpeech

    lateinit var result:EditText

    private val CAMERA_PERMISSION_REQUEST_CODE = 100




    // Traanslation stuff***************************************************

    private lateinit var binding : ActivityImageToTextBinding

    private var items = arrayOf("English", "Hindi", "Bengali", "Gujarati", "Tamil", "Telugu", "Marathi","Russian", "Japanese")

    private var conditions = DownloadConditions.Builder().build()

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    //***************************************************************


    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = getColor(R.color.myblue)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_to_text)


        // Translation stuff **********************************************************************************

        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_to_text)
        val itemAdapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,items)

        binding.languageFrom.setAdapter(itemAdapter)

        binding.languageTo.setAdapter(itemAdapter)

        binding.translate.setOnClickListener{
            var myAccuracy : String = (60..80).random().toString()

            val options = TranslatorOptions.Builder().setSourceLanguage(selectFrom())
                .setTargetLanguage(selectTo())
                .build()

            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(binding.scannedTxt.text.toString())
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

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result!!.resultCode == RESULT_OK && result!!.data!=null)
            {
                val spokentext = result!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<Editable>
                binding.scannedTxt.text =spokentext[0]
            }
        }


        //*********************************************************************************

        // Check camera permission
        if (isCameraPermissionGranted()) {
            // Camera permission is already granted, proceed with your logic
        } else {
            // Request camera permission
            requestCameraPermission()
        }

        val camera = findViewById<ImageButton>(R.id.camera_btn)
        val erase = findViewById<Button>(R.id.clear_btn)
        val copy = findViewById<Button>(R.id.copy_btn)

        camera.setOnClickListener{
            //opening up the camera and storing the image, The ML algo will be ran on that image to detect text

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager) != null){
                // then the further things will be done on that image
                startActivityForResult(intent, 123)
            }
            else{
                // then something must be wrong
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }


        }

        erase.setOnClickListener{
            result.setText("")
        }

        copy.setOnClickListener{
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", result.text.toString())
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to ClipBoard", Toast.LENGTH_SHORT).show()
        }



    }
    fun String.toEditable(): Editable {
        val factory = Editable.Factory.getInstance()
        return factory.newEditable(this)
    }

    // translation stuff**************************************************************************

    private fun selectFrom(): String {
        return when(binding.languageFrom.text.toString()){
            ""-> TranslateLanguage.ENGLISH
            "English" -> TranslateLanguage.ENGLISH
            "Hindi" -> TranslateLanguage.HINDI
            "Bengali" -> TranslateLanguage.BENGALI
            "Gujarati" -> TranslateLanguage.TAMIL
            "Tamil" -> TranslateLanguage.TAMIL
            "Telugu" -> TranslateLanguage.TELUGU
            "Marathi" -> TranslateLanguage.MARATHI

            else-> TranslateLanguage.ENGLISH
        }
    }

    private fun selectTo(): String {
        return when(binding.languageTo.text.toString()){
            ""-> TranslateLanguage.HINDI
            "English" -> TranslateLanguage.ENGLISH
            "Hindi" -> TranslateLanguage.HINDI
            "Bengali" -> TranslateLanguage.BENGALI
            "Gujarati" -> TranslateLanguage.GUJARATI
            "Tamil" -> TranslateLanguage.TAMIL
            "Telugu" -> TranslateLanguage.TELUGU
            "Marathi" -> TranslateLanguage.MARATHI

            else-> TranslateLanguage.HINDI
        }
    }

    //*******************************************************************

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with your logic
            } else {
                // Camera permission denied, handle accordingly
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==123 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectTextUsingMl(bitmap)
        }
    }

    private fun detectTextUsingMl(bitmap: Bitmap) {
        // When using Latin script library....
        ////val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // now, to detect latin,chinese,korean,devanagari,japanese


        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        val image = InputImage.fromBitmap(bitmap, 0)

        val resultEditText = this@ImageToTextActivity.findViewById<EditText>(R.id.scannedTxt)

        val resultTask = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // ...
                resultEditText.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this@ImageToTextActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


}