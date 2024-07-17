package com.bielcode.stockmobile.data.repository

import android.net.Uri
import android.util.Log
import com.bielcode.stockmobile.data.model.Contact
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.model.PartnerType
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.model.StockInput
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.data.preferences.Account
import com.bielcode.stockmobile.data.preferences.UserPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class Repository private constructor(
    private val userPreference: UserPreference,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    suspend fun saveSession(account: Account) {
        Log.d(TAG, "saveSession: $account")
        userPreference.saveUser(account)
    }

    fun getSession(): Flow<Account> = userPreference.getUser()

    suspend fun logout() {
        auth.signOut()
        userPreference.clearUser()
    }

    suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val accountDocument = firestore.collection("users").document(uid).get().await()
                    .toObject(Account::class.java)
                if (accountDocument != null) {
                    userPreference.saveUser(accountDocument)
                } else {
                    Log.e("Repository", "Account document is null")
                    throw Exception("Account document is null")
                }
            } else {
                Log.e("Repository", "User ID is null")
                throw Exception("User ID is null")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error logging in", e)
            throw e
        }
    }

    suspend fun register(email: String, password: String, account: Account) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).set(account).await()
                logout()  // Sign out immediately after registering
            } else {
                Log.e("Repository", "User ID is null")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error registering", e)
        }
    }

    suspend fun uploadFile(fileUri: Uri, fileName: String, destination: String) {
        val storageRef = storage.reference.child("$destination/$fileName")
        storageRef.putFile(fileUri).await()
    }

    suspend fun downloadFile(fileName: String, destination: String): Uri? {
        val storageRef = storage.reference.child("$destination/$fileName")
        return storageRef.downloadUrl.await()
    }

    suspend fun getTotalCurrentStock(): Int {
        var totalCurrentStock = 0

        try {
            val stocksCollection = firestore.collection("stocks").get().await()
            for (document in stocksCollection.documents) {
                val productDetails = document.get("productDetails") as Map<*, *>
                for ((_, value) in productDetails) {
                    val stockDetails = value as Map<*, *>
                    val stockCurrent = (stockDetails["stockCurrent"] as Long).toInt()
                    totalCurrentStock += stockCurrent
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalCurrentStock
    }

    suspend fun getStocks(): List<Stock> {
        return try {
            val stocksCollection = firestore.collection("stocks").get().await()
            val stocks = stocksCollection.documents.mapNotNull { document ->
                document.toObject(Stock::class.java)
            }
            Log.d("Repository", "Fetched stocks: $stocks")
            stocks
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching stocks", e)
            emptyList()
        }
    }

    suspend fun getImageUrl(imagePath: String): Uri? {
        return try {
            val storageReference = storage.reference.child(imagePath)
            storageReference.downloadUrl.await()
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching image URL", e)
            null
        }
    }

    suspend fun getProductDetailsByCatalog(catalog: String): Stock? {
        return try {
            val document = firestore.collection("stocks").document(catalog).get().await()
            Log.d("Repository", "Fetched document for catalog: $catalog -> ${document.data}")
            val stock = document.toObject(Stock::class.java)
            if (stock == null) {
                Log.e(
                    "Repository",
                    "Document for catalog: $catalog is null or cannot be converted to Stock"
                )
            }
            stock
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching product details for catalog: $catalog", e)
            null
        }
    }

    suspend fun saveTransaction(transactionItems: Map<String, TransactionItem>, destination: String, type: String) {
        val transactionData = mapOf(
            "transactionDestination" to destination,
            "transactionItems" to transactionItems,
            "transactionType" to type,
            "transactionDate" to com.google.firebase.Timestamp.now(), // Add timestamp
            "transactionCode" to "IN-${transactionItems.values.first().itemCatalog}-${transactionItems.values.first().itemSize}-1", // Example code generation
            "transactionAddress" to "", // Add other fields as necessary
            "transactionContact" to mapOf<String, Any>(), // Example empty map for contact
            "transactionCoordination" to com.google.firebase.firestore.GeoPoint(0.0, 0.0), // Example geolocation
            "transactionDocumentUrl" to "", // Example document URL
            "transactionDocumentationUrl" to "", // Example documentation URL
            "transactionPhone" to "" // Example phone number
        )
        firestore.collection("transactions").add(transactionData).await()
    }

    suspend fun saveProduct(stock: Stock) {
        try {
            firestore.collection("stocks").document(stock.productCatalog).set(stock).await()
        } catch (e: Exception) {
            Log.e("Repository", "Error saving product", e)
        }
    }

    suspend fun deleteProduct(catalog: String) {
        try {
            // Delete the document from Firestore
            firestore.collection("stocks").document(catalog).delete().await()

            // Define the path to the image in Firebase Storage
            val imagePath = "images/$catalog.png"

            // Get a reference to the image file
            val imageRef = storage.reference.child(imagePath)

            // Delete the image from Firebase Storage
            imageRef.delete().await()

            Log.d("Repository", "Product and image deleted successfully")
        } catch (e: Exception) {
            Log.e("Repository", "Error deleting product or image", e)
        }
    }

    suspend fun uploadPhoto(imageData: ByteArray, filename: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val storageRef = storage.reference
            val fileRef = storageRef.child(filename)

            val uploadTask = fileRef.putBytes(imageData)
            uploadTask.addOnSuccessListener {
                onSuccess(fileRef.path)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun getPartners(): List<Partner> {
        return try {
            val partnersCollection = firestore.collection("partners").get().await()
            val partners = partnersCollection.documents.mapNotNull { document ->
                document.toPartner()
            }
            Log.d("Repository", "Fetched partners: $partners")
            partners
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching partners", e)
            emptyList()
        }
    }

    // Custom deserialization function
    private fun DocumentSnapshot.toPartner(): Partner? {
        return try {
            Log.d("Repository", "Document Data: ${this.data}")
            val partnerTypeMap = this.get("partnerType") as? Map<String, Any>

            val partnerType = PartnerType(
                isClient = partnerTypeMap?.get("isClient") as? Boolean ?: false,
                isConsign = partnerTypeMap?.get("isConsign") as? Boolean ?: false
            )

            val contactsMap = this.get("partnerContacts") as? List<Map<String, Any>>
            val contacts = contactsMap?.map { contactMap ->
                Contact(
                    contactName = contactMap["contactName"] as? String ?: "",
                    contactPhone = contactMap["contactPhone"] as? String ?: "",
                    contactPosition = contactMap["contactPosition"] as? String ?: ""
                )
            } ?: emptyList()

            Partner(
                partnerAddress = this.getString("partnerAddress") ?: "",
                partnerCategory = this.getString("partnerCategory") ?: "",
                partnerContacts = contacts,
                partnerCoordinate = this.getGeoPoint("partnerCoordinate"),
                partnerName = this.getString("partnerName") ?: "",
                partnerPhone = this.getString("partnerPhone") ?: "",
                partnerPhotoUrl = this.getString("partnerPhotoUrl") ?: "",
                partnerType = partnerType
            )
        } catch (e: Exception) {
            Log.e("Repository", "Error mapping Partner", e)
            null
        }
    }

    suspend fun getPartnerDetailsByName(name: String): Partner?{
        return try {
            val partnersCollection = firestore.collection("partners")
                .whereEqualTo("partnerName", name)
                .get()
                .await()

            if (partnersCollection.documents.isNotEmpty()) {
                val document = partnersCollection.documents[0]
                Log.d("Repository", "Fetched document for name: $name -> ${document.data}")
                document.toPartner()
            } else {
                Log.e("Repository", "No document found for name: $name")
                null
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching partner details for name: $name", e)
            null
        }
    }

    suspend fun savePartner(partner: Partner) {
        val partnerData = hashMapOf(
            "partnerAddress" to partner.partnerAddress,
            "partnerCategory" to partner.partnerCategory,
            "partnerContacts" to partner.partnerContacts,
            "partnerCoordinate" to partner.partnerCoordinate,
            "partnerName" to partner.partnerName,
            "partnerPhone" to partner.partnerPhone,
            "partnerPhotoUrl" to partner.partnerPhotoUrl,
            "partnerType" to hashMapOf(
                "isClient" to partner.partnerType.isClient,
                "isConsign" to partner.partnerType.isConsign
            )
        )

        try {
            firestore.collection("partners")
                .document(partner.partnerName)
                .set(partnerData)
                .await()
            Log.d(TAG, "Partner saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving partner", e)
        }
    }


    suspend fun deletePartner(name: String) {
        try {
            // Delete the document from Firestore
            firestore.collection("partners").document(name).delete().await()

            // Define the path to the image in Firebase Storage
            val imagePath = "partners/$name.jpg"

            // Get a reference to the image file
            val imageRef = storage.reference.child(imagePath)

            // Delete the image from Firebase Storage
            imageRef.delete().await()

            Log.d("Repository", "Partner and image deleted successfully")
        } catch (e: Exception) {
            Log.e("Repository", "Error deleting partner or image", e)
        }
    }

    // Helper method to convert a Contact object to a Map
    private fun Contact.toMap(): Map<String, Any> {
        return mapOf(
            "contactName" to this.contactName,
            "contactPhone" to this.contactPhone,
            "contactPosition" to this.contactPosition
        )
    }

    // Helper method to convert a list of Contact objects to a list of Maps
    private fun List<Contact>.toMapList(): List<Map<String, Any>> {
        return this.map { it.toMap() }
    }

    // Helper method to convert a DocumentSnapshot to a list of Contact objects
    private fun DocumentSnapshot.toContactList(): List<Contact> {
        val contactsMap = this.get("partnerContacts") as? List<Map<String, Any>>
        return contactsMap?.map { contactMap ->
            Contact(
                contactName = contactMap["contactName"] as? String ?: "",
                contactPhone = contactMap["contactPhone"] as? String ?: "",
                contactPosition = contactMap["contactPosition"] as? String ?: ""
            )
        } ?: emptyList()
    }


    suspend fun addOrUpdateContact(partnerName: String, contact: Contact) {
        val partnerDoc = firestore.collection("partners").document(partnerName)
        val snapshot = partnerDoc.get().await()
        val currentContacts = snapshot.toContactList().toMutableList()

        // Find the index of the existing contact if it exists, else add the new contact
        val index = currentContacts.indexOfFirst { it.contactName == contact.contactName }
        if (index >= 0) {
            currentContacts[index] = contact
        } else {
            currentContacts.add(contact)
        }

        partnerDoc.update("partnerContacts", currentContacts.toMapList()).await()
    }

    suspend fun deleteContact(partnerName: String, contactName: String) {
        val partnerDoc = firestore.collection("partners").document(partnerName)
        val snapshot = partnerDoc.get().await()
        val currentContacts = snapshot.toContactList().filterNot { it.contactName == contactName }

        partnerDoc.update("partnerContacts", currentContacts.toMapList()).await()
    }

    suspend fun saveLocation(address: String, coordinate: String) {
        userPreference.saveLocation(address, coordinate)
    }

    fun getLocation(): Flow<Pair<String, String>> = userPreference.getLocation()
    suspend fun clearLocation(){
        userPreference.clearLocation()
    }


    suspend fun getTransactions(): List<Transaction> {
        return try {
            val snapshot = firestore.collection("transactions").get().await()
            snapshot.documents.mapNotNull { document -> document.toTransaction() }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching transactions", e)
            emptyList()
        }
    }

    private fun DocumentSnapshot.toTransaction(): Transaction? {
        return try {
            val transactionItemsMap = this.get("transactionItems") as? Map<String, Map<String, Any>>
            val transactionItems = transactionItemsMap?.mapValues { (_, itemMap) ->
                TransactionItem(
                    isChecked = itemMap["isChecked"] as? Boolean ?: false,
                    itemCatalog = itemMap["itemCatalog"] as? String ?: "",
                    itemName = itemMap["itemName"] as? String ?: "",
                    itemQty = (itemMap["itemQty"] as? Long)?.toInt() ?: 0,
                    itemSize = itemMap["itemSize"] as? String ?: ""
                )
            } ?: emptyMap()

            Transaction(
                transactionCode = this.getString("transactionCode") ?: "",
                transactionAddress = this.getString("transactionAddress") ?: "",
                transactionContact = this.get("transactionContact") as? Map<String, Any> ?: emptyMap(),
                transactionCoordination = this.getGeoPoint("transactionCoordination") ?: GeoPoint(0.0, 0.0),
                transactionDate = this.getTimestamp("transactionDate")?.toDate(),
                transactionDestination = this.getString("transactionDestination") ?: "",
                transactionDocumentUrl = this.getString("transactionDocumentUrl") ?: "",
                transactionDocumentationUrl = this.getString("transactionDocumentationUrl") ?: "",
                transactionItems = transactionItems,
                transactionPhone = this.getString("transactionPhone") ?: "",
                transactionType = this.getString("transactionType") ?: ""
            )
        } catch (e: Exception) {
            Log.e("Repository", "Error mapping Transaction", e)
            null
        }
    }

    suspend fun getAllPartners(): List<Partner> {
        return firestore.collection("partners").get().await().documents.mapNotNull { document ->
            document.toPartner()
        }
    }

    suspend fun getConsignPartners(): List<Partner> {
        return firestore.collection("partners")
            .whereEqualTo("partnerType.isConsign", true)
            .get().await().documents.mapNotNull { document ->
                document.toPartner()
            }
    }

    suspend fun saveTransaction(transaction: Transaction) {
        val transactionData = transaction.toMap()
        firestore.collection("transactions").add(transactionData).await()
    }

    suspend fun getTransactionCountByPrefix(prefix: String): Int {
        val snapshot = firestore.collection("transactions")
            .whereGreaterThanOrEqualTo("transactionCode", prefix)
            .whereLessThanOrEqualTo("transactionCode", "$prefix\uf8ff")
            .get().await()
        return snapshot.size()
    }

    private fun Transaction.toMap(): Map<String, Any?> {
        return mapOf(
            "transactionCode" to transactionCode,
            "transactionAddress" to transactionAddress,
            "transactionContact" to transactionContact,
            "transactionCoordination" to transactionCoordination,
            "transactionDate" to transactionDate,
            "transactionDestination" to transactionDestination,
            "transactionDocumentUrl" to transactionDocumentUrl,
            "transactionDocumentationUrl" to transactionDocumentationUrl,
            "transactionItems" to transactionItems.mapValues { it.value.toMap() },
            "transactionPhone" to transactionPhone,
            "transactionType" to transactionType
        )
    }

    private fun TransactionItem.toMap(): Map<String, Any?> {
        return mapOf(
            "isChecked" to isChecked,
            "itemCatalog" to itemCatalog,
            "itemName" to itemName,
            "itemQty" to itemQty,
            "itemSize" to itemSize
        )
    }

    suspend fun getTransactionByCode(transactionCode: String): Transaction? {
        val snapshot = firestore.collection("transactions")
            .whereEqualTo("transactionCode", transactionCode)
            .get().await()
        return if (snapshot.documents.isNotEmpty()) {
            snapshot.documents[0].toTransaction()
        } else {
            null
        }
    }

    // Fungsi untuk menyimpan transaksi ke dalam preferences
    suspend fun saveTransactionToPreferences(transaction: Transaction) {
        userPreference.saveTransaction(transaction)
    }

    // Fungsi untuk mengambil transaksi dari preferences
    fun getTransactionFromPreferences(): Flow<Transaction?> = userPreference.getTransaction()

    // Fungsi untuk menghapus transaksi dari preferences
    suspend fun clearTransactionFromPreferences() {
        userPreference.clearTransaction()
    }

    suspend fun saveTransactionCodeToPreferences(transactionCode: String) {
        userPreference.saveTransactionCode(transactionCode)
    }

    suspend fun getTransactionCodeFromPreferences(): String? {
        return userPreference.getTransactionCode().firstOrNull()
    }

    suspend fun saveDocumentUris(transactionCode: String, pdfUri: String, jpgUri: String) {
        userPreference.saveDocumentUris(transactionCode, pdfUri, jpgUri)
    }

    suspend fun getDocumentUris(transactionCode: String): Pair<String?, String?> {
        return userPreference.getDocumentUris(transactionCode).firstOrNull() ?: Pair(null, null)
    }



    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            userPreference: UserPreference,
            auth: FirebaseAuth,
            firestore: FirebaseFirestore,
            storage: FirebaseStorage
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, auth, firestore, storage)
            }
    }
}
