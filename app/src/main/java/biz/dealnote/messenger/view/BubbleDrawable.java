package biz.dealnote.messenger.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class BubbleDrawable extends Drawable {
    private final RectF mRect;
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float mArrowWidth;
    private final float mAngle;
    private final float mArrowHeight;
    private final float mRadius;
    private final int bubbleColor;
    private final int secondary_bubbleColor;
    private final Bitmap bubbleBitmap;
    private final ArrowLocation mArrowLocation;
    private final BubbleType bubbleType;
    private final boolean mArrowCenter;
    private BitmapShader mBitmapShader;
    private LinearGradient gradient;
    private float mArrowPosition;

    private BubbleDrawable(Builder builder) {
        this.mRect = builder.mRect;
        this.mAngle = builder.mAngle;
        this.mArrowHeight = builder.mArrowHeight;
        this.mArrowWidth = builder.mArrowWidth;
        this.mArrowPosition = builder.mArrowPosition;
        this.mRadius = builder.mRadius;
        this.bubbleColor = builder.bubbleColor;
        this.secondary_bubbleColor = builder.secondary_bubbleColor;
        this.bubbleBitmap = builder.bubbleBitmap;
        this.mArrowLocation = builder.mArrowLocation;
        this.bubbleType = builder.bubbleType;
        this.mArrowCenter = builder.arrowCenter;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        setUp(canvas);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    private void setUpPath(ArrowLocation mArrowLocation, Path path) {
        switch (mArrowLocation) {
            case LEFT:
                setUpLeftPath(mRect, path);
                break;
            case RIGHT:
                setUpRightPath(mRect, path);
                break;
            case TOP:
                setUpTopPath(mRect, path);
                break;
            case BOTTOM:
                setUpBottomPath(mRect, path);
                break;
        }
    }

    private void setUp(Canvas canvas) {
        switch (bubbleType) {
            case COLOR:
                if (secondary_bubbleColor == bubbleColor)
                    mPaint.setColor(bubbleColor);
                else {
                    if (gradient == null) {
                        gradient = new LinearGradient(0f, 0f, canvas.getWidth(), canvas.getHeight(),
                                bubbleColor, secondary_bubbleColor, Shader.TileMode.CLAMP);
                    }
                    mPaint.setShader(gradient);
                }
                break;
            case BITMAP:
                if (bubbleBitmap == null)
                    return;
                if (mBitmapShader == null) {
                    mBitmapShader = new BitmapShader(bubbleBitmap,
                            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                mPaint.setShader(mBitmapShader);
                setUpShaderMatrix();
                break;
        }
        setUpPath(mArrowLocation, mPath);
        canvas.drawPath(mPath, mPaint);
    }

    private void setUpLeftPath(RectF rect, Path path) {

        if (mArrowCenter) {
            mArrowPosition = (rect.bottom - rect.top) / 2 - mArrowWidth / 2;
        }

        path.moveTo(mArrowWidth + rect.left + mAngle, rect.top);
        path.lineTo(rect.width() - mAngle, rect.top);
        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius, rect.top, rect.right,
                mAngle + rect.top + 2 * mRadius), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngle);
        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius, rect.bottom - mAngle - 2 * mRadius,
                rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mArrowWidth + mAngle, rect.bottom);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.bottom - mAngle - 2 * mRadius,
                mAngle + rect.left + mArrowWidth + 2 * mRadius, rect.bottom), 90, 90);
        path.lineTo(rect.left + mArrowWidth, mArrowHeight + mArrowPosition);
        path.lineTo(rect.left, mArrowPosition + mArrowHeight / 2);
        path.lineTo(rect.left + mArrowWidth, mArrowPosition);
        path.lineTo(rect.left + mArrowWidth, rect.top + mAngle);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.top, mAngle + 2 * mRadius
                + rect.left + mArrowWidth, mAngle + rect.top + 2 * mRadius), 180, 90);
        path.close();
    }

    private void setUpTopPath(RectF rect, Path path) {

        if (mArrowCenter) {
            mArrowPosition = (rect.right - rect.left) / 2 - mArrowWidth / 2;
        }

        path.moveTo(rect.left + Math.min(mArrowPosition, mAngle), rect.top + mArrowHeight);
        path.lineTo(rect.left + mArrowPosition, rect.top + mArrowHeight);
        path.lineTo(rect.left + mArrowWidth / 2 + mArrowPosition, rect.top);
        path.lineTo(rect.left + mArrowWidth + mArrowPosition, rect.top + mArrowHeight);
        path.lineTo(rect.right - mAngle, rect.top + mArrowHeight);

        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius,
                rect.top + mArrowHeight, rect.right, mAngle + rect.top + mArrowHeight + 2 * mRadius), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngle);

        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius, rect.bottom - mAngle - 2 * mRadius,
                rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mAngle, rect.bottom);

        path.arcTo(new RectF(rect.left, rect.bottom - mAngle - 2 * mRadius,
                mAngle + rect.left + 2 * mRadius, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top + mArrowHeight + mAngle);
        path.arcTo(new RectF(rect.left, rect.top + mArrowHeight, mAngle
                + rect.left + 2 * mRadius, mAngle + rect.top + mArrowHeight + 2 * mRadius), 180, 90);
        path.close();
    }

    private void setUpRightPath(RectF rect, Path path) {

        if (mArrowCenter) {
            mArrowPosition = (rect.bottom - rect.top) / 2 - mArrowWidth / 2;
        }

        path.moveTo(rect.left + mAngle, rect.top);
        path.lineTo(rect.width() - mAngle - mArrowWidth, rect.top);
        path.arcTo(new RectF(rect.right - mAngle - mArrowWidth - 2 * mRadius,
                rect.top, rect.right - mArrowWidth, mAngle + rect.top + 2 * mRadius), 270, 90);
        path.lineTo(rect.right - mArrowWidth, mArrowPosition);
        path.lineTo(rect.right, mArrowPosition + mArrowHeight / 2);
        path.lineTo(rect.right - mArrowWidth, mArrowPosition + mArrowHeight);
        path.lineTo(rect.right - mArrowWidth, rect.bottom - mAngle);

        path.arcTo(new RectF(rect.right - mAngle - mArrowWidth - 2 * mRadius, rect.bottom - mAngle - 2 * mRadius,
                rect.right - mArrowWidth, rect.bottom), 0, 90);
        path.lineTo(rect.left + mArrowWidth, rect.bottom);

        path.arcTo(new RectF(rect.left, rect.bottom - mAngle - 2 * mRadius,
                mAngle + rect.left + 2 * mRadius, rect.bottom), 90, 90);

        path.arcTo(new RectF(rect.left, rect.top, mAngle
                + rect.left + 2 * mRadius, mAngle + rect.top + 2 * mRadius), 180, 90);
        path.close();
    }

    private void setUpBottomPath(RectF rect, Path path) {
        if (mArrowCenter) {
            mArrowPosition = (rect.right - rect.left) / 2 - mArrowWidth / 2;
        }
        path.moveTo(rect.left + mAngle, rect.top);
        path.lineTo(rect.width() - mAngle, rect.top);
        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius,
                rect.top, rect.right, mAngle + rect.top + 2 * mRadius), 270, 90);

        path.lineTo(rect.right, rect.bottom - mArrowHeight - mAngle);
        path.arcTo(new RectF(rect.right - mAngle - 2 * mRadius, rect.bottom - mAngle - mArrowHeight - 2 * mRadius,
                rect.right, rect.bottom - mArrowHeight), 0, 90);

        path.lineTo(rect.left + mArrowWidth + mArrowPosition, rect.bottom - mArrowHeight);
        path.lineTo(rect.left + mArrowPosition + mArrowWidth / 2, rect.bottom);
        path.lineTo(rect.left + mArrowPosition, rect.bottom - mArrowHeight);
        path.lineTo(rect.left + Math.min(mAngle, mArrowPosition), rect.bottom - mArrowHeight);

        path.arcTo(new RectF(rect.left, rect.bottom - mAngle - mArrowHeight - 2 * mRadius,
                mAngle + rect.left + 2 * mRadius, rect.bottom - mArrowHeight), 90, 90);
        path.lineTo(rect.left, rect.top + mAngle);
        path.arcTo(new RectF(rect.left, rect.top, mAngle
                + rect.left + 2 * mRadius, mAngle + rect.top + 2 * mRadius), 180, 90);
        path.close();
    }

    private void setUpShaderMatrix() {
        Matrix mShaderMatrix = new Matrix();
        mShaderMatrix.set(null);
        int mBitmapWidth = bubbleBitmap.getWidth();
        int mBitmapHeight = bubbleBitmap.getHeight();
        float scaleX = getIntrinsicWidth() / (float) mBitmapWidth;
        float scaleY = getIntrinsicHeight() / (float) mBitmapHeight;
        mShaderMatrix.postScale(scaleX, scaleY);
        mShaderMatrix.postTranslate(mRect.left, mRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mRect.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mRect.height();
    }

    public enum ArrowLocation {
        LEFT(0x00),
        RIGHT(0x01),
        TOP(0x02),
        BOTTOM(0x03);

        private final int mValue;

        ArrowLocation(int value) {
            this.mValue = value;
        }

        public static ArrowLocation mapIntToValue(int stateInt) {
            for (ArrowLocation value : ArrowLocation.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }
            return getDefault();
        }

        public static ArrowLocation getDefault() {
            return LEFT;
        }

        public int getIntValue() {
            return mValue;
        }
    }

    public enum BubbleType {
        COLOR,
        BITMAP
    }

    public static class Builder {
        public static final float DEFAULT_ARROW_WITH = 25;
        public static final float DEFAULT_ARROW_HEIGHT = 25;
        public static final float DEFAULT_ANGLE = 20;
        public static final float DEFAULT_ARROW_POSITION = 50;
        public static final int DEFAULT_BUBBLE_COLOR = Color.RED;
        public static final float DEFAULT_RADIUS = 20;
        private RectF mRect;
        private float mArrowWidth = DEFAULT_ARROW_WITH;
        private float mAngle = DEFAULT_ANGLE;
        private float mArrowHeight = DEFAULT_ARROW_HEIGHT;
        private float mArrowPosition = DEFAULT_ARROW_POSITION;
        private float mRadius = DEFAULT_RADIUS;
        private int bubbleColor = DEFAULT_BUBBLE_COLOR;
        private int secondary_bubbleColor = DEFAULT_BUBBLE_COLOR;
        private Bitmap bubbleBitmap;
        private BubbleType bubbleType = BubbleType.COLOR;
        private ArrowLocation mArrowLocation = ArrowLocation.LEFT;
        private boolean arrowCenter;

        public Builder rect(RectF rect) {
            this.mRect = rect;
            return this;
        }

        public Builder arrowWidth(float mArrowWidth) {
            this.mArrowWidth = mArrowWidth;
            return this;
        }

        public Builder angle(float mAngle) {
            this.mAngle = mAngle * 2;
            return this;
        }

        public Builder arrowHeight(float mArrowHeight) {
            this.mArrowHeight = mArrowHeight;
            return this;
        }

        public Builder arrowPosition(float mArrowPosition) {
            this.mArrowPosition = mArrowPosition;
            return this;
        }

        public Builder cornerRadius(float mRadius) {
            this.mRadius = mRadius;
            return this;
        }

        public Builder bubbleColor(int bubbleColor) {
            this.bubbleColor = bubbleColor;
            bubbleType(BubbleType.COLOR);
            return this;
        }

        public Builder bubbleColorGradient(int first_bubbleColor, int second_bubbleColor) {
            this.bubbleColor = first_bubbleColor;
            this.secondary_bubbleColor = second_bubbleColor;
            bubbleType(BubbleType.COLOR);
            return this;
        }

        public Builder bubbleBitmap(Bitmap bubbleBitmap) {
            this.bubbleBitmap = bubbleBitmap;
            bubbleType(BubbleType.BITMAP);
            return this;
        }

        public Builder arrowLocation(ArrowLocation arrowLocation) {
            this.mArrowLocation = arrowLocation;
            return this;
        }

        public Builder bubbleType(BubbleType bubbleType) {
            this.bubbleType = bubbleType;
            return this;
        }

        public Builder arrowCenter(boolean arrowCenter) {
            this.arrowCenter = arrowCenter;
            return this;
        }

        public BubbleDrawable build() {
            if (mRect == null) {
                throw new IllegalArgumentException("BubbleDrawable Rect can not be null");
            }
            return new BubbleDrawable(this);
        }
    }
}