package com.sound.birdstone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sound.birdstone.database.BirdDao
import com.sound.birdstone.database.BirdEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirdViewModel @Inject constructor(
    private val birdDao: BirdDao,
) : ViewModel() {


    fun birdTypeData(categoryId: Int) = birdDao.getBirdTypeData(categoryId).asLiveData()
    val allBird = birdDao.allBirds().asLiveData()
    val birdFavorite = birdDao.getBirdFavorite().asLiveData()

    fun update(i: Int, id: Int) {
        viewModelScope.launch {
            birdDao.update(i, id)
        }
    }

    fun isEmpty(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                birdDao.getAllBird().isEmpty().also {
                    callback(it)
                }
            }
        }
    }

    fun addAll(list: ArrayList<BirdEntity>) {
        viewModelScope.launch {
            birdDao.addAllBirds(list)
        }

    }
}