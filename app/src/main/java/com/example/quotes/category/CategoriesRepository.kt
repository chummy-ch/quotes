package com.example.quotes.category

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quotes.Result
import com.example.quotes.onIO
import com.example.quotes.quote.QuoteRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CategoriesRepository(
    private val db: FirebaseFirestore,
    private val versionConfig: FirebaseRemoteConfig,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private const val COLLECTION_ID = "categories"
        private val LONG_LOCAL_VERSION_KEY = longPreferencesKey("category_version")
        private val STRING_SELECTED_KEY = stringPreferencesKey("selectedIds")
    }

    private var categories = mutableListOf<CategoryPresentationModel>()
    private var selectedIds = mutableSetOf<Long>()
    private var localCurrentVersion: Long? = null

    private var selectedCategoryFlow: MutableStateFlow<List<CategoryPresentationModel>> = MutableStateFlow(listOf())

    suspend fun getCategoriesList(): List<CategoryPresentationModel> {
        if (categories.isEmpty()) fillCategories()
        fillSelectedCategories()
        selectSavedCategories()
        return categories.toList()
    }

    suspend fun getSelectedCategoriesFlow(): StateFlow<List<CategoryPresentationModel>> {
        selectedCategoryFlow.value = getSelectedCategories()
        return selectedCategoryFlow.asStateFlow()
    }

    suspend fun getLocalVersion(): Long {
        if (localCurrentVersion == null) loadLocalStoreVersion()
        return localCurrentVersion!!
    }

    private suspend fun loadLocalStoreVersion() {
        localCurrentVersion = dataStore.data.first().getStoreVersion()
    }

    suspend fun selectCategory(id: Long): CategoryPresentationModel {
        val selectedCategoryPresentation = getCategoryById(id)
        if (selectedCategoryPresentation.status !is Unlocked) return selectedCategoryPresentation
        val newCategory = selectedCategoryPresentation.copy(status = Unlocked(true))
        val newCategories = categories.map {
            if (it.id == selectedCategoryPresentation.id) newCategory else it
        }
        updateCategories(newCategories)
        saveCategoriesIdToDataStore(newCategory.id)
        return newCategory
    }

    suspend fun getUnlockedCategories() = getCategoriesList().filter {
        it.status is Unlocked
    }

    suspend fun unselectCategory(
        id: Long
    ): CategoryPresentationModel {
        val selectedCategoryPresentation = getCategoryById(id)
        val newCategory = selectedCategoryPresentation.copy(status = Unlocked(false))
        val newCategories = categories.map {
            if (it.id == selectedCategoryPresentation.id) newCategory else it
        }
        updateCategories(newCategories)
        saveCategoriesIdToDataStore(newCategory.id)
        return newCategory
    }

    suspend fun changeCategoryStatus(id: Long, newStatus: Status): CategoryPresentationModel {
        val newCategory = getCategoryById(id).copy(status = newStatus)
        val categoriesList = categories.map { category ->
            if (category.id == id) newCategory
            else category
        }
        updateCategories(categoriesList)
        return newCategory
    }

    suspend fun getSelectedCategories(): List<CategoryPresentationModel> {
        if (categories.isEmpty()) getCategoriesList()
        return categories.filter { it.status is Unlocked && it.status.isSelected }
    }

    private fun Preferences.getStoreVersion() = this[LONG_LOCAL_VERSION_KEY] ?: 0

    private fun getSelectedCategoriesString() = dataStore.data.map { preference ->
        preference.loadSelectedCategories()
    }

    private suspend fun isStoreVersionRelevant(): Boolean {
        val localVersion = getLocalVersion()
        val remoteVersion = versionConfig.getLong(QuoteRepository.REMOTE_STORE_VERSION_KEY)
        if (remoteVersion > localVersion) return false
        return true
    }

    private fun Preferences.loadSelectedCategories() =
        this[STRING_SELECTED_KEY] ?: ""

    private suspend fun fillSelectedCategories() {
        var string = getSelectedCategoriesString().first()
        string = string.replace("[", "").replace("]", "")
        selectedIds = if (string.isBlank()) mutableSetOf()
        else {
            string.split(',').map { it.trim().toLong() }.toMutableSet()
        }
    }

    private fun selectSavedCategories() {
        categories = categories.map { category ->
            if (selectedIds.contains(category.id)) {
                category.copy(status = Unlocked(true))
            } else category
        }.toMutableList()
    }

    private suspend fun getCategoryById(id: Long): CategoryPresentationModel {
        if (categories.isEmpty()) fillCategories()
        return categories.first { it.id == id }
    }

    private suspend fun saveCategoriesIdToDataStore(categoriesId: Long) {
        if (!selectedIds.contains(categoriesId)) {
            selectedIds.add(categoriesId)
        } else {
            selectedIds.remove(categoriesId)
        }

        val stringIds = selectedIds.toString()
        onIO {
            dataStore.edit { categories ->
                categories[STRING_SELECTED_KEY] = stringIds
            }
        }
    }

    private suspend inline fun <reified T : Any> Task<T>.await(): Result<T> {
        return suspendCancellableCoroutine { con ->
            addOnSuccessListener { con.resume(Result.Success(it)) }
            addOnFailureListener { con.resume(Result.Error(it)) }
        }
    }

    private suspend fun fillCategories() {
        val source = when (isStoreVersionRelevant()) {
            true -> Source.CACHE
            false -> Source.SERVER
        }
        val result = db.collection(COLLECTION_ID).get(source).await()
        if (result is Result.Success) {
            val docs = result.data
            docs.forEach { doc ->
                val transferModel = doc.toObject(CategoryTransferModel::class.java)
                with(transferModel) {
                    val presentationModel = CategoryPresentationModel(
                        id, name, icon, when (status) {
                            "Unlocked" -> Unlocked(false)
                            "Locked" -> Locked
                            "Premium" -> Premium
                            else -> throw Error("No such status")
                        },
                        groupId
                    )
                    categories.add(presentationModel)
                }
            }
            if (source == Source.SERVER) syncVersions()
        }
    }

    private suspend fun syncVersions() {
        localCurrentVersion = versionConfig.getLong(QuoteRepository.REMOTE_STORE_VERSION_KEY)
        dataStore.edit {
            it[LONG_LOCAL_VERSION_KEY] = localCurrentVersion!!
        }
    }

    private suspend fun updateCategories(newCategoryPresentations: List<CategoryPresentationModel>) {
        categories = newCategoryPresentations.toMutableList()
        selectedCategoryFlow.value = getSelectedCategories()
    }
}
