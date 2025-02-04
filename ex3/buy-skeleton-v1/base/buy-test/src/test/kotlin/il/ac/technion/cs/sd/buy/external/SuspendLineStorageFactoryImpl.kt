package il.ac.technion.cs.sd.buy.external

import com.google.inject.Inject
import kotlinx.coroutines.delay

class SuspendLineStorageFactoryImpl @Inject constructor() : SuspendLineStorageFactory {
    private val productsSuspendLineStorage = SuspendLineStorageImpl()
    private val ordersSuspendLineStorage = SuspendLineStorageImpl()
    private val usersSuspendLineStorage = SuspendLineStorageImpl()

    override suspend fun open(fileName: String): SuspendLineStorage {
        delay (100)
        return if (fileName == "products") {
            productsSuspendLineStorage
        } else if (fileName == "orders") {
            ordersSuspendLineStorage
        } else {
            usersSuspendLineStorage
        }
    }
}