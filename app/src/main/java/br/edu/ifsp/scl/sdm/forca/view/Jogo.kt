package br.edu.ifsp.scl.sdm.forca.view

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import br.edu.ifsp.scl.sdm.forca.R
import br.edu.ifsp.scl.sdm.forca.databinding.ActivityJogoBinding
import br.edu.ifsp.scl.sdm.forca.viewModel.ForcaViewModel
import java.text.Normalizer

class Jogo : AppCompatActivity() {

    private val activityJogoBinding: ActivityJogoBinding by lazy {
        ActivityJogoBinding.inflate(layoutInflater)
    }

    private var keyboardEnabled = false
    private var word: String = ""
    private var ganhouRound = false

    private lateinit var configurarActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var forcaViewModel: ForcaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityJogoBinding.root)

        forcaViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(ForcaViewModel::class.java)

        addKeyboardListeners()

        activityJogoBinding.proximaBt.setOnClickListener {
            forcaViewModel.finishRound(ganhouRound)
        }

        startGame()
        observeWord()
        observeIdentifiers()
        getTotalRounds()
        observeCurrentRound()
        observeAttempts()
        observeGameEnded()
    }

    fun startGame() {
        forcaViewModel.startGame()
    }

    fun CharSequence.unaccent(): String {
        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    fun guess(key: String) {
        forcaViewModel.guess(key)
        val stringBuilder = StringBuilder()
        stringBuilder.append(activityJogoBinding.letrasTv.text)
        if (activityJogoBinding.letrasTv.text.length > 0) {
            stringBuilder.append(" - ")
        }
        stringBuilder.append(key)



        if (word.uppercase().contains(key.uppercase())) {
            Toast.makeText(this, "Alternativa correta", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Alternativa incorreta", Toast.LENGTH_SHORT).show()
        }

        for (index in 0 until word.length) {
            var guessingWord = activityJogoBinding.palavraTv.text.toString()
            if (word[index].toString().unaccent().uppercase() == key.uppercase()) {
                var before = " "
                var after = ""
                if (index > 0) {
                    before = guessingWord.substring(0, index * 2 + 1)
                }
                if (index < word.length - 1) {
                    after = guessingWord.substring((index + 1) * 2)
                }

                activityJogoBinding.palavraTv.text = "${before}${key}${after}"
                if (!activityJogoBinding.palavraTv.text.contains("_")) {
                    activityJogoBinding.botoesLay.visibility = View.VISIBLE
                    activityJogoBinding.tecladoCl.visibility = View.GONE
                    ganhouRound = true
                    Toast.makeText(this, "Você ganhou esta rodada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        activityJogoBinding.letrasTv.text = stringBuilder.toString()
    }

    fun observeWord() {
        forcaViewModel.wordMld.observe(this) { updatedWord ->
            keyboardEnabled = true
            word = updatedWord.palavras
            val stringBuilder = StringBuilder()

            for (index in 0 until updatedWord.letra) {
                stringBuilder.append(" _")
            }
            runOnUiThread {
                activityJogoBinding.palavraTv.text = stringBuilder.toString()
                activityJogoBinding.letrasTv.text = ""
                activityJogoBinding.botoesLay.visibility = View.GONE
                activityJogoBinding.tecladoCl.visibility = View.VISIBLE
                ganhouRound = false
                enabledAllKeyboardKeys()
            }
        }
    }

    fun observeIdentifiers() {
        forcaViewModel.identifiersMld.observe(this) { identifiers ->
            forcaViewModel.generateRoundIdentifiers()
            forcaViewModel.nextRound()
        }
    }

    fun getTotalRounds() {
        val total = forcaViewModel.getRounds()
        activityJogoBinding.totalRodadasTv.text = total.toString()
    }

    fun observeCurrentRound() {
        forcaViewModel.currentRoundMdl.observe(this) { currentRound ->
            runOnUiThread {
                activityJogoBinding.rodadaAtualTv.text = "Rodada $currentRound de "
                val total = activityJogoBinding.totalRodadasTv.text.toString().toInt()
                if (currentRound < total) {
                    activityJogoBinding.proximaBt.text = "Próxima rodada"
                } else {
                    activityJogoBinding.proximaBt.text = "Ver resultados"
                }
            }
        }
    }

    /* fun observeCurrentLevel() {
         val nivel = forcaViewModel.getDifficulty()
         activityJogoBinding.nivelAtualTv.text = nivel.toString()

     }*/

    fun observeAttempts() {
        forcaViewModel.attemptsMdl.observe(this) { attempts ->
            updateAttempts(attempts)
        }
    }

    fun updateAttempts(remainingAttempts: Int) {

        activityJogoBinding.cabecaTv.paintFlags = 0
        activityJogoBinding.troncoTv.paintFlags = 0
        activityJogoBinding.bracoDireitoTv.paintFlags = 0
        activityJogoBinding.bracoEsquerdoTv.paintFlags = 0
        activityJogoBinding.pernaDireitaTv.paintFlags = 0
        activityJogoBinding.pernaEsquerdaTv.paintFlags = 0

        if (remainingAttempts < 6) {
            activityJogoBinding.cabecaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 5) {
            activityJogoBinding.troncoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 4) {
            activityJogoBinding.bracoDireitoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 3) {
            activityJogoBinding.bracoEsquerdoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 2) {
            activityJogoBinding.pernaDireitaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 1) {
            activityJogoBinding.pernaEsquerdaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            activityJogoBinding.botoesLay.visibility = View.VISIBLE
            activityJogoBinding.tecladoCl.visibility = View.GONE
            Toast.makeText(this, "Você perdeu esta rodada", Toast.LENGTH_SHORT).show()
        }
    }

    fun addKeyboardListeners() {
        with(activityJogoBinding) {
            letraABt.setOnClickListener { pressKey("A") }
            letraBBt.setOnClickListener { pressKey("B") }
            letraCBt.setOnClickListener { pressKey("C") }
            letraDBt.setOnClickListener { pressKey("D") }
            letraEBt.setOnClickListener { pressKey("E") }
            letraFBt.setOnClickListener { pressKey("F") }
            letraGBt.setOnClickListener { pressKey("G") }
            letraHBt.setOnClickListener { pressKey("H") }
            letraIBt.setOnClickListener { pressKey("I") }
            letraJBt.setOnClickListener { pressKey("J") }
            letraKBt.setOnClickListener { pressKey("K") }
            letraLBt.setOnClickListener { pressKey("L") }
            letraMBt.setOnClickListener { pressKey("M") }
            letraNBt.setOnClickListener { pressKey("N") }
            letraOBt.setOnClickListener { pressKey("O") }
            letraPBt.setOnClickListener { pressKey("P") }
            letraQBt.setOnClickListener { pressKey("Q") }
            letraRBt.setOnClickListener { pressKey("R") }
            letraSBt.setOnClickListener { pressKey("S") }
            letraTBt.setOnClickListener { pressKey("T") }
            letraUBt.setOnClickListener { pressKey("U") }
            letraVBt.setOnClickListener { pressKey("V") }
            letraWBt.setOnClickListener { pressKey("W") }
            letraXBt.setOnClickListener { pressKey("X") }
            letraYBt.setOnClickListener { pressKey("Y") }
            letraZBt.setOnClickListener { pressKey("Z") }
        }
    }

    fun enabledAllKeyboardKeys() {
        with(activityJogoBinding) {
            letraABt.isEnabled = true
            letraBBt.isEnabled = true
            letraCBt.isEnabled = true
            letraDBt.isEnabled = true
            letraEBt.isEnabled = true
            letraFBt.isEnabled = true
            letraGBt.isEnabled = true
            letraHBt.isEnabled = true
            letraIBt.isEnabled = true
            letraJBt.isEnabled = true
            letraKBt.isEnabled = true
            letraLBt.isEnabled = true
            letraMBt.isEnabled = true
            letraNBt.isEnabled = true
            letraOBt.isEnabled = true
            letraPBt.isEnabled = true
            letraQBt.isEnabled = true
            letraRBt.isEnabled = true
            letraSBt.isEnabled = true
            letraTBt.isEnabled = true
            letraUBt.isEnabled = true
            letraVBt.isEnabled = true
            letraWBt.isEnabled = true
            letraXBt.isEnabled = true
            letraYBt.isEnabled = true
            letraZBt.isEnabled = true
        }
    }

    fun disableKey(key: String) {
        with(activityJogoBinding) {
            when (key) {
                "A" -> letraABt.isEnabled = false
                "B" -> letraBBt.isEnabled = false
                "C" -> letraCBt.isEnabled = false
                "D" -> letraDBt.isEnabled = false
                "E" -> letraEBt.isEnabled = false
                "F" -> letraFBt.isEnabled = false
                "G" -> letraGBt.isEnabled = false
                "H" -> letraHBt.isEnabled = false
                "I" -> letraIBt.isEnabled = false
                "J" -> letraJBt.isEnabled = false
                "K" -> letraKBt.isEnabled = false
                "L" -> letraLBt.isEnabled = false
                "M" -> letraMBt.isEnabled = false
                "N" -> letraNBt.isEnabled = false
                "O" -> letraOBt.isEnabled = false
                "P" -> letraPBt.isEnabled = false
                "Q" -> letraQBt.isEnabled = false
                "R" -> letraRBt.isEnabled = false
                "S" -> letraSBt.isEnabled = false
                "T" -> letraTBt.isEnabled = false
                "U" -> letraUBt.isEnabled = false
                "V" -> letraVBt.isEnabled = false
                "W" -> letraWBt.isEnabled = false
                "X" -> letraXBt.isEnabled = false
                "Y" -> letraYBt.isEnabled = false
                "Z" -> letraZBt.isEnabled = false
            }
        }
    }

    fun pressKey(key: String) {
        if (keyboardEnabled) {
            disableKey(key)
            guess(key)
        }
    }

    fun observeGameEnded() {
        forcaViewModel.gameEndedMdl.observe(this) { gameEnded ->
            if (gameEnded) {
                val intent = Intent(this, MainResultado::class.java)
                val wrongAnswers = forcaViewModel.getWrongAnswers()
                val correctAnswers = forcaViewModel.getCorrectAnswers()

                intent.putStringArrayListExtra("correctAnswers", ArrayList(correctAnswers))
                intent.putStringArrayListExtra("wrongAnswers", ArrayList(wrongAnswers))
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_jogo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.configurarJogo -> {
                val intentConfigurar = Intent(this, Configuracoes::class.java)
                configurarActivityResultLauncher.launch(intentConfigurar)
                true
            }
            else -> false
        }
    }


}