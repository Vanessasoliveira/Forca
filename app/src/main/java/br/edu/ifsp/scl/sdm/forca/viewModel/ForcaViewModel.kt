package br.edu.ifsp.scl.sdm.forca.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.telecom.Call
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ifsp.scl.sdm.forca.model.ForcaApi
import br.edu.ifsp.scl.sdm.forca.model.Identificador
import br.edu.ifsp.scl.sdm.forca.model.Palavras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer
import java.util.*
import javax.security.auth.callback.Callback

class ForcaViewModel(application: Application) : AndroidViewModel(application){


    val identifiersMld: MutableLiveData<Identificador> = MutableLiveData()
    val wordMld: MutableLiveData<Palavras> = MutableLiveData()
    val currentRoundMdl: MutableLiveData<Int> = MutableLiveData()
    val attemptsMdl: MutableLiveData<Int> = MutableLiveData()
    val gameEndedMdl: MutableLiveData<Boolean> = MutableLiveData()

    private var correctAnswerCounter: MutableList<String> = mutableListOf()
    private var wrongAnswerCounter: MutableList<String> = mutableListOf()
    private var currentDifficulty: Int? = getDifficulty()
    private var totalRounds: Int? = getRounds()
    private var gameIdentifiers: MutableList<Int> = ArrayList()


    companion object {
        val BASE_URL = "https://www.nobile.pro.br/forcaws/"
        val SHARED_PREFERENCES_KEY = "FORCA_SHARED_PREFERENCES_KEY"
        val TOTAL_ROUNDS_KEY = "TOTAL_ROUNDS_KEY"
        val TOTAL_ROUNDS_DEFAULT = 1
        val DIFFICULTY_KEY = "DIFFICULTY_KEY"
        val DIFFICULTY_DEFAULT = 1
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("${BASE_URL}")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val forcaApi: ForcaApi = retrofit.create(ForcaApi::class.java)

    fun CharSequence.unaccent(): String {
        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    fun guess(key: String) {
        val word: Palavras = wordMld.value!!
        if (word.palavras.unaccent().uppercase().contains(key.uppercase())) {

        } else {
            val attempts: Int = attemptsMdl.value!!
            attemptsMdl.postValue(attempts - 1)
        }
    }

    fun startGame() {
        currentRoundMdl.postValue(0)
        getIdentifiers(currentDifficulty!!)
        correctAnswerCounter = mutableListOf()
        wrongAnswerCounter = mutableListOf()
        gameEndedMdl.postValue(false)
    }

    fun nextRound() {
        val index = gameIdentifiers[currentRoundMdl.value!!]
        attemptsMdl.postValue(6)
        getWord(index)
        currentRoundMdl.postValue(currentRoundMdl.value!! + 1)
    }

    @SuppressLint("LongLogTag")
    fun finishRound(ganhouRound: Boolean) {
        Log.d("WORD - finishRound() ganhouRound", ganhouRound.toString())
        if (ganhouRound) {
            correctAnswerCounter.add(wordMld.value?.palavras!!)

            Log.d("WORD - finishRound() correctAnswerCounterMdl", correctAnswerCounter.toString())
        } else {
            wrongAnswerCounter.add(wordMld.value?.palavras!!)
            Log.d("WORD - finishRound() wrongAnswerCounterMdl", wrongAnswerCounter.toString())
        }

        if (currentRoundMdl.value!! < totalRounds!!) {
            nextRound()
        } else {
            gameEndedMdl.postValue(true)
        }

    }

    fun generateRoundIdentifiers() {
        val random = Random()
        gameIdentifiers = ArrayList()
        while (gameIdentifiers.size < totalRounds!!) {
            val randomIndex = random.nextInt(identifiersMld.value!!.palavras.size - 1)
            val randomIdentifier = identifiersMld.value!!.palavras[randomIndex]
            if (!gameIdentifiers.contains(randomIdentifier)) {
                gameIdentifiers.add(randomIdentifier)
            }
        }
    }

    fun getCorrectAnswers(): MutableList<String> {
        return correctAnswerCounter
    }

    fun getWrongAnswers(): MutableList<String> {
        return wrongAnswerCounter
    }

    fun getRounds(): Int? {

        val application = getApplication<Application>()
        val sharedPref =
            application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val rodadas = sharedPref?.getInt(TOTAL_ROUNDS_KEY, TOTAL_ROUNDS_DEFAULT)
        return rodadas
    }

    fun setTotalRounds(rodadas: Int) {
        val application = getApplication<Application>()
        val sharedPref =
            application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(TOTAL_ROUNDS_KEY, rodadas)
            apply()
        }

        totalRounds = rodadas
    }

    fun getDifficulty(): Int? {
        val application = getApplication<Application>()
        val sharedPref =
            application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val difficulty = sharedPref?.getInt(DIFFICULTY_KEY, DIFFICULTY_DEFAULT)
        return difficulty
    }

    fun setDifficulty(nivel: Int) {
        val application = getApplication<Application>()
        val sharedPref =
            application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(DIFFICULTY_KEY, nivel)
            apply()
        }
        currentDifficulty = nivel
    }

    fun getIdentifiers(id: Int) {
        scope.launch {
            forcaApi.retrieveIdentificadores(id).enqueue(object : retrofit2.Callback<Array<Int>> {
                override fun onResponse(
                    call: retrofit2.Call<Array<Int>>,
                    response: Response<Array<Int>>
                ) {
                    val list: Array<Int> = response.body()!!
                    val ident = Identificador(list)
                    identifiersMld.postValue(ident)
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(call: retrofit2.Call<Array<Int>>, t: Throwable) {
                    Log.e("${BASE_URL}", t.message.toString())
                }
            })
        }
    }

    fun getWord(id: Int) {
        scope.launch {
            forcaApi.retrievePalavras(id).enqueue(object : retrofit2.Callback<Array<Palavras>> {
                override fun onResponse(
                    call: retrofit2.Call<Array<Palavras>>,
                    response: Response<Array<Palavras>>
                ) {
                    Log.d("WORD AQUI", response.body()!!.get(0).palavras)
                    wordMld.postValue(response.body()!!.get(0))
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(call: retrofit2.Call<Array<Palavras>>, t: Throwable) {
                    Log.e("${BASE_URL}/palavra/${id}", t.message.toString())
                }
            })
        }
    }

}

