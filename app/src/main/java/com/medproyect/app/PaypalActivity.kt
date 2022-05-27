package com.medproyect.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.medproyect.app.databinding.ActivityPaypalBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONObject
import java.sql.Time
import kotlin.system.measureTimeMillis

const val url = "https://api-m.sandbox.paypal.com/"
class PaypalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaypalBinding
    private var mCustomTabsSession: CustomTabsSession? = null
    private lateinit var prefs : SharedPreferences
    private lateinit var editPrefs : SharedPreferences.Editor
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences("app.preferences", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()
        setup()
    }
    private fun setup(){
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            when(uri!!.getQueryParameter("type")){
                "capture" -> capturePayment()
                "cancel" -> cancelPayment()
            }
        }else{
            val amount = intent.getDoubleExtra("amount",0.0)
            createPayment(amount)
        }
    }
    private fun createPayment(amount : Double){
        val bodyParams = JSONObject(String.format("""{
                "intent" : "CAPTURE",
                "purchase_units" : [
                    {
                        "amount" : {
                            "currency_code" : "USD",
                            "value" : "%.1f"
                        }
                    }
                 ],
                "application_context" : {
                    "return_url" : "app://paypal-activity?type=capture",
                    "cancel_url" : "app://paypal-activity?type=cancel"
                }
            }""",amount).trimIndent())

        fun response(resp : JSONObject){
            editPrefs.putString("idPayment",resp.getString("id"))
            editPrefs.putString("appointment",intent.getStringExtra("appointment"))
            editPrefs.putString("direccion",intent.getStringExtra("direccion"))
            editPrefs.apply()
            val links = resp.getJSONArray("links")
            val href = links.getJSONObject(1).getString("href")
            goToUrl(href)
        }
        executePaypalRequest(bodyParams,"v2/checkout/orders",::response)
    }

    private fun capturePayment(){
        val idPayment = prefs.getString("idPayment",null)
        val appointment = prefs.getString("appointment",null)
        val direccion = prefs.getString("direccion",null)
        fun response(resp : JSONObject){
            val user = Firebase.auth.currentUser
            db.collection("appointments")
                .document(appointment.toString())
                .get()
                .addOnSuccessListener { appointment ->
                    val userAppointment = hashMapOf(
                        "user_id" to user?.uid,
                        "payment_id" to idPayment,
                        "appointment_id" to appointment.id,
                        "payment_date" to Timestamp.now(),
                        "appointment_date" to appointment["itemDate"] as Timestamp,
                        "direccion" to direccion
                    )
                    db.collection("user_appointments")
                        .add(userAppointment)
                        .addOnSuccessListener { doc ->
                            appointment.reference.update("isAvailable",false)
                            Toast.makeText(this, "Se guardo",Toast.LENGTH_SHORT).show()
                            val ticketIntent = Intent(Intent.ACTION_VIEW,Uri.parse("app://ticket-activity?id=${doc.id}"))
                            startActivity(ticketIntent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al ejecuta $e",Toast.LENGTH_SHORT).show()
                        }
                }
        }

        executePaypalRequest(bodyParams = null,"v2/checkout/orders/$idPayment/capture",::response)
    }
    private fun executePaypalRequest(bodyParams: JSONObject? = null, endPoint: String,closure:(resp : JSONObject) -> Unit){
        val queue = Volley.newRequestQueue(this)
        val urlRequest = url + endPoint
        val requestToRegisterUser = object : JsonObjectRequest(Method.POST,urlRequest, bodyParams,
            Response.Listener { response -> closure(response)},
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al ejecutar paypal $error",Toast.LENGTH_SHORT).show()
            })
        {
            override fun getHeaders() : Map<String,String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Basic QWR1RnlBdlc0M3lacFdTQXNHbE9vakxucS03ZW1fUUU1Rjl0RG1PU0hvQUFjLTY1cWttUFVMd3g3SnBGUjJCaFZ6RFVvb2NpM1hkNEJjUUI6RU1rVkQ4dlJFUFd3ZnBsMU9jYnN6bndMS1pBd1F0MVhzU3RlX0txcmRsTFpZWEZtMmx2VEtXQlFvemZSelNGeDMyeVpvMHlpSEYtckY5Umw="
                return headers
            }
        }
        queue.add(requestToRegisterUser)
    }
    private fun cancelPayment(){
        binding.btnTryAgain.isEnabled = true
        Toast.makeText(this,"Cancelastes la compra con Paypal o hubo un error",Toast.LENGTH_SHORT).show()
    }
    private fun goToUrl(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder(mCustomTabsSession)
            .setToolbarColor(ContextCompat.getColor(this,R.color.primary))
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}