package com.bielcode.stockmobile.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveUser(account: Account) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = account.email ?: ""
            preferences[NAME_KEY] = account.name ?: ""
            preferences[PHONE_KEY] = account.phone ?: ""
            preferences[ROLE_KEY] = account.role ?: ""
        }
        Log.d(
            "UserPreference",
            "saveUser: ${account.email} ${account.name} ${account.phone} ${account.role}"
        )
//        Log.d(
//            "UserPreference",
//            "saveUser: ${dataStore.data.collect()}"
//        )
    }

    fun getUser(): Flow<Account> {
        return dataStore.data.map { preferences ->
            Account(
                preferences[EMAIL_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ROLE_KEY] ?: ""
            )
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // New methods to save and retrieve location
    suspend fun saveLocation(address: String, coordinate: String) {
        dataStore.edit { preferences ->
            preferences[ADDRESS_KEY] = address
            preferences[COORDINATE_KEY] = coordinate
        }
    }

    fun getLocation(): Flow<Pair<String, String>> {
        return dataStore.data.map { preferences ->
            val address = preferences[ADDRESS_KEY] ?: ""
            val coordinate = preferences[COORDINATE_KEY] ?: ""
            Pair(address, coordinate)
        }
    }

    suspend fun clearLocation(){
        dataStore.edit { preferences ->
            preferences.remove(ADDRESS_KEY)
            preferences.remove(COORDINATE_KEY)
        }
    }

    // Fungsi untuk menyimpan transaksi
    suspend fun saveTransaction(transaction: Transaction) {
        val transactionJson = transactionToJson(transaction).toString()
        dataStore.edit { preferences ->
            preferences[TRANSACTION_KEY] = transactionJson
        }
    }

    // Fungsi untuk mengambil transaksi
    fun getTransaction(): Flow<Transaction?> {
        return dataStore.data.map { preferences ->
            val transactionJson = preferences[TRANSACTION_KEY] ?: return@map null
            jsonToTransaction(JSONObject(transactionJson))
        }
    }

    // Fungsi untuk menghapus transaksi
    suspend fun clearTransaction() {
        dataStore.edit { preferences ->
            preferences.remove(TRANSACTION_KEY)
            preferences.remove(TRANSACTION_CODE_KEY)
            preferences.remove(DOCUMENT_PDF_URI_KEY)
            preferences.remove(DOCUMENT_JPG_URI_KEY)

        }
    }

    private fun transactionToJson(transaction: Transaction): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("transactionCode", transaction.transactionCode)
        jsonObject.put("transactionAddress", transaction.transactionAddress)
        jsonObject.put("transactionContact", JSONObject(transaction.transactionContact))
        jsonObject.put("transactionCoordination", JSONObject().apply {
            put("latitude", transaction.transactionCoordination?.latitude ?: 0.0)
            put("longitude", transaction.transactionCoordination?.longitude ?: 0.0)
        })
        jsonObject.put("transactionDate", transaction.transactionDate?.time)
        jsonObject.put("transactionDestination", transaction.transactionDestination)
        jsonObject.put("transactionDocumentUrl", transaction.transactionDocumentUrl)
        jsonObject.put("transactionDocumentationUrl", transaction.transactionDocumentationUrl)
        val itemsArray = JSONArray()
        transaction.transactionItems.forEach { (_, item) ->
            itemsArray.put(JSONObject().apply {
                put("isChecked", item.isChecked)
                put("itemCatalog", item.itemCatalog)
                put("itemName", item.itemName)
                put("itemQty", item.itemQty)
                put("itemSize", item.itemSize)
            })
        }
        jsonObject.put("transactionItems", itemsArray)
        jsonObject.put("transactionPhone", transaction.transactionPhone)
        jsonObject.put("transactionType", transaction.transactionType)
        return jsonObject
    }

    private fun jsonToTransaction(jsonObject: JSONObject): Transaction {
        val itemsArray = jsonObject.getJSONArray("transactionItems")
        val itemsMap = mutableMapOf<String, TransactionItem>()
        for (i in 0 until itemsArray.length()) {
            val itemJson = itemsArray.getJSONObject(i)
            val item = TransactionItem(
                itemJson.getBoolean("isChecked"),
                itemJson.getString("itemCatalog"),
                itemJson.getString("itemName"),
                itemJson.getInt("itemQty"),
                itemJson.getString("itemSize")
            )
            itemsMap[item.itemCatalog] = item
        }
        return Transaction(
            jsonObject.getString("transactionCode"),
            jsonObject.getString("transactionAddress"),
            jsonToMap(jsonObject.getJSONObject("transactionContact")),
            GeoPoint(
                jsonObject.getJSONObject("transactionCoordination").getDouble("latitude"),
                jsonObject.getJSONObject("transactionCoordination").getDouble("longitude")
            ),
            Date(jsonObject.getLong("transactionDate")),
            jsonObject.getString("transactionDestination"),
            jsonObject.getString("transactionDocumentUrl"),
            jsonObject.getString("transactionDocumentationUrl"),
            itemsMap,
            jsonObject.getString("transactionPhone"),
            jsonObject.getString("transactionType")
        )
    }

    private fun jsonToMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = jsonObject.get(key)
        }
        return map
    }

    suspend fun saveTransactionCode(transactionCode: String) {
        dataStore.edit { preferences ->
            preferences[TRANSACTION_CODE_KEY] = transactionCode
        }
    }

    val transactionCodeFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TRANSACTION_CODE_KEY]
        }

    suspend fun getTransactionCode(): Flow<String?> {
        return transactionCodeFlow
    }

    suspend fun saveDocumentUris(transactionCode: String, pdfUri: String, jpgUri: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("${transactionCode}_pdf_uri")] = pdfUri
            preferences[stringPreferencesKey("${transactionCode}_jpg_uri")] = jpgUri
        }
    }

    fun getDocumentUris(transactionCode: String): Flow<Pair<String?, String?>> {
        return dataStore.data.map { preferences ->
            val pdfUri = preferences[stringPreferencesKey("${transactionCode}_pdf_uri")]
            val jpgUri = preferences[stringPreferencesKey("${transactionCode}_jpg_uri")]
            Pair(pdfUri, jpgUri)
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val COORDINATE_KEY = stringPreferencesKey("coordinate")
        private val TRANSACTION_KEY = stringPreferencesKey("transaction")
        private val TRANSACTION_CODE_KEY = stringPreferencesKey("transaction_code")
        private val DOCUMENT_PDF_URI_KEY = stringPreferencesKey("document_pdf_uri")
        private val DOCUMENT_JPG_URI_KEY = stringPreferencesKey("document_jpg_uri")


        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}