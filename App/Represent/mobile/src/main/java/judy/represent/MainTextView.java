package judy.represent;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Judy on 3/6/2016.
 */
public class MainTextView extends TextView {

    public MainTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MainTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getResources().getAssets(), "perpetua.ttf");
            setTypeface(tf);
        }
    }

}