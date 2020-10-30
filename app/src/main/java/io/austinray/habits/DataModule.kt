/*
 *  This file is part of Habit Themes.
 * 
 *  Habit Themes is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Habit Themes is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Habit Themes. If not, see <https://www.gnu.org/licenses/>.
 */
package io.austinray.habits

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.austinray.habits.data.HabitThemeRepo
import io.austinray.habits.data.RoomHabitThemeRepo
import io.austinray.habits.viewmodel.ThemeDatabase
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideHabitThemeRepo(themeDatabase: ThemeDatabase): HabitThemeRepo {
        return RoomHabitThemeRepo(themeDatabase)
    }

    @Provides
    @Singleton
    fun provideThemeDatabase(@ApplicationContext appContext: Context): ThemeDatabase {
        return Room.databaseBuilder(appContext, ThemeDatabase::class.java, "theme-database")
            .build()
    }
}
