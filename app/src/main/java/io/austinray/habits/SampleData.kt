package io.austinray.habits

import java.time.LocalDate

val today: LocalDate = LocalDate.now()

val SAMPLE_DATA = listOf(
    Theme(
        name = "Physical Health",
        habits = listOf(
            Habit(
                name = "Drink 1 gallon water",
                createDate = today.minusDays(6),
                completeDates =
                mutableListOf(
                    today.minusDays(1),
                    today.minusDays(2),
                    today.minusDays(3)
                )
            ),
            Habit(
                name = "Do push-ups",
                createDate = today.minusDays(6),
                completeDates =
                mutableListOf(
                    today.minusDays(1),
                    today.minusDays(3)
                )
            )
        )
    ),
    Theme(
        name = "Mental Health",
        habits = listOf(
            Habit(
                name = "Meditate",
                createDate = today.minusDays(6),
                completeDates =
                mutableListOf(
                    today.minusDays(1),
                    today.minusDays(2),
                    today.minusDays(3),
                    today.minusDays(4),
                    today.minusDays(5)
                )
            )
        )
    )
)