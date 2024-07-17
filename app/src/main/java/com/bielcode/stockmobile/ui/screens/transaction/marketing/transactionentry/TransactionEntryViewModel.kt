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
        fetchTransactionFromPreferences()
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

    fun saveTransaction(onTransactionSaved: (String) -> Unit) {
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
                transactionDate = _transactionDate.value ?: Date(),
                transactionDestination = _selectedPartner.value?.partnerName ?: "",
                transactionDocumentUrl = "",
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
            repository.saveTransactionToPreferences(transaction)
            Log.d("TransactionEntryVM", "Transaction saved with code: $transactionCode")
            onTransactionSaved(transactionCode)  // Call the callback with the transaction code
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

    fun saveDocumentUris(pdfUri: String, jpgUri: String) {
        viewModelScope.launch {
            val transactionCode = getTransactionCodeFromPreferences() ?: return@launch
            repository.saveDocumentUris(transactionCode, pdfUri, jpgUri)
        }
    }

    fun loadDocumentUris(transactionCode: String) {
        viewModelScope.launch {
            _documentUris.value = repository.getDocumentUris(transactionCode)
        }
    }
}
