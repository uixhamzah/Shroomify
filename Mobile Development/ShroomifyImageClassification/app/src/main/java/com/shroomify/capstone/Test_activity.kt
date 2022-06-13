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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.File

class Test_activity : AppCompatActivity() {

    private lateinit var imgview2: ImageView
    private val mInputSize = 224
    private val mModelPath = "mushroom_model.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var classifier: Classifier
    // Get a non-default Storage bucket
    private val storage = Firebase.storage("gs://molten-muse-352811.appspot.com")
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
            val bitmap = (imgview2.drawable as BitmapDrawable).bitmap
            val result = classifier.recognizeImage(bitmap)
            val result1 = result.get(0).title
            val result2 = result.get(0).confidence
            tv.setText("Result : "+result1+"\nConfidence : "+
                    NumberFormat.getPercentInstance().format(result2))

            var mushroom_name = editText1.text.toString()
            val mushroom_file = mushroom_name.replace("\\s".toRegex(), "-")
            var mushroom_desc = editText2.text.toString()
            val storageRef = storage.reference
            // Get the data from an ImageView as bytes
            imgview2.isDrawingCacheEnabled = true
            imgview2.buildDrawingCache()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            // Create the file metadata
            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            val file = Uri.fromFile(File("path/to/"+ mushroom_file +".jpg"))
            val mountainsRef = storageRef.child("images/${file.lastPathSegment}")
            var uploadTask = mountainsRef.putBytes(data, metadata)

        // Listen for state changes, errors, and completion of the upload.
        // You'll need to import com.google.firebase.storage.ktx.component1 and
        // com.google.firebase.storage.ktx.component2
            uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                val progress = (100.0 * bytesTransferred) / totalByteCount
                Log.d(TAG, "Upload is $progress% done")
            }.addOnPausedListener {
                Log.d(TAG, "Upload is paused")
            }.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // Handle successful uploads on complete
                // ...
            }

            // Access a Cloud Firestore instance from your Activity
            val db = Firebase.firestore

            // Create a new user with a first, middle, and last name
            val create = hashMapOf(
                "name" to mushroom_name,
                "description" to mushroom_desc,
                "result" to result1,
                "confidence" to result2,
                "file path" to mountainsRef.toString(),
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Add a new document with a generated ID
            db.collection("mushrooms")
                .add(create)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
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