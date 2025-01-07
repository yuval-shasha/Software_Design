package il.ac.technion.cs.sd.books.app

import dev.misfitlabs.kotlinguice4.KotlinModule

class BookScoreModule : KotlinModule() {
    override fun configure() {
        bind<BookScoreInitializer>().to<BookScoreInitializerImpl>()
        bind<BookScoreReader>().to<BookScoreReaderImpl>()
    }
}
