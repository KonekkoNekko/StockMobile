package com.bielcode.stockmobile.ui.screens.transaction.marketing.transactionentry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Contact
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.data.repository.Repository
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class TransactionEntryViewModel(private val repository: Repository) : ViewModel() {
    private val _partners = MutableStateFlow<List<Partner>>(emptyList())
    val partners: StateFlow<List<Partner>> = _partners

    private val _selectedPartner = MutableStateFlow<Partner?>(null)
    val selectedPartner: StateFlow<Partner?> = _selectedPartner

    private val _filteredPartners = MutableStateFlow<List<Partner>>(emptyList())
    val filteredPartners: StateFlow<List<Partner>> = _filteredPartners

    private val _transactionType = MutableStateFlow("Penjualan")
    val transactionType: StateFlow<String> = _transactionType

    private val _transactionDate = MutableStateFlow(Date())
    val transactionDate: StateFlow<Date> = _transactionDate

    private val _selectedContacts = MutableStateFlow<List<Pair<Contact, Boolean>>>(emptyList())
    val selectedContacts: StateFlow<List<Pair<Contact, Boolean>>> = _selectedContacts

    private val _transactionItems = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactionItems: StateFlow<List<TransactionItem>> = _transactionItems

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _documentUris = MutableStateFlow<Pair<String?, String?>>(Pair(null, null))
    val documentUris: StateFlow<Pair<String?, String?>> = _documentUris

    init {
        fetchPartners()
        clearTransactionFromPreferences()
    }

    fun fetchPartners() {
        viewModelScope.launch {
            val partners = if (_transactionType.value == "Konsinyasi") {
                repository.getConsignPartners()
            } else {
                repository.getAllPartners()
            }
            _partners.value = partners
            _filteredPartners.value = partners
        }
    }

    fun selectPartner(partner: Partner) {
        _selectedPartner.value = partner
        _selectedContacts.value = partner.partnerContacts.map { it to false }
        _query.value = partner.partnerName // Update query value when partner is selected
    }

    fun toggleContactSelection(contact: Contact) {
        _selectedContacts.value = _selectedContacts.value.map {
            if (it.first == contact) {
                it.first to !it.second
            } else {
                it
            }
        }
    }

    fun setTransactionType(type: String) {
        _transactionType.value = type
        fetchPartners()
    }

    fun filterPartners(query: String) {
        _filteredPartners.value = if (query.isEmpty()) {
            _partners.value
        } else {
            _partners.value.filter {
                it.partnerName.contains(query, ignoreCase = true)
            }
        }
    }

    fun setTransactionDate(date: Date) {
        _transactionDate.value = date
    }

    fun saveTransactionToPreferences(onTransactionSaved: (String) -> Unit) {
        viewModelScope.launch {
            val transactionCode = generateTransactionCode()
            saveTransactionCodeToPreferences(transactionCode)  // Save transaction code to preferences

            val transaction = Transaction(
                transactionCode = transactionCode,
                transactionAddress = _selectedPartner.value?.partnerAddress ?: "",
                transactionContact = _selectedPartner.value?.partnerContacts?.firstOrNull()?.let {
                    mapOf(
                        "contactName" to it.contactName,
                        "contactPhone" to it.contactPhone,
                        "contactPosition" to it.contactPosition
                    )
                } ?: emptyMap(),
                transactionCoordination = _selectedPartner.value?.partnerCoordinate ?: GeoPoint(0.0, 0.0),
                transactionDate = _transactionDate.value,
                transactionDestination = _selectedPartner.value?.partnerName ?: "",
                transactionDocumentUrl = "",
                transactionDocumentationUrl = "",
                transactionItems = _transactionItems.value.associateBy { it.itemCatalog }.mapValues { entry ->
                    entry.value
                },
                transactionPhone = _selectedPartner.value?.partnerPhone ?: "",
                transactionType = _transactionType.value
            )

            repository.saveTransactionToPreferences(transaction)
            Log.d("TransactionEntryVM", "Transaction saved to preferences with code: $transactionCode")
            Log.d("TransactionEntryVM", "Transaction data: $transaction")
            onTransactionSaved(transactionCode)  // Call the callback with the transaction code
        }
    }

    // New function to clear transaction from preferences
    fun clearTransactionData() {
        viewModelScope.launch {
            repository.clearTransactionFromPreferences()
            Log.d("TransactionEntryVM", "Transaction preferences cleared")
            _transactionType.value = "Penjualan"
            _transactionDate.value = Date()
            _selectedPartner.value = null
            _selectedContacts.value = emptyList()
            _transactionItems.value = emptyList()
            _query.value = ""
        }
    }

    // Logging tambahan pada saveTransaction
    fun saveTransaction(onTransactionSaved: (String) -> Unit) {
        viewModelScope.launch {
            val transactionCode = _transaction.value?.transactionCode ?: generateTransactionCode()
            saveTransactionCodeToPreferences(transactionCode)  // Save transaction code to preferences

            val transaction = Transaction(
                transactionCode = transactionCode,
                transactionAddress = _selectedPartner.value?.partnerAddress ?: "",
                transactionContact = _selectedPartner.value?.partnerContacts?.firstOrNull()?.let {
                    mapOf(
                        "contactName" to it.contactName,
                        "contactPhone" to it.contactPhone,
                        "contactPosition" to it.contactPosition
                    )
                } ?: emptyMap(),
                transactionCoordination = _selectedPartner.value?.partnerCoordinate ?: GeoPoint(0.0, 0.0),
                transactionDate = _transactionDate.value ?: Date(),
                transactionDestination = _selectedPartner.value?.partnerName ?: "",
                transactionDocumentUrl = "transactions/$transactionCode",
                transactionDocumentationUrl = "",
                transactionItems = _transactionItems.value.associateBy { it.itemCatalog }.mapValues { entry ->
                    TransactionItem(
                        entry.value.isChecked,
                        entry.value.itemCatalog,
                        entry.value.itemName,
                        entry.value.itemQty,
                        entry.value.itemSize
                    )
                },
                transactionPhone = _selectedPartner.value?.partnerPhone ?: "",
                transactionType = _transactionType.value
            )

            repository.saveTransaction(transaction)
            clearTransactionData()  // Clear transaction data after saving
            onTransactionSaved(transactionCode)  // Call the callback with the transaction code
        }
    }

    fun updateTransaction(onTransactionUpdated: (String) -> Unit) {
        viewModelScope.launch {
            val transactionCode = _transaction.value?.transactionCode ?: generateTransactionCode()
            saveTransactionCodeToPreferences(transactionCode)  // Save transaction code to preferences

            val transaction = Transaction(
                transactionCode = transactionCode,
                transactionAddress = _selectedPartner.value?.partnerAddress ?: "",
                transactionContact = _selectedPartner.value?.partnerContacts?.firstOrNull()?.let {
                    mapOf(
                        "contactName" to it.contactName,
                        "contactPhone" to it.contactPhone,
                        "contactPosition" to it.contactPosition
                    )
                } ?: emptyMap(),
                transactionCoordination = _selectedPartner.value?.partnerCoordinate ?: GeoPoint(0.0, 0.0),
                transactionDate = _transactionDate.value ?: Date(),
                transactionDestination = _selectedPartner.value?.partnerName ?: "",
                transactionDocumentUrl = "transactions/$transactionCode",
                transactionDocumentationUrl = "",
                transactionItems = _transactionItems.value.associateBy { it.itemCatalog }.mapValues { entry ->
                    TransactionItem(
                        entry.value.isChecked,
                        entry.value.itemCatalog,
                        entry.value.itemName,
                        entry.value.itemQty,
                        entry.value.itemSize
                    )
                },
                transactionPhone = _selectedPartner.value?.partnerPhone ?: "",
                transactionType = _transactionType.value
            )

            repository.updateTransactionInFirebase(transaction)
            clearTransactionData()  // Clear transaction data after updating
            onTransactionUpdated(transactionCode)  // Call the callback with the transaction code
        }
    }

    fun saveTransactionToFirebase() {
        viewModelScope.launch {
            _transaction.value?.let { transaction ->
                repository.saveTransaction(transaction)
                repository.clearTransactionFromPreferences()
                Log.d("TransactionEntryVM", "Transaction uploaded to Firebase and cleared from preferences")
            }
        }
    }

    private suspend fun generateTransactionCode(): String {
        val prefix = if (_transactionType.value == "Konsinyasi") "CNS" else "SLD"
        val destination = (_selectedPartner.value?.partnerName ?: "").take(5).replace(" ", "").toUpperCase()
        val count = repository.getTransactionCountByPrefix("$prefix-$destination-") + 1
        val transactionCode = "$prefix-$destination-$count"
        Log.d("TransactionEntryVM", "Generated Transaction Code: $transactionCode")
        return transactionCode
    }

    fun fetchTransactionDetails(transactionCode: String) {
        viewModelScope.launch {
            val transaction = repository.getTransactionByCode(transactionCode)
            _transaction.value = transaction

            transaction?.let {
                _transactionType.value = it.transactionType
                _transactionDate.value = it.transactionDate ?: Date()
                _transactionItems.value = it.transactionItems.values.toList()
                _selectedPartner.value = partners.value.find { partner -> partner.partnerName == it.transactionDestination }
                _query.value = _selectedPartner.value?.partnerName ?: ""

                // Update _selectedContacts with transaction contact details
                _selectedContacts.value = _selectedPartner.value?.partnerContacts?.map { contact ->
                    contact to (it.transactionContact["contactName"] == contact.contactName)
                } ?: emptyList()

                Log.d("TransactionEntryVM", "Selected Contacts: ${_selectedContacts.value}")
            }
        }
    }


    fun updateTransactionInFirebase(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransactionInFirebase(transaction)
                clearTransactionData()  // Clear transaction data after updating
                Log.d("TransactionEntryVM", "Transaction updated in Firebase with code: ${transaction.transactionCode}")
            } catch (e: Exception) {
                Log.e("TransactionEntryVM", "Error updating transaction in Firebase", e)
            }
        }
    }


    fun addTransactionItem(item: TransactionItem) {
        _transactionItems.value += item
        Log.d("TransactionEntryVM", "Added item: $item")
        Log.d("TransactionEntryVM", "Current transaction items: ${_transactionItems.value}")
        updateTransactionInPreferences()
    }

    fun removeTransactionItem(item: TransactionItem) {
        _transactionItems.value = _transactionItems.value.filterNot { it == item }
        Log.d("TransactionEntryVM", "Removed item: $item")
        Log.d("TransactionEntryVM", "Current transaction items: ${_transactionItems.value}")
        updateTransactionInPreferences()
    }

    fun updateTransactionItems(items: List<TransactionItem>) {
        Log.d("TransactionEntryVM", "Updating transaction items: $items")
        _transactionItems.value = items
        updateTransactionInPreferences()
    }

    private fun fetchTransactionFromPreferences() {
        viewModelScope.launch {
            repository.getTransactionFromPreferences().collectLatest { transaction ->
                transaction?.let {
                    _transaction.value = it
                    _transactionType.value = it.transactionType
                    _transactionDate.value = it.transactionDate ?: Date()
                    _transactionItems.value = it.transactionItems.values.toList()
                    _selectedPartner.value = partners.value.find { partner -> partner.partnerName == it.transactionDestination }
                    _query.value = _selectedPartner.value?.partnerName ?: ""

                    // Mengupdate daftar kontak yang dipilih
                    _selectedContacts.value = _selectedPartner.value?.partnerContacts?.map { contact ->
                        contact to (it.transactionContact["contactName"] == contact.contactName)
                    } ?: emptyList()

                    Log.d("TransactionEntryVM", "Transaction restored from preferences: $it")
                }
            }
        }
    }


    internal fun updateTransactionInPreferences() {
        viewModelScope.launch {
            _transaction.value?.let { transaction ->
                val updatedTransaction = transaction.copy(
                    transactionItems = _transactionItems.value.associateBy { it.itemCatalog },
                    transactionDestination = _selectedPartner.value?.partnerName ?: "",
                    transactionType = _transactionType.value,
                    transactionDate = _transactionDate.value,
                    transactionContact = _selectedContacts.value.filter { it.second }.map { it.first }
                        .firstOrNull()?.let {
                            mapOf(
                                "contactName" to it.contactName,
                                "contactPhone" to it.contactPhone,
                                "contactPosition" to it.contactPosition
                            )
                        } ?: emptyMap()
                )
                repository.saveTransactionToPreferences(updatedTransaction)
                Log.d("TransactionEntryVM", "Transaction updated in preferences: $updatedTransaction")
            }
        }
    }

    fun clearTransactionFromPreferences() {
        viewModelScope.launch {
            repository.clearTransactionFromPreferences()
        }
    }

    fun restoreTransactionState() {
        viewModelScope.launch {
            fetchTransactionFromPreferences()
        }
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    fun saveTransactionCodeToPreferences(transactionCode: String) {
        viewModelScope.launch {
            Log.d("TransactionEntryVM", "Saving Transaction Code to Preferences: $transactionCode")
            repository.saveTransactionCodeToPreferences(transactionCode)
        }
    }

    fun getTransactionCodeFromPreferences(): String? {
        var code: String? = null
        viewModelScope.launch {
            code = repository.getTransactionCodeFromPreferences()
            Log.d("TransactionEntryVM", "Fetched Transaction Code from Preferences: $code")
        }
        return code
    }

    fun saveDocumentUrls(transactionCode: String, documentUrl: String) {
        viewModelScope.launch {
            val transaction = _transaction.value
            if (transaction != null) {
                val updatedTransaction = transaction.copy(
                    transactionDocumentUrl = documentUrl,
                    transactionDocumentationUrl = "$documentUrl.jpg"
                )
                _transaction.value = updatedTransaction
                repository.saveTransaction(updatedTransaction)
                Log.d("TransactionEntryVM", "Document URLs saved to transaction: $transactionCode")
            }
        }
    }

    fun loadDocumentUris(transactionCode: String) {
        viewModelScope.launch {
            _documentUris.value = repository.getDocumentUris(transactionCode)
        }
    }

    suspend fun getTransactionFromPreferences(): Flow<Transaction?> {
        return repository.getTransactionFromPreferences()
    }

    fun updateTransaction(transaction: Transaction) {
        _transaction.value = transaction
        _transactionType.value = transaction.transactionType
        _transactionDate.value = transaction.transactionDate ?: Date()
        _transactionItems.value = transaction.transactionItems.values.toList()
        _selectedPartner.value = partners.value.find { partner -> partner.partnerName == transaction.transactionDestination }
        _query.value = _selectedPartner.value?.partnerName ?: ""

        // Mengupdate daftar kontak yang dipilih
        _selectedContacts.value = _selectedPartner.value?.partnerContacts?.map { contact ->
            contact to (transaction.transactionContact["contactName"] == contact.contactName)
        } ?: emptyList()

        Log.d("TransactionEntryVM", "Transaction updated: $transaction")
    }

    fun deleteTransaction(transactionCode: String) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionCode)
            clearTransactionFromPreferences()
            Log.d("TransactionEntryVM", "Transaction deleted: $transactionCode")
        }
    }
}
