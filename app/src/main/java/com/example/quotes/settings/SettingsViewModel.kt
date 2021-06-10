package com.example.quotes.settings

import com.airbnb.mvrx.*
import com.example.quotes.AdRepository
import com.example.quotes.category.*
import com.example.quotes.notification.NotificationRepository
import com.example.quotes.notification.NotificationTimeRepository
import com.example.quotes.notification.NotificationUsecase
import com.example.quotes.quote.QuoteRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val initialState: SettingsState,
    private val fontRepository: FontRepository,
    private val categoriesRepository: CategoriesRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationTimeRepository: NotificationTimeRepository,
    private val quoteRepository: QuoteRepository,
    private val adRepository: AdRepository
) : MavericksViewModel<SettingsState>(initialState) {

    init {
        viewModelScope.launch {
            setState {
                copy(
                    categories = Loading(),
                    fonts = Loading(),
                    notification = Loading(),
                    hasFavorites = Loading()
                )
            }

            val hasFavorites = quoteRepository.hasFavorites()
            setState { copy(hasFavorites = Success(hasFavorites)) }

            val categories = categoriesRepository.getCategoriesList()
            setState { copy(categories = Success(categories)) }

            val fonts = fontRepository.getFonts()
            setState { copy(fonts = Success(fonts)) }

            val notification = notificationRepository.loadNotificationPeriod()
            setState { copy(notification = Success(notification)) }

            val alarms = notificationTimeRepository.loadAlarms()
            setState { copy(alarms = Success(alarms)) }
        }
    }

    fun saveAlarm(alarm: Long) {
        val notificationUsecase: NotificationUsecase by inject(NotificationUsecase::class.java)
        viewModelScope.launch {
            val alarms = notificationTimeRepository.saveAlarms(alarm)
            setState { copy(alarms = Success(alarms)) }
            notificationUsecase.notifyQuote()
        }
    }

    fun getNotificationList() = notificationRepository.getNotificationList()

    fun saveNotificationPeriod(id: Int) {
        viewModelScope.launch {
            val newNotification = notificationRepository.saveSelectedNotification(id)
            setState { copy(notification = Success(newNotification)) }
        }
        val notificationUsecase: NotificationUsecase by inject(NotificationUsecase::class.java)
        notificationUsecase.switchBootReceiver(id == 0)
    }

    fun saveFont(fontRes: Int) {
        viewModelScope.launch {
            val newList = fontRepository.saveFont(fontRes)
            updateFonts(newList)
        }
    }

    fun showAd(id: Long) {
        viewModelScope.launch {
            val result = adRepository.bindAdAndGetResult()
            if (result == AdRepository.AdResult.CLAIMED) unlock(id)

            val event: Event<CategoriesEvent> = when (result) {
                AdRepository.AdResult.CLAIMED -> Event(AdShow(true))

                AdRepository.AdResult.SHOWED -> Event(AdShow(false))

                AdRepository.AdResult.FAILED -> Event(AdFailToShow)
            }
            setState { copy(adResult = event) }
        }
    }

    fun selectCategory(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.selectCategory(id)
            updateCategories(newCategory)
        }
    }

    fun unselectCategory(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.unselectCategory(id)
            updateCategories(newCategory)
        }
    }

    private fun unlock(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.changeCategoryStatus(id, Unlocked(true))
            updateCategories(newCategory)
        }
    }

    private fun updateFonts(newList: List<FontModel>) {
        setState {
            copy(fonts = Success(newList))
        }
    }

    private fun updateCategories(category: CategoryPresentationModel) {
        setState {
            val categories = this.categories() ?: return@setState copy()
            val newList = categories.map { if (it.name == category.name) category else it }

            copy(categories = Success(newList))
        }
    }

    companion object : MavericksViewModelFactory<SettingsViewModel, SettingsState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SettingsState
        ): SettingsViewModel {
            with(viewModelContext.activity) {
                val fontRepository: FontRepository by inject()
                val categoriesRepository: CategoriesRepository by inject()
                val adRepository: AdRepository by inject()
                val notificationRepository: NotificationRepository by inject()
                val notificationTimeRepository: NotificationTimeRepository by inject()
                val quoteRepository: QuoteRepository by inject()
                return SettingsViewModel(
                    state,
                    fontRepository,
                    categoriesRepository,
                    notificationRepository,
                    notificationTimeRepository,
                    quoteRepository,
                    adRepository
                )
            }
        }
    }
}
