package il.ac.technion.cs.sd.books.external

import dev.misfitlabs.kotlinguice4.KotlinModule

class LineStorageModule : KotlinModule()
{
    override fun configure()
    {
        bind<LineStorageFactory>().to<LineStorageFactoryMock>()
        bind<LineStorage>().to<LineStorageMock>()
    }
}