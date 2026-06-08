package com.langapp.di

import com.langapp.data.repository.*
import com.langapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuth(i: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton
    abstract fun bindLanguage(i: LanguageRepositoryImpl): LanguageRepository
    @Binds @Singleton
    abstract fun bindLevel(i: LevelRepositoryImpl): LevelRepository
    @Binds @Singleton
    abstract fun bindLesson(i: LessonRepositoryImpl): LessonRepository
    @Binds @Singleton
    abstract fun bindExercise(i: ExerciseRepositoryImpl): ExerciseRepository
    @Binds @Singleton
    abstract fun bindProfile(i: ProfileRepositoryImpl): ProfileRepository
    @Binds @Singleton
    abstract fun bindEnrollment(i: EnrollmentRepositoryImpl): EnrollmentRepository
    @Binds @Singleton
    abstract fun bindProgress(i: ProgressRepositoryImpl): ProgressRepository
    @Binds @Singleton
    abstract fun bindAdmin(i: AdminRepositoryImpl): AdminRepository
}
