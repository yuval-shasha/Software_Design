package il.ac.technion.cs.sd.books.external

import dev.misfitlabs.kotlinguice4.KotlinModule
import io.mockk.*

class LineStorageModule : KotlinModule()
{
    override fun configure()
    {
        val lineStorageFactoryMock = mockk<LineStorageFactoryImpl>()
        every { lineStorageFactoryMock.reviewers } returns LineStorageImpl()
        every { lineStorageFactoryMock.books } returns LineStorageImpl()
        every { lineStorageFactoryMock.open(any()) } answers {
            when (firstArg<String>()) {
                "reviewers" -> lineStorageFactoryMock.reviewers
                else -> lineStorageFactoryMock.books
            }
        }

        bind<LineStorageFactory>().toInstance(lineStorageFactoryMock)
    }
}
