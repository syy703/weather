package com.weather.android.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;


import com.weather.android.R;
import com.weather.android.gson.PieChartData;
import com.weather.android.util.DisplayUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {

    private Context ctx;
    private DecimalFormat format;
    private List<PieChartData> mList;

    // 画笔
    private Paint arcPaint;
    private Paint linePaint;
    private Paint textPaint;

    // 圆心
    private float centerX;
    private float centerY;
    // 大圆半径
    private float radius;
    // 小圆半径
    private float radius_cover;
    private float total;
    // 初始角度
    private float startAngle;
    private float textAngle;

    private List<PointF[]> lineList;
    private List<PointF> textList;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ctx = context;
        lineList = new ArrayList<>();
        textList = new ArrayList<>();
        mList = new ArrayList<>();
        // 取整
        format = new DecimalFormat("##0");

        // 扇形画笔
        arcPaint = new Paint();
        // 抗锯齿
        arcPaint.setAntiAlias(true);
        // 防抖动
        arcPaint.setDither(true);
        // 设置画笔风格，实心
        arcPaint.setStyle(Paint.Style.FILL);

        // 间隔线画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(DisplayUtil.dip2px(ctx, 2));

        // 文本画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 取屏幕宽度与屏幕高度的较小值
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = Math.min(heightSpecSize, Math.min(DisplayUtil.getScreenSize(ctx)[0],
                    DisplayUtil.getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = Math.min(widthSpecSize, Math.min(DisplayUtil.getScreenSize(ctx)[0],
                    DisplayUtil.getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = height = Math.min(DisplayUtil.getScreenSize(ctx)[0],
                    DisplayUtil.getScreenSize(ctx)[1]);
        } else {
            width = widthSpecSize;
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textList.clear();
        lineList.clear();
        lineList = new ArrayList<>();
        textList = new ArrayList<>();

        if (mList != null) {
            // 初始化缩放系数
            float k = 1.0f;
            // 开始画各个扇形
            for (int i = 0; i < mList.size(); i++) {
                // 限定饼图所在矩形大小
                float left = centerX - radius * k;  // 矩形左边的X坐标
                float top = centerY - radius * k;  // 矩形顶部的Y坐标
                float right = centerX + radius * k;  // 矩形右边的X坐标
                float bottom = centerY + radius * k;  // 矩形底部的Y坐标
                RectF mRectF = new RectF(left, top, right, bottom);

                // 设置扇形颜色
                arcPaint.setColor(mList.get(i).color);
                // 绘制扇形，参数：画布大小，起始角度，扇形角度，是否描边，画笔属性
                canvas.drawArc(mRectF, startAngle, mList.get(i).percent / total * 360f, true, arcPaint);

                // 记录间隔线位置
                lineList.add(getLinePointF());

                // 记录文本位置
                textAngle = startAngle + mList.get(i).percent / total * 360f / 2;
                textList.add(getTextPointF(k));

                //重新计算起始角度
                startAngle += mList.get(i).percent / total * 360f;
                // 缩小扇形半径
                k -= 0.2f;
            }
            // 中心空白：绘制圆形遮盖，颜色设置与背景色一致
            arcPaint.setColor(ContextCompat.getColor(getContext(), R.color.color2));
            canvas.drawCircle(centerX, centerY, radius_cover, arcPaint);
            //绘制间隔线
            drawLine(canvas, lineList);
            //绘制文字
            drawText(canvas);
        }

    }

    /**
     * 获取间隔线位置 [开始坐标,结束坐标]
     */
    private PointF[] getLinePointF() {
        // 从圆心开始
        float startX = centerX;
        float startY = centerY;
        // 到扇形边界结束
        float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth())
                * Math.cos(Math.toRadians(startAngle)));
        float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth())
                * Math.sin(Math.toRadians(startAngle)));
        PointF startPoint = new PointF(startX, startY);
        PointF stopPoint = new PointF(stopX, stopY);
        return new PointF[]{startPoint, stopPoint};
    }

    /**
     * 获取文本位置
     */
    private PointF getTextPointF(float k) {
        float textPointX = (float) (centerX + 0.6 * k * radius * Math.cos(Math.toRadians(textAngle)));
        float textPointY = (float) (centerY + 0.6 * k * radius * Math.sin(Math.toRadians(textAngle)));
        return new PointF(textPointX, textPointY);
    }

    /**
     * 绘制间隔线
     */
    private void drawLine(Canvas canvas, List<PointF[]> pointFs) {
        for (PointF[] fp : pointFs) {
            canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
        }
    }

    /**
     * 绘制文本
     */
    private void drawText(Canvas canvas) {
        for (int i = 0; i < textList.size(); i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            // 字号
            textPaint.setTextSize(radius / 7);
            // 字体
            textPaint.setTypeface(Typeface.MONOSPACE);
            // 显示百分比
            canvas.drawText(format.format(mList.get(i).percent * 100 / total) + "%",
                    textList.get(i).x - 10, textList.get(i).y - 10, textPaint);
        }
    }

    /**
     * 设置扇形和遮盖圆半径
     */
    public void setRadius(int radius, int radius_cover) {
        this.radius = radius;
        this.radius_cover = DisplayUtil.dip2px(ctx, radius_cover);
    }

    /**
     * 设置间隔线的颜色
     */
    public void setLineColor(int color) {
        linePaint.setColor(color);
    }

    /**
     * 设置文本颜色
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    /**
     * 设置开始角度
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * 设置饼的数据
     */
    public void setPieChartData(List<PieChartData> mList) {
        total = 0;
        if (mList == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            total += mList.get(i).percent;
        }
        this.mList.clear();
        this.mList = mList;
        invalidate();
    }

}
