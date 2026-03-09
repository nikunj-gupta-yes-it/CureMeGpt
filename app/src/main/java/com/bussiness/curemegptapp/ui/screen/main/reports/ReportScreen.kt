package com.bussiness.curemegptapp.ui.screen.main.reports


import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.bussiness.curemegptapp.ui.component.DocumentItem2
import com.bussiness.curemegptapp.ui.component.PriorityImageTag
import com.bussiness.curemegptapp.ui.component.TopBarHeader2

@Composable
fun ReportScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)).statusBarsPadding()
    ) {
        var shareChatMessage = stringResource(R.string.share_chat_message)
        val context = LocalContext.current
        val attachmentList = listOf(
            stringResource(R.string.attachment_xray),
            stringResource(R.string.attachment_analysis_report),
            stringResource(R.string.attachment_blood_test),
            stringResource(R.string.attachment_prescription)
          /*  "xray_001.jpg",
            "analysis_report.pdf",
            "blood_test_result.png",
            "prescription_2025.pdf"*/
        )
        var priority = "Attention"
        TopBarHeader2(title = stringResource(R.string.back_to_reports)/*"Back to Reports"*/, onBackClick = {navController.navigateUp()})
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(19.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp,140.dp)
                        .background(Color(0xFF4C3FCC), RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                      painter = painterResource(id = R.drawable.ic_report_mini_icon),
                        contentDescription = null,
                        modifier = Modifier.size(19.dp,23.dp)
                    )
                }

                // Title and Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.blood_test_results)/*"Blood Test Results"*/,
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(10.dp))


                    PriorityImageTag(
                        label = priority,
                        color = if (priority == "Attention") Color(0xFFF31D1D) else Color(0xFF19BB9B),
                        backgroundColor = if (priority == "Attention") Color(0xFFF6DFE6) else Color(0xFFD3ECEC),
                        borderColor = if (priority == "Attention") Color(0xFFF31D1D) else Color(0xFF19BB9B),
                        icon = if (priority == "Attention") R.drawable.ic_attention_icon_red else R.drawable.ic_normal_icon_green
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(29.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_calender_health_icon),
                                contentDescription = null,
                                modifier = Modifier.size(29.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.report_date)/*"8/26/2025"*/,
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(29.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_profile_icon4),
                                contentDescription = null,
                                modifier = Modifier.size(29.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.patient_name)/*"James Logan"*/,
                            fontSize = 16.sp,
                            color = Color(0xFF4338CA),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_share_icon),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp).clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null,){
                                    // Share logic
                                    val shareText = shareChatMessage

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }

                                    context.startActivity(
                                        Intent.createChooser(intent, "Share chat via")
                                    )
                                },
                            )
                    Spacer(modifier = Modifier.height(20.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_download_black_icon),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.summary)/*"Summary"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Minor cavity detected in upper left molar. Early intervention recommended.",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF181818),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Analysis Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.detailed_analysis)/*"Detailed Analysis"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.detailed_analysis_content)/*"AI analysis detected a small cavity formation in tooth #14 (upper left first molar). The cavity appears to be in the early stages of development, affecting only the enamel layer. Recommended treatment includes fluoride application and monitoring."*/,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF181818),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Insights Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.ai_insights)/*"AI Insights"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

       /*             InsightRow("Cavity size", "2mm diameter")
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow("Location", "Occlusal surface")
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow("Severity", "Early stage")
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow("Treatment urgency", "Within 2-4 weeks")*/
                    InsightRow(
                        label = stringResource(R.string.cavity_size),
                        value = stringResource(R.string.cavity_size_value)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow(
                        label = stringResource(R.string.location),
                        value = stringResource(R.string.location_value)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow(
                        label = stringResource(R.string.severity),
                        value = stringResource(R.string.severity_value)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    InsightRow(
                        label = stringResource(R.string.treatment_urgency),
                        value = stringResource(R.string.treatment_urgency_value)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Attachments Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF9F9FD),
                border = BorderStroke(1.dp, Color(0xFFE7E6F8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.attachments)/*"Attachments"*/,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4338CA)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        attachmentList.forEach { fileName ->
                            DocumentItem2(
                                label = fileName,
                                onDownloadClick = { /* Download event */ }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF211C64),
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF181818),
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
        )
    }
}



@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportScreenPreview() {
    val navController = rememberNavController()
        ReportScreen(navController)
}