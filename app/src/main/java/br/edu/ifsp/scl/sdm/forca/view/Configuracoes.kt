package br.edu.ifsp.scl.sdm.forca.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import br.edu.ifsp.scl.sdm.forca.R
import br.edu.ifsp.scl.sdm.forca.databinding.ActivityConfiguracoesBinding
import br.edu.ifsp.scl.sdm.forca.viewModel.ForcaViewModel

class Configuracoes : AppCompatActivity() {

    private val activityConfiguracoesBinding: ActivityConfiguracoesBinding by lazy {
        ActivityConfiguracoesBinding.inflate(layoutInflater)
    }

    private lateinit var forcaViewModel: ForcaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityConfiguracoesBinding.root)

        forcaViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(ForcaViewModel::class.java)

        getInitialValues()

        activityConfiguracoesBinding.nivelSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                forcaViewModel.setDifficulty(value)
                runOnUiThread {
                    activityConfiguracoesBinding.configuracoesTv.text = "Dificuldade: ${value}"
                    activityConfiguracoesBinding.nivelSb.progress = value
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //
            }
        })

        activityConfiguracoesBinding.rodadasSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                forcaViewModel.setTotalRounds(value)
                runOnUiThread {
                    activityConfiguracoesBinding.rodadasLabelTv.text = "Rodadas: ${value}"
                    activityConfiguracoesBinding.rodadasSb.progress = value
                }
            }


            override fun onStartTrackingTouch(p0: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //
            }
        })

        activityConfiguracoesBinding.fecharBt.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    fun getInitialValues() {
        val initialDifficulty = forcaViewModel.getDifficulty()
        val initialRounds = forcaViewModel.getRounds()

        activityConfiguracoesBinding.rodadasLabelTv.text = "Rodadas: ${initialRounds}"
        activityConfiguracoesBinding.rodadasSb.progress = initialRounds!!

        activityConfiguracoesBinding.configuracoesTv.text = "Dificuldade: ${initialDifficulty}"
        activityConfiguracoesBinding.nivelSb.progress = initialDifficulty!!

    }


}