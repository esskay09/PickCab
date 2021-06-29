package com.terrranullius.pickcab.other

fun getFormattedConfirmationMessage(
    forMail: Boolean = true,
    phoneNumber: String,
    startDate: String,
    endDate: String = "",
    time: String,
    oneWay: Boolean = true,
    identityUrl: String,
    startDestination: String,
    endDestination: String,
    forAdmin: Boolean = true
) =
    "Booking Confirmed" + "\n\n" +
            "From $startDestination to $endDestination" + "\n" +
            "${if (oneWay) "One Way" else "Two Way"}" + "\n" +
            "${if (oneWay) "On $startDate" else "For $startDate to $endDate"}" + "\n" +
            "Pickup Time: $time\n" +
            "${if (forMail) "identity URL: $identityUrl" else ""}" + "\n" +
            "Contact number: $phoneNumber" + "\n" +
            if (!forAdmin) "Have a great trip!!" else ""
