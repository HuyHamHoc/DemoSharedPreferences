package com.example.demosharedpreferences

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.demosharedpreferences.databinding.ActivitySharedprefBinding

class DemoSharedPrefsViewModel(application: Application) : AndroidViewModel(application){
    private val prefs = PreferenceManager.getDefaultSharedPreferences(getApplication())

    private val _textLiveData = MutableLiveData<String?>(null)
    private val _namesLiveData = MutableLiveData<Set<String>?>(null)

    internal val textLiveData: LiveData<String?> get() = _textLiveData
    internal val namesLiveData: LiveData<Set<String>?> get() = _namesLiveData


    // SAM conversion
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                TEXT_KEY -> _textLiveData.value = sharedPreferences.getString(key, null)
                NAMES -> _namesLiveData.value = sharedPreferences.getStringSet(key, null)
                else -> Unit
            }
        }

    init {

        _textLiveData.value = prefs.getString(TEXT_KEY, null)
        _namesLiveData.value = prefs.getStringSet(NAMES, null)

        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }

    fun saveText(text: String) {
        prefs.edit(commit = false) {
            val names = prefs.getStringSet(NAMES, null).orEmpty()
            putStringSet(NAMES, names + text)

            putString(TEXT_KEY, text)
        }
    }

    private companion object {
        const val TEXT_KEY = "text"
        const val NAMES = "names"
    }
}
class DemoSharedPreferences : AppCompatActivity(){
    private lateinit var binding : ActivitySharedprefBinding

    private val vm by viewModels<DemoSharedPrefsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedprefBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            vm.saveText(binding.textInputEditText.text?.toString() ?: "")
        }
        vm.textLiveData.observe(this){text ->
            binding.textView1.text = text
        }

    }
}