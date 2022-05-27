package com.medproyect.app

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.medproyect.app.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.content.Intent
import com.medproyect.app.GeneralActivity
import com.google.android.gms.tasks.OnFailureListener
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.medproyect.app.LoginActivity
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    var editTextName: EditText? = null
    var editTextLastName: EditText? = null
    var editTextEmail: EditText? = null
    var editTextMobile: EditText? = null
    var editTextPassword: EditText? = null
    var cirRegisterButton: Button? = null
    var googleButton: TextView? = null
    var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        changeStatusBarColor()
        editTextName = findViewById(R.id.editTextName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextMobile = findViewById(R.id.editTextMobile)
        editTextPassword = findViewById(R.id.editTextPassword)
        cirRegisterButton = findViewById(R.id.cirRegisterButton)
        googleButton = findViewById(R.id.googleButton)
        firebaseAuth = FirebaseAuth.getInstance()
        cirRegisterButton!!.setOnClickListener(View.OnClickListener {
            val correo = editTextEmail!!.getText().toString()
            val pass = editTextPassword!!.getText().toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                editTextEmail!!.setError("Correo no válido")
                editTextEmail!!.setFocusable(true)
            } else if (pass.length < 6) {
                editTextPassword!!.setError("Contraseña debe ser mayor a 6")
                editTextPassword!!.setFocusable(true)
            } else {
                Registrar(correo, pass)
            }
        })
    }

    private fun Registrar(correo: String, pass: String) {
        firebaseAuth!!.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth!!.currentUser!!
                    val uid = user.uid
                    val nombres = editTextName!!.text.toString()
                    val apellidos = editTextLastName!!.text.toString()
                    val correo = editTextEmail!!.text.toString()
                    val telefono = editTextMobile!!.text.toString()
                    val pass = editTextPassword!!.text.toString()
                    val DatosUsuario = HashMap<Any, String>()
                    DatosUsuario["userID"] = uid
                    DatosUsuario["firtsName"] = nombres
                    DatosUsuario["lastName"] = apellidos
                    DatosUsuario["phoneNumber"] = telefono
                    DatosUsuario["correo"] = correo
                    DatosUsuario["address"] = ""
                    DatosUsuario["imagen"] = ""
                    db.collection("profiles").document(uid).set(DatosUsuario)
                    Toast.makeText(this@RegisterActivity, "Se registro correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, GeneralActivity::class.java))
                } else {
                    Toast.makeText(this@RegisterActivity, "Algo ha salido mal", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    //anim
    fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.register_bk_color)
    }

    fun onLoginClick(view: View?) {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}