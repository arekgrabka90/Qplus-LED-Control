package com.arekgrabka90.qplusledcontrol.led

import android.util.Log
import kotlinx.coroutines.delay

/**
 * LED effects and animations.
 */
class LEDEffects {
    companion object {
        private const val TAG = "LEDEffects"
    }

    /**
     * Breathing effect - gradually increase and decrease brightness.
     */
    suspend fun breathing(
        ledController: ILEDController,
        durationMs: Long = 2000
    ) {
        val steps = 10
        val stepDuration = durationMs / steps / 2

        // Breathing in
        for (i in 0..steps) {
            if (i % 2 == 0) {
                ledController.setNormalLED(true)
                delay(stepDuration)
            }
        }

        // Breathing out
        for (i in steps downTo 0) {
            if (i % 2 == 0) {
                ledController.setNormalLED(false)
                delay(stepDuration)
            }
        }
    }

    /**
     * Blinking effect - on and off cycles.
     */
    suspend fun blinking(
        ledController: ILEDController,
        onDurationMs: Long = 500,
        offDurationMs: Long = 500,
        cycles: Int = 5
    ) {
        repeat(cycles) {
            ledController.setNormalLED(true)
            delay(onDurationMs)
            ledController.setNormalLED(false)
            delay(offDurationMs)
        }
    }

    /**
     * Rainbow effect - cycle through multiple LEDs.
     */
    suspend fun rainbow(
        ledController: ILEDController,
        stepDurationMs: Long = 500,
        cycles: Int = 3
    ) {
        repeat(cycles) {
            // Normal LED
            ledController.setNormalLED(true)
            delay(stepDurationMs)
            ledController.setNormalLED(false)

            // Standby LED
            ledController.setStandbyLED(true)
            delay(stepDurationMs)
            ledController.setStandbyLED(false)

            // Network LED
            ledController.setNetworkLED(true)
            delay(stepDurationMs)
            ledController.setNetworkLED(false)
        }
    }

    /**
     * Random effect - random LED states.
     */
    suspend fun random(
        ledController: ILEDController,
        stepDurationMs: Long = 500,
        cycles: Int = 10
    ) {
        repeat(cycles) {
            val rand1 = Math.random() > 0.5
            val rand2 = Math.random() > 0.5
            val rand3 = Math.random() > 0.5

            ledController.setNormalLED(rand1)
            ledController.setStandbyLED(rand2)
            ledController.setNetworkLED(rand3)

            delay(stepDurationMs)
        }
    }
}
