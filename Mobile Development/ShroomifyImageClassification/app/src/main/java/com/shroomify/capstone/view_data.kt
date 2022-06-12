package com.shroomify.capstone

import android.R.layout.simple_list_item_1
import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class view_data : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_data)

        val list_db:ListView =findViewById(R.id.data_list)
        val db = Firebase.firestore
        val list : ArrayList<String> = ArrayList()
        val img_list : ArrayList<String> = ArrayList()
        db.collection("mushrooms")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    list.add("Name        : ${document.getString("name")}\n"+
                             "Description : ${document.getString("description")}\n"+
                             "Result      : ${document.getString("result")}\n"+
                             "Confidence  : ${NumberFormat.getPercentInstance().format(document.get("confidence"))}\n"+
                             "Timestamp   : ${document.getTimestamp("timestamp")!!.toDate()}")
                    img_list.add("${document.getString("file path")}")
                    list_db.adapter = ArrayAdapter(this, simple_list_item_1, list)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        list_db.setOnItemClickListener { parent, view, position, id ->
            val bundle = Bundle()
            bundle.putString("name", list[position])
            bundle.putString("file path", img_list[position])
            val inten = Intent(this,view_field::class.java)
            inten.putExtras(bundle)
            this.startActivity(inten)
        }
    }
}