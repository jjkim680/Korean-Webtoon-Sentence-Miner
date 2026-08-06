package com.example.koreanwebtoonsentenceminer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModel(private val dao: DictionaryDao) : ViewModel() {

    val searchQuery = MutableStateFlow("사과") 

    // Every time searchQuery changes, flatMapLatest triggers a new DB query. latest is so
    // that the response to the old query doesn't overwrite the response to the new query
    val searchResults: StateFlow<List<Translation>> = searchQuery
        .flatMapLatest { query ->
            dao.getAll(query) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // A helper to insert data on the IO dispatcher
    fun insertDummyCard() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val text = "사과"
            val hash = Translation.generateHash(text)
            
            val dummy = Translation(
                idHash = hash,
                koreanWord = text,
                englishDefinition = "Apple",
                partsOfSpeech = "Noun",
                payloadData = "{}"
            )
            
            dao.insertAll(dummy)
        }
    }
}