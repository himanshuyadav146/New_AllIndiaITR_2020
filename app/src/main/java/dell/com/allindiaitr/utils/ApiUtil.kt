package dell.com.allindiaitr.utils

class ApiUtil {

    val BASE_URL = "https://www.allindiaitr.com/"

      val SUCCESS_PAYMENT = BASE_URL + "api/PayUGateway/Mobile/SuccessPayment"
     val FAILURE_PAYMENT = BASE_URL + "api/PayUGateway/Mobile/FailurePayment"
    var PAYTM_GATEWAY = BASE_URL + "api/PayTMGateway/Payment"

    // PAyu
//    var SUCCESS_PAYMENT = BASE_URL + "api/PayUGateway/Mobile/SuccessPayment"
//    var FAILURE_PAYMENT = BASE_URL + "api/PayUGateway/Mobile/FailurePayment"
}

