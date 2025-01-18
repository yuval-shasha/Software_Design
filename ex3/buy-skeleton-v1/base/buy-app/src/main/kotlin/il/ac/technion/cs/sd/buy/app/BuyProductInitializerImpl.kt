package il.ac.technion.cs.sd.buy.app

class BuyProductInitializerImpl : BuyProductInitializer {
    /** Saves the XML data persistently, so that it could be queried using BuyProductReader */
    override suspend fun setupXml(xmlData: String)
    {}
    /** Saves the JSON data persistently, so that it could be queried using BuyProductReader */
    override suspend fun setupJson(jsonData: String)
    {}
}