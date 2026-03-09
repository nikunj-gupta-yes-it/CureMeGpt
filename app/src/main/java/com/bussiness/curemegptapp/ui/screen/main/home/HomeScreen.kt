package com.bussiness.curemegptapp.ui.screen.main.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.HomeHeader
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.dialog.CompleteProfileDialog
import com.bussiness.curemegptapp.ui.viewModel.main.HomeViewModel
import com.bussiness.curemegptapp.util.SessionManager
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedMood by viewModel.selectedMood
    val context = LocalContext.current

    val sessionManager = SessionManager.getInstance(context)

    val activity = context as? Activity
    var showMoodCard by remember {
        mutableStateOf(!SessionManager(context).isMoodGivenToday(context))
    }

    var showAlertDialog2 by remember { mutableStateOf(false) }
    var showAlertDialog3 by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(5000)
        showAlertDialog2 = true
    }

    if (showAlertDialog2) {
        AlertCardDialog(
            icon =  R.drawable.ic_medication,
            title = stringResource(R.string.medication_reminder_title),
            message =  stringResource(R.string.medication_reminder_message),
            highlightText = stringResource(R.string.medication_reminder_highlight),
            warningText = stringResource(R.string.medication_reminder_warning),
            cancelText = stringResource(R.string.snooze),
            confirmText = stringResource(R.string.mark_as_taken),
            onDismiss = { showAlertDialog2 = false },
            onConfirm = { showAlertDialog2 = false
                showAlertDialog3 = true  }
        )

    }

    if (showAlertDialog3) {
        AlertCardDialog(
            icon =  R.drawable.ic_appointment,
            title = stringResource(R.string.appointment_reminder_title),
            message = stringResource(R.string.appointment_reminder_message),
            highlightText = stringResource(R.string.appointment_reminder_highlight),
            cancelText = stringResource(R.string.remind_later),
            confirmText = stringResource(R.string.got_it),
            onDismiss = { showAlertDialog3 = false },
            onConfirm = { showAlertDialog3 = false
                showCompleteDialog = true}
        )
    }
    if (showCompleteDialog) {
        CompleteProfileDialog(
            icon = R.drawable.ic_person_complete_icon,
            title = stringResource(R.string.complete_profile_title),
            checklist = listOf(
                stringResource(R.string.complete_profile_benefit_1),
                stringResource(R.string.complete_profile_benefit_2),
                stringResource(R.string.complete_profile_benefit_3)
            ),
            cancelText = stringResource(R.string.remind_later),
            confirmText = stringResource(R.string.complete_now),
            skipText = stringResource(R.string.skip_for_now),

            onDismiss = {
                showCompleteDialog = false
            },
            onConfirm = {
                showCompleteDialog = false
                navController.navigate(AppDestination.EditProfileScreen)
            },
            onSkip = {
                showCompleteDialog = false
                // TODO – "Skip for Now"
            }
        )
    }

    BackHandler {
        activity?.finish()
    }

    // Initialize or refresh data when screen appears
    LaunchedEffect(Unit) {
        // Future mein: viewModel.refreshData()
        // For now, just use initial data
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeHeader(
            logoRes = R.drawable.ic_logo,
            notificationRes = R.drawable.ic_notification_home_icon,
            profileRes = R.drawable.ic_profile_image,
            onClick = {
                navController.navigate(AppDestination.MyProfileScreen)
            },
            onClickNotification = {
                navController.navigate(AppDestination.AlertScreen)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,

            ) {
            Spacer(modifier = Modifier.height(23.dp))

            WelcomeSection(userGreating = uiState.userGreating,
                userName =sessionManager.getUserName())

            Spacer(modifier = Modifier.height(20.dp))

            if (showMoodCard) {
                DailyMoodCheckCard(
                    selectedMood = selectedMood,
                    onMoodSelected = { mood -> viewModel.updateMood(mood)
                        Toast.makeText(
                            context,
                            "Thanks for giving your daily mood check 😊",
                            Toast.LENGTH_SHORT
                        ).show()
                        SessionManager(context).saveMoodDate(context)
                        showMoodCard = false
                    },
                    onClose = {
                        showMoodCard = false
                    },
                    onSkip = {
                        showMoodCard = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ProfileCompletionBar(progress = uiState.profileCompletion)

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FD)),
                shape = RoundedCornerShape(30.dp)
            ) {
                MedicationsAndAllergies(
                    medications = uiState.medications,
                    allergies = uiState.allergies,
                    onEditClick = {
                        navController.navigate(AppDestination.EditProfileScreen)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
                RecommendedSteps(steps = uiState.recommendedSteps)
//              Spacer(modifier = Modifier.height(10.dp))
            }
            //  AppDestination.EditProfileScreen
            Spacer(modifier = Modifier.height(20.dp))
            ThingsNeedingAttention(
                attentionItems = uiState.attentionItems,
                onScheduleClick = { itemId ->
                    viewModel.scheduleAttentionItem(itemId)
                },
                onViewAllClick = {
                    navController.navigate(AppDestination.ThingNeedingAttention)
                }
            )
            //
            Spacer(modifier = Modifier.height(25.dp))
            HealthOverviewSection(alerts = uiState.alerts, onAddClick = {
                navController.navigate("addFamilyMember?from=main")
            },onEditClick = {
                navController.navigate(AppDestination.EditFamilyMemberDetailsScreen)
            }, onSchedule = {navController.navigate(AppDestination.ScheduleNewAppointment)}, onAskAi = {navController.navigate("openChat?from=main")})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}




