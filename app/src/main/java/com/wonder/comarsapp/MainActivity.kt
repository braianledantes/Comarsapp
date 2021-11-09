package com.wonder.comarsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wonder.comarsapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
            obtenerSector()
        }
    }

    private fun obtenerSector() {
        GlobalScope.launch {
            val call = getRetrofit().create(APIService::class.java).getRemito("88888")
            val remito = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    // show recyclerview
                    showResult(remito)
                } else {
                    showError("algo")
                }
            }
        }
    }

    private fun showResult(remito: RemitoResponse?) {
        remito?.let {
            binding.tvResult.text = remito.nroRemito.toString()
        }
    }

    private fun showError(error: String) {
        binding.tvResult.text = "Error: $error"
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("192.168.0.20:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}