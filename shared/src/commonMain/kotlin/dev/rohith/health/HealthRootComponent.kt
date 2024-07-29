package dev.rohith.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

interface State

interface Event

abstract class HealthViewModel<S: State, E: Event>(
    initialState: S,
): ViewModel(){

    open val dispatcher: CoroutineDispatcher = Dispatchers.Default

    private val _uiState: MutableStateFlow<S> = MutableStateFlow(
        initialState
    )

    private val _event: MutableSharedFlow<E> = MutableSharedFlow()
    private val eventSink: MutableSharedFlow<E> = _event

    val event: SharedFlow<E>
        get() = _event.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val state: StateFlow<S>
        get() = _uiState

    fun setState(reducer: S.() -> S) {
        _uiState.value  = reducer.invoke(_uiState.value)
    }

    fun withState(reducer: (S) -> Unit) {
        reducer(_uiState.value)
    }

    fun emitEvent(event: E) {
        viewModelScope.launch(dispatcher) {
            _event.emit(event)
        }
    }

}

