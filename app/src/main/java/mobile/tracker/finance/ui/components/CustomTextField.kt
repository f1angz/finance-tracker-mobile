package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mobile.tracker.finance.ui.theme.Black
import mobile.tracker.finance.ui.theme.InputBackground
import mobile.tracker.finance.ui.theme.InputTextColor

/**
 * Кастомное поле ввода
 *
 * @param label Подпись поля
 * @param value Текущее значение
 * @param onValueChange Колбэк при изменении значения
 * @param placeholder Плейсхолдер
 * @param visualTransformation Трансформация
 * @param keyboardOptions Настройки клавиатуры
 * @param isError Флаг ошибки
 * @param errorMessage Текст ошибки
 */
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier) {
        // Лейбл поля
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Поле ввода
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = InputTextColor
                )
            },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = InputBackground,
                errorContainerColor = InputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedTextColor = Black,
                unfocusedTextColor = Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(InputBackground, RoundedCornerShape(10.dp))
        )

        // Сообщение об ошибке
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
