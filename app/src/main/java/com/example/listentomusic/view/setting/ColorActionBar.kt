package com.example.listentomusic.view.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.listentomusic.R
import com.example.listentomusic.utils.Contain

class ColorActionBar : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val CHANGE_ACTION_BAR_COLOR = "change_action_bar_color"
    private val PREF_KEY_SWITCH = "pref_key_switch"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sPNavigation: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pre_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view!!.setBackgroundColor(resources.getColor(android.R.color.white))
        return view
    }


    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                Color.parseColor(
                    preferenceManager.sharedPreferences.getString(
                        CHANGE_ACTION_BAR_COLOR,
                        "#3F51B5"
                    )
                )
            )
        )

        val colorActionBar = findPreference<Preference>(CHANGE_ACTION_BAR_COLOR)
        val colorSelectedValue =
            preferenceManager.sharedPreferences.getString(CHANGE_ACTION_BAR_COLOR, "Blue")
        colorActionBar?.summary = Contain.ENTRIES_APP_COLOR_ACTION_BAR[colorSelectedValue]

    }

    var color = R.color.black;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        when (p1) {
            CHANGE_ACTION_BAR_COLOR -> {
                (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
                    ColorDrawable(Color.parseColor(p0?.getString(p1, "")))
                )

                val colorActionBar = findPreference<Preference>(CHANGE_ACTION_BAR_COLOR)
                val colorSelectedValue =
                    preferenceManager.sharedPreferences.getString(CHANGE_ACTION_BAR_COLOR, "")
                colorActionBar?.summary = Contain.ENTRIES_APP_COLOR_ACTION_BAR[colorSelectedValue]

                val key = Contain.ENTRIES_APP_COLOR_ACTION_BAR[colorSelectedValue];
                when (key) {
                    "Yellow" -> color = R.color.yellow
                    "Blue" -> color = R.color.Blue
                    "Green" -> color = R.color.Green
                    "Pink" -> color = R.color.Pink
                    "Light blue" -> color = R.color.light_blue
                    "Orange" -> color = R.color.Orange

                }
                if (preferenceManager.sharedPreferences.getBoolean(PREF_KEY_SWITCH, false)) {
                    (activity as AppCompatActivity).window.navigationBarColor =
                        resources.getColor(color)
                }


                sharedPreferences = (activity as AppCompatActivity).getSharedPreferences(
                    "checkColorActionBar",
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString("codeColorActionBar", p0?.getString(p1, ""));
                editor.putInt("codeColorNavigation", color)
                editor.apply()
            }
            PREF_KEY_SWITCH -> {
                val colorSelectedValue = preferenceManager.sharedPreferences.getString(CHANGE_ACTION_BAR_COLOR, "")
                val key = Contain.ENTRIES_APP_COLOR_ACTION_BAR[colorSelectedValue];
                when (key) {
                    "Yellow" -> color = R.color.yellow
                    "Blue" -> color = R.color.Blue
                    "Green" -> color = R.color.Green
                    "Pink" -> color = R.color.Pink
                    "Light blue" -> color = R.color.light_blue
                    "Orange" -> color = R.color.Orange

                }

                sPNavigation = (activity as AppCompatActivity).getSharedPreferences(
                    "checkStatusNavigation",
                    Context.MODE_PRIVATE
                )

                val check = preferenceManager.sharedPreferences.getBoolean(PREF_KEY_SWITCH, false)
                if (check) {
                    (activity as AppCompatActivity).window.navigationBarColor =
                        resources.getColor(color)
                } else {
                    (activity as AppCompatActivity).window.navigationBarColor =
                        resources.getColor(R.color.black)
                }
                val editorNavigation = sPNavigation.edit()
                editorNavigation.putBoolean("checkNavigation",check)
                editorNavigation.apply()
            }
        }
    }
}