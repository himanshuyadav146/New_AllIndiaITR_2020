package dell.com.allindiaitr.utils

import android.content.Context
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Pattern

class Validation {

    fun isMobileValid(mobile : String, editText: EditText): Boolean{
        if (TextUtils.isEmpty(mobile)){
            editText.requestFocus()
            editText.error = "Enter your mobile number"
            return false
        }
        else if (Pattern.matches("[a-zA-Z]+", mobile)){
            editText.requestFocus()
            editText.error = "Mobile no. contains digits only"
            return false
        }
        else if (!Pattern.matches("[6-9][0-9]{9}$", mobile)){
            editText.requestFocus()
            editText.error = "Enter correct mobile number"
            return false
        }
        else if (mobile.length != 10){
            editText.requestFocus()
            editText.error = "Mobile no. should be of 10 digits only"
            return false
        }
        return true
    }

    fun isEmpaty(txt:String,editText: EditText,msg:String):Boolean{
        if(TextUtils.isEmpty(txt)){
            editText.requestFocus()
            //editText.error="Please Enter a value"
            editText.error=msg
            return  false
        }
        return true
    }


    fun isOTPValid(otp : String, editText: EditText): Boolean{
        if (TextUtils.isEmpty(otp)){
            editText.requestFocus()
            editText.error = "Enter your 6-digit otp"
            return false
        }
        else if (otp.length != 6){
            editText.requestFocus()
            editText.error = "OTP should be of 6-digits"
            return false
        }
        return true
    }

    fun isFirstNameValid(firstName: String, editText: EditText): Boolean {
        when {
            TextUtils.isEmpty(firstName) -> {
                editText.requestFocus()
                editText.error = "Enter first name"
                return false
            }
            firstName.length < 3 -> {
                editText.requestFocus()
                editText.error = "Enter full first name"
                return false
            }
            firstName.matches(".*\\d+.*".toRegex()) -> {
                editText.requestFocus()
                editText.error = "Enter your correct name"
                return false
            }
            else -> return true
        }
    }

