package com.bussiness.curemegptapp.ui.screen.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.AttentionItem
import com.bussiness.curemegptapp.ui.component.GradientButton2
import com.bussiness.curemegptapp.ui.viewModel.main.AttentionItem

@Composable
fun ThingsNeedingAttention(
    attentionItems: List<AttentionItem>,
    onScheduleClick: (Int) -> Unit,
    onViewAllClick: () -> Unit
) {

    val attention = listOf(
        Triple(
            stringResource(R.string.tooth_pain_detected_title),
            stringResource(R.string.for_label) + " " + stringResource(R.string.james_logan_name),
            true
        ),
        Triple(
            stringResource(R.string.overdue_dental_cleaning_title),
            stringResource(R.string.for_label) + " " + stringResource(R.string.rosy_logan_name),
            true
        ),
    )


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.things_needing_attention_title)/*"Thing Needing Attention"*/,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        GradientButton2(
            text = stringResource(R.string.view_all_button)/*"View All"*/,
            fontSize = 12.sp,
            paddingHorizontal = 2.dp,
            onClick = onViewAllClick,
            modifier = Modifier
                .width(88.dp)
                .height(42.dp)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    if (!attention.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            attention.forEach { attention ->

                AttentionItem(
                    title = attention.first,
                    subtitle = attention.second,
                    isUrgent = attention.third,

                )
            }
        }
    } else {

    }

}


