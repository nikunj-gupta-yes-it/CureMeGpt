package com.bussiness.curemegptapp.ui.screen.main.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.HealthProfile
import com.bussiness.curemegptapp.ui.component.AppointmentBox
import com.bussiness.curemegptapp.ui.component.GradientRedButton


val profiles = listOf(
    HealthProfile(
        id = 1,
        name = "James Logan",
        age = "40 yrs",
        relation = "Self",
        lastCheckup = "45 days ago",
        alerts = listOf(
            "Blood pressure medication reminder",
            "Annual checkup due"
        )
    ),
    HealthProfile(
        id = 2,
        name = "Rose Logan",
        age = "38 yrs",
        relation = "Spouse",
        lastCheckup = "20 days ago",
        alerts = listOf(
            "Vitamin D reminder"
        )
    ),
    HealthProfile(
        id = 3,
        name = "Peter Logan",
        age = "12 yrs",
        relation = "Son",
        lastCheckup = "10 days ago",
        alerts = listOf(
            "Vaccination due"
        )
    )
)


@Composable
fun HealthOverviewSection(
    alerts: List<String>,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onSchedule: () -> Unit,
    onAskAi: () -> Unit
) {
    val profiles = remember { profiles }
    var selectedProfile by remember {
        mutableStateOf(profiles.first())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.health_overview_title)/*"Health Overview"*/,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        GradientRedButton(
            text = stringResource(R.string.add_button_text)/*"Add"*/,
            icon = R.drawable.ic_plus_normal_icon,
            width = 88.dp,
            height = 42.dp,
            fontSize = 14.sp,
            imageSize = 16.dp,
            gradientColors = listOf(
                Color(0xFF4338CA),
                Color(0xFF211C64)
            ),
            onClick = { onAddClick() }
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

//    val profiles = listOf(
//        "James (Myself)",
//        "Rose Logan",
//        "Peter Logan"
//    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        profiles.forEach { name ->
//            ProfileTab(
//                name = name.substringBefore(" "),
//                isSelected = name == "James (Myself)",
//                modifier = Modifier.weight(1f),
//                onClick = {
//                    selectedProfile = profile
//                }
//            )
//        }
        profiles.forEach { profile ->
            ProfileTab(
                //   name = profile.name.substringBefore(" "),
                name = profile.name,
                isSelected = profile.id == selectedProfile.id,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedProfile = profile
                }
            )
        }
    }



    Spacer(modifier = Modifier.height(42.dp))
    UserHealthCard(profile = selectedProfile, onEditClick = {
        onEditClick()
    }, onSchedule = { onSchedule() }, onAskAi = { onAskAi() })

    Spacer(modifier = Modifier.height(71.dp))
}

//@Composable
//fun ProfileTab(
//    name: String,
//    isSelected: Boolean,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        modifier = modifier,
//        shape = RoundedCornerShape(50.dp),
//        color = if (isSelected) Color.Black else Color.White,
//        border = if (!isSelected) BorderStroke(1.dp, Color(0xFF697383)) else null,
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 5.dp, vertical = 3.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = name,
//                fontSize = 12.sp,
//                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
//                fontWeight = FontWeight.Medium,
//                color = if (isSelected) Color.White else Color(0xFF697383)
//            )
//        }
//    }
//
//}

@Composable
fun ProfileTab(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected) Color(0xFF3C3C3C) else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFF3C3C3C)) else null
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color(0xFF697383)
            )
        }
    }
}


@Composable
fun UserHealthCard(
    profile: HealthProfile,
    onEditClick: () -> Unit,
    onSchedule: () -> Unit,
    onAskAi: () -> Unit
) {
    val alerts = listOf(
        "Blood pressure medication reminder",
        "Annual checkup due"
    )


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFB)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(20.dp))

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_image),
                            contentDescription = null,
                            modifier = Modifier
                                .matchParentSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(7.dp))
                    Column {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(100.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                modifier = Modifier.width(46.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFE8E4FF)
                            ) {
                                Text(
                                    text = profile.age,
                                    fontSize = 9.sp,
                                    color = Color(0xFF4338CA),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = profile.relation,
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Last checkup: ${profile.lastCheckup}",
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_edit_icon_cirlcular),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(43.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onEditClick()
                        }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.active_alerts_title)/*"Active Alerts"*/,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF697383)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profile.alerts.forEach { alert ->
                    AlertItem(alert)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3   // Optional: control max items in a row
            ) {

                GradientRedButton(
                    text = stringResource(R.string.schedule_button_text)/*"Schedule"*/,
                    icon = R.drawable.ic_schedule_attention_icon,
                    width = 101.dp,
                    height = 42.dp,
                    imageSize = 16.dp,
                    fontSize = 12.sp,
                    horizontalPadding = 12.dp,
                    modifier = Modifier.alignByBaseline(),
                    onClick = { onSchedule() }
                )

                GradientRedButton(
                    text = stringResource(R.string.ask_ai_button_text)/*"Ask Ai"*/,
                    icon = R.drawable.ic_ask_ai_icon,
                    width = 80.dp,
                    height = 42.dp,
                    imageSize = 18.dp,
                    fontSize = 12.sp,
                    horizontalPadding = 10.dp,
                    modifier = Modifier.alignByBaseline(),
                    gradientColors = listOf(
                        Color(0xFF4338CA),
                        Color(0xFF211C64)
                    ),
                    onClick = { onAskAi() }
                )

                AppointmentBox(
                    text = stringResource(R.string.appointment_in_days_format, 7),
                    modifier = Modifier
                        .width(180.dp)
                        .height(42.dp)
                        .alignByBaseline(),
                    iconRes = R.drawable.ic_appointed_icon
                )
            }

        }
    }
}

@Composable
fun AlertItem(alertText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDFD5FC), RoundedCornerShape(50.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_alert_notification),
            contentDescription = null,
            modifier = Modifier.size(29.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = alertText,
            fontSize = 13.sp,
            color = Color(0xFF181818),
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium

        )
    }
}
