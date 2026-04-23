package mobile.tracker.finance

import android.app.Application
import kotlinx.coroutines.runBlocking
import mobile.tracker.finance.data.DraftStore
import mobile.tracker.finance.data.ThemeManager
import mobile.tracker.finance.data.TokenManager
import mobile.tracker.finance.notifications.NotificationHelper
import mobile.tracker.finance.notifications.NotificationScheduler

class FinanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        DraftStore.init(this)
        ThemeManager.init(this)
        runBlocking {
            TokenManager.loadFromStorage()
            ThemeManager.load()
        }
        NotificationHelper.createChannel(this)
        NotificationScheduler.schedule(this)
    }
}
