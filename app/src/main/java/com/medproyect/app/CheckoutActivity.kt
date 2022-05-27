package com.medproyect.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.medproyect.app.databinding.ActivityCheckoutBinding
import com.medproyect.app.services.PaypalService
import java.text.DecimalFormat


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var paypalService : PaypalService
    private var methodCode : String? = null
    private var paymentMethod : String? = null
    private lateinit var prefs : SharedPreferences
    private lateinit var editPrefs : SharedPreferences.Editor
    private var total = 0.0


    private val responseMethodLauncher = registerForActivityResult(StartActivityForResult()){activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            paymentMethod = activityResult.data?.getStringExtra("paymentMethodValue").toString()
            methodCode = activityResult.data?.getStringExtra("paymentMethodCode").toString()
            binding.paymentMethodButton.text = paymentMethod
            binding.paymentMethodButton.setTextColor(ContextCompat.getColor(this, R.color.primary))

            //Save payment method in shared preferences
            editPrefs.putString("paymentMethod",paymentMethod)
            editPrefs.putString("methodCode",methodCode)
            editPrefs.apply()
        }
    }


    private val responsePaymentLauncher = registerForActivityResult(StartActivityForResult()){activityResult ->
        val data = activityResult.data
        val status = data?.getStringExtra("status")
        if(activityResult.resultCode == RESULT_OK) {
            val ticketIntent = Intent(this,TicketActivity::class.java)
            startActivity(ticketIntent)
        }else{
            Toast.makeText(this,"Hubo un error al completar el pago",Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("app.preferences", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()

        setup()
    }
    private fun setup(){
        //fields
        val data = intent.getSerializableExtra("data") as HashMap<String,String>
        binding.paymentMethodButton.text = prefs.getString("paymentMethod","escoger metodo")
        binding.tvDirectionName.text = data["direccion"]
        binding.tvService.text = data["serviceName"]
        binding.tvSpecialistName.text = data["nameSpecialist"]
        binding.tvDateTime.text = data["datetime"]
        binding.tvValuePrice.text = "S/. ${formatNumber(data["price"]!!.toDouble())}"
        val fee = (data["price"])!!.toDouble() * 0.08
        binding.tvValueFee.text = "S/. ${formatNumber(fee)}"
        total = data["price"]!!.toDouble() + fee
        binding.tvValueTotal.text = "S./ ${formatNumber( total)}"

        paymentMethod = prefs.getString("paymentMethod",null)
        methodCode = prefs.getString("methodCode",null)

        //click listeners
        binding.paymentMethodButton.setOnClickListener{ openPaymentMethods()}
        binding.btnPay.setOnClickListener{ finishPayment(total,data["appointment"]!!)}
    }
    private fun openPaymentMethods(){
        val intent = Intent(this,PaymentMethodActivity::class.java)
        responseMethodLauncher.launch(intent)
    }
    private fun finishPayment(amount : Double,appointment: String) {
        if(paymentMethod.isNullOrBlank()){
            binding.paymentMethodButton.text = "Debe escoger un metodo de pago"
            binding.paymentMethodButton.setTextColor(ContextCompat.getColor(this, R.color.danger))
        }else{
            when(methodCode) {
                "PAGO_EFECTIVO" -> openPagoEfectivo(amount)
                "CREDIT_CARD" -> openCreditCard(amount)
                "INSTANT" -> openInstant(amount)
                "PAYPAL" -> openPaypalActivity(amount,appointment)
            }
        }
    }
    //Payment Method Activities
    private fun openInstant(amount: Double) {
        TODO("Not yet implemented")
    }

    private fun openCreditCard(amount: Double) {
        TODO("Not yet implemented")
    }

    private fun openPagoEfectivo(amount: Double) {
        TODO("Not yet implemented")
    }

    private fun openPaypalActivity(amount: Double, appointment : String){
        val intent = Intent(this,PaypalActivity::class.java)
        intent.putExtra("amount",amount)
        println(binding.tvDirectionName.text.toString())
        intent.putExtra("direccion", binding.tvDirectionName.text.toString())
        intent.putExtra("appointment",appointment)
        responsePaymentLauncher.launch(intent)
    }
    private fun openTicketActivity(){
        val intent = Intent(this,TicketActivity::class.java)
        startActivity(intent)
    }

    private fun formatNumber(value : Double) : String{
        val pattern = DecimalFormat("#,###.00")
        return pattern.format(value)
    }

}