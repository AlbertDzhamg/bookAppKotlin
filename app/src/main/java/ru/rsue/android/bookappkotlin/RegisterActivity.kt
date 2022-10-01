package ru.rsue.android.bookappkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ru.rsue.android.bookappkotlin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //viewbinding
    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    //данные для регистрации
    private var name = ""
    private var email = ""
    private var password = ""
    private var cPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //инициализируем firebase
        firebaseAuth = FirebaseAuth.getInstance()
        //инициализируем dialog, который появится при создании аккаунта в acitivity registeruser
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Authentication in progress...")
        //метод для того чтобы пользователь не мог отменить progressDialog, кликнув вне диалогового окна
        progressDialog.setCanceledOnTouchOutside(false)
        //хэндлер для смены acitivity при нажатии на кнопку back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //хендлер для нажатия на кнопку регистрации
        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }


    //Функции для корректной валидации пароля
    fun String.isLongEnough() = length >= 8
    fun String.hasEnoughDigits() = count(Char::isDigit) > 0
    fun String.isMixedCase() = any(Char::isLowerCase) && any(Char::isUpperCase)
    fun String.hasSpecialChar() = any { it in "!,+^" }

    private fun validateData() {
        name = binding.nameEt.text.toString().trim() // приведение к строке с удалением первых и последних пробелов
        email = binding.emailEt.text.toString().trim() //.text позволяет получить значения из textview
        password = binding.passwordEt.text.toString().trim()
        cPassword = binding.passwordconfEt.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(this, "Enter name to continue", Toast.LENGTH_LONG).show()
            //для инициализация объекта toast, нужно вызвать метод makeText, и вызвать метод (toast)
            //с помощью show(), length_short - показывает уведомление на короткий промежуток времени
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show()
            //проверка на валидность формата введнной почты, с помощью встроенной опции Patterns
            //и конкретного паттерна EMAIL_ADDRESS
        } else if(password.isEmpty()){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        } else if(cPassword.isEmpty()){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        } else if(password != cPassword){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        //создаем окно с индикатором прогресса, и сообщением о то что происходит создание аккаунта
        progressDialog.setMessage("Creating in process")
        progressDialog.show() //используем метод для вызова модального окна

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                //заводим два listener на случай успешного создания аккаунта или неудачи
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener{e ->
                //закрывает окно с индикатром прогресса и показывает сообщение о неудаче c конкретной ошибкой
                 progressDialog.dismiss()
                Toast.makeText(this, "Account creation failed. Erorr: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user's data")
        val timestamp = System.currentTimeMillis() // сохраняем время регистрации
        val uid = firebaseAuth.uid// получаем userid после регистрации
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] =  name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //Добавляем данные в БД
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!) //преобразование uid к типу без поддержки null, пытаемся избежать NPE
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
                //дальше просто запускаем dashboard
            }
            .addOnFailureListener{ e -> //ловим exception который выведем на экран
                progressDialog.dismiss()
                Toast.makeText(this, "Something went wrong ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}