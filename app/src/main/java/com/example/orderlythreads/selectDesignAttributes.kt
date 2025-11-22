package com.example.orderlythreads

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.animation.LayoutTransition
import androidx.core.view.get
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.core.view.isGone


class selectDesignAttributes : AppCompatActivity() {
    private lateinit var upperWearInfo: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_design_attributes)

        upperWearInfo = findViewById(R.id.upperWearLayout)

        upperWearInfo.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    fun expandUpperWear(view: View) {
        val showUpperWear = if (upperWearInfo.isGone) View.VISIBLE else View.GONE

        TransitionManager.beginDelayedTransition(upperWearInfo, AutoTransition())
        upperWearInfo.visibility = showUpperWear
    }

}
