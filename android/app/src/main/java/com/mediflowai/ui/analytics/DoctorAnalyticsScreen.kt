package com.mediflowai.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.DoctorAnalyticsResponse
import com.mediflowai.navigation.DoctorBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.DoctorAnalyticsViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


// ------------------------------------------------
// COLORS
// ------------------------------------------------

private val Navy =
    Color(0xFF081225)

private val NavyLight =
    Color(0xFF111F38)

private val Teal =
    Color(0xFF00A6A6)

private val Cyan =
    Color(0xFF38BDF8)

private val Green =
    Color(0xFF16A34A)

private val Amber =
    Color(0xFFF59E0B)

private val Red =
    Color(0xFFDC2626)

private val Purple =
    Color(0xFF7C3AED)

private val Muted =
    Color(0xFF64748B)

private val Background =
    Color(0xFFF5F8FB)

private val Track =
    Color(0xFFE8EEF5)


// ------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------

@Composable
fun DoctorAnalyticsScreen(
    onNavigate: (String) -> Unit,
    viewModel: DoctorAnalyticsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.loadAnalytics()
    }


    Scaffold(
        containerColor =
            Background,

        bottomBar = {

            DoctorBottomBar(
                currentRoute =
                    Screen.DoctorAnalytics.route,

                onNavigate =
                    onNavigate
            )
        }

    ) { paddingValues ->


        when {

            state.isLoading &&
                    state.analytics == null -> {

                LoadingScreen(
                    modifier =
                        Modifier.padding(
                            paddingValues
                        )
                )
            }


            state.error != null &&
                    state.analytics == null -> {

                ErrorScreen(
                    modifier =
                        Modifier.padding(
                            paddingValues
                        ),

                    message =
                        state.error
                            ?: "Unable to load analytics.",

                    onRetry = {

                        viewModel.loadAnalytics()
                    }
                )
            }


            state.analytics != null -> {

                AnalyticsContent(
                    modifier =
                        Modifier.padding(
                            paddingValues
                        ),

                    analytics =
                        state.analytics!!,

                    isRefreshing =
                        state.isLoading,

                    onRefresh = {

                        viewModel.loadAnalytics()
                    }
                )
            }
        }
    }
}


// ------------------------------------------------
// CONTENT
// ------------------------------------------------

