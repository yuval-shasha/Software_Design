package il.ac.technion.cs.sd.books.app

import com.google.inject.Singleton
import com.google.inject.name.Names
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.sd.books.lib.*


class BookScoreModule : KotlinModule() {
    override fun configure() {
        bind<KeyValueElement>().to<BookWithScore>()
        bind<KeyListOfValuesElement>().to<Reviewer>()
        bind<BookScoreInitializer>().to<BookScoreInitializerImpl>()
        bind<BookScoreReader>().to<BookScoreReaderImpl>()
        bind<StorageLibrary>()
            .annotatedWith(Names.named("ReviewersDB"))
            .to<StorageLibrary>().`in`<Singleton>()
        bind<StorageLibrary>()
            .annotatedWith(Names.named("BooksDB"))
            .to<StorageLibrary>().`in`<Singleton>()
    }
}
