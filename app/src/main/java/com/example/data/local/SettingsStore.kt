package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppSettings
import com.example.data.model.CustomerUser
import com.example.data.model.OwnerBankInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.fastBiteDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "fastbite_settings"
)

class SettingsStore(private val context: Context) {

    private object Keys {
        val CUSTOMER_NAME = stringPreferencesKey("customer_name")
        val CUSTOMER_CONTACT = stringPreferencesKey("customer_contact")
        val CUSTOMER_METHOD = stringPreferencesKey("customer_method")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val PENALTY_PERCENT = intPreferencesKey("penalty_percent")
        val BANK_HOLDER = stringPreferencesKey("bank_holder")
        val BANK_NAME = stringPreferencesKey("bank_name")
        val BANK_ACCOUNT = stringPreferencesKey("bank_account")
        val BANK_IFSC = stringPreferencesKey("bank_ifsc")
        val BANK_UPI = stringPreferencesKey("bank_upi")
    }

    val settingsFlow: Flow<AppSettings> = context.fastBiteDataStore.data.map { prefs ->
        val name = prefs[Keys.CUSTOMER_NAME]
        val contact = prefs[Keys.CUSTOMER_CONTACT]
        val method = prefs[Keys.CUSTOMER_METHOD]
        val user = if (!name.isNullOrBlank() && !contact.isNullOrBlank() && !method.isNullOrBlank()) {
            CustomerUser(name = name, contact = contact, method = method)
        } else {
            null
        }
        val defaults = OwnerBankInfo()
        AppSettings(
            customerUser = user,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            penaltyPercent = prefs[Keys.PENALTY_PERCENT] ?: 15,
            ownerBank = OwnerBankInfo(
                accountHolder = prefs[Keys.BANK_HOLDER] ?: defaults.accountHolder,
                bankName = prefs[Keys.BANK_NAME] ?: defaults.bankName,
                accountNumber = prefs[Keys.BANK_ACCOUNT] ?: defaults.accountNumber,
                ifsc = prefs[Keys.BANK_IFSC] ?: defaults.ifsc,
                upiId = prefs[Keys.BANK_UPI] ?: defaults.upiId
            )
        )
    }

    suspend fun saveCustomerUser(user: CustomerUser?) {
        context.fastBiteDataStore.edit { prefs ->
            if (user == null) {
                prefs.remove(Keys.CUSTOMER_NAME)
                prefs.remove(Keys.CUSTOMER_CONTACT)
                prefs.remove(Keys.CUSTOMER_METHOD)
            } else {
                prefs[Keys.CUSTOMER_NAME] = user.name
                prefs[Keys.CUSTOMER_CONTACT] = user.contact
                prefs[Keys.CUSTOMER_METHOD] = user.method
            }
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.fastBiteDataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setPenaltyPercent(percent: Int) {
        context.fastBiteDataStore.edit { prefs ->
            prefs[Keys.PENALTY_PERCENT] = percent.coerceIn(0, 50)
        }
    }

    suspend fun saveOwnerBank(bank: OwnerBankInfo) {
        context.fastBiteDataStore.edit { prefs ->
            prefs[Keys.BANK_HOLDER] = bank.accountHolder
            prefs[Keys.BANK_NAME] = bank.bankName
            prefs[Keys.BANK_ACCOUNT] = bank.accountNumber
            prefs[Keys.BANK_IFSC] = bank.ifsc
            prefs[Keys.BANK_UPI] = bank.upiId
        }
    }
}
