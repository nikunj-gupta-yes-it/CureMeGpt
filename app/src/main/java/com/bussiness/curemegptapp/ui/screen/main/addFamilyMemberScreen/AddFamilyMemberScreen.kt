package com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen

//AddFamilyMemberScreen


import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.BloodGroupDropdown
import com.bussiness.curemegptapp.ui.component.DisclaimerBox
import com.bussiness.curemegptapp.ui.component.Dropdown1
import com.bussiness.curemegptapp.ui.component.FileAttachment
import com.bussiness.curemegptapp.ui.component.GenderDropdown
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.ProfileInputField
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField
import com.bussiness.curemegptapp.ui.component.ProfileInputWithoutLabelField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.TopBarHeader
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.CompleteProfileDialog
import com.bussiness.curemegptapp.ui.dialog.LogOutDialog
import com.bussiness.curemegptapp.ui.dialog.MemberAddedSuccessfullyDialog
import com.bussiness.curemegptapp.ui.viewModel.main.AddFamilyMemberViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.EditProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFamilyMemberScreen(
    navController: NavHostController, from: String? = "",
    viewModel: AddFamilyMemberViewModel = hiltViewModel()
) {
    val currentStep by remember { viewModel.currentStep }
    val profileData by remember { viewModel.profileData }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showAlertDialog2 by remember { mutableStateOf(false) }
    var showAlertDialog3 by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }



    BackHandler {
        if (currentStep == 0) {
            if (from == "main") {
                navController.popBackStack()
            } else {
                navController.navigate(AppDestination.MainScreen) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
        } else {
            viewModel.goToPreviousStep()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding().imePadding()
            .background(Color(0xFFFFFFFF))
    ) {
        // Top Bar
        TopBarHeader(
            currentStep = currentStep,
            onBackClick = {
                if (currentStep == 0) {
                    if (from == "main") {
                        navController.popBackStack()
                    } else {
                        navController.navigate(AppDestination.MainScreen) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                } else {
                    viewModel.goToPreviousStep()
                }
            },
            title = "Add Family Members",
            skipDisplay = false
        )

        when (currentStep) {
            0 -> PersonalInfoStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = { viewModel.goToNextStep() }
            )

            1 -> GeneralInfoStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = { viewModel.goToNextStep() }
            )

            2 -> HistoryStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = { viewModel.goToNextStep() }
            )

            3 -> DocumentsStep(
                viewModel = viewModel,
                profileData = profileData,
                onNext = {
                    viewModel.submitProfile()
                    showAlertDialog = true  // Dialog show करें
                },
                onBack = {
                    viewModel.goToPreviousStep()
                }
            )
        }
    }

    if (showAlertDialog) {
        MemberAddedSuccessfullyDialog(
            title = "Member Added Successfully",
            message = "Your family member has been added.\n" +
                    "\n" +
                    "Would you like to add another member?",
            cancelText = "Add Another Member",
            confirmText = "Done",
            onDismiss = {
                showAlertDialog = false
                navController.navigateUp()
            },
            onConfirm = {
                showAlertDialog = false
                navController.navigateUp()
            }
        )

    }

}
/*
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun PersonalInfoStep(
//    viewModel: AddFamilyMemberViewModel,
//    profileData: ProfileData,
//    onNext: () -> Unit
//) {
//    var fullName by remember { mutableStateOf(profileData.fullName) }
//    var contactNumber by remember { mutableStateOf(profileData.contactNumber) }
//    var email by remember { mutableStateOf(profileData.email) }
//    var dateOfBirth by remember { mutableStateOf(profileData.dateOfBirth) }
//    var gender by remember { mutableStateOf(profileData.gender) }
//    var showDialog by remember { mutableStateOf(false) }
//    val genderOptions =
//        listOf("Male", "Female", "Other") // Added example options
//
//    if (showDialog) {
//        CalendarDialog(
//            onDismiss = { showDialog = false },
//            onDateApplied = {
//                // SELECTED DATE HERE
//                showDialog = false
//                dateOfBirth = it.toString()
//            }
//        )
//    }
//
//
//    // Height and weight with units
//    var heightValue by remember {
//        mutableStateOf(profileData.height.split(" ").getOrNull(0) ?: "")
//    }
//    var heightUnit by remember {
//        mutableStateOf(profileData.height.split(" ").getOrNull(1) ?: "Cm")
//    }
//
//    var weightValue by remember {
//        mutableStateOf(profileData.weight.split(" ").getOrNull(0) ?: "")
//    }
//    var weightUnit by remember {
//        mutableStateOf(profileData.weight.split(" ").getOrNull(1) ?: "Kg")
//    }
//
//    var selectedProfilePhotoUri by remember { mutableStateOf(profileData.profilePhotoUri) }
//    var selectedProfilePhotoName by remember {
//        mutableStateOf(profileData.profilePhotoUri?.lastPathSegment ?: "No file chosen")
//    }
//
//    val profilePhotoPickerLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
//            uri?.let {
//                selectedProfilePhotoUri = it
//                selectedProfilePhotoName = it.lastPathSegment ?: "selected_file"
//            }
//        }
//
//    fun openProfilePhotoPicker() {
//        profilePhotoPickerLauncher.launch(arrayOf("image/*"))
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//        ProfileInputField(
//            label = "Full Name",
//            isImportant = true,
//            placeholder = "e.g., James Carter",
//            value = fullName,
//            onValueChange = { fullName = it }
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        ProfileInputField(
//            label = "Contact Number",
//            placeholder = "555 123 456",
//            value = contactNumber,
//            onValueChange = { contactNumber = it },
//            keyboardType = KeyboardType.Phone
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        ProfileInputField(
//            label = "Email Address",
//            placeholder = "james@gmail.com",
//            value = email,
//            onValueChange = { email = it },
//            keyboardType = KeyboardType.Email
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        UniversalInputField(
//            title = "Date of Birth",
//            isImportant = true,
//            placeholder = "MM-DD-YYYY",
//            value = dateOfBirth,
//            rightIcon = R.drawable.ic_calender_icon
//        ) {
//            showDialog = true
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
//
//            Text(
//                text = "Gender",
//                color = Color.Black,
//                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                fontWeight = FontWeight.Normal,
//                modifier = Modifier.padding(start = 3.dp, bottom = 6.dp)
//            )
//            Text(
//                text = " *",
//                color = Color.Red,
//                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                fontWeight = FontWeight.Normal,
//                modifier = Modifier.padding(bottom = 6.dp)
//            )
//        }
//
//        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
//            CustomPowerSpinner(
//                selectedText = gender,
//                onSelectionChanged = { reason ->
//                    gender = reason
//                },
//                horizontalPadding = 24.dp,
//                reasons = genderOptions // Pass the list of options here
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
//            Dropdown1(
//                label = "Height (Cm/Ft)",
//                isImportant = true,
//                placeholder = "e.g., 172 Cm",
//                value = heightValue,
//                onValueChange = { heightValue = it },
//                dropdownItems = listOf("Cm", "Ft"),
//                selectedUnit = heightUnit,
//                onUnitSelected = { newUnit ->
//                    heightUnit = newUnit
//                }
//            )
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(modifier = Modifier.padding(horizontal = 7.dp)) {
//            Dropdown1(
//                label = "Weight (Kg/Lb)",
//                isImportant = true,
//                placeholder = "e.g., 78 Kg",
//                value = weightValue,
//                onValueChange = { weightValue = it },
//                dropdownItems = listOf("Kg", "Lb"),
//                selectedUnit = weightUnit,
//                onUnitSelected = { newUnit ->
//                    weightUnit = newUnit
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(modifier = Modifier.padding(horizontal = 9.dp)) {
//            ProfilePhotoPicker(
//                label = "Profile Photo (Optional)",
//                fileName = selectedProfilePhotoName,
//                onChooseClick = { openProfilePhotoPicker() }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        GradientButton(
//            horizontalPadding = 2.dp,
//            text = "Save & Continue",
//            onClick = {
//                val height = if (heightValue.isNotBlank()) "$heightValue $heightUnit" else ""
//                val weight = if (weightValue.isNotBlank()) "$weightValue $weightUnit" else ""
//
//                viewModel.updatePersonalInfo(
//                    fullName = fullName,
//                    contactNumber = contactNumber,
//                    email = email,
//                    dateOfBirth = dateOfBirth,
//                    gender = gender,
//                    height = height,
//                    weight = weight,
//                    profilePhotoUri = selectedProfilePhotoUri
//                )
//                onNext()
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//    }
//}

//@Composable
//fun GeneralInfoStep(
//    viewModel: AddFamilyMemberViewModel,
//    profileData: ProfileData,
//    onNext: () -> Unit
//) {
//    var bloodGroup by remember { mutableStateOf(profileData.bloodGroup) }
//    var selectedAllergies by remember { mutableStateOf(profileData.allergies.toSet()) }
//    var customAllergy by remember { mutableStateOf("") }
//    var emergencyName by remember { mutableStateOf(profileData.emergencyContactName) }
//    var emergencyPhone by remember { mutableStateOf(profileData.emergencyContactPhone) }
//
//    val allergyOptions = listOf(
//        "Drug", "Food", "Environmental", "Aspirin",
//        "Latex", "Ibuprofen", "Shellfish", "Nuts",
//        "Penicillin", "Others"
//    )
//    val bloodOptions = listOf(
//        "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//
//        Column(modifier = Modifier.padding(horizontal = 9.dp)) {
//
//            Row {
//
//                Text(
//                    text = "Blood Group",
//                    color = Color.Black,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(bottom = 6.dp)
//                )
//                Text(
//                    text = " *",
//                    color = Color.Red,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(bottom = 6.dp)
//                )
//            }
//
//
//            CustomPowerSpinner(
//                selectedText = bloodGroup,
//                onSelectionChanged = { reason ->
//                    bloodGroup = reason
//                },
//                horizontalPadding = 24.dp,
//                reasons = bloodOptions // Pass the list of options here
//            )
//
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row {
//
//
//                Text(
//                    text = "Known Allergies",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Text(
//                    text = "*",
//                    color = Color.Red,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(bottom = 6.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(max = 600.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
//                userScrollEnabled = false
//            ) {
//                items(allergyOptions) { item ->
//                    val isSelected = item in selectedAllergies
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(40.dp))
//                            .border(
//                                1.dp,
//                                if (isSelected) Color(0xFF5B4FFF) else Color(0xFFE0E0E0),
//                                RoundedCornerShape(40.dp)
//                            )
//                            .background(
//                                if (isSelected) Color(0x205B4FFF) else Color.Transparent
//                            )
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                selectedAllergies = if (isSelected)
//                                    selectedAllergies - item
//                                else
//                                    selectedAllergies + item
//                            }
//                            .padding(vertical = 5.dp)
//                            .fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = item,
//                            fontSize = 13.sp,
//                            color = if (isSelected) Color(0xFF5B4FFF) else Color.Black
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if ("Others" in selectedAllergies) {
//                ProfileInputWithoutLabelField(
//                    placeholder = "Write allergy",
//                    value = customAllergy,
//                    onValueChange = { customAllergy = it }
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            ProfileInputField(
//                label = "Emergency Contact Name (Optional)",
//                isImportant = false,
//                placeholder = "e.g. Bob Dsouza",
//                value = emergencyName,
//                onValueChange = { emergencyName = it }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            ProfileInputField(
//                label = "Emergency Phone Number (Optional)",
//                isImportant = false,
//                placeholder = "e.g. 555 945 325",
//                value = emergencyPhone,
//                onValueChange = { emergencyPhone = it }
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//        }
//        GradientButton(
//            text = "Save & Continue",
//            onClick = {
//                val allergiesList = selectedAllergies.toMutableList()
//                if ("Others" in selectedAllergies && customAllergy.isNotEmpty()) {
//                    allergiesList.add(customAllergy)
//                }
//
//                viewModel.updateGeneralInfo(
//                    bloodGroup = bloodGroup,
//                    allergies = allergiesList,
//                    emergencyName = emergencyName,
//                    emergencyPhone = emergencyPhone
//                )
//                onNext()
//            }
//        )
//    }
//}

//@Composable
//fun HistoryStep(
//    viewModel: AddFamilyMemberViewModel,
//    profileData: ProfileData,
//    onNext: () -> Unit
//) {
//    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
//    var surgicalHistory by remember { mutableStateOf("") }
//    var currentMedications by remember { mutableStateOf(listOf("")) }
//    var currentSupplements by remember { mutableStateOf(listOf("")) }
//    var customCondition by remember { mutableStateOf("") }
//    val conditions = listOf(
//        "Diabetes", "Asthma", "Hypertension", "Thyroid",
//        "Arthritis", "Heart Disease", "Anxiety", "Depression", "Others"
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//        Column(modifier = Modifier.padding(horizontal = 9.dp)) {
//
//            Row {
//                Text(
//                    text = "Chronic Conditions",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Text(
//                    text = "*",
//                    color = Color.Red,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(bottom = 6.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // ⭐ Same UI as allergies → 3-column Grid chips
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(max = 600.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
//                userScrollEnabled = false
//            ) {
//
//                items(conditions) { condition ->
//
//                    val isSelected = condition in selectedConditions
//
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(40.dp))
//                            .border(
//                                1.dp,
//                                if (isSelected) Color(0xFF5B4FFF) else Color(0xFFE0E0E0),
//                                RoundedCornerShape(40.dp)
//                            )
//                            .background(
//                                if (isSelected) Color(0x205B4FFF) else Color.Transparent
//                            )
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                selectedConditions =
//                                    if (isSelected) selectedConditions - condition
//                                    else selectedConditions + condition
//                            }
//                            .padding(vertical = 6.dp)
//                            .fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = condition,
//                            fontSize = 13.sp,
//                            color = if (isSelected) Color(0xFF5B4FFF) else Color.Black
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            // ⭐ Custom allergy field — only if Others selected
//            if ("Others" in selectedConditions) {
//
//                ProfileInputWithoutLabelField(
//                    placeholder = "Write Condition",
//                    value = customCondition,
//                    onValueChange = { customCondition = it }
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            ProfileInputMultipleLineField(
//                label = "Surgical History (Optional)",
//                isImportant = false,
//                placeholder = "Any previous surgeries or major medical procedures...",
//                value = surgicalHistory,
//                onValueChange = { surgicalHistory = it }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//
//            Text(
//                text = "Current Medications (Optional)",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            currentMedications.forEachIndexed { index, medication ->
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    OutlinedTextField(
//                        value = medication,
//                        onValueChange = { newValue ->
//                            // ⭐ Sirf first item editable hona chahiye
//                            if (index == 0) {
//                                val updated = currentMedications.toMutableList()
//                                updated[index] = newValue
//                                currentMedications = updated
//                            }
//                        },
//                        placeholder = {
//                            if (index == 0)
//                                Text("Any medications you're currently taking...")
//                            else
//                                Text("Added item")  // non editable item placeholder
//                        },
//                        modifier = Modifier
//                            .weight(1f)
//                            .clip(RoundedCornerShape(30.dp))
//                            .border(1.dp, Color(0xFFC3C6CB), RoundedCornerShape(30.dp)),
//                        enabled = index == 0,    // ⭐ only first row editable
//                        colors = TextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            disabledIndicatorColor = Color.Transparent
//                        )
//                    )
//
//                    if (index == 0) {
//                        // ⭐ ADD ICON only for the FIRST item
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_add_icon),
//                            contentDescription = "Add",
//                            modifier = Modifier
//                                .padding(start = 8.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//
//                                    // ⭐ Add tabhi hoga jab first field empty na ho
//                                    if (currentMedications[0].isNotBlank()) {
//                                        //   currentMedications = currentMedications + medication
//                                        val updated = currentMedications.toMutableList()
//
//                                        // ⭐ 1. Add a new empty item at bottom
//                                        updated.add(currentMedications[0])
//
//                                        // ⭐ 2. Clear first input field
//                                        updated[0] = ""
//
//                                        currentMedications = updated
//                                    }
//                                }
//                        )
//                    } else {
//                        // ⭐ Other items → REMOVE icon
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_remove_icon),
//                            contentDescription = "Remove",
//                            modifier = Modifier
//                                .padding(start = 8.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    val updated = currentMedications.toMutableList()
//                                    updated.removeAt(index)
//                                    currentMedications = updated
//                                }
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//
//
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//
//            // ⭐ Current Supplements
//            Text(
//                text = "Current Supplements (Optional)",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            currentSupplements.forEachIndexed { index, supplement ->
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    OutlinedTextField(
//                        value = supplement,
//                        onValueChange = { newValue ->
//                            // ⭐ Sirf first item editable hona chahiye
//                            if (index == 0) {
//                                val updated = currentSupplements.toMutableList()
//                                updated[index] = newValue
//                                currentSupplements = updated
//                            }
//                        },
//                        placeholder = {
//                            if (index == 0)
//                                Text("Any supplements you're currently taking...")
//                            else
//                                Text("Added item")
//                        },
//                        modifier = Modifier
//                            .weight(1f)
//                            .clip(RoundedCornerShape(30.dp))
//                            .border(
//                                1.dp,
//                                Color(0xFFC3C6CB),
//                                RoundedCornerShape(30.dp)
//                            ),
//                        enabled = index == 0,   // ⭐ only first item editable
//                        colors = TextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White, // ⭐ FIX
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            disabledIndicatorColor = Color.Transparent
//                        )
//                    )
//
//                    if (index == 0) {
//                        // ⭐ ADD icon - first item only
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_add_icon),
//                            contentDescription = "Add",
//                            modifier = Modifier
//                                .padding(start = 8.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//
//                                    if (currentSupplements[0].isNotBlank()) {
//
//                                        val updated = currentSupplements.toMutableList()
//
//                                        // ⭐ Add new item in list
//                                        updated.add(currentSupplements[0])
//
//                                        // ⭐ Clear first input
//                                        updated[0] = ""
//
//                                        currentSupplements = updated
//                                    }
//                                }
//                        )
//                    } else {
//                        // ⭐ REMOVE icon - for added items
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_remove_icon),
//                            contentDescription = "Remove",
//                            modifier = Modifier
//                                .padding(start = 8.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    val updated = currentSupplements.toMutableList()
//                                    updated.removeAt(index)
//                                    currentSupplements = updated
//                                }
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//
//
//            Spacer(modifier = Modifier.height(24.dp))
//        }
//        GradientButton(
//            text = "Save & Continue",
//            onClick = {
//                val conditionsList = selectedConditions.toMutableList()
//                if ("Others" in selectedConditions && customCondition.isNotEmpty()) {
//                    conditionsList.add(customCondition)
//                }
//
//                viewModel.updateMedicalHistory(
//                    chronicConditions = conditionsList,
//                    surgicalHistory = surgicalHistory,
//                    currentMedications = currentMedications.filter { it.isNotBlank() },
//                    currentSupplements = currentSupplements.filter { it.isNotBlank() }
//                )
//                onNext()
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//    }
//}

*/*/




@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddFamilyMemberScreenPreview() {
    val navController = rememberNavController()
    AddFamilyMemberScreen(navController = navController)
}