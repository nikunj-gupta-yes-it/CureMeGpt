package com.bussiness.curemegptapp.ui.screen.main.schedule


import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.CommonHeader
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.dialog.SummaryDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.FilterAppointmentsBottomSheet
import com.bussiness.curemegptapp.ui.sheet.FilterFamilyMembersSheet
import com.bussiness.curemegptapp.ui.viewModel.main.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
val appDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M-d-yyyy")

data class Appointment(
    val title: String,
    val doctor: String,
    val patientName: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val icon: Int,
    val isVisibleItem: Boolean = true
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthScheduleScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state = viewModel.uiState
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadAppointments()
            viewModel.loadMedications()
            viewModel.getFamilyMembers()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog1 by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showSheet1 by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedMember by remember { mutableStateOf<String?>(null) }
    var deleteId by remember { mutableStateOf(0) }
    val members by viewModel.memberOption.collectAsState()
    val appointments = state.appointmentList
    val medications = state.medicationList

    val filteredAppointments = appointments.filter { item ->
        val searchMatch =
            item.title.contains(searchQuery, true) ||
                    item.doctor.contains(searchQuery, true) ||
                    item.patientName.contains(searchQuery, true)
        val memberMatch =
            selectedMember == null || item.patientName == selectedMember
        Log.d("TESTING_FILTER", "SELECTED FILTER IS " + selectedFilter)
        val dateMatch = when (selectedFilter) {
            "Today" -> item.date == getTodayDate()
            "Upcoming" -> isUpcoming(item.date)
            "Past" -> isPast(item.date)
            else -> true
        }
        searchMatch && memberMatch && dateMatch
    }

    var selectedMemberMed by remember { mutableStateOf<String?>(null) }

    val filteredMedications = medications.filter { item ->
        val searchMatch = item.title.contains(searchQuery, true) || item.patientName.contains(searchQuery, true)
        val memberMatch = selectedMemberMed == null || item.patientName ==selectedMemberMed
        Log.d("TESTING_FILTER", "SELECTED FILTER IS Medication" + selectedMemberMed)
        searchMatch && memberMatch
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            CommonHeader(stringResource(R.string.health_schedule_title))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabButton(
                    text = stringResource(R.string.appointments_title),
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = stringResource(R.string.medications_title),
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedTab == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                                    text = stringResource(R.string.search_placeholder),
                                    color = Color(0xFFBCBCBC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            leadingIcon = {
                                Row {

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_search_icon),
                                        contentDescription = stringResource(R.string.search_icon_description),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

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
                        contentDescription = stringResource(R.string.filter_icon_description),
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showSheet = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- LIST ----------
                if (filteredAppointments.isEmpty()) {
                    NoDataFound("No Appointments Found")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredAppointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onEditClick = {
                                    Log.d("TESTING_ID", "HERE INSIDE SCREEN" + appointment.id)

                                    navController.currentBackStackEntry
                                        ?.savedStateHandle?.set("appointmentId", appointment.id)
                                    navController.navigate(AppDestination.RescheduleAppointmentScreen)

                                },
                                onDeleteClick = {
                                    deleteId = appointment.id
                                    showDeleteDialog = true
                                },
                                onViewClick = {
                                    showViewDialog = true
                                },
                                onCheckClick = {
                                    viewModel.markAppointmentComplete(appointment.id, {
                                        Toast.makeText(
                                            context,
                                            "Appointment marked as complete",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }, { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    })
                                }
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                                    text = stringResource(R.string.search_placeholder),
                                    color = Color(0xFFBCBCBC),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            leadingIcon = {
                                Row {

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_search_icon),
                                        contentDescription = stringResource(R.string.search_icon_description),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
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
                        contentDescription = stringResource(R.string.filter_icon_description),
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showSheet1 = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- LIST ----------
                if (filteredMedications.isEmpty()) {
                    NoDataFound("No Medications Found")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredMedications) { medication ->
                            MedicationsCard(
                                medication = medication,
                                onEditClick = {
                                    val id = medication.id
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("medicationId", id)
                                    navController.navigate(AppDestination.EditMedicationScreen)
                                },
                                onDeleteClick = {
                                    deleteId = medication.id
                                    showDeleteDialog1 = true
                                }
                            )
                        }
                    }
                }
            }
        }

        GradientRedButton(
            text = if (selectedTab == 0) stringResource(R.string.schedule_button_text) else stringResource(
                R.string.add_medication_button
            ),
            icon = R.drawable.ic_plus_normal_icon,
            width = if (selectedTab == 0) 145.dp else 170.dp,
            height = 52.dp,
            fontSize = 15.sp,
            imageSize = 20.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 15.dp, vertical = 30.dp),
            gradientColors = listOf(
                Color(0xFF4338CA),
                Color(0xFF211C64)
            ),
            onClick = {
                if (selectedTab == 0) navController.navigate(AppDestination.ScheduleNewAppointment)
                else navController.navigate(AppDestination.AddMedication)
            }
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.Blue
                )
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
            Log.d("TESTING_MEMBER", "OPENING FILTER SHEET WITH MEMBERS " + members.size)
            FilterAppointmentsBottomSheet(
                onDismiss = { showSheet = false },
                memberOptions = members,
                onApply = { filter, member ->
                    // Apply filter logic here
                    println("Applied filter: $filter, member: $member")
                    selectedFilter = filter
                    selectedMember = member
                    showSheet = false
                }
            )
        }
    }

    if (showSheet1) {
        BottomSheetDialog(
            onDismissRequest = {
                showSheet1 = false
            },
            properties = BottomSheetDialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                dismissWithAnimation = true,
                enableEdgeToEdge = false,
            )
        ) {


            FilterFamilyMembersSheet(
                members = members,
                selected = selectedMemberMed,
                onSelect = { member ->
                    selectedMemberMed = member     // 🔥 filter apply
                    showSheet1 = false             // 🔥 close sheet
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = stringResource(R.string.delete_appointment_dialog_title),
            message = stringResource(R.string.delete_appointment_dialog_message),
            confirmText = stringResource(R.string.delete_button),
            cancelText = stringResource(R.string.cancel_button),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {

                viewModel.deleteAppointment(deleteId, { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    showDeleteDialog = false
                }, {
                    showDeleteDialog = false
                })

            }
        )
    }

    if (showViewDialog) {
        SummaryDialog(
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",   // jo text show karna hai
            onDismiss = {
                showViewDialog = false
            }
        )

    }

    if (showDeleteDialog1) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = stringResource(R.string.delete_medication_dialog_title),
            message = stringResource(R.string.delete_medication_dialog_message),
            confirmText = stringResource(R.string.delete_button),
            cancelText = stringResource(R.string.cancel_button),
            onDismiss = { showDeleteDialog1 = false },
            onConfirm = {
                viewModel.deleteMedication(deleteId, {
                    showDeleteDialog1 = false
                }, { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                })

            }
        )
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF2C2C2C) else Color.White,
            contentColor = if (selected) Color.White else Color(0xFF697383)
        ),
        shape = RoundedCornerShape(50.dp),
        border = if (!selected) BorderStroke(1.dp, Color(0xFF697383)) else null
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDate(): String {
    return LocalDate.now().format(appDateFormatter)   // aap yaha real formatter laga sakte ho
}

@RequiresApi(Build.VERSION_CODES.O)
fun isUpcoming(date: String): Boolean {
    val itemDate = LocalDate.parse(date, appDateFormatter)
    val today = LocalDate.now()
    return !itemDate.isBefore(today) // includes today
}

@RequiresApi(Build.VERSION_CODES.O)
fun isPast(date: String): Boolean {
    val itemDate = LocalDate.parse(date, appDateFormatter)
    val today = LocalDate.now()
    return itemDate.isBefore(today) // strictly past only
}

@Composable
fun NoDataFound(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Image(
//                painter = painterResource(R.drawable.ic_no_data), // optional
//                contentDescription = null,
//                modifier = Modifier.size(120.dp)
//            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = text,
                color = Color(0xFF9E9E9E),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular))
            )
        }
    }
}