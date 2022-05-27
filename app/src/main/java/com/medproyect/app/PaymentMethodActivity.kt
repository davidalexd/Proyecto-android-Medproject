package com.medproyect.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import com.medproyect.app.databinding.ActivityCheckoutBinding
import com.medproyect.app.databinding.ActivityPaymentMethodBinding

enum class PaymentMethod(val str : String){
    PAGO_EFECTIVO("Pago en Efectivo"),
    CREDIT_CARD("Tarjeta de Credito"),
    INSTANT("Pago al instante"),
    PAYPAL("Pago en Paypal")
}

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private var paymentMethod: PaymentMethod? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChoosePayment.setOnClickListener { savePaymentMethod() }
    }

    fun onRadioButtonClicked(view : View){
        resetColor()
        if(view is RadioButton){
            val checked = view.isChecked
            if(checked){
                when (view.getId()) {
                    R.id.pagoEfectivo -> {
                        paymentMethod = PaymentMethod.PAGO_EFECTIVO
                        binding.pagoEfectivo.setTextColor(Color.WHITE)
                    }
                    R.id.creditCard -> {
                        paymentMethod = PaymentMethod.CREDIT_CARD
                        binding.creditCard.setTextColor(Color.WHITE)
                    }
                    R.id.instant -> {
                        paymentMethod = PaymentMethod.INSTANT
                        binding.instant.setTextColor(Color.WHITE)
                    }
                    R.id.paypal -> {
                        paymentMethod = PaymentMethod.PAYPAL
                        binding.paypal.setTextColor(Color.WHITE)
                    }
                }
            }
        }
    }

    private fun resetColor() {
        binding.pagoEfectivo.setTextColor(Color.BLACK)
        binding.creditCard.setTextColor(Color.BLACK)
        binding.instant.setTextColor(Color.BLACK)
        binding.paypal.setTextColor(Color.BLACK)
    }

    private fun savePaymentMethod(){
        val intent = Intent(this,CheckoutActivity::class.java)
        intent.putExtra("paymentMethodCode", paymentMethod.toString())
        intent.putExtra("paymentMethodValue", paymentMethod?.str.toString())
        setResult(RESULT_OK, intent)
        finish()
    }
}