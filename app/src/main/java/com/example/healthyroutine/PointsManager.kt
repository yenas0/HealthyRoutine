package com.example.healthyroutine

object PointsManager {
    private var points: Int = 0

    fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
    }

    fun removePoints(pointsToRemove: Int) {
        points -= pointsToRemove
    }

    fun getPoints(): Int {
        return points
    }
}
