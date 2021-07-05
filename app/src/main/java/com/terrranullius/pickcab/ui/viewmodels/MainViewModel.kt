package com.terrranullius.pickcab.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrranullius.pickcab.network.PickCabApi
import com.terrranullius.pickcab.network.ServerResponse
import com.terrranullius.pickcab.other.Event
import com.terrranullius.pickcab.util.ApiEmptyResponse
import com.terrranullius.pickcab.util.ApiErrorResponse
import com.terrranullius.pickcab.util.ApiSuccessResponse
import com.terrranullius.pickcab.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var capturedPhotoUri: Uri? = null

    var phonenumber = 0L

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

    private val _phoneNumberSetEvent = MutableLiveData<Event<Resource<Long>>>()
    val verificationStartEvent: LiveData<Event<Resource<Long>>>
        get() = _phoneNumberSetEvent


    //TODO return verified number
    private val _otpSetEvent = MutableLiveData<Event<Resource<Long>>>()
    val otpSetEvent: LiveData<Event<Resource<Long>>>
        get() = _otpSetEvent

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

    fun startVerification(number: Long) {

        phonenumber = number

        _phoneNumberSetEvent.value = Event(Resource.Success(phonenumber))

      /*     PickCabApi.retrofitService.startVerification(phonenumber).observeForever{

               Log.d("sha","startverification response : $it")

               when(it){
                   is ApiSuccessResponse -> {
                       _phoneNumberSetEvent.value = Event(Resource.Success(phonenumber))
                   }
                   is ApiEmptyResponse -> {
                       _phoneNumberSetEvent.value = Event(Resource.Error(Exception()))
                   }
                   is ApiErrorResponse -> {
                       _phoneNumberSetEvent.value = Event(Resource.Error(Exception()))
                   }
                   null -> {
                       _phoneNumberSetEvent.value = Event(Resource.Error(Exception()))
                   }
               }
          }*/

    }

    fun verifyOtp(otp: Int) {

        Log.d("sha", "number: $phonenumber")

            /*PickCabApi.retrofitService.verifyOtp(phonenumber, otp).observeForever{

                Log.d("sha","verify otp response : $it")

                when(it){
                    is ApiSuccessResponse -> {
                        if (it.body.result == "not verified" || it.body.result == "error") {
                            _otpSetEvent.value = Event(Resource.Error(NullPointerException()))
                        } else if (it.body.result == "verified") {
                            _otpSetEvent.value = Event(Resource.Success(phonenumber))
                        }
                    }
                    is ApiEmptyResponse -> {
                        _otpSetEvent.value = Event(Resource.Error(NullPointerException()))
                    }
                    is ApiErrorResponse -> {
                        _otpSetEvent.value = Event(Resource.Error(NullPointerException()))
                    }
                    null -> {
                        _otpSetEvent.value = Event(Resource.Error(NullPointerException()))
                    }

                }
            }*/
    }
}

