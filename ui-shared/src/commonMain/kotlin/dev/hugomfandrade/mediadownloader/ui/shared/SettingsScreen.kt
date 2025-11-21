package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(items: List<PreferenceData>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            item.render()
        }
    }
}

@Composable
fun PreferenceItem(item: PreferenceData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(item.title, style = MaterialTheme.typography.titleMedium)
        item.summary?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PreferenceItem(item: PreferenceData.ClickablePreference) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(16.dp)
    ) {
        Text(item.title, style = MaterialTheme.typography.titleMedium)
        item.summary?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PreferenceItem(item: PreferenceData.SwitchPreference) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onCheckedChange(!item.checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            item.summary?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = item.checked,
            onCheckedChange = item.onCheckedChange
        )
    }
}

sealed class PreferenceData(val title: String, val summary: String? = null) {

    @Composable open fun render() {
        PreferenceItem(this)
    }

    class ClickablePreference(
        title: String,
        summary: String? = null,
        val onClick: () -> Unit
    ) : PreferenceData(title, summary) {

        @Composable
        override fun render() {
            PreferenceItem(this)
        }
    }

    class SwitchPreference(
        title: String,
        summary: String? = null,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : PreferenceData(title, summary) {

        @Composable
        override fun render() {
            PreferenceItem(this)
        }
    }
}