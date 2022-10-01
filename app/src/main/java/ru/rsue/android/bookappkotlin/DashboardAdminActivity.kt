package ru.rsue.android.bookappkotlin

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.rsue.android.bookappkotlin.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutbtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        binding.addGenreBtn.setOnClickListener{
            startActivity(Intent(this@DashboardAdminActivity, GenreAddActivity::class.java))
        }

    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}