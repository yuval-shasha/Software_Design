package il.ac.technion.cs.sd.buy.app

import dev.misfitlabs.kotlinguice4.KotlinModule

class BuyProductModule : KotlinModule() {
    override fun configure() {
        bind<BuyProductInitializer>().to<BuyProductInitializerImpl>()
        bind<BuyProductReader>().to<BuyProductReaderImpl>()
    }
}
