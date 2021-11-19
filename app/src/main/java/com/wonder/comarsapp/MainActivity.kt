package com.wonder.comarsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.wonder.comarsapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQRBascula.setOnClickListener {
            initScanner()
        }
    }

    private fun obtenerSector(nroRemito: Int) {
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MainActivity", "haciendo consulta a: $nroRemito")
            val call = getRetrofit().create(APIService::class.java).getRemito(nroRemito.toString())
            val remito = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    // show recyclerview
                    showResult(remito)
                } else {
                    showError("algo")
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun initScanner(){
        // para que limpie la pantalla
        showResult(null)

        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el QR para obtener el sector.")
        // integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
                binding.tvResult.text = result.contents
                try {
                    //obtenerSector(result.contents.toInt())
                    val gson = Gson()
                    val remito = gson.fromJson(result.contents, RemitoResponse::class.java)
                    showResult(remito)
                } catch (e: Exception) {
                    showError("no es un entero: ${result.contents}")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showResult(remito: RemitoResponse?) {
        if (remito != null) {
            binding.tvResult.text = "{\n${remito.puntoVenta},\n${remito.nroRemito},\n${remito.sector},\n" +
                    "${remito.descargado}\n}"
        } else {
            binding.tvResult.text = ""
        }
    }

    private fun showError(error: String) {
        binding.tvResult.text = "Error: $error"
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.20:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}