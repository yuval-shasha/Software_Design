package il.ac.technion.cs.sd.buy.external

import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule

class LineStorageModule : KotlinModule()
{
    override fun configure()
    {
        bind<SuspendLineStorageFactory>().to<SuspendLineStorageFactoryImpl>().`in`<Singleton>()
    }
}