package io.maido.m8client

import android.os.Build
import android.util.Log
import android.view.HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
import android.view.HapticFeedbackConstants.VIRTUAL_KEY
import android.view.HapticFeedbackConstants.VIRTUAL_KEY_RELEASE
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.OnTouchListener

internal class M8TouchListener(
    private val key: M8Key,
) : OnTouchListener {

    companion object {
        private val TAG = M8TouchListener::class.simpleName
        private val modifiers = HashSet<M8Key>()
        private const val reset = 132.toChar()
        private const val exit = 96.toChar()
        fun resetModifiers() = modifiers.clear()

        private external fun sendClickEvent(event: Char)

        external fun resetScreen()

        private external fun exit()

        fun handleTouch(key: M8Key, action: Int): Boolean {
            when (action) {
                ACTION_DOWN, ACTION_POINTER_DOWN -> {
                    modifiers.add(key)
                    when (val code = key.getCode(modifiers)) {
                        reset -> {
                            Log.d(TAG, "Sending reset")
                            resetScreen()
                        }

                        exit -> {
                            Log.d(TAG, "Sending exit")
                            exit()
                        }

                        else -> {
                            Log.d(TAG, "Sending $key as ${code.code}")
                            sendClickEvent(code)
                        }
                    }
                }

                ACTION_UP, ACTION_POINTER_UP -> {
                    modifiers.remove(key)
                    Log.d(TAG, "Key up $key")
                    sendClickEvent(0.toChar())
                }
            }
            return true
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        handleTouch(key, motionEvent.actionMasked)
        when (motionEvent.actionMasked) {
            ACTION_DOWN, ACTION_POINTER_DOWN -> {
                view.performHapticFeedback(VIRTUAL_KEY, FLAG_IGNORE_VIEW_SETTING)
            }

            ACTION_UP, ACTION_POINTER_UP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    view.performHapticFeedback(VIRTUAL_KEY_RELEASE, FLAG_IGNORE_VIEW_SETTING)
                }
                view.performClick()
            }
        }
        return true
    }
}