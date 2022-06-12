package com.shroomify.capstone

import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.NumberFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private lateinit var imgview: ImageView
    private val mInputSize = 224
    private val mModelPath = "mushroom_model.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgview = findViewById(R.id.imageView)

        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        var camera: Button = findViewById(R.id.cameraButton)
        var select: Button = findViewById(R.id.selectButton)
        var search: ImageButton = findViewById(R.id.imageButton)

        search.setOnClickListener(View.OnClickListener {
            val inten = Intent(this, view_data::class.java)
            startActivity(inten)
        })

        camera.setOnClickListener(View.OnClickListener{
            val inten = Intent(this, Test_activity::class.java)
            startActivity(inten)
        })

        select.setOnClickListener(View.OnClickListener{


            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent,100)



        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var tv:TextView = findViewById(R.id.textView)

        imgview.setImageURI(data?.data)

        var uri: Uri?= data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val result = classifier.recognizeImage(bitmap)
        tv.setText("Result : "+result.get(0).title+"\nConfidence : "+
                    NumberFormat.getPercentInstance().format(result.get(0).confidence))

    }

}