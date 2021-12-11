package com.enty.drawingapp

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.slider.RangeSlider
import petrov.kristiyan.colorpicker.ColorPicker
import java.io.OutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var paint: DrawView

    private lateinit var save: ImageButton
    private lateinit var color: ImageButton
    private lateinit var stroke: ImageButton
    private lateinit var undo: ImageButton

    private lateinit var rangeSlider: RangeSlider



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paint = findViewById(R.id.draw_view)
        rangeSlider = findViewById(R.id.rangebar)
        undo = findViewById(R.id.btn_undo)
        save =  findViewById(R.id.btn_save)
        color =  findViewById(R.id.btn_color)
        stroke =  findViewById(R.id.btn_stroke)

        undo.setOnClickListener {
            paint.undo()
        }
        save.setOnClickListener {
            val bitmap = paint.save()

            var outputStream: OutputStream? = null

            val contentValues: ContentValues = ContentValues()

            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png")

            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            try {
                uri?.let {
                    outputStream = contentResolver.openOutputStream(it)

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                    outputStream!!.close()
                }
            } catch (e: Exception){
                e.printStackTrace()
            }


        }

        color.setOnClickListener {
            val colorPicker = ColorPicker(this)
            colorPicker.setOnFastChooseColorListener(object: ColorPicker.OnFastChooseColorListener{
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    paint.currentColor = color

                }

                override fun onCancel() {
                    colorPicker.dismissDialog()
                }

            })

                .setColumns(5)
                .setDefaultColorButton(Color.parseColor("#000000"))
                .show()
        }
        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = 200f

        stroke.setOnClickListener {
            if (rangeSlider.visibility == View.VISIBLE){
                rangeSlider.visibility = View.GONE
            } else rangeSlider.visibility = View.VISIBLE
        }

        rangeSlider.addOnChangeListener { _, value, _ ->
            paint.strokeWidth = value.toInt()
        }

        val vto: ViewTreeObserver = paint.viewTreeObserver
        vto.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                paint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = paint.measuredWidth
                val height = paint.measuredHeight
                paint.setWidth(width, height)
            }
        })
    }
}