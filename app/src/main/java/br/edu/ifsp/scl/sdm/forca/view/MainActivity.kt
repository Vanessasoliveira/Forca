package br.edu.ifsp.scl.sdm.forca.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.scl.sdm.forca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.jogarBt.setOnClickListener {
            val jogoIntent: Intent = Intent(this, Jogo::class.java)
            startActivity(jogoIntent)
        }

        activityMainBinding.configuracaoBt.setOnClickListener {
            val intent: Intent = Intent(this, Configuracoes::class.java)
            startActivity(intent)
        }
    }
}