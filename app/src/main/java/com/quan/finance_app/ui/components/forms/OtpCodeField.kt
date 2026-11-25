package com.quan.finance_app.ui.components.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

/**
 * Ô nhập mã OTP 6 chữ số bằng bàn phím số thật của máy, hỗ trợ dán (paste) chuỗi chứa số.
 */
@Composable
fun OtpCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
    hasError: Boolean = false,
    autoFocus: Boolean = false,
    onDone: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val sanitized = code.filter { it.isDigit() }.take(length)
    val textFieldValue = remember(sanitized) {
        TextFieldValue(
            text = sanitized,
            selection = TextRange(sanitized.length)
        )
    }

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { incoming ->
            val nextDigits = incoming.text.filter { it.isDigit() }.take(length)
            if (nextDigits != sanitized) {
                onCodeChange(nextDigits)
            }
        },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                // Giữ innerTextField cực nhỏ nhưng hiện diện để hệ điều hành neo menu Dán (Paste)
                Box(
                    modifier = Modifier
                        .size(1.dp)
                        .alpha(0.01f)
                ) {
                    innerTextField()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MoneyFlowTheme.spacing.space3)
                ) {
                    repeat(length) { index ->
                        val filled = index < sanitized.length
                        val isActive = isFocused && (
                            index == sanitized.length ||
                                (sanitized.length == length && index == length - 1)
                            )

                        val borderColor = when {
                            hasError -> MoneyFlowTheme.colors.stateDanger
                            isActive -> MoneyFlowTheme.colors.borderBrand
                            filled -> MoneyFlowTheme.colors.borderDefault
                            else -> MoneyFlowTheme.colors.borderSubtle
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(MoneyFlowTheme.radii.md),
                            color = MoneyFlowTheme.colors.surfaceCard,
                            border = BorderStroke(
                                width = if (isActive || hasError) 1.5.dp else 1.dp,
                                color = borderColor
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (filled) sanitized[index].toString() else "",
                                    style = MoneyFlowTheme.typography.amountMd.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OtpCodeFieldPreview() {
    MoneyFlowTheme {
        Box(modifier = Modifier.padding(MoneyFlowTheme.spacing.space5)) {
            OtpCodeField(code = "428", onCodeChange = {})
        }
    }
}
