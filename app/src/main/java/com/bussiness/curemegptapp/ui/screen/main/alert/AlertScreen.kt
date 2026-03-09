package com.bussiness.curemegptapp.ui.screen.main.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.AlertModel
import com.bussiness.curemegptapp.ui.component.TopBarHeader1
import com.bussiness.curemegptapp.ui.screen.main.scheduleNewAppointment.ScheduleNewAppointmentScreen

@Composable
fun AlertScreen(
    navController: NavHostController
) {  var backPressedTime by remember { mutableStateOf(0L) }
    val alertList = listOf(
        AlertModel(
            id = 1,
            name = stringResource(R.string.rosy_logan_name),
            title = stringResource(R.string.medication_reminder_title),
            description = stringResource(R.string.medication_reminder_description),
            time = stringResource(R.string.just_now_time),
            priority = "",
            actionRequired = false
        ),
        AlertModel(
            id = 2,
            name = stringResource(R.string.james_logan_name),
            title = stringResource(R.string.urgent_health_report_title),
            description = stringResource(R.string.urgent_health_report_description),
            time = stringResource(R.string.thirty_minutes_ago_time),
            priority = stringResource(R.string.high_priority),
            actionRequired = true
        ),
        AlertModel(
            id = 3,
            name = stringResource(R.string.james_logan_name),
            title = stringResource(R.string.appointment_reminder_title),
            description = stringResource(R.string.appointment_reminder_description),
            time = stringResource(R.string.thirty_two_minutes_ago_time),
            priority = stringResource(R.string.medium_priority),
            actionRequired = false
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) .statusBarsPadding()
    ) {

        TopBarHeader1(title = stringResource(R.string.alerts_title), onBackClick = {
//            val currentTime = System.currentTimeMillis()
//            if (currentTime - backPressedTime > 1000) { // 1 second threshold
//                backPressedTime = currentTime
//                navController.popBackStack()
//            }
            navController.navigateUp()
        })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 50.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),

                ) {
                items(alertList) { item ->
                    AlertCard(
                        name = item.name,
                        title = item.title,
                        description = item.description,
                        time = item.time,
                        showTags = item.priority.isNotEmpty(),
                        priority = item.priority,
                        actionRequired = item.actionRequired
                    )
                }
            }

        }


    }

}




@Preview(showBackground = true)
@Composable
fun AlertScreenPreview() {
    val navController = rememberNavController()
    AlertScreen(navController = navController)
}

//@Composable
//fun AlertCard(
//    name: String,
//    title: String,
//    description: String,
//    time: String,
//    showTags: Boolean,
//    priority: String = "",
//    actionRequired: Boolean = false
//) {
//    var checked by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(30.dp))
//            .border(1.dp, Color(0xFFE7E6F8), RoundedCornerShape(30.dp))
//            .background(Color(0xFFF9F9FD))
//            .padding(20.dp)
//    ) {
//
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(
//                text = "For: $name",
//                color = Color(0xFF211C64),
//                fontSize = 12.sp,
//                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
//                fontWeight = FontWeight.Medium
//            )
//
//            Text(
//                text = time,
//                color = Color.Black,
//                fontSize = 16.sp,
//                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
//                fontWeight = FontWeight.Medium
//            )
//        }
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            text = title,
//            color = Color.Black,
//            fontSize = 14.sp,
//            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//            fontWeight = FontWeight.Normal
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            text = description,
//            color = Color(0xFF404657),
//            fontSize = 15.sp,
//            lineHeight = 20.sp
//        )
//
//        Spacer(Modifier.height(12.dp))
//
//        if (showTags) {
//            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//
//                PriorityTag(
//                    label = priority,
//                    color = if (priority == "High") Color(0xFFEB5757) else Color(0xFFFFA24C)
//                )
//
//                if (actionRequired) {
//                    ActionRequiredTag()
//                }
//            }
//
//            Spacer(Modifier.height(12.dp))
//        }
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Icon(
//                painter = painterResource(id = R.drawable.ic_appointed_gray_icon),
//                contentDescription = "",
//                tint = Color.White,
//                modifier = Modifier
//                    .size(48.dp)
//                    .background(Color(0xFF413BD1), CircleShape)
//                    .padding(10.dp)
//            )
//
//            Spacer(Modifier.width(16.dp))
//
//            Row(
//                modifier = Modifier.clickable( interactionSource = remember { MutableInteractionSource() },
 //                       indication = null){ checked = !checked },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Checkbox(
//                    checked = checked,
//                    onCheckedChange = { checked = it }
//                )
//                Text(
//                    "Mark As Complete",
//                    fontSize = 16.sp,
//                    color = Color.Black
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun PriorityTag(label: String, color: Color) {
//    Box(
//        modifier = Modifier
//            .border(1.dp, color, RoundedCornerShape(50.dp))
//            .padding(vertical = 4.dp, horizontal = 12.dp)
//    ) {
//        Text(
//            text = label,
//            color = color,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}
//
//@Composable
//fun ActionRequiredTag() {
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(50.dp))
//            .background(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(Color(0xFFE74C3C), Color(0xFFAA0015))
//                )
//            )
//            .padding(vertical = 5.dp, horizontal = 14.dp)
//    ) {
//        Text(
//            text = "Action Required",
//            color = Color.White,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}

