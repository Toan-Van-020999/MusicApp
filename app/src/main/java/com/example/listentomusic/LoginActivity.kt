package com.example.listentomusic

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.listentomusic.model.User
import com.google.android.material.textfield.TextInputEditText
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.util.*

class LoginActivity : AppCompatActivity() {
    var edtUserName : TextInputEditText? = null
    var edtPassWord : TextInputEditText? = null
    var btnLogin : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        init()
        btnLogin?.setOnClickListener {
            AsyncCallWS().execute()
        }


    }

    inner class AsyncCallWS : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            checkLogin()
            return null
        }

        private fun checkLogin() {
            val SOAP_ACTION = ""
            val NAMESPACE = "http://services/"
            val URL = "http://192.168.1.188:8080/MyService/MusicService"
            val METHOD_NAME = "checkLogin";
            try {
                val request = SoapObject(NAMESPACE, METHOD_NAME)
//                Request.addProperty("id",1)
                val username = edtUserName?.text.toString()
                val password = edtPassWord?.text.toString()

//                val user = User()
//
//                request.addSoapObject()
                val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                soapEnvelope.setOutputSoapObject(request)

                val transport = HttpTransportSE(URL)
                transport.call(SOAP_ACTION, soapEnvelope)
                val result = soapEnvelope.response as SoapObject


            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }
        }
    }

    private fun init(){
        edtUserName = findViewById(R.id.txtUsername)
        edtPassWord = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }
}