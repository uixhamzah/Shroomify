package com.shroomify.capstone

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.text.NumberFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Test_activity : AppCompatActivity() {

    private lateinit var imgview2: ImageView
    private val mInputSize = 224
    private val mModelPath = "mushroom_model.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var classifier: Classifier
    companion object {
        private const val CAMERA_PERMISSON_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        imgview2 = findViewById(R.id.imageView2)
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        val editText1: EditText = findViewById(R.id.editTextTextPersonName)
        val editText2: EditText = findViewById(R.id.editTextTextPersonName2)
        val camButton: Button = findViewById(R.id.button1)
        val uploadButton: Button = findViewById(R.id.button2)
        val generateButton: Button = findViewById(R.id.button3)
        camButton.setOnClickListener { it: View? ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSON_CODE
                )
            }
        }

        uploadButton.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent,100)
        }

        generateButton.setOnClickListener {
            var tv: TextView = findViewById(R.id.textView2)
            val bitmap = ((imgview2 as ImageView).drawable as BitmapDrawable).bitmap
            val result = classifier.recognizeImage(bitmap)
            val result1 = result.get(0).title
            val result2 = result.get(0).confidence
            tv.setText("Result : "+result1+"\nConfidence : "+
                    NumberFormat.getPercentInstance().format(result2))

            // Access a Cloud Firestore instance from your Activity
            val db = Firebase.firestore

            // Create a new user with a first, middle, and last name
            val user = hashMapOf(
                "name" to editText1.text.toString(),
                "description" to editText2.text.toString(),
                "result" to result1,
                "confidence" to result2
            )

            // Add a new document with a generated ID
            db.collection("mushrooms")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            db.collection("mushrooms")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }

        }




    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResult: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)
        if (requestCode == CAMERA_PERMISSON_CODE){
            if (grantResult.isNotEmpty() && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission camera." +
                            "Don't worry you can allow it in the settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA_REQUEST_CODE){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                imgview2.setImageBitmap(thumbnail)
            }else {
                imgview2.setImageURI(data?.data)

                var uri: Uri?= data?.data
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                imgview2.setImageBitmap(bitmap)
            }
        }

    }
}