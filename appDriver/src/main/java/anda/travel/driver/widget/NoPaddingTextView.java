package anda.travel.driver.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class NoPaddingTextView extends AppCompatTextView {

    public NoPaddingTextView(Context context) {
        this(context, null);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoPaddingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Paint paint = new Paint();
        final Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int top = (int) Math.ceil(Math.abs((fontMetricsInt.top - fontMetricsInt.ascent) / 2.0));
        setPadding(0, -(Math.abs(fontMetricsInt.top - fontMetricsInt.ascent) + top)
                , 0,
                fontMetricsInt.top - fontMetricsInt.ascent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}