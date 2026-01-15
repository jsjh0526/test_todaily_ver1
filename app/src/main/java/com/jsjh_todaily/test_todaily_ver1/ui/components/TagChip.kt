package com.jsjh_todaily.test_todaily_ver1.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagChip(
    tag: String,
    onRemove: (() -> Unit)? = null
) {
    AssistChip(
        onClick = { onRemove?.invoke() },
        label = { Text("#$tag") },
        trailingIcon = if (onRemove != null) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "제거",
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null
    )
}
