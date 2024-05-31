package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource.dessertList
import com.example.dessertclicker.data.DessertClickerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertClickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DessertClickerUiState())
    var dessertClickerUiState: StateFlow<DessertClickerUiState> =
        _uiState.asStateFlow()

    init {
        resetDessertClicker()
    }

    private fun resetDessertClicker() {
        _uiState.value = DessertClickerUiState(currentDessertIndex = 0)
    }

    fun checkoutDessertPurchase() {
        updateTheRevenue()
        showTheNextDessert()
    }

    fun updateTheRevenue() {
        _uiState.update { currentState ->
            currentState.copy(
                revenue = currentState.revenue + currentState.currentDessertPrice,
                dessertsSold = currentState.dessertsSold.inc()
            )
        }
    }

    fun showTheNextDessert() {
        val nextDessertIndex = determineDessertIndex(_uiState.value.dessertsSold)
        _uiState.update { currentState ->
            currentState.copy(
                currentDessertIndex = nextDessertIndex,
                currentDessertImageId = dessertList[nextDessertIndex].imageId,
                currentDessertPrice = dessertList[nextDessertIndex].price
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    fun determineDessertIndex(
        dessertsSold: Int
    ): Int {
        var dessertIndex = 0
        for (index in dessertList.indices) {
            if (dessertsSold >= dessertList[index].startProductionAmount) {
                dessertIndex = index
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }
        return dessertIndex
    }
}