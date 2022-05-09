package br.edu.ifsp.scl.sdm.forca.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.scl.sdm.forca.R
import br.edu.ifsp.scl.sdm.forca.databinding.ActivityMainResultadoBinding

class MainResultado : AppCompatActivity() {

    private val activityMainResultadoBinding: ActivityMainResultadoBinding by lazy {
        ActivityMainResultadoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainResultadoBinding.root)

        val correctAnswers = intent.getStringArrayListExtra("correctAnswers")
        val wrongAnswers = intent.getStringArrayListExtra("wrongAnswers")

        val acertosStringBuffer = StringBuffer()
        for (index in 0 until (correctAnswers?.size ?: 0)) {
            acertosStringBuffer.append("- " + (correctAnswers?.get(index) ?: "") + "\n")
        }

        val errosStringBuffer = StringBuffer()
        for (index in 0 until (wrongAnswers?.size ?: 0)) {
            errosStringBuffer.append("- " + (wrongAnswers?.get(index) ?: "") + "\n")
        }

        activityMainResultadoBinding.acertosLabelTv.text = "${correctAnswers?.size} acertos:"
        activityMainResultadoBinding.errosLabelTv.text = "${wrongAnswers?.size} erros:"
        activityMainResultadoBinding.totalPalavrasTv.text =
            "${(correctAnswers?.size ?: 0) + (wrongAnswers?.size ?: 0)} palavras"
        activityMainResultadoBinding.acertosTv.text = acertosStringBuffer.toString()
        activityMainResultadoBinding.errosTv.text = errosStringBuffer.toString()
        activityMainResultadoBinding.voltarBt.setOnClickListener {
            finish()
        }
        activityMainResultadoBinding.proximaRodadaBt.setOnClickListener {
            val intent = Intent(this, Jogo::class.java)
            startActivity(intent)
            finish()
        }

    }
}