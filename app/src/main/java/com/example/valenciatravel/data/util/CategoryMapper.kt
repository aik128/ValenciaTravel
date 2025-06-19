package com.example.valenciatravel.data.util

object CategoryMapper {
    private val categoryToIdMap = mapOf(
        "Пляжи" to 1,
        "Археология" to 2,
        "Архитектура" to 3,
        "Храмы и соборы" to 4,
        "Сады и парки" to 5,
        "Исторические музеи" to 6,
        "Искусство" to 7,
        "Развлечения" to 8,
        "Активный отдых" to 9,
        "Рестораны" to 10
    )

    private val idToCategoryMap = mapOf(
        1 to "Пляжи",
        2 to "Археология",
        3 to "Архитектура",
        4 to "Храмы и соборы",
        5 to "Сады и парки",
        6 to "Исторические музеи",
        7 to "Искусство",
        8 to "Развлечения",
        9 to "Активный отдых",
        10 to "Рестораны"
    )

    fun getCategoryId(categoryName: String): Int {
        return categoryToIdMap[categoryName] ?: 0
    }

    fun getCategoryName(categoryId: Int): String {
        return idToCategoryMap[categoryId] ?: "Error"
    }

    fun getAllCategories(): List<String> {
        return categoryToIdMap.keys.toList().sorted()
    }
}