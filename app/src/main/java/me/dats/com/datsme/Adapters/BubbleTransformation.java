package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;

import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.R;

import static me.dats.com.datsme.R.color.datsme_color;

public class BubbleTransformation implements com.squareup.picasso.Transformation {
    private static final int outerMargin = 13;
    private final int margin;  // dp

    // margin is the board in dp
    public BubbleTransformation(final int margin) {
        this.margin = margin;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight()+10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintBorder = new Paint();
        paintBorder.setColor(Color.parseColor("#67D0C5"));
        paintBorder.setStrokeWidth(margin);
        canvas.drawCircle(source.getHeight()/2, source.getHeight()/2, 70, paintBorder);

        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        trianglePaint.setStrokeWidth(1);
        trianglePaint.setColor(Color.parseColor("#67D0C5"));
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);

        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(outerMargin, source.getHeight() / 2+2);
        triangle.lineTo(source.getWidth()/2,source.getHeight()+10);
        triangle.lineTo(source.getWidth()-outerMargin,source.getHeight()/2+10);
        triangle.close();

        canvas.drawPath(triangle, trianglePaint);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(source.getHeight()/2, source.getHeight()/2, 65, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}