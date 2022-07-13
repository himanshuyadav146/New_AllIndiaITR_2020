package dell.com.allindiaitr.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

public class ButtonVisibilityClass {

    public void hideButton(final Context context, final View view, final Button btn) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = view.getRootView().getHeight() - view.getHeight();
                if (heightDiff > dpToPx(context, 200)) { // if more than 200 dp, it's probably a keyboard...
                    btn.setVisibility(View.GONE);
                }
                else {
                    btn.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
