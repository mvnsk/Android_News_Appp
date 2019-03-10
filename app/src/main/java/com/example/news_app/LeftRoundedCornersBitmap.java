package com.example.news_app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class LeftRoundedCornersBitmap extends RoundedCornersBitmap {

    public LeftRoundedCornersBitmap() {
        super();
    }

    public LeftRoundedCornersBitmap(float cornderRadius, int borderColor, int strokeWidth) {
        super(cornderRadius, borderColor, strokeWidth);
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(mStrokeWidth, mStrokeWidth, (bitmap.getWidth() - mStrokeWidth), bitmap.getHeight()
                - mStrokeWidth);
        final RectF rectF = new RectF(rect);
        final Rect topRightRect = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
        final Rect bottomRect = new Rect(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setColor(mBorderColor);
        paint.setStrokeWidth(3);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
        canvas.drawRect(topRightRect, paint);
        canvas.drawRect(bottomRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }
}
