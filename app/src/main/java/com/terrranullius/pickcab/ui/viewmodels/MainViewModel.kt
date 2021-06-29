package com.terrranullius.pickcab.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terrranullius.pickcab.other.Event

class MainViewModel : ViewModel() {

    var capturedPhotoUri: Uri? = null

    var phonenumber = ""
        set(value) {
            field = value
            _phoneNumberSetEvent.value = Event(Unit)
        }
    var startDestination = ""
    var endDestination = ""
        set(value) {
            field = value
            _destinationsSetEvent.value = Event(Unit)
        }

    var startDate = ""
        set(value) {
            field = value
            if (oneWay) _datesSetEvent.value = Event(Unit)
        }
    var endDate = ""
        set(value) {
            field = value
            _datesSetEvent.value = Event(Unit)
        }

    var time = ""
        set(value) {
            field = value
            _timeSetEvent.value = Event(Unit)
        }
    var oneWay = false
        set(value) {
            field = value
            _numWaysSetEvent.value = Event(Unit)
        }

    var identityProofFireUri = ""
        set(value) {
            field = value
            _identitySetEvent.value = Event(Unit)
        }

    private val _phoneNumberSetEvent = MutableLiveData<Event<Unit>>()
    val phoneNumberSetEvent: LiveData<Event<Unit>>
        get() = _phoneNumberSetEvent

    private val _destinationsSetEvent = MutableLiveData<Event<Unit>>()
    val destinationsSetEvent: LiveData<Event<Unit>>
        get() = _destinationsSetEvent

    private val _datesSetEvent = MutableLiveData<Event<Unit>>()
    val datesSetEvent: LiveData<Event<Unit>>
        get() = _datesSetEvent

    private val _timeSetEvent = MutableLiveData<Event<Unit>>()
    val timeSetEvent: LiveData<Event<Unit>>
        get() = _timeSetEvent

    private val _numWaysSetEvent = MutableLiveData<Event<Unit>>()
    val numWaysSetEvent: LiveData<Event<Unit>>
        get() = _numWaysSetEvent

    private val _identitySetEvent = MutableLiveData<Event<Unit>>()
    val identitySetEvent: LiveData<Event<Unit>>
        get() = _identitySetEvent

}