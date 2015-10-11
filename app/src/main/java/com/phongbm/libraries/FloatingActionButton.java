package com.phongbm.libraries;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.phongbm.freephonecall.R;

public class FloatingActionButton extends ImageButton {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MINI = 1;
    protected int type = TYPE_NORMAL;
    protected int shadowSize;
    protected boolean isMarginsSet = false;

    protected int colorPressed;
    protected int colorDisabled;
    protected int colorNormal;
    protected int normalIcon;
    protected Drawable normalIconDrawable;
    protected String labelText;

    public FloatingActionButton(Context context) {
        super(context);
        init(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        shadowSize = getDimension(R.dimen.fab_shadow_size);
        colorNormal = getColor(R.color.material_blue_500);
        colorPressed = getColor(R.color.material_blue_600);
        colorDisabled = getColor(android.R.color.darker_gray);
        if (attrs != null) initAttributes(context, attrs);
        updateBackground();
    }

    protected void initAttributes(Context context, AttributeSet attributeSet) {
        TypedArray attr = getTypedArray(context, attributeSet, R.styleable.FloatingActionButton);
        if (attr != null) {
            try {
                colorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal, getColor(R.color.material_blue_500));
                colorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed, getColor(R.color.material_blue_600));
                colorDisabled = attr.getColor(R.styleable.FloatingActionButton_fab_colorDisabled, colorDisabled);
                normalIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_normal_icon, 0);
                type = attr.getInt(R.styleable.FloatingActionButton_fab_type, TYPE_NORMAL);
                labelText = attr.getString(R.styleable.FloatingActionButton_fab_labelText);
            } finally {
                attr.recycle();
            }
        }
    }

    protected void updateBackground() {
        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{
                        createFillStateListDrawable(),
                        getIconDrawable()
                });

        int circleSize = getDimension(type == TYPE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
        int inset = (circleSize - getDimension(type == TYPE_NORMAL ? R.dimen.fab_icon_size : R.dimen.fab_icon_size_mini)) / 2;

        layerDrawable.setLayerInset(1,
                shadowSize + inset, shadowSize + inset,
                shadowSize + inset, shadowSize + inset);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(layerDrawable);
        }
    }

    protected StateListDrawable createFillStateListDrawable() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, createFillDrawable(colorPressed));
        drawable.addState(new int[]{-android.R.attr.state_enabled}, createFillDrawable(colorDisabled));
        drawable.addState(new int[]{}, createFillDrawable(colorNormal));
        return drawable;
    }

    protected Drawable createFillDrawable(int color) {
        OvalShape ovalShape = new OvalShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(color);

        Drawable shadowDrawable = ContextCompat.getDrawable(getContext(), type == TYPE_NORMAL ? R.drawable.shadow : R.drawable.shadow_mini);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shadowDrawable, shapeDrawable});
        layerDrawable.setLayerInset(1, shadowSize, shadowSize, shadowSize, shadowSize);
        return layerDrawable;
    }

    protected Drawable getIconDrawable() {
        if (normalIconDrawable == null)
            normalIconDrawable = getCopyOfDrawableFromResources(normalIcon);
        return normalIconDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getDimension(type == TYPE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
        size += shadowSize * 2;
        setMarginsWithoutShadow();
        setMeasuredDimension(size, size);
    }

    protected void setMarginsWithoutShadow() {
        if (!isMarginsSet) {
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                int leftMargin = layoutParams.leftMargin - shadowSize;
                int topMargin = layoutParams.topMargin - shadowSize;
                int rightMargin = layoutParams.rightMargin - shadowSize;
                int bottomMargin = layoutParams.bottomMargin - shadowSize;
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                requestLayout();
                isMarginsSet = true;
            }
        }
    }

    protected int getDimension(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    protected int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    protected Drawable getCopyOfDrawableFromResources(@DrawableRes int rid) {
        if (rid != 0) {
            return ContextCompat.getDrawable(getContext(), rid).mutate().getConstantState().newDrawable();
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

}