@Composable
private fun AnalyticsContent(
    modifier: Modifier,
    analytics: DoctorAnalyticsResponse,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {

    val appointments =
        analytics.appointments

    val risk =
        analytics.patient_risk


    var animationsStarted by remember(
        analytics
    ) {
        mutableStateOf(false)
    }


    LaunchedEffect(
        analytics
    ) {

        animationsStarted =
            false

        delay(
            100
        )

        animationsStarted =
            true
    }


    LazyColumn(
        modifier =
            modifier
                .fillMaxSize(),

        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(
                    bottom = 28.dp
                ),

        verticalArrangement =
            Arrangement.spacedBy(
                18.dp
            )
    ) {


        // -----------------------------------------
        // HEADER
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    0
            ) {

                AnalyticsHeader(
                    doctorName =
                        analytics.doctor_name,

                    specialization =
                        analytics.specialization,

                    isRefreshing =
                        isRefreshing,

                    onRefresh =
                        onRefresh
                )
            }
        }


        // -----------------------------------------
        // KPI SECTION
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    80
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    SectionTitle(
                        title =
                            "Overview",

                        subtitle =
                            "Live statistics from your assigned patients and appointments."
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        KpiCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            value =
                                analytics
                                    .assigned_patients
                                    .toString(),

                            label =
                                "Patients",

                            accent =
                                Teal,

                            animationDelay =
                                120
                        )


                        KpiCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            value =
                                appointments
                                    .total
                                    .toString(),

                            label =
                                "Appointments",

                            accent =
                                Cyan,

                            animationDelay =
                                180
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        KpiCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            value =
                                "${formatPercentage(appointments.completion_rate)}%",

                            label =
                                "Completion",

                            accent =
                                Green,

                            animationDelay =
                                240
                        )


                        KpiCard(
                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            value =
                                risk
                                    .total_predictions
                                    .toString(),

                            label =
                                "Risk Profiles",

                            accent =
                                Purple,

                            animationDelay =
                                300
                        )
                    }
                }
            }
        }


        // -----------------------------------------
        // APPOINTMENT DONUT
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    180
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    SectionTitle(
                        title =
                            "Appointment Distribution",

                        subtitle =
                            "Current distribution of your appointment workload."
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    AppointmentDistributionCard(
                        total =
                            appointments.total,

                        scheduled =
                            appointments.scheduled,

                        completed =
                            appointments.completed,

                        cancelled =
                            appointments.cancelled
                    )
                }
            }
        }


        // -----------------------------------------
        // APPOINTMENT PERFORMANCE
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    260
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    SectionTitle(
                        title =
                            "Appointment Performance",

                        subtitle =
                            "Status breakdown across all your assigned appointments."
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateContentSize(),

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
                                Modifier.padding(
                                    20.dp
                                ),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    20.dp
                                )
                        ) {


                            AnalyticsProgressRow(
                                title =
                                    "Scheduled",

                                count =
                                    appointments.scheduled,

                                total =
                                    appointments.total,

                                accent =
                                    Teal,

                                animationDelay =
                                    100
                            )


                            AnalyticsProgressRow(
                                title =
                                    "Completed",

                                count =
                                    appointments.completed,

                                total =
                                    appointments.total,

                                accent =
                                    Green,

                                animationDelay =
                                    220
                            )


                            AnalyticsProgressRow(
                                title =
                                    "Cancelled",

                                count =
                                    appointments.cancelled,

                                total =
                                    appointments.total,

                                accent =
                                    Red,

                                animationDelay =
                                    340
                            )
                        }
                    }
                }
            }
        }


        // -----------------------------------------
        // COMPLETION RATE
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    340
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    CompletionRateCard(
                        completionRate =
                            appointments
                                .completion_rate,

                        completed =
                            appointments.completed,

                        total =
                            appointments.total
                    )
                }
            }
        }


        // -----------------------------------------
        // RISK ANALYTICS
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    420
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    SectionTitle(
                        title =
                            "Readmission Risk",

                        subtitle =
                            "Latest saved ML prediction for each assigned patient."
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    RiskDistributionCard(
                        total =
                            risk.total_predictions,

                        low =
                            risk.low_risk,

                        medium =
                            risk.medium_risk,

                        high =
                            risk.high_risk
                    )
                }
            }
        }


        // -----------------------------------------
        // RISK BREAKDOWN
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    500
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        )
                ) {

                    SectionTitle(
                        title =
                            "Risk Breakdown",

                        subtitle =
                            "Distribution across LOW, MEDIUM and HIGH risk categories."
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateContentSize(),

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
                                Modifier.padding(
                                    20.dp
                                ),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    20.dp
                                )
                        ) {


                            AnalyticsProgressRow(
                                title =
                                    "Low Risk",

                                count =
                                    risk.low_risk,

                                total =
                                    risk.total_predictions,

                                accent =
                                    Green,

                                animationDelay =
                                    100
                            )


                            AnalyticsProgressRow(
                                title =
                                    "Medium Risk",

                                count =
                                    risk.medium_risk,

                                total =
                                    risk.total_predictions,

                                accent =
                                    Amber,

                                animationDelay =
                                    220
                            )


                            AnalyticsProgressRow(
                                title =
                                    "High Risk",

                                count =
                                    risk.high_risk,

                                total =
                                    risk.total_predictions,

                                accent =
                                    Red,

                                animationDelay =
                                    340
                            )
                        }
                    }
                }
            }
        }


        // -----------------------------------------
        // DATA NOTE
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    580
            ) {

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp
                            ),

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Teal.copy(
                                    alpha = 0.08f
                                )
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                16.dp
                            )
                    ) {

                        Text(
                            text =
                                "Live Analytics",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Navy
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    5.dp
                                )
                        )


                        Text(
                            text =
                                "These statistics are calculated from your real assigned appointments, patients and their latest saved readmission-risk predictions.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                Muted
                        )
                    }
                }
            }
        }


        // -----------------------------------------
        // DISCLAIMER
        // -----------------------------------------

        item {

            AnimatedSection(
                visible =
                    animationsStarted,

                delayMillis =
                    650
            ) {

                Text(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp
                        ),

                    text =
                        "This application is an academic prototype and is not intended for clinical diagnosis or medical decision-making.",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        Muted
                )
            }
        }
    }
}


