package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatRequest
import mobile.tracker.finance.data.models.ChatResponse
import mobile.tracker.finance.data.models.InsightType
import mobile.tracker.finance.utils.Result

/**
 * Mock-реализация AiRepository для разработки.
 * TODO: заменить на реальную реализацию при подключении AI-бекенда (OpenAI / GigaChat / YandexGPT).
 */
class MockAiRepository : AiRepository {

    override suspend fun getHealthScore(): Result<AiHealthScore> {
        delay(300)
        return Result.Success(
            AiHealthScore(
                overall = 78,
                expenses = 72,
                savings = 85,
                goals = 68,
                discipline = 82
            )
        )
    }

    override suspend fun getInsights(): Result<List<AiInsight>> {
        delay(400)
        return Result.Success(
            listOf(
                AiInsight(
                    id = "1",
                    type = InsightType.DANGER,
                    title = "Превышение лимита",
                    description = "Продукты: +8,500₽ сверх плана",
                    recommendation = "Планируйте меню на неделю вперёд"
                ),
                AiInsight(
                    id = "2",
                    type = InsightType.SUCCESS,
                    title = "Отличная экономия",
                    description = "Развлечения: -2,100₽ к прошлому месяцу",
                    recommendation = "Продолжайте в том же духе!"
                ),
                AiInsight(
                    id = "3",
                    type = InsightType.WARNING,
                    title = "Возможность инвестиций",
                    description = "Накоплено 245,680₽",
                    recommendation = "Разместите 50,000₽ в индексные фонды"
                )
            )
        )
    }

    override suspend fun getTips(): Result<List<AiTip>> {
        delay(350)
        return Result.Success(
            listOf(
                AiTip(
                    id = "1",
                    category = "Оптимизация",
                    title = "Смените тариф связи",
                    effect = "~300₽/мес"
                ),
                AiTip(
                    id = "2",
                    category = "Доход",
                    title = "Фриланс в дизайне",
                    effect = "+15,000₽/мес"
                ),
                AiTip(
                    id = "3",
                    category = "Цели",
                    title = "До MacBook 2.3 мес",
                    effect = "По плану"
                ),
                AiTip(
                    id = "4",
                    category = "Бюджет",
                    title = "Правило 50/30/20",
                    effect = "Экономия 20%"
                ),
                AiTip(
                    id = "5",
                    category = "Накопления",
                    title = "Автоплатёж на вклад",
                    effect = "+5,000₽/мес"
                )
            )
        )
    }

    override suspend fun sendMessage(request: ChatRequest): Result<ChatResponse> {
        delay(1200) // Имитация задержки AI-сервиса
        // TODO: заменить на реальный API-вызов (POST /ai/chat)
        val reply = when {
            request.message.contains("расход", ignoreCase = true) ->
                "Ваши расходы за этот месяц составили 45,200₽. Основные категории: продукты (28%), транспорт (18%), развлечения (15%). Рекомендую сократить расходы на развлечения на 10–15%."
            request.message.contains("накоп", ignoreCase = true) ->
                "Текущий баланс накоплений: 245,680₽. Вы накапливаете в среднем 18,000₽ в месяц. При сохранении темпа цель в 500,000₽ будет достигнута через 14 месяцев."
            request.message.contains("инвест", ignoreCase = true) ->
                "Для инвестиций рекомендую начать с индексных фондов (ETF). Они диверсифицированы, низкий порог входа и минимальные комиссии. Начните с 10% от ежемесячного дохода."
            request.message.contains("бюджет", ignoreCase = true) ->
                "Для планирования бюджета используйте правило 50/30/20: 50% на обязательные расходы, 30% на желания, 20% на накопления. Ваш текущий баланс между категориями близок к этому распределению."
            request.message.contains("совет", ignoreCase = true) || request.message.contains("помог", ignoreCase = true) ->
                "Главный совет: создайте финансовую подушку безопасности на 3–6 месяцев расходов. После этого начните инвестировать минимум 10% дохода. Автоматизируйте оба процесса."
            else ->
                "Понял ваш вопрос! Для получения персонализированных рекомендаций подключите AI-сервис в настройках приложения. В демо-режиме я могу отвечать на вопросы о расходах, накоплениях, инвестициях и бюджете."
        }
        return Result.Success(ChatResponse(reply = reply))
    }
}
