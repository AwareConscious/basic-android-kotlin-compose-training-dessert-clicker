package com.example.dessertclicker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertClickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DessertClickerUiState())
    var dessertClickerUiState: StateFlow<DessertClickerUiState> =
        _uiState.asStateFlow()

    val desserts = Datasource.dessertList

    init {
        resetDessertClicker()
    }

    private fun resetDessertClicker() {
        _uiState.value = DessertClickerUiState(currentDessertIndex = 0)
        _uiState.update { currentState ->
            currentState.copy(
                currentDessertPrice = desserts[_uiState.value.currentDessertIndex].price,
                currentDessertImageId = desserts[_uiState.value.currentDessertIndex].imageId
            )
        }
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
        val dessertToShow = determineDessertToShow(desserts, _uiState.value.dessertsSold)
        _uiState.update { currentState ->
            currentState.copy(
                currentDessertImageId = dessertToShow.imageId,
                currentDessertPrice = dessertToShow.price
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    fun determineDessertToShow(
        desserts: List<Dessert>,
        dessertsSold: Int
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }
        return dessertToShow
    }
}