// ------------------------------------------------
// REUSABLE ENTRANCE ANIMATION
// ------------------------------------------------

@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {

    AnimatedVisibility(
        visible =
            visible,

        enter =
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = 550,
                        delayMillis = delayMillis,
                        easing =
                            FastOutSlowInEasing
                    )
            ) +
                    slideInVertically(
                        animationSpec =
                            tween(
                                durationMillis = 550,
                                delayMillis = delayMillis,
                                easing =
                                    FastOutSlowInEasing
                            ),
                        initialOffsetY = {
                            it / 5
                        }
                    ),

        exit =
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = 150
                    )
            ) +
                    slideOutVertically(
                        animationSpec =
                            tween(
                                durationMillis = 150
                            ),
                        targetOffsetY = {
                            it / 10
                        }
                    )
    ) {

        content()
    }
}


// ------------------------------------------------
// HEADER
// ------------------------------------------------

@Composable
private fun AnalyticsHeader(
    doctorName: String,
    specialization: String?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Navy,
                                    NavyLight
                                )
                        ),
                    shape =
                        RoundedCornerShape(
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            "Doctor Analytics",

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )


                    Text(
                        text =
                            doctorName,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            Color.White
                    )


                    specialization
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?.let {

                            Text(
                                modifier =
                                    Modifier.padding(
                                        top = 2.dp
                                    ),

                                text =
                                    it,

                                color =
                                    Teal
                            )
                        }
                }


                Box(
                    modifier =
                        Modifier
                            .size(
                                48.dp
                            )
                            .background(
                                Color.White.copy(
                                    alpha = 0.10f
                                ),
                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    if (isRefreshing) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    22.dp
                                ),

                            strokeWidth =
                                2.dp,

                            color =
                                Color.White
                        )

                    } else {

                        IconButton(
                            onClick =
                                onRefresh
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Refresh,

                                contentDescription =
                                    "Refresh analytics",

                                tint =
                                    Color.White
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Text(
                text =
                    "Real-time clinical workflow insights",

                color =
                    Color.White.copy(
                        alpha = 0.70f
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


// ------------------------------------------------
// SECTION TITLE
// ------------------------------------------------

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {

    Text(
        text =
            title,

        style =
            MaterialTheme
                .typography
                .titleLarge,

        fontWeight =
            FontWeight.Bold,

        color =
            Navy
    )


    Text(
        modifier =
            Modifier.padding(
                top = 3.dp
            ),

        text =
            subtitle,

        style =
            MaterialTheme
                .typography
                .bodySmall,

        color =
            Muted
    )
}


// ------------------------------------------------
// KPI CARD
// ------------------------------------------------

@Composable
private fun KpiCard(
    modifier: Modifier,
    value: String,
    label: String,
    accent: Color,
    animationDelay: Int = 0
) {

    var animationStarted by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        delay(
            animationDelay.toLong()
        )

        animationStarted =
            true
    }


    val scale by
    animateFloatAsState(
        targetValue =
            if (animationStarted) {
                1f
            } else {
                0.92f
            },

        animationSpec =
            tween(
                durationMillis = 550,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "kpiScale"
    )


    Card(
        modifier =
            modifier
                .scale(
                    scale
                )
                .animateContentSize(),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {

            Box(
                modifier =
                    Modifier
                        .size(
                            10.dp
                        )
                        .background(
                            accent,
                            CircleShape
                        )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Text(
                text =
                    value,

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy
            )


            Text(
                modifier =
                    Modifier.padding(
                        top = 2.dp
                    ),

                text =
                    label,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    Muted
            )
        }
    }
}


// ------------------------------------------------
// APPOINTMENT DISTRIBUTION
// ------------------------------------------------

@Composable
private fun AppointmentDistributionCard(
    total: Int,
    scheduled: Int,
    completed: Int,
    cancelled: Int
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize(),

        shape =
            RoundedCornerShape(
                24.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    20.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                DonutChart(
                    modifier =
                        Modifier.size(
                            155.dp
                        ),

                    values =
                        listOf(
                            scheduled,
                            completed,
                            cancelled
                        ),

                    colors =
                        listOf(
                            Teal,
                            Green,
                            Red
                        ),

                    centerValue =
                        total.toString(),

                    centerLabel =
                        "Total"
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            22.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            13.dp
                        )
                ) {

                    LegendItem(
                        label =
                            "Scheduled",

                        value =
                            scheduled,

                        color =
                            Teal
                    )


                    LegendItem(
                        label =
                            "Completed",

                        value =
                            completed,

                        color =
                            Green
                    )


                    LegendItem(
                        label =
                            "Cancelled",

                        value =
                            cancelled,

                        color =
                            Red
                    )
                }
            }
        }
    }
}


// ------------------------------------------------
// RISK DISTRIBUTION
// ------------------------------------------------

@Composable
private fun RiskDistributionCard(
    total: Int,
    low: Int,
    medium: Int,
    high: Int
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize(),

        shape =
            RoundedCornerShape(
                24.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    20.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                DonutChart(
                    modifier =
                        Modifier.size(
                            155.dp
                        ),

                    values =
                        listOf(
                            low,
                            medium,
                            high
                        ),

                    colors =
                        listOf(
                            Green,
                            Amber,
                            Red
                        ),

                    centerValue =
                        total.toString(),

                    centerLabel =
                        "Predicted"
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            22.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            13.dp
                        )
                ) {

                    LegendItem(
                        label =
                            "Low Risk",

                        value =
                            low,

                        color =
                            Green
                    )


                    LegendItem(
                        label =
                            "Medium",

                        value =
                            medium,

                        color =
                            Amber
                    )


                    LegendItem(
                        label =
                            "High Risk",

                        value =
                            high,

                        color =
                            Red
                    )
                }
            }
        }
    }
}


