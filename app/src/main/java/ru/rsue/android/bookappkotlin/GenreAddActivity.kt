package ru.rsue.android.bookappkotlin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ru.rsue.android.bookappkotlin.databinding.ActivityGenreAddBinding

class GenreAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenreAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait ...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener{
            validateData()
        }



    }

    private var genre = ""

    private fun validateData() {
        genre = binding.genreEt.text.toString().trim()

        if (genre.isEmpty()){
            Toast.makeText(this, "Add genre", Toast.LENGTH_SHORT).show()
        }else{
            addGenreFirebase()
        }
    }

    private fun addGenreFirebase() {
        progressDialog.show()

        var timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["genre"] = genre
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Genres")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Genre added", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Erorr occured: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}