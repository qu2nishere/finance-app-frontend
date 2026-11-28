package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.quan.finance_app.data.AccountTimeZone
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.ui.components.core.MfScreenHeader
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
internal fun TimezoneSettingsScreen(
    userId: String,
    onBack: () -> Unit,
    loadProfile: suspend (String) -> String = ServiceLocator.profileRepository::load,
    saveProfile: suspend (String, String) -> String = ServiceLocator.profileRepository::update
) {
    val current by AccountTimeZone.state.collectAsState()
    val scope = rememberCoroutineScope()
    var query by rememberSaveable(userId) { mutableStateOf("") }
    var selected by rememberSaveable(userId) { mutableStateOf<String?>(null) }
    var busy by remember(userId) { mutableStateOf(true) }
    var error by remember(userId) { mutableStateOf<String?>(null) }
    var attempt by remember(userId) { mutableIntStateOf(0) }
    val matches = remember(query) {
        val term = query.trim().replace(' ', '_')
        AccountTimeZone.regionIds.filter { it.contains(term, ignoreCase = true) }
    }

    suspend fun sync(save: String?) {
        busy = true
        error = null
        try {
            if (save == null) loadProfile(userId) else {
                saveProfile(userId, save)
                onBack()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            error = when (e.code()) {
                400 -> "Máy chủ từ chối múi giờ này. Hãy chọn múi giờ khác."
                401 -> "Phiên đăng nhập đã hết hạn."
                else -> "Chưa đọc được hồ sơ từ máy chủ."
            }
        } catch (_: IOException) {
            error = "Chưa xác nhận được múi giờ. Kiểm tra kết nối rồi thử lại."
        } catch (_: IllegalStateException) {
            error = "Tài khoản đã thay đổi. Hãy mở lại Cài đặt."
        } finally {
            busy = false
        }
    }

    LaunchedEffect(userId, attempt) { sync(null) }
    Column(
        Modifier.fillMaxSize().background(MoneyFlowTheme.colors.surfacePage).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MfScreenHeader(title = "Múi giờ", onBack = onBack)
        Text("Đang dùng · ${current.id}", style = MaterialTheme.typography.titleMedium)
        Text("Múi giờ mới áp dụng cho giao dịch tiếp theo. Các giao dịch đã ghi giữ nguyên ngày.",
            style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(query, { query = it }, label = { Text("Tìm khu vực hoặc thành phố") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        if (busy) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { selected = null; attempt++ }) { Text("Thử lại") }
        }
        if (matches.isEmpty()) Text("Không tìm thấy múi giờ phù hợp.")
        LazyColumn(Modifier.weight(1f).selectableGroup()) {
            items(matches, key = { it }) { id ->
                val picked = (selected ?: current.id) == id
                Row(
                    Modifier.fillMaxWidth().selectable(picked, enabled = !busy, role = Role.RadioButton,
                        onClick = { selected = id }).heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = picked, onClick = null)
                    Text(id, Modifier.padding(start = 12.dp))
                }
            }
        }
        Button(
            onClick = { selected?.let { zone -> scope.launch { sync(zone) } } },
            enabled = !busy && selected != null && selected != current.id,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Lưu múi giờ") }
    }
}
