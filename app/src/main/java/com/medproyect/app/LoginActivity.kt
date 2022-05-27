package com.medproyect.app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    //Google Sing
    private val GOOGLE_SIGN_IN = 100
    private lateinit var googleSignInClient: GoogleSignInClient

    //progres Dialog
    private lateinit var progresDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Data
    private var address = ""
    private var pass = ""

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //progres dailog
        progresDialog = ProgressDialog(this)
        progresDialog.setTitle("Espera por favor")
        progresDialog.setMessage("Logeando...")
        progresDialog.setCanceledOnTouchOutside(false)

        //iniciar firebase
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        binding.signUpButton.setOnClickListener{
            validateData()
        }
        googleSignUp()


    }

    private fun validateData() {
        address = binding.email.text.toString().trim()
        pass = binding.password.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(address).matches()){
            binding.email.error = "Formato invalido"
        }
        else if (TextUtils.isEmpty(pass)){
            binding.password.error = "Porfavor ingrese su contraseÃ±a"
        }
        else{
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        progresDialog.show()
        firebaseAuth.signInWithEmailAndPassword(address, pass)
            .addOnSuccessListener {
                progresDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Te logeaste con $email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, GeneralActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                progresDialog.dismiss()
                Toast.makeText(this, "Login error por ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun googleSignUp(){
        binding.googleButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("LoginActivity", "firebaseAuthGoogle:" + account.id)
                    firebaseAuthGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("LoginActivity", "Google sing in failed",e)
                }
            }else{
                Log.w("LoginActivity", exception.toString())
            }
        }
    }

    private fun firebaseAuthGoogle(idToken : String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Log.d("LoginActivity", "Success")
                    val user = firebaseAuth.currentUser!!
                    val uid = user!!.uid
                    val nombres= user?.displayName.toString()
                    val correo= user?.email.toString()
                    val image = user?.photoUrl.toString()
                    val DatosUsuario = HashMap<Any, String>()
                    DatosUsuario["userID"] = uid
                    DatosUsuario["firtsName"] = nombres
                    DatosUsuario["lastName"] = ""
                    DatosUsuario["correo"] = correo
                    DatosUsuario["phoneNumber"] = ""
                    DatosUsuario["address"] = ""
                    DatosUsuario["imagen"] = image

                    val docProfile = db.collection("profiles").document(uid)
                    docProfile.get()
                        .addOnSuccessListener { document ->
                            if (document.data != null) {
                                Toast.makeText(this, "Accedio correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                db.collection("profiles").document(uid).set(DatosUsuario)
                                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }
                    startActivity(Intent(this, GeneralActivity::class.java))
                    val intent = Intent(this, GeneralActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Log.w("LoginActivity", "fallo", task.exception)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkUser() {
        val firebaseAuth = firebaseAuth.currentUser
        if(firebaseAuth !=null){
            startActivity(Intent(this, GeneralActivity::class.java))
            finish()
        }

    }

    fun onLoginClick(view: View?) {
        startActivity(Intent(this, RegisterActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
    }
}