package com.bielcode.stockmobile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.data.repository.Repository
import com.bielcode.stockmobile.ui.screens.authentication.login.LoginViewModel
import com.bielcode.stockmobile.ui.screens.authentication.register.RegisterViewModel
import com.bielcode.stockmobile.ui.screens.home.HomeViewModel
import com.bielcode.stockmobile.ui.screens.partner.contactentry.ContactEntryViewModel
import com.bielcode.stockmobile.ui.screens.partner.detail.PartnerDetailViewModel
import com.bielcode.stockmobile.ui.screens.partner.entry.PartnerEntryViewModel
import com.bielcode.stockmobile.ui.screens.partner.PartnerViewModel
import com.bielcode.stockmobile.ui.screens.stock.StockViewModel
import com.bielcode.stockmobile.ui.screens.stock.detail.StockDetailViewModel
import com.bielcode.stockmobile.ui.screens.stock.entry.StockEntryViewModel
import com.bielcode.stockmobile.ui.screens.transaction.TransactionDetail.TransactionDetailViewModel
import com.bielcode.stockmobile.ui.screens.transaction.TransactionViewModel
import com.bielcode.stockmobile.ui.screens.transaction.gudang.TransactionDeliveryConfirm.TransactionDeliveryConfirmScreenPreview
import com.bielcode.stockmobile.ui.screens.transaction.gudang.TransactionDeliveryConfirm.TransactionDeliveryConfirmViewModel
import com.bielcode.stockmobile.ui.screens.transaction.marketing.SearchAddProduct.SearchAddProductViewModel
import com.bielcode.stockmobile.ui.screens.transaction.marketing.TransactionEntry.TransactionEntryViewModel
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.ScannerViewModel
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.StockInputViewModel
import com.bielcode.stockmobile.ui.screens.utility.camera.CameraViewModel
import com.bielcode.stockmobile.ui.screens.utility.mapspicker.MapsPickerViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Authentication Flow
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            // First Bottom Nav
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StockViewModel::class.java) -> {
                StockViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StockDetailViewModel::class.java) -> {
                StockDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScannerViewModel::class.java) -> {
                ScannerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StockInputViewModel::class.java) -> {
                StockInputViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StockEntryViewModel::class.java) -> {
                StockEntryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(repository) as T
            }

            // Partners
            modelClass.isAssignableFrom(PartnerViewModel::class.java) -> {
                PartnerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PartnerDetailViewModel::class.java) -> {
                PartnerDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PartnerEntryViewModel::class.java) -> {
                PartnerEntryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ContactEntryViewModel::class.java) -> {
                ContactEntryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsPickerViewModel::class.java) -> {
                MapsPickerViewModel(repository) as T
            }

            // Transaction
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TransactionDetailViewModel::class.java) -> {
                TransactionDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TransactionEntryViewModel::class.java) -> {
                TransactionEntryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SearchAddProductViewModel::class.java) -> {
                SearchAddProductViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TransactionDeliveryConfirmViewModel::class.java) -> {
                TransactionDeliveryConfirmViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}