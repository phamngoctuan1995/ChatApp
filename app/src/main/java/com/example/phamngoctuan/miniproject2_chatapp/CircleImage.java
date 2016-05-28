package com.example.phamngoctuan.miniproject2_chatapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by phamngoctuan on 28/05/2016.
 */
public class CircleImage extends ImageView {

    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int radius;
    private Shader shader = null;

    @Override
    protected void onDraw(Canvas canvas) {

        if (getHeight() > getWidth())
            radius = getWidth() / 2;
        else
            radius = getHeight() / 2;

        Bitmap original = Bitmap.createBitmap(
                getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas originalCanvas = new Canvas(original);
        super.onDraw(originalCanvas);    // ImageView
        // Lợi dụng hệ thống vẽ ảnh source vào bitmap original

        shader = new BitmapShader(original,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        Paint paint = new Paint();
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }
}
