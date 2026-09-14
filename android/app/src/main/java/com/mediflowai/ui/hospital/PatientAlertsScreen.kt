package com.mediflowai.ui.hospital

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.AlertItem
import com.mediflowai.navigation.PatientBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.PatientAlertsViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material3.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale

private val AlertsNavy =
    Color(0xFF081225)

private val AlertsTeal =
    Color(0xFF00A6A6)

private val AlertsBackground =
    Color(0xFFF5F8FB)

private val AlertsMuted =
    Color(0xFF64748B)

private val AlertsRed =
    Color(0xFFDC2626)

private val AlertsAmber =
    Color(0xFFD97706)


@Composable
fun PatientAlertsScreen(
    onNavigate: (String) -> Unit,
    viewModel: PatientAlertsViewModel = viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    Scaffold(
        containerColor =
            AlertsBackground,

        bottomBar = {

            PatientBottomBar(
                currentRoute =
                    Screen.PatientAlerts.route,
                onNavigate =
                    onNavigate
            )
        }
    ) { paddingValues ->


        when {

            state.isLoading -> {

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }


            state.error != null -> {

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            )
                            .padding(
                                24.dp
                            ),
                    verticalArrangement =
                        Arrangement.Center,
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Unable to load alerts",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            AlertsNavy
                    )

                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            AlertsMuted
                    )

                    Button(
                        modifier =
                            Modifier.padding(
                                top = 18.dp
                            ),
                        onClick = {
                            viewModel.loadAlerts()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            else -> {

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),
                    contentPadding =
                        PaddingValues(
                            horizontal = 18.dp,
                            vertical = 20.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {


                    item {

                        Column {

                            Text(
                                text =
                                    "Alerts & Notifications",
                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineMedium,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    AlertsNavy
                            )

                            Text(
                                text =
                                    "Hospital updates and important notifications.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    AlertsMuted
                            )
                        }
                    }


                    item {

                        val unreadCount =
                            state.alerts.count {
                                it.status.equals(
                                    "UNREAD",
                                    ignoreCase = true
                                )
                            }

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    20.dp
                                ),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        AlertsNavy
                                )
                        ) {

                            Row(
                                modifier =
                                    Modifier.padding(
                                        18.dp
                                    ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier =
                                        Modifier
                                            .size(
                                                48.dp
                                            )
                                            .background(
                                                Color.White.copy(
                                                    alpha = 0.12f
                                                ),
                                                CircleShape
                                            ),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined
                                                .Notifications,
                                        contentDescription =
                                            null,
                                        tint =
                                            Color.White
                                    )
                                }


                                Column(
                                    modifier =
                                        Modifier.padding(
                                            start = 14.dp
                                        )
                                ) {

                                    Text(
                                        text =
                                            "$unreadCount unread",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleLarge,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            Color.White
                                    )

                                    Text(
                                        text =
                                            "${state.alerts.size} total alerts",
                                        color =
                                            Color.White.copy(
                                                alpha = 0.72f
                                            )
                                    )
                                }
                            }
                        }
                    }


                    if (
                        state.alerts.isEmpty()
                    ) {

                        item {

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(
                                        22.dp
                                    ),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Color.White
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                28.dp
                                            ),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally,
                                    verticalArrangement =
                                        Arrangement.spacedBy(
                                            8.dp
                                        )
                                ) {

                                    Box(
                                        modifier =
                                            Modifier
                                                .size(
                                                    56.dp
                                                )
                                                .background(
                                                    AlertsTeal.copy(
                                                        alpha = 0.10f
                                                    ),
                                                    CircleShape
                                                ),
                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Outlined
                                                    .Notifications,
                                            contentDescription =
                                                null,
                                            tint =
                                                AlertsTeal
                                        )
                                    }

                                    Text(
                                        text =
                                            "No alerts yet",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            AlertsNavy
                                    )

                                    Text(
                                        text =
                                            "Hospital notifications will appear here.",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium,
                                        color =
                                            AlertsMuted
                                    )
                                }
                            }
                        }

                    } else {

                        items(
                            items =
                                state.alerts,
                            key = {
                                it.id
                            }
                        ) { alert ->

                            PatientAlertCard(
                                alert = alert,
                                isUpdating =
                                    state.markingReadId ==
                                            alert.id,
                                onClick = {

                                    if (
                                        alert.status.equals(
                                            "UNREAD",
                                            ignoreCase = true
                                        )
                                    ) {
                                        viewModel.markAsRead(
                                            alert.id
                                        )
                                    }
                                }
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "This application is an academic prototype and is not intended for clinical diagnosis or medical decision-making.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                AlertsMuted,
                            modifier =
                                Modifier.padding(
                                    vertical = 10.dp
                                )
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PatientAlertCard(
    alert: AlertItem,
    isUpdating: Boolean,
    onClick: () -> Unit
) {

    val type =
        alert.type.uppercase()

    val iconColor =
        when {

            type.contains("HIGH") ||
                    type.contains("RISK") ||
                    type.contains("CRITICAL") ->
                AlertsRed

            type.contains("WARNING") ||
                    type.contains("APPOINTMENT") ->
                AlertsAmber

            else ->
                AlertsTeal
        }


    val isUnread =
        alert.status.equals(
            "UNREAD",
            ignoreCase = true
        )


    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    enabled =
                        isUnread &&
                                !isUpdating
                ) {
                    onClick()
                },
        shape =
            RoundedCornerShape(
                20.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (isUnread)
                        2.dp
                    else
                        0.dp
            )
    ) {

        Column {

            if (isUpdating) {

                LinearProgressIndicator(
                    modifier =
                        Modifier.fillMaxWidth()
                )
            }


            Row(
                modifier =
                    Modifier.padding(
                        18.dp
                    ),
                verticalAlignment =
                    Alignment.Top
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(
                                46.dp
                            )
                            .background(
                                iconColor.copy(
                                    alpha = 0.10f
                                ),
                                CircleShape
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            if (
                                type.contains("RISK") ||
                                type.contains("WARNING") ||
                                type.contains("CRITICAL")
                            ) {
                                Icons.Outlined
                                    .WarningAmber
                            } else {
                                Icons.Outlined
                                    .Notifications
                            },
                        contentDescription =
                            null,
                        tint =
                            iconColor
                    )
                }


                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                start = 14.dp
                            ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            6.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            modifier =
                                Modifier.weight(1f),
                            text =
                                formatAlertType(
                                    alert.type
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                AlertsNavy
                        )


                        if (isUnread) {

                            Box(
                                modifier =
                                    Modifier
                                        .size(
                                            9.dp
                                        )
                                        .background(
                                            AlertsTeal,
                                            CircleShape
                                        )
                            )
                        }
                    }


                    Text(
                        text =
                            alert.message,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            AlertsMuted
                    )


                    Text(
                        text =
                            formatAlertDateTime(
                                alert.created_at
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            AlertsMuted
                    )


                    Text(
                        text =
                            if (isUpdating)
                                "MARKING AS READ..."
                            else
                                alert.status.uppercase(),
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            if (isUnread)
                                AlertsTeal
                            else
                                AlertsMuted
                    )
                }
            }
        }
    }
}


private fun formatAlertType(
    value: String
): String {

    return value
        .replace(
            "_",
            " "
        )
        .lowercase()
        .split(" ")
        .joinToString(" ") { word ->

            word.replaceFirstChar {
                it.uppercase()
            }
        }
}

private fun formatAlertDateTime(
    value: String
): String {

    return try {

        val formats =
            listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss"
            )

        var parsedDate:
                java.util.Date? = null

        for (pattern in formats) {

            try {

                parsedDate =
                    SimpleDateFormat(
                        pattern,
                        Locale.US
                    ).parse(value)

                if (parsedDate != null) {
                    break
                }

            } catch (_: Exception) {
            }
        }


        if (parsedDate != null) {

            SimpleDateFormat(
                "dd MMM yyyy • hh:mm a",
                Locale.getDefault()
            ).format(
                parsedDate
            )

        } else {

            value
        }

    } catch (_: Exception) {

        value
    }
}