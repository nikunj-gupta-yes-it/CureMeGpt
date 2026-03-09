package com.bussiness.curemegptapp.ui.screen.main.thingNeedingAttention


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.AttentionItem
import com.bussiness.curemegptapp.ui.component.SettingHeader

@Composable
fun ThingNeedingAttentionScreen(navController: NavHostController) {
    var backPressedTime by remember { mutableStateOf(0L) }
    val attention = listOf(
        Triple(
            stringResource(R.string.thing_needing_attention_tooth_pain),
            stringResource(R.string.thing_for_james_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_dental_cleaning),
            stringResource(R.string.thing_for_rosy_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_tooth_pain),
            stringResource(R.string.thing_for_james_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_dental_cleaning),
            stringResource(R.string.thing_for_rosy_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_tooth_pain),
            stringResource(R.string.thing_for_james_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_dental_cleaning),
            stringResource(R.string.thing_for_rosy_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_tooth_pain),
            stringResource(R.string.thing_for_james_logan),
            true
        ),
        Triple(
            stringResource(R.string.thing_needing_attention_dental_cleaning),
            stringResource(R.string.thing_for_rosy_logan),
            true
        ),
    )

    Column(
        Modifier.fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {

        SettingHeader(
            title = stringResource(R.string.thing_needing_attention_title),
            onBackClick = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime > 1000) { // 1 second threshold
                    backPressedTime = currentTime
                    navController.popBackStack()
                }
            }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 30.dp)
        ) {
            items(attention) { item ->
                AttentionItem(
                    title = item.first,
                    subtitle = item.second,
                    isUrgent = item.third
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThingNeedingAttentionScreenPreview() {
    val navController = rememberNavController()
    ThingNeedingAttentionScreen(navController = navController)
}
/*
@Composable
fun ThingNeedingAttentionScreen(navController: NavHostController){
    var backPressedTime by remember { mutableStateOf(0L) }
    val attention = listOf(
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true),
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true),
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true),
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true),

        )

    Column (Modifier.fillMaxSize()
        .background(Color.White) .statusBarsPadding()) {

        SettingHeader(title = "Thing Needing Attention", onBackClick = {

            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime > 1000) { // 1 second threshold
                backPressedTime = currentTime
                navController.popBackStack()
            }
        })

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 30.dp)
        ) {
            items(attention) { item ->
                AttentionItem(
                    title = item.first,
                    subtitle = item.second,
                    isUrgent = item.third
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun ThingNeedingAttentionScreenPreview() {
    val navController = rememberNavController()
    ThingNeedingAttentionScreen(navController = navController)
}
 */
