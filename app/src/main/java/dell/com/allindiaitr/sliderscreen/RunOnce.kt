package dell.com.allindiaitr.sliderscreen

import android.content.Context
import android.content.SharedPreferences

class RunOnce(internal var _context: Context) {
    private var introPreference: SharedPreferences
    private var editor: SharedPreferences.Editor
    // shared pref mode
    private var PREF_FILE_NAME = "androidhive-welcomef"
    private var RUN_ONCE_KEY = "IsFirstTimeLaunch"
    private var SHARED_PREF_PRIVATE_MODE = 0
    var isFirstTimeLaunch: Boolean
        get() = introPreference.getBoolean(RUN_ONCE_KEY, true)
        set(isFirstTime) {
            editor.putBoolean(RUN_ONCE_KEY, isFirstTime)
            editor.commit()
        }
    init {
        introPreference = _context.getSharedPreferences(PREF_FILE_NAME, SHARED_PREF_PRIVATE_MODE)
        editor = introPreference.edit()
    }
}