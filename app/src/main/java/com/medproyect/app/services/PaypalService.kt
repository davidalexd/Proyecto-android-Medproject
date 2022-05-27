package com.medproyect.app.services

import android.content.Context
import android.util.Base64
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.collections.HashMap


class PaypalService{
    private var url = "https://api-m.sandbox.paypal.com/v2/"
    private var clientId ="AduFyAvW43yZpWSAsGlOojLnq-7em_QE5F9tDmOSHoAAc-65qkmPULwx7JpFR2BhVzDUooci3Xd4BcQB"
    private var secretKey = "EMkVD8vREPWwfpl1OcbsznwLKZAwQt1XsSte_KqrdlLZYXFm2lvTKWBQozfRzSFx32yZo0yiHF-rF9Rl"
    private var context : Context
    private var queue : RequestQueue
    private lateinit var response: JSONObject
    constructor(context: Context){
        this.context = context
        this.queue = Volley.newRequestQueue(context)
    }

    private fun headers(): Map<String,String>{
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = getBasicAuthorization()
        return headers
    }
    private fun getBasicAuthorization() : String{
        val credential = String.format("%s:%s",clientId,secretKey).toByteArray()
        val encoded = Base64.encode(credential,Base64.DEFAULT).toString()
        return encoded
    }
    public fun orderRequestBody(amount : Int) : JSONObject{
        val bodyParams = JSONObject(String.format("""{
                "intent" : "CAPTURE",
                "purchase_units" : [
                    {
                        "reference_id" : "default",
                        "amount" : {
                            "currency_code" : "USD",
                            "value" : "%d"
                        }
                    }
                ],
                "application_context" : {
                    "return_url" : "",
                    "cancel_url" : ""
                }
            }""",amount).trimIndent())
        return bodyParams
    }
    fun makeRequest(params : JSONObject, method : Int):JSONObject{
        val request = object : JsonObjectRequest(
                method,
                url,
                params,
                Response.Listener { response -> getResponse(response)},
                Response.ErrorListener { error -> responseError(error)}
        )
        {
            override fun getHeaders() : Map<String,String> {
                return headers()
            }
        }
        queue.add(request)
        return this.response

    }
    private fun getResponse(jsonResponse : JSONObject){
        this.response =jsonResponse
    }
    private fun responseError(error : VolleyError){
        Log.e("Error",error.toString())
    }

}