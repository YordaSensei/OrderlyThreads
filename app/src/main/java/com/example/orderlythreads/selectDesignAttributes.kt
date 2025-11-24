package com.example.orderlythreads

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.animation.LayoutTransition
import android.widget.ImageView
import androidx.core.view.get
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.core.view.isGone


class selectDesignAttributes : AppCompatActivity() {
    private lateinit var upperWearInfo: LinearLayout
    private lateinit var lowerWearInfo: LinearLayout
    private lateinit var upperWearSign: ImageView
    private lateinit var lowerWearSign: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_design_attributes)

        upperWearInfo = findViewById(R.id.upperWearLayout)
        upperWearSign = findViewById(R.id.upperWearIcon)
        lowerWearInfo = findViewById(R.id.lowerWearLayout)
        lowerWearSign = findViewById(R.id.lowerWearIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    fun expandUpperWear(view: View) {
        val showUpperWear = if (upperWearInfo.isGone) View.VISIBLE else View.GONE

        upperWearInfo.visibility = showUpperWear

        if (showUpperWear == View.VISIBLE) {
            upperWearSign.setImageResource(R.drawable.icon_minus)
        } else {
            upperWearSign.setImageResource(R.drawable.icon_plus)
        }

    }
    fun expandLowerWear(view: View) {
        val showLowerWear = if (lowerWearInfo.isGone) View.VISIBLE else View.GONE

        lowerWearInfo.visibility = showLowerWear

        if (showLowerWear == View.VISIBLE) {
            lowerWearSign.setImageResource(R.drawable.icon_minus)
        } else {
            lowerWearSign.setImageResource(R.drawable.icon_plus)
        }

    }

}
