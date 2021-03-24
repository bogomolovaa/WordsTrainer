package bogomolov.aa.wordstrainer.dagger

import android.app.Application
import bogomolov.aa.wordstrainer.WordsTrainerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ViewModelsModule::class, InjectionsModule::class, MainModule::class])
interface AppComponent: AndroidInjector<WordsTrainerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(application: WordsTrainerApplication)
}