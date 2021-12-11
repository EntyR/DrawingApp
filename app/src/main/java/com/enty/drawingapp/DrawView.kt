package com.enty.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(ctx: Context, attributeSet: AttributeSet): View(ctx, attributeSet) {
    companion object {
        val touch_tolerance = 4f
    }
    private var x1: Float = 0f
    private var y1: Float = 0f

    private val paint: Paint = Paint()
    private lateinit var mPath: Path

    private val strokes: ArrayList<Stroke> = arrayListOf()

    var currentColor: Int = Color.GREEN
    var strokeWidth: Int = 20


    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private val mPaint = Paint(Paint.DITHER_FLAG)

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.alpha = 0xff
    }

    fun setWidth(width: Int, height: Int){
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = Canvas(mBitmap)
    }

    fun undo(){
        strokes.removeAt(strokes.size-1)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()

        val backgroundColor = Color.WHITE
        mCanvas.drawColor(backgroundColor)

        strokes.forEach {
            paint.color = it.color
            paint.strokeWidth = it.strokeWidth
            mCanvas.drawPath(it.path, paint)

        }
        canvas?.drawBitmap(mBitmap, 0f, 0f, mPaint)
        canvas?.restore()

    }

    fun touchStart(x: Float, y: Float){
        mPath = Path()
        val fp = Stroke(currentColor, strokeWidth.toFloat(), mPath)
        strokes.add(fp)

        mPath.reset()
        mPath.moveTo(x, y)
        x1 = x
        y1 = y
    }

    fun touchMove(x:Float, y: Float){
        val dx = Math.abs(x-x1)
        val dy = Math.abs(y-y1)

        if (dx>touch_tolerance || dy>touch_tolerance){
            mPath.quadTo(x1, y1, (x+x1)/2, (y+y1)/2)
            x1 = x
            y1 = y
        }

    }
    fun touchUp(){
        mPath.lineTo(x1, y1)
    }

    fun save() = mBitmap


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.x
            val y = it.y

            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    touchStart(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    touchMove(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    touchUp()
                    invalidate()
                }

            }


        }
        return true
    }

}