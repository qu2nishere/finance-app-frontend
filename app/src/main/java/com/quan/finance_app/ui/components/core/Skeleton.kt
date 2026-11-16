package com.quan.finance_app.ui.components.core

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFFE7ECF3),
        Color(0xFFF7F9FC),
        Color(0xFFE7ECF3)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 600f, y = translateAnim - 600f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    this
        .clip(shape)
        .background(brush)
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(modifier = modifier.shimmerEffect(shape))
}

@Composable
fun DashboardBalanceCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(modifier = Modifier.size(width = 120.dp, height = 16.dp))
                SkeletonBox(modifier = Modifier.size(width = 90.dp, height = 24.dp), shape = RoundedCornerShape(12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            SkeletonBox(modifier = Modifier.size(width = 220.dp, height = 44.dp))
            Spacer(modifier = Modifier.height(12.dp))
            SkeletonBox(modifier = Modifier.size(width = 160.dp, height = 18.dp))
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonBox(modifier = Modifier.size(width = 110.dp, height = 20.dp))
                SkeletonBox(modifier = Modifier.size(width = 110.dp, height = 20.dp))
            }
        }
    }
}

@Composable
fun TransactionRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBox(modifier = Modifier.size(44.dp), shape = CircleShape)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(modifier = Modifier.size(width = 140.dp, height = 18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonBox(modifier = Modifier.size(width = 90.dp, height = 14.dp))
        }
        SkeletonBox(modifier = Modifier.size(width = 80.dp, height = 18.dp))
    }
}

@Composable
fun DashboardTransactionsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(modifier = Modifier.size(width = 140.dp, height = 20.dp))
            SkeletonBox(modifier = Modifier.size(width = 60.dp, height = 16.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        repeat(5) {
            TransactionRowSkeleton()
            if (it < 4) {
                HorizontalDivider(color = Color(0xFFF8F9FA), modifier = Modifier.padding(start = 58.dp))
            }
        }
    }
}

@Composable
fun AllTransactionsDaySkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonBox(modifier = Modifier.size(width = 120.dp, height = 16.dp))
            SkeletonBox(modifier = Modifier.size(width = 80.dp, height = 16.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        repeat(3) {
            TransactionRowSkeleton()
        }
    }
}