// ------------------------------------------------
// ANIMATED DONUT GRAPH
// ------------------------------------------------

@Composable
private fun DonutChart(
    modifier: Modifier,
    values: List<Int>,
    colors: List<Color>,
    centerValue: String,
    centerLabel: String
) {

    val total =
        values.sum()


    var animationStarted by remember(
        values
    ) {
        mutableStateOf(false)
    }


    LaunchedEffect(
        values
    ) {

        animationStarted =
            false

        delay(
            120
        )

        animationStarted =
            true
    }


    val animation by
    animateFloatAsState(
        targetValue =
            if (animationStarted) {
                1f
            } else {
                0f
            },

        animationSpec =
            tween(
                durationMillis = 1200,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "donutAnimation"
    )


    val centerScale by
    animateFloatAsState(
        targetValue =
            if (animationStarted) {
                1f
            } else {
                0.75f
            },

        animationSpec =
            tween(
                durationMillis = 700,
                delayMillis = 350,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "donutCenterScale"
    )


    Box(
        modifier =
            modifier,

        contentAlignment =
            Alignment.Center
    ) {

        Canvas(
            modifier =
                Modifier.fillMaxSize()
        ) {

            val strokeWidth =
                22.dp.toPx()

            val inset =
                strokeWidth / 2f


            drawArc(
                color =
                    Track,

                startAngle =
                    -90f,

                sweepAngle =
                    360f,

                useCenter =
                    false,

                topLeft =
                    Offset(
                        inset,
                        inset
                    ),

                size =
                    Size(
                        size.width -
                                strokeWidth,

                        size.height -
                                strokeWidth
                    ),

                style =
                    Stroke(
                        width =
                            strokeWidth,

                        cap =
                            StrokeCap.Round
                    )
            )


            if (
                total > 0
            ) {

                var startAngle =
                    -90f


                values.forEachIndexed {
                        index,
                        value ->


                    if (
                        value > 0
                    ) {

                        val finalSweep =
                            (
                                    value.toFloat()
                                            / total.toFloat()
                                    ) *
                                    360f


                        val animatedSweep =
                            finalSweep *
                                    animation


                        drawArc(
                            color =
                                colors[
                                    index %
                                            colors.size
                                ],

                            startAngle =
                                startAngle,

                            sweepAngle =
                                animatedSweep,

                            useCenter =
                                false,

                            topLeft =
                                Offset(
                                    inset,
                                    inset
                                ),

                            size =
                                Size(
                                    size.width -
                                            strokeWidth,

                                    size.height -
                                            strokeWidth
                                ),

                            style =
                                Stroke(
                                    width =
                                        strokeWidth,

                                    cap =
                                        StrokeCap.Butt
                                )
                        )


                        startAngle +=
                            animatedSweep
                    }
                }
            }
        }


        Column(
            modifier =
                Modifier.scale(
                    centerScale
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    centerValue,

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy
            )


            Text(
                text =
                    centerLabel,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    Muted
            )
        }
    }
}


// ------------------------------------------------
// LEGEND
// ------------------------------------------------

@Composable
private fun LegendItem(
    label: String,
    value: Int,
    color: Color
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier =
                Modifier
                    .size(
                        10.dp
                    )
                    .background(
                        color,
                        CircleShape
                    )
        )


        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )


        Text(
            modifier =
                Modifier.weight(
                    1f
                ),

            text =
                label,

            color =
                Muted,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )


        Text(
            text =
                value.toString(),

            color =
                Navy,

            fontWeight =
                FontWeight.Bold
        )
    }
}


