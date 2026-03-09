package com.bussiness.curemegptapp.ui.screen.main.healthReports

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.CommonHeader
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.FilterHealthReportsBottomSheet

//Health Reports

data class ReportData(
    val icon: Int,
    val title: String,
    val patientName: String,
    val priority: String,
    val date: String,
    val note: String,
    val filesCount: Int
)


@Composable
fun HealthReportsScreen(navController: NavHostController) {
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog1 by remember { mutableStateOf(false) }
    var shareChatMessage = stringResource(R.string.share_chat_message)
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var selectedMember by remember { mutableStateOf<String?>(null) }
    var members: List<String> = listOf(
        "Peter Logan",
        "Rosy Logan",
        "Peter Logan (Son)"
    )

    val reportData = listOf(
        ReportData(
            icon = R.drawable.ic_app_reporting_icon,   // apna icon change kar lena
            title = stringResource(R.string.dental_xray_report_title)/*"Dental X-ray Analysis"*/,
            patientName = "Peter Logan",
            priority = "Attention",
            date = "08/26/2025",
            note = "Minor cavity detected in upper left molar.\nEarly intervention recommended.",
            filesCount = 2
        ),

        ReportData(
            icon = R.drawable.ic_app_reporting_icon,   // same icon use ho sakta hai
            title = "Blood Test Results",
            patientName = "Rosy Logan",
            priority = "Normal",
            date = "08/26/2025",
            note = "All blood markers within normal range.\nExcellent overall health indicators.",
            filesCount = 2
        )
    )

    val filteredList1 = reportData.filter { item ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.patientName.contains(searchQuery, ignoreCase = true)

        // yaha member "James (Myself)" jaisa aayega, tum mapping chaho to yaha map kar sakte ho
        val matchesMember =
            selectedMember.isNullOrBlank() ||
                    item.patientName.contains(selectedMember ?: "", ignoreCase = true)

        matchesSearch && matchesMember
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            CommonHeader(stringResource(R.string.health_reports_title)/*"Health Reports"*/)

            Spacer(modifier = Modifier.height(7.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFF4F4F4)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            maxLines = 1,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_placeholder)/*"Search"*/,
                                    color = Color(0xFFBCBCBC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = stringResource(R.string.search_placeholder),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                disabledContainerColor = Color(0xFFF4F4F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_icon),
                        contentDescription = "Filter",
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = null) {
                                showSheet = true
                            }
                    )

                }



                // ---------- LIST ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(filteredList1) { item ->
                        ReportCard(
                            icon = item.icon,
                            title = item.title,
                            patientName = item.patientName,
                            priority = item.priority, // or your required field
                            date = item.date,
                            note = item.note,
                            filesCount = item.filesCount,
                            onViewClick = {
                             navController.navigate(AppDestination.ReportScreen)
                            },
                            onShareClick = {
                                showDeleteDialog1 = true
                                // Share logic
                                val shareText = shareChatMessage

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }

                                context.startActivity(
                                    Intent.createChooser(intent, "Share chat via")
                                )
                            }
                        )


                    }

                }

            }


    }


    if (showSheet) {
        BottomSheetDialog(
            onDismissRequest = {
                showSheet = false
            },
            properties = BottomSheetDialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                dismissWithAnimation = true,
                enableEdgeToEdge = false,
            )
        ) {

            FilterHealthReportsBottomSheet(
                memberOptions = members,
                onDismiss = { showSheet = false },
                onApply = { filter, member ->
                    // ✅ yahi se filter & member set karna hai
                    selectedFilter = filter          // agar baad me priority ya type se filter karna ho
                    selectedMember = member          // yahi patientName based filter ka base hoga
                    showSheet = false
                }
            )
        }
    }




//    if (showDeleteDialog) {
//        AlertCardDialog(
//            icon = R.drawable.ic_delete_icon_new,
//            title = "Delete Appointment?",
//            message = "Are you sure you want to delete Peter’s appointment? This action cannot be undone.",
//            confirmText = "Delete",
//            cancelText = "Cancel",
//            onDismiss = { showDeleteDialog = false},
//            onConfirm = {  showDeleteDialog = false
//            }
//        )
//
//    }

}