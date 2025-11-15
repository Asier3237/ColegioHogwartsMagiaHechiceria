package com.example.Service

import DAO.UsuarioDaoImpl.getHouseOccupancy

class HouseService {

    fun selectHouse(preferences: List<Int>): Int {
        var selected: Int? = null
        val probabilities = listOf(70, 40, 30, 20)

        if (preferences.isEmpty()) {
            val houseOccupancy: Map<Int, Int> = getHouseOccupancy()
            val houses = houseOccupancy.entries.sortedBy { it.value }.map { it.key }

            var i = 0
            while (i < houses.size && selected == null) {
                val p = probabilities.getOrElse(i) { probabilities.last() }
                if ((0..100).random() <= p) selected = houses[i]
                i++
            }
            if (selected == null) selected = houses.first()
        } else {
            var i = 0
            while (i < preferences.size && selected == null) {
                val p = probabilities.getOrElse(i) { probabilities.last() }
                if ((0..100).random() <= p) selected = preferences[i]
                i++
            }
            if (selected == null) selected = preferences.last()
        }

        return selected
    }




}