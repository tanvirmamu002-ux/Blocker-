package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AddictionTrigger
import com.example.state.FocusViewModel
import com.example.ui.components.CategoryDonutChartCard
import com.example.ui.components.DisciplineMasteryCard
import com.example.ui.components.GradientTriggerProgressItem
import com.example.ui.components.InteractiveTrendChart
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguri

@Composable
fun AnalyticsScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val strings = com.example.util.LocalAppStrings.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.bindContext(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // 1. Header
        Text(
            text = strings.analyticsTitle,
            color = colors.textPrimary,
            fontSize = 20.sp,
            fontFamily = HindSiliguri,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = strings.analyticsSubtitle,
            color = colors.textSecondary,
            fontSize = 13.sp,
            fontFamily = HindSiliguri
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Period Selector: "সাপ্তাহিক" (Weekly) | "মাসিক" (Monthly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceElevated.copy(alpha = 0.8f))
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            val isWeekly = viewModel.analyticsPeriod == "weekly"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isWeekly) colors.primary else Color.Transparent)
                    .clickable { viewModel.setPeriod("weekly") }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.analyticsWeekly,
                    color = if (isWeekly) (if (colors.isDark) Color(0xFF0B0E14) else Color.White) else colors.textSecondary,
                    fontSize = 13.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isWeekly) colors.primary else Color.Transparent)
                    .clickable { viewModel.setPeriod("monthly") }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.analyticsMonthly,
                    color = if (!isWeekly) (if (colors.isDark) Color(0xFF0B0E14) else Color.White) else colors.textSecondary,
                    fontSize = 13.sp,
                    fontFamily = HindSiliguri,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Mastery & Encouragement Card
        DisciplineMasteryCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Two Highlight Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: Total Saved Time
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                colors.surfaceElevated.copy(alpha = 0.7f),
                                colors.surface
                            )
                        )
                    )
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = if (viewModel.analyticsPeriod == "weekly") strings.analyticsWeeklySaved else strings.analyticsMonthlySaved,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = viewModel.totalSavedTimeString,
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (colors.isDark) Color(0xFF0F2B1F) else Color(0xFFE8F5E9))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Up",
                            tint = colors.secondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (viewModel.analyticsPeriod == "weekly") strings.analyticsWeeklyTrend else strings.analyticsMonthlyTrend,
                            color = colors.secondary,
                            fontSize = 10.sp,
                            fontFamily = HindSiliguri,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right: Total Blocked Attempts
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                colors.surfaceElevated.copy(alpha = 0.7f),
                                colors.surface
                            )
                        )
                    )
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = strings.analyticsTotalBlocked,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = HindSiliguri
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${viewModel.totalBlockedAttemptsCount}", // Removed 'বার' for better generic format
                        color = colors.alert,
                        fontSize = 20.sp,
                        fontFamily = HindSiliguri,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = strings.analyticsMostBlocked,
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        fontFamily = HindSiliguri,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Interactive Trend Chart (Smooth Curved Bezier & Dual Bar)
        InteractiveTrendChart(
            stats = viewModel.activeDisciplineStats,
            period = viewModel.analyticsPeriod,
            insightText = viewModel.bestDayInsightText
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 6. Category Donut Chart
        val totalBlocked = viewModel.topTriggers.sumOf { it.blockedCount }
        CategoryDonutChartCard(
            triggers = viewModel.topTriggers,
            totalBlockedCount = totalBlocked
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Top Addiction Triggers with Gradient Progress Bars
        Text(
            text = strings.analyticsTopTriggers,
            color = colors.textPrimary,
            fontSize = 17.sp,
            fontFamily = HindSiliguri,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            colors.surfaceElevated.copy(alpha = 0.7f),
                            colors.surface
                        )
                    )
                )
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            viewModel.topTriggers.forEach { trigger ->
                GradientTriggerProgressItem(trigger = trigger)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 8. Export / Report Download Button
        Button(
            onClick = {
                val reportType = if (viewModel.analyticsPeriod == "weekly") strings.analyticsWeekly else strings.analyticsMonthly
                viewModel.showToast(strings.analyticsReportToast(reportType))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.surfaceElevated,
                contentColor = colors.textPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
                .testTag("btn_export_analytics")
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export",
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (viewModel.analyticsPeriod == "weekly") strings.analyticsDownloadWeekly else strings.analyticsDownloadMonthly,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                fontFamily = HindSiliguri
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
