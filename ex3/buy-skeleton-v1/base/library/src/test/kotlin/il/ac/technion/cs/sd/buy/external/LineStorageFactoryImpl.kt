package il.ac.technion.cs.sd.buy.external

import kotlinx.coroutines.delay

class SuspendLineStorageFactoryImpl : SuspendLineStorageFactory {
    var products = SuspendLineStorageImpl()
    var orders = SuspendLineStorageImpl()
    var users = SuspendLineStorageImpl()

    override suspend fun open(fileName: String): SuspendLineStorage {
        delay (100)
        if (fileName == "products") {
            return products
        }
        else if (fileName == "orders") {
            return orders
        }
        else {
            return users
        }
    }
}