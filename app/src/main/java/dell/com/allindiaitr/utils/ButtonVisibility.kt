package dell.com.allindiaitr.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button

class ButtonVisibility {

    fun hideButton(context: Context, view: View, btn: Button) {

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = view.rootView.height - view.height
            if (heightDiff > dpToPx(context, 200f)) { // if more than 200 dp, it's probably a keyboard...
                btn.visibility = View.GONE
            } else {
                btn.visibility = View.VISIBLE
            }
        }

    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }
}