// ------------------------------------------------
// ANIMATED PROGRESS GRAPH
// ------------------------------------------------

@Composable
private fun AnalyticsProgressRow(
    title: String,
    count: Int,
    total: Int,
    accent: Color,
    animationDelay: Int = 0
) {

    val targetProgress =
        if (
            total <= 0
        ) {

            0f

        } else {

            (
                    count.toFloat()
                            / total.toFloat()
                    )
                .coerceIn(
                    0f,
                    1f
                )
        }


    var animationStarted by remember(
        count,
        total
    ) {
        mutableStateOf(false)
    }


    LaunchedEffect(
        count,
        total
    ) {

        animationStarted =
            false

        delay(
            animationDelay.toLong()
        )

        animationStarted =
            true
    }


    val progress by
    animateFloatAsState(
        targetValue =
            if (animationStarted) {
                targetProgress
            } else {
                0f
            },

        animationSpec =
            tween(
                durationMillis = 900,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "analyticsProgress"
    )


    val displayedPercentage =
        (
                progress *
                        100
                )
            .roundToInt()


    Column {

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    title,

                color =
                    Navy,

                fontWeight =
                    FontWeight.SemiBold
            )


            Text(
                text =
                    "$count  •  $displayedPercentage%",

                color =
                    Muted,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    9.dp
                )
        )


        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        9.dp
                    )
                    .background(
                        Track,
                        RoundedCornerShape(
                            50.dp
                        )
                    )
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(
                            progress
                        )
                        .height(
                            9.dp
                        )
                        .background(
                            accent,
                            RoundedCornerShape(
                                50.dp
                            )
                        )
            )
        }
    }
}


