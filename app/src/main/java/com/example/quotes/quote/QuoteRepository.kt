package com.example.quotes.quote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.quotes.Result
import com.example.quotes.onIO
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class QuoteRepository(
    private val db: FirebaseFirestore,
    private val versionConfig: FirebaseRemoteConfig,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val VIEWED_IDS_KEY = stringPreferencesKey("viewed_ids")
        private const val DB_COLLECTION = "quotes"
        private const val FIELD_TEXT = "text"
        private const val FIELD_CATEGORY = "category"
        private val STRING_SET_FAVORITES_IDS_KEY = stringSetPreferencesKey("favorite_ids")
        private val LONG_LOCAL_VERSION_KEY = longPreferencesKey("store_version")
        const val REMOTE_STORE_VERSION_KEY = "store_version"
    }

    private var quotes = mutableListOf<QuoteModel>()
    private var viewsQuotesIds = setOf<Long>()
    private var localCurrentVersion: Long? = null
    private var favoriteQuotesIds = setOf<Long>()

    suspend fun getQuotesListSuspend(): List<QuoteModel> {
        if (quotes.isEmpty()) {
            fillQuotes()
        }
        fillViewedQuotes()
        loadFavoriteQuotes()
        return quotes.toList()
    }

    suspend fun saveViewedIds(id: Long) {
        if (viewsQuotesIds.contains(id)) return
        viewsQuotesIds = viewsQuotesIds.plus(id)
        val stringIds = viewsQuotesIds.toString()
        onIO {
            dataStore.edit { preferences ->
                preferences[VIEWED_IDS_KEY] = stringIds
            }
        }
    }

    suspend fun hasFavorites(): Boolean {
        val list = getFavoriteQuoteList()
        return list.isNotEmpty()
    }

    suspend fun getViewedQuotes(): List<QuoteModel> {
        val quoteList = getQuotesListSuspend()
        return quoteList.filter { it.isViewed }
    }

    suspend fun getFavoriteQuoteList(): List<QuoteModel> {
        if (quotes.isEmpty()) {
            fillQuotes()
            loadFavoriteQuotes()
        }
        return quotes.filter { quote -> quote.isFavorite }
    }

    fun getById(quoteId: Long) = quotes.first { it.id == quoteId }

    suspend fun addToFavorite(quoteId: Long): QuoteModel {
        val quote = quotes.first { quote -> quote.id == quoteId }
        favoriteQuotesIds = favoriteQuotesIds.plus(quoteId)
        saveFavoriteQuotes()
        return updateQuote(quote, true)
    }

    suspend fun removeFromFavorite(quoteId: Long): QuoteModel {
        val quote = quotes.first { quote -> quote.id == quoteId }
        favoriteQuotesIds = favoriteQuotesIds.minus(quoteId)
        saveFavoriteQuotes()
        return updateQuote(quote, false)
    }

    private suspend fun loadFavoriteQuotes() {
        val idSet = dataStore.data.first().getStringSetFavoritesIds()
        favoriteQuotesIds = idSet.map { it.toLong() }.toSet()
        fillFavoriteQuotes()
    }

    private suspend fun saveFavoriteQuotes() {
        onIO {
            val stringSet = favoriteQuotesIds.map { it.toString() }.toSet()
            dataStore.edit { pref ->
                pref[STRING_SET_FAVORITES_IDS_KEY] = stringSet
            }
        }
    }

    private fun fillFavoriteQuotes() {
        quotes = quotes.map { quote ->
            if (favoriteQuotesIds.any { id ->
                    quote.id == id
                }) quote.copy(isFavorite = true)
            else quote
        }.toMutableList()
    }

    private fun Preferences.getStringSetFavoritesIds() = this[STRING_SET_FAVORITES_IDS_KEY] ?: setOf()

    private fun Preferences.getStoreVersion() = this[LONG_LOCAL_VERSION_KEY] ?: 0

    private suspend fun loadLocalStoreVersion() {
        localCurrentVersion = dataStore.data.first().getStoreVersion()
    }

    private suspend fun loadViewedIds() {
        var idsString = dataStore.data.first().loadViewedIds()
        idsString = idsString.replace("[", "").replace("]", "")
        viewsQuotesIds = if (idsString.isEmpty()) mutableSetOf()
        else idsString.split(',').map { it.trim().toLong() }.toMutableSet()
    }

    private suspend fun syncVersions() {
        localCurrentVersion = versionConfig.getLong(REMOTE_STORE_VERSION_KEY)
        dataStore.edit {
            it[LONG_LOCAL_VERSION_KEY] = localCurrentVersion!!
        }
    }

    private suspend fun isStoreVersionRelevant(): Boolean {
        val localVersion = getLocalVersion()
        val remoteVersion = versionConfig.getLong(REMOTE_STORE_VERSION_KEY)
        if (remoteVersion > localVersion) return false
        return true
    }

    private suspend fun getLocalVersion(): Long {
        if (localCurrentVersion == null) loadLocalStoreVersion()
        return localCurrentVersion!!
    }

    private fun Preferences.loadViewedIds() = this[VIEWED_IDS_KEY] ?: ""

    private suspend fun fillViewedQuotes() {
        loadViewedIds()
        if (viewsQuotesIds.isNullOrEmpty()) return
        quotes = quotes.map { quote ->
            if (viewsQuotesIds.contains(quote.id)) quote.copy(isViewed = true)
            else quote
        }.toMutableList()
    }

    private fun updateQuote(quote: QuoteModel, isFavorite: Boolean): QuoteModel {
        val newQuote = quote.copy(isFavorite = isFavorite)
        val updatedList = quotes.map { if (it.id == newQuote.id) newQuote else it }
        updateQuoteList(updatedList)
        return newQuote
    }

    private fun updateQuoteList(list: List<QuoteModel>) {
        quotes = list.toMutableList()
    }

    private suspend inline fun <reified T : Any> Task<T>.await(): Result<T> {
        return suspendCancellableCoroutine { con ->
            addOnSuccessListener { con.resume(Result.Success(it)) }
            addOnFailureListener { con.resume(Result.Error(it)) }
        }
    }

    private suspend fun fillQuotes() {
        val source = when (isStoreVersionRelevant()) {
            true -> Source.CACHE
            false -> Source.SERVER
        }
        val result = db.collection(DB_COLLECTION).get(source).await()
        if (result is Result.Success) {
            val docs = result.data
            docs.forEach { document ->
                quotes.add(
                    QuoteModel(
                        document.id.toLong(),
                        document.getField(FIELD_TEXT)!!,
                        document.getField(FIELD_CATEGORY)!!,
                        isFavorite = false,
                        isViewed = false
                    )
                )
            }
            if (source == Source.SERVER) syncVersions()
        }
    }
}