    fun isLastNameValid(lastName: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(lastName)){
            editText.requestFocus()
            editText.error = "Enter last name"
            return false
        }
        else if (lastName.matches(".*\\d+.*".toRegex())){
            editText.requestFocus()
            editText.error = "Enter your correct name"
            return false
        }
        return true
    }

    fun isEmailValid(email: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(email)){
            editText.requestFocus()
            editText.error = "Enter your email id"
            return false
        }
        else if (!email.matches("[a-zA-Z0-9._-]+[_A-Za-z0-9-]+@[a-z]+\\.[a-z]+".toRegex())){
            editText.requestFocus()
            editText.error = "Enter your valid email id"
            return false
        }
        return true
    }

    fun isPanValid(pan: String, editText: EditText): Boolean {
        when {
            TextUtils.isEmpty(pan) -> {
                editText.requestFocus()
                editText.error = "Enter your pan number"
                return false
            }
            !pan.matches("[A-Za-z]{5}\\d{4}[A-Za-z]{1}".toRegex()) -> {
                editText.requestFocus()
                editText.error = "Enter your correct pan number"
                return false
            }
            pan.length != 10 -> {
                editText.requestFocus()
                editText.error = "Pan number should be 10 characters long"
                return false
            }
            else -> return true
        }
    }

    fun isDobValid(dob: String, textView: TextView): Boolean {
        if (dob.isEmpty()){
            textView.requestFocus()
            textView.error = "Select the date"
            return false
        }
        return true
    }

    fun isFatherNameValid(fatherName: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(fatherName)){
            editText.requestFocus()
            editText.error = "Enter father's name"
            return false
        }
        else if (fatherName.matches(".*\\d+.*".toRegex())){
            editText.requestFocus()
            editText.error = "Enter correct father's name"
            return false
        }
        return true
    }

    fun isAadhaarValid(aadhaar: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(aadhaar)){
            editText.requestFocus()
            editText.error = "Enter your aadhaar card"
            return false
        }
        else if (aadhaar.length < 12 || aadhaar.length > 28) {
            editText.requestFocus()
            editText.error = "Enter your correct aadhaar card"
            return false
        }
        return true
    }

    fun isAddressValid(address: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(address)){
            editText.requestFocus()
            editText.error = "Enter your Address"
            return false
        }
        else if (address.contains("&") || address.contains("*") || address.contains("(") || address.contains(")")) {
            editText.requestFocus()
            editText.error = "Address should not contain &, *, ( and )."
            return false
        }
        return true
    }

    fun isStateValid(state: String, context: Context): Boolean{
        if (state == "Select State"){
            Toast.makeText(context, "Please Select a State", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun isCityValid(city: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(city)){
            editText.requestFocus()
            editText.error = "Enter your city"
            return false
        }
        else if (city.matches(".*\\d+.*".toRegex())){
            editText.requestFocus()
            editText.error = "Enter your correct city"
            return false
        }
        return true
    }

    fun isPinValid(pin: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(pin)){
            editText.requestFocus()
            editText.error = "Enter your Pincode"
            return false
        }
        else if (pin.length != 6){
            editText.requestFocus()
            editText.error = "Enter your correct Pincode"
            return false
        }
        return true
    }

    fun isBankNameValid(bankName: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(bankName)){
            editText.requestFocus()
            editText.error = "Enter your bank name"
            return false
        }
        else if (bankName.matches(".*\\d+.*".toRegex())){
            editText.requestFocus()
            editText.error = "Enter your correct bank name"
            return false
        }
        else if (!bankName.matches("^[a-zA-Z ]+\$".toRegex())) {
            editText.requestFocus()
            editText.error = "Bank name have Characters only"
            return false
        }
        return true
    }

    fun isIfscValid(ifsc: String, editText: EditText): Boolean {
        when {
            TextUtils.isEmpty(ifsc) -> {
                editText.requestFocus()
                editText.error = "Enter your ifsc code"
                return false
            }
            !ifsc.matches("^[A-Z]{4}0[0-9A-Z]{6}\$".toRegex()) -> {
                editText.requestFocus()
                editText.error = "Enter Your Valid IFSC Code"
                return false
            }
            ifsc.length != 11 -> {
                editText.requestFocus()
                editText.error = "IFSC Code should be of 11 Digits"
                return false
            }
            else -> return true
        }
    }

    fun isBankAccountNumberValid(accNum: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(accNum)){
            editText.requestFocus()
            editText.error = "Enter Bank Account Number"
            return false
        }
        else if (accNum.length < 9){
            editText.requestFocus()
            editText.error = "Account Number Should be more than 8 Digits"
            return false
        }
        return true
    }

    fun isFieldValid(amount: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(amount)){
            editText.requestFocus()
            editText.error = "Field is mandatory"
            return false
        }
        return true
    }

    fun isNameValid(name: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(name)){
            editText.requestFocus()
            editText.error = "Enter name"
            return false
        }
        else if (name.matches(".*\\d+.*".toRegex())){
            editText.requestFocus()
            editText.error = "Enter correct name"
            return false
        }
        return true
    }

    fun isGstInNumber(gstInNo: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(gstInNo)) {
            editText.requestFocus()
            editText.error = "Enter Your GSTIN"
            return false
        }
        else if (gstInNo.length < 15) {
            editText.requestFocus()
            editText.error = "Enter Your 15 digit GSTIN"
            return false
        }
        else if (!gstInNo.matches("[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}".toRegex())) {
            editText.requestFocus()
            editText.error = "Enter Your correct GSTIN"
            return false
        }
        return true
    }

    fun isGstInCompanyNameAddress(gstInName: String, editText: EditText): Boolean {
        if (TextUtils.isEmpty(gstInName)) {
            editText.requestFocus()
            editText.error = "Field is mandatory"
            return false
        }
        return true
    }
}