package com.terrranullius.pickcab.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terrranullius.pickcab.network.*
import com.terrranullius.pickcab.other.IdentitySource
import com.terrranullius.pickcab.util.Event
import com.terrranullius.pickcab.util.Resource

class MainViewModel : ViewModel() {

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

    private val _verificationStartedEvent = MutableLiveData<Event<Unit>>()
    val verificationStartedEvent: LiveData<Event<Unit>>
        get() = _verificationStartedEvent

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

    private val _getIdentityEvent = MutableLiveData<Event<IdentitySource>>()
    val getIdentityEvent: LiveData<Event<IdentitySource>>
        get() = _getIdentityEvent

    private val _showNumberChooser = MutableLiveData<Event<Unit>>()
    val showNumberChooser: LiveData<Event<Unit>>
        get() = _showNumberChooser

    private val _phoneNumber = MutableLiveData(0L)
    val phoneNumber: LiveData<Long>
        get() = _phoneNumber

    private val _otpLiveData = MutableLiveData(0)
    val otpLiveData: LiveData<Int>
        get() = _otpLiveData

    fun getIdentity(identitySource: IdentitySource) {
        _getIdentityEvent.value = Event(identitySource)
    }

    fun showNumbersHint() {
        _showNumberChooser.value = Event(Unit)
    }

    fun setNumber(number: Long) {
        _phoneNumber.value = number
    }

    fun setOtp(otp: Int){
        _otpLiveData.value = otp
    }


    fun startVerification(number: Long) {

        _verificationStartedEvent.value = Event(Unit)

        setNumber(number)

//        _phoneNumberSetEvent.value = Event(Resource.Success(number))

        PickCabApi.retrofitService.startVerification(number).observeForever {

            Log.d("sha", "start verification response : $it")

            when (it) {
                is ApiSuccessResponse -> {
                    _phoneNumberSetEvent.value = Event(Resource.Success(number))
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
        }

    }

    fun verifyOtp(otp: Int) {

        _otpSetEvent.value = Event(Resource.Loading)

//        _otpSetEvent.value = Event(Resource.Success(phoneNumber.value ?: 0L))

        PickCabApi.retrofitService.verifyOtp(phoneNumber.value!!, otp).observeForever {

            Log.d("sha", "verify otp response : $it")

            when (it) {
                is ApiSuccessResponse -> {
                    if (it.body.result == "not verified" || it.body.result == "error") {
                        _otpSetEvent.value = Event(Resource.Error(NullPointerException()))
                    } else if (it.body.result == "verified") {
                        _otpSetEvent.value = Event(Resource.Success(phoneNumber.value!!))
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
        }
    }

    fun sendConfirmation() {
        //TODO UNCOMMENT

         /* PickCabApi.retrofitService.sendConfirmation(
              ConfirmationRequest(
                  number = phoneNumber.value!!,
                  startDate = startDate,
                  endDate = endDate,
                  time = time,
                  oneWay = oneWay,
                  identityUrl = identityProofFireUri,
                  startDestination = startDestination,
                  endDestination = endDestination,
                  forAdmin = true,
                  forMail = true
              )
          ).observeForever {

              Log.d("sha", "sendConfirmation Response: $it")

              when (it) {
                  is ApiSuccessResponse -> {
                  }
                  else -> {
                  }
              }
          }*/
    }

}

