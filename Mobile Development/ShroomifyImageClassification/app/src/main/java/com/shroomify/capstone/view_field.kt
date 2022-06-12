package com.shroomify.capstone

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class view_field : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_field)

        val storage = Firebase.storage

        var bundle = intent.extras
        // Reference to an image file in Cloud Storage
        val imgview: ImageView = findViewById(R.id.imageView3)
        val tv3: TextView = findViewById(R.id.textView3)
        val gsReference = storage.getReferenceFromUrl("${bundle!!.getString("file path")}")
        val localfile = File.createTempFile("tempImage","jpg")
        gsReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imgview.setImageBitmap(bitmap)
        }

        var name = bundle!!.getString("name")
        tv3.setText(name)
    }
}