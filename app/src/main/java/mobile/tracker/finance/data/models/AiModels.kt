package mobile.tracker.finance.data.models

// ─── AI Health Score ──────────────────────────────────────────────────────────

data class AiHealthScore(
    val overall: Int,
    val expenses: Int,
    val savings: Int,
    val goals: Int,
    val discipline: Int
)

// ─── Insights ────────────────────────────────────────────────────────────────

enum class InsightType { DANGER, SUCCESS, WARNING, INFO }

data class AiInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val recommendation: String
)

// ─── Tips ────────────────────────────────────────────────────────────────────

data class AiTip(
    val id: String,
    val category: String,   // "Оптимизация", "Доход", "Цели", ...
    val title: String,
    val effect: String      // "~300₽/мес", "+15,000₽/мес", "По плану", ...
)

// ─── Chat ────────────────────────────────────────────────────────────────────

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatRequest(val message: String)

data class ChatResponse(val reply: String)