// ------------------------------------------------
// ANIMATED COMPLETION RATE
// ------------------------------------------------

@Composable
private fun CompletionRateCard(
    completionRate: Double,
    completed: Int,
    total: Int
) {

    val targetRate =
        completionRate
            .toFloat()
            .coerceIn(
                0f,
                100f
            )


    var animationStarted by remember(
        completionRate
    ) {
        mutableStateOf(false)
    }


    LaunchedEffect(
        completionRate
    ) {

        animationStarted =
            false

        delay(
            150
        )

        animationStarted =
            true
    }


    val animatedRate by
    animateFloatAsState(
        targetValue =
            if (animationStarted) {
                targetRate
            } else {
                0f
            },

        animationSpec =
            tween(
                durationMillis = 1200,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "completionRateAnimation"
    )


    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize(),

        shape =
            RoundedCornerShape(
                24.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Navy
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    22.dp
                )
        ) {

            Text(
                text =
                    "Appointment Completion",

                color =
                    Color.White.copy(
                        alpha = 0.72f
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(
                text =
                    "${formatPercentage(animatedRate.toDouble())}%",

                style =
                    MaterialTheme
                        .typography
                        .displaySmall,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )


            Text(
                modifier =
                    Modifier.padding(
                        top = 3.dp
                    ),

                text =
                    "$completed of $total appointments completed",

                color =
                    Color.White.copy(
                        alpha = 0.65f
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            9.dp
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.12f
                            ),
                            RoundedCornerShape(
                                50.dp
                            )
                        )
            ) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(
                                animatedRate /
                                        100f
                            )
                            .height(
                                9.dp
                            )
                            .background(
                                brush =
                                    Brush.horizontalGradient(
                                        colors =
                                            listOf(
                                                Teal,
                                                Cyan
                                            )
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        50.dp
                                    )
                            )
                )
            }
        }
    }
}


// ------------------------------------------------
// LOADING
// ------------------------------------------------

@Composable
private fun LoadingScreen(
    modifier: Modifier
) {

    var visible by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        visible =
            true
    }


    Box(
        modifier =
            modifier
                .fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        AnimatedVisibility(
            visible =
                visible,

            enter =
                fadeIn(
                    animationSpec =
                        tween(
                            400
                        )
                )
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color =
                        Teal
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                Text(
                    text =
                        "Loading analytics...",

                    color =
                        Muted,

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )
            }
        }
    }
}


// ------------------------------------------------
// ERROR
// ------------------------------------------------

@Composable
private fun ErrorScreen(
    modifier: Modifier,
    message: String,
    onRetry: () -> Unit
) {

    var visible by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        visible =
            true
    }


    Box(
        modifier =
            modifier
                .fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        AnimatedVisibility(
            visible =
                visible,

            enter =
                fadeIn(
                    animationSpec =
                        tween(
                            400
                        )
                ) +
                        slideInVertically(
                            animationSpec =
                                tween(
                                    400
                                ),
                            initialOffsetY = {
                                it / 5
                            }
                        )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        24.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "Analytics unavailable",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Navy
                )


                Text(
                    modifier =
                        Modifier.padding(
                            top = 8.dp
                        ),

                    text =
                        message,

                    color =
                        Muted,

                    textAlign =
                        TextAlign.Center
                )


                Button(
                    modifier =
                        Modifier.padding(
                            top = 18.dp
                        ),

                    onClick =
                        onRetry
                ) {

                    Text(
                        "Retry"
                    )
                }
            }
        }
    }
}


// ------------------------------------------------
// HELPERS
// ------------------------------------------------

private fun formatPercentage(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toInt()
            .toString()

    } else {

        String.format(
            "%.1f",
            value
        )
    }
}