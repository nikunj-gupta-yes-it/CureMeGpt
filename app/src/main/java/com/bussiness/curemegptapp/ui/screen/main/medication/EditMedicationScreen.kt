package com.bussiness.curemegptapp.ui.screen.main.medication

//EditMedicationScreen


import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.CancelButton
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.ProfileInputMultipleLineField2
import com.bussiness.curemegptapp.ui.component.ProfileInputSmallField
import com.bussiness.curemegptapp.ui.component.ProfilePhotoPicker
import com.bussiness.curemegptapp.ui.component.RoundedCustomCheckbox
import com.bussiness.curemegptapp.ui.component.TopBarHeader1
import com.bussiness.curemegptapp.ui.component.UniversalInputField
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner
import com.bussiness.curemegptapp.ui.dialog.CalendarDialog
import com.bussiness.curemegptapp.ui.dialog.SuccessfulDialog
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditMedicationScreen(
    navController: NavHostController
) {
    // DUMMY DATA INITIALIZED WITH REALISTIC VALUES
    var dateOfBirth by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("04-15-2024") } // DUMMY: Two weeks ago
    var endDate by remember { mutableStateOf("05-15-2024") } // DUMMY: One month course
    var currentReminder by remember { mutableStateOf("") }
    var medicationName by remember { mutableStateOf("Amoxicillin") } // DUMMY: Common antibiotic
    var dosage by remember { mutableStateOf("500mg") } // DUMMY: Standard dosage
    var selectedMyself by remember { mutableStateOf("Myself") } // DUMMY: For myself
    var selectFrequency by remember { mutableStateOf("Daily") } // DUMMY: Daily medication
    var selectDayName by remember { mutableStateOf("Monday") } // DUMMY: Starting day
    val myselfOptions = listOf("Myself", "Jane Smith", "Alice Johnson", "Bob Williams")
    val selectedMedicationTypeOptions = listOf("Medicine", "Supplements")
    val selectDayNameOptions = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday"
    )
    val selectFrequencyOptions = listOf("Daily", "Alternate Days", "Weekly")
    var selectedMedicationType by remember { mutableStateOf("Medicine") } // DUMMY: Prescription medicine
    var description by remember { mutableStateOf("Take one tablet after breakfast. Avoid dairy products 2 hours before or after. May cause mild stomach upset.") } // DUMMY: Real instructions
    var showDialog by remember { mutableStateOf(false) }
    var showDialog1 by remember { mutableStateOf(false) }
    var showDialogSuccessFully by remember { mutableStateOf(false) }
    val appointmentOptions = listOf(
        "Normal Check-up", "Dental Check-up", "Root Canal", "Brain Check-up",
        "Hair Check-up", "Skin Check-up", "Heart Check-up", "Lungs Check-up",
        "Liver Check-up", "Intestine Check-up", "Kidney Check-up", "Bones Check-up",
        "Feet Check-up", "Hand Check-up", "ENT Check-up"
    )

    // DUMMY: Two reminder times - morning and evening
    var currentReminderTime by remember {
        mutableStateOf(listOf("08:00:00", "20:00:00"))
    }

    var uploadedFiles by remember { mutableStateOf<Uri?>(null) }
    var checked by remember { mutableStateOf(true) } // DUMMY: Reminders enabled by default

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            uploadedFiles = uri
        }
    }

    // HH:MM:SS format function
    fun formatTimeInput(input: String): String {
        // Remove all non-digits
        val digits = input.filter { it.isDigit() }

        // Limit to 6 digits (HHMMSS)
        val limitedDigits = digits.take(6)

        return when (limitedDigits.length) {
            0 -> ""
            1 -> "0$limitedDigits:"  // H -> 0H:
            2 -> "$limitedDigits:"    // HH -> HH:
            3 -> "${limitedDigits.substring(0, 2)}:${limitedDigits[2]}"  // HH:M -> HH:M
            4 -> "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:"  // HH:MM -> HH:MM:
            5 -> "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:${limitedDigits[4]}"  // HH:MM:S -> HH:MM:S
            6 -> {
                // Format as HH:MM:SS
                "${limitedDigits.substring(0, 2)}:${limitedDigits.substring(2, 4)}:${limitedDigits.substring(4, 6)}"
            }
            else -> input
        }
    }

    fun getFileName(uri: Uri): String {
        return uri.lastPathSegment ?: "file"
    }
    val context = LocalContext.current
    fun validateFields(): Boolean {
        // Debug: Print current reminder times
        println("DEBUG: Current reminder times: $currentReminderTime")
        println("DEBUG: Valid reminder times: ${currentReminderTime.filter { it.trim().isNotBlank() }}")

        // Validate Member Selection
        if (selectedMyself == "Myself" || selectedMyself.isBlank()) {
            // "Myself" is the default and valid option
        } else if (!myselfOptions.contains(selectedMyself)) {
            Toast.makeText(context, "Please select a valid member", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Medication Type Selection
        if (selectedMedicationType == "Select Appointment Type" || selectedMedicationType.isBlank()) {
            Toast.makeText(context, "Please select a medication type", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Medication Name
        if (medicationName.isBlank()) {
            Toast.makeText(context, "Medication name is required", Toast.LENGTH_SHORT).show()
            return false
        }

        val trimmedMedicationName = medicationName.trim()
        if (trimmedMedicationName.length < 2) {
            Toast.makeText(context, "Medication name should be at least 2 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Dosage
        if (dosage.isBlank()) {
            Toast.makeText(context, "Dosage is required", Toast.LENGTH_SHORT).show()
            return false
        }

        val trimmedDosage = dosage.trim()
        if (trimmedDosage.length < 2) {
            Toast.makeText(context, "Please enter a valid dosage (e.g., 500mg)", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Frequency Selection
        if (selectFrequency == "Select Frequency" || selectFrequency.isBlank()) {
            Toast.makeText(context, "Please select frequency", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Day Selection if Weekly
        if (selectFrequency == "Weekly" && (selectDayName == "Select Frequency" || selectDayName.isBlank())) {
            Toast.makeText(context, "Please select a day for weekly schedule", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Reminder Times
        val validReminderTimes = currentReminderTime.filter { it.trim().isNotBlank() }
        if (validReminderTimes.isEmpty()) {
            Toast.makeText(context, "Please add at least one reminder time", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate each reminder time format - HH:MM:SS format
        for (reminderTime in validReminderTimes) {
            val trimmedTime = reminderTime.trim()
            println("DEBUG: Validating time: '$trimmedTime'")

            // HH:MM:SS format regex (24-hour format)
            val timeRegex = Regex("^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\$")

            if (!timeRegex.matches(trimmedTime)) {
                println("DEBUG: Time '$trimmedTime' does not match regex")
                Toast.makeText(context, "Please enter time in HH:MM:SS format (e.g., 08:30:00)", Toast.LENGTH_SHORT).show()
                return false
            }

            // Additional validation for proper time ranges
            if (trimmedTime.length == 8) {
                val parts = trimmedTime.split(":")
                if (parts.size == 3) {
                    val hour = parts[0].toIntOrNull() ?: 0
                    val minute = parts[1].toIntOrNull() ?: 0
                    val second = parts[2].toIntOrNull() ?: 0

                    if (hour !in 0..23) {
                        Toast.makeText(context, "Hour must be between 00 and 23", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    if (minute !in 0..59) {
                        Toast.makeText(context, "Minute must be between 00 and 59", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    if (second !in 0..59) {
                        Toast.makeText(context, "Second must be between 00 and 59", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }
        }

        // Validate Start Date
        if (startDate.isBlank()) {
            Toast.makeText(context, "Start date is required", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate Start Date format
        val dateRegex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
        if (!dateRegex.matches(startDate)) {
            Toast.makeText(context, "Please enter start date in MM-DD-YYYY format", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate End Date (Optional but if provided, validate format and that it's after start date)
        if (endDate.isNotBlank()) {
            if (!dateRegex.matches(endDate)) {
                Toast.makeText(context, "Please enter end date in MM-DD-YYYY format", Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate that end date is after start date
            try {
                val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                val startDateObj = dateFormat.parse(startDate)
                val endDateObj = dateFormat.parse(endDate)

                if (startDateObj != null && endDateObj != null && endDateObj.before(startDateObj)) {
                    Toast.makeText(context, "End date must be after start date", Toast.LENGTH_SHORT).show()
                    return false
                }
            } catch (e: Exception) {
                // Date parsing failed
            }
        }

        // Validate Notes/Description (Optional)
        if (description.isNotBlank()) {
            val trimmedDescription = description.trim()
            if (trimmedDescription.length < 5) {
                Toast.makeText(context, "Notes should be at least 5 characters if provided", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        // Validate File Upload (Optional)
      /*  if (uploadedFiles != null) {
            val fileName = uploadedFiles?.lastPathSegment ?: ""
            val allowedExtensions = listOf(".pdf", ".jpg", ".jpeg", ".png", ".dcm", ".dicom")
            val extension = fileName.substringAfterLast(".", "").lowercase()

            if (extension !in allowedExtensions &&
                !fileName.endsWith(".pdf", ignoreCase = true) &&
                !fileName.endsWith(".jpg", ignoreCase = true) &&
                !fileName.endsWith(".jpeg", ignoreCase = true) &&
                !fileName.endsWith(".png", ignoreCase = true) &&
                !fileName.endsWith(".dcm", ignoreCase = true) &&
                !fileName.endsWith(".dicom", ignoreCase = true)) {

                Toast.makeText(context, "Please upload only PDF, JPG, PNG, or DICOM files", Toast.LENGTH_SHORT).show()
                return false
            }
        }
*/
        // Validate that reminder checkbox is checked if reminder times are set
        if (validReminderTimes.isNotEmpty() && !checked) {
            Toast.makeText(context, "Please enable reminders for the set times", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFFFFF))
    ) {

        TopBarHeader1(
            title = stringResource(R.string.edit_medication_title),
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp, vertical = 30.dp)
        ) {

            Text(
                text = stringResource(R.string.for_family_member_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedMyself,
                onSelectionChanged = { reason ->
                    selectedMyself = reason
                },
                horizontalPadding = 24.dp,
                reasons = myselfOptions
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.medication_type_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectedMedicationType,
                onSelectionChanged = { reason ->
                    selectedMedicationType = reason
                },
                horizontalPadding = 24.dp,
                reasons = selectedMedicationTypeOptions
            )

            Row(Modifier.padding(vertical = 24.dp)) {
                ProfileInputSmallField(
                    label = stringResource(R.string.medication_name_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.medication_name_placeholder),
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(5.dp))

                ProfileInputSmallField(
                    label = stringResource(R.string.dosage_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.dosage_placeholder),
                    value = dosage,
                    onValueChange = { dosage = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.frequency_label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(8.dp))

            CustomPowerSpinner(
                selectedText = selectFrequency,
                onSelectionChanged = { reason ->
                    selectFrequency = reason
                },
                horizontalPadding = 24.dp,
                reasons = selectFrequencyOptions
            )

            if (selectFrequency == "Weekly") {
                Text(
                    text = stringResource(R.string.day_label),
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(8.dp))

                CustomPowerSpinner(
                    selectedText = selectDayName,
                    onSelectionChanged = { reason ->
                        selectDayName = reason
                    },
                    horizontalPadding = 24.dp,
                    reasons = selectDayNameOptions
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.reminder_time_label),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily(Font(R.font.urbanist_regular))
            )

            Spacer(modifier = Modifier.height(8.dp))

            currentReminderTime.forEachIndexed { index, reminderValue ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = reminderValue,
                        onValueChange = { raw ->

                            if (index != 0) return@OutlinedTextField

                            val old = reminderValue

                            // 🧨 Agar user ne backspace dabaya (koi bhi delete)
                            if (raw.length < old.length) {
                                val list = currentReminderTime.toMutableList()
                                list[index] = ""          // ⬅ full clear
                                currentReminderTime = list
                                return@OutlinedTextField
                            }

                            // Agar user ne sab delete kar diya
                            if (raw.isBlank()) {
                                val list = currentReminderTime.toMutableList()
                                list[index] = ""
                                currentReminderTime = list
                                return@OutlinedTextField
                            }

                            // Sirf digits lo
                            val digits = raw.filter { it.isDigit() }.take(6)

                            // HH:MM:SS format
                            val formatted = when (digits.length) {
                                0 -> ""
                                1 -> "0${digits}:"
                                2 -> "${digits}:"
                                3 -> "${digits.substring(0,2)}:${digits[2]}"
                                4 -> "${digits.substring(0,2)}:${digits.substring(2,4)}:"
                                5 -> "${digits.substring(0,2)}:${digits.substring(2,4)}:${digits[4]}"
                                else -> "${digits.substring(0,2)}:${digits.substring(2,4)}:${digits.substring(4,6)}"
                            }

                            val list = currentReminderTime.toMutableList()
                            list[index] = formatted
                            currentReminderTime = list
                        },

                        placeholder = {
                            Text("00:00:00", color = Color(0xFF697383),fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        },

                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                        ),

                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp)),

                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    /*OutlinedTextField(
                        value = reminderValue,
                        onValueChange = { newValue ->
                            if (index == 0) {
                                // पहले यह check करें कि क्या newValue, reminderValue का prefix है
                                // (यानी deletion end से हुई है)
                                val isDeletingFromEnd = reminderValue.startsWith(newValue) && newValue.length < reminderValue.length

                                if (isDeletingFromEnd) {
                                    // End से delete करना allow करें
                                    val formattedValue = formatTimeInput(newValue)
                                    val updated = currentReminderTime.toMutableList()
                                    updated[index] = formattedValue
                                    currentReminderTime = updated
                                } else if (newValue.length > reminderValue.length) {
                                    // नया text add हो रहा है
                                    val formattedValue = formatTimeInput(newValue)
                                    val updated = currentReminderTime.toMutableList()
                                    updated[index] = formattedValue
                                    currentReminderTime = updated
                                } else if (newValue.length == reminderValue.length) {
                                    // Text replace हो रहा है (जैसे कि middle में type करना)
                                    // इसे भी allow करें लेकिन cursor को end में रखें
                                    val formattedValue = formatTimeInput(newValue)
                                    val updated = currentReminderTime.toMutableList()
                                    updated[index] = formattedValue
                                    currentReminderTime = updated
                                }
                                // अगर middle से delete करने की कोशिश की (और end से नहीं), तो ignore करें
                            }
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        placeholder = {
                            Text("00:00:00", color = Color(0xFF697383))
                        },
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_appointed_gray_icon),
                                contentDescription = "clock",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        enabled = index == 0,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )*/
                   /* OutlinedTextField(
                        value = reminderValue,
                        onValueChange = { newValue ->
                            if (index == 0) {
                                // अगर deletion हो रही है (नया text छोटा है)
                                if (newValue.length < reminderValue.length) {
                                    // हमेशा अंत से ही delete करने दें
                                    // अगर user ने middle से delete करने की कोशिश की, तो ignore करें
                                    // और केवल अंत के character को ही remove करें
                                    val shouldDeleteFromEnd = reminderValue.dropLast(1)
                                    val formattedValue = formatTimeInput(shouldDeleteFromEnd)
                                    val updated = currentReminderTime.toMutableList()
                                    updated[index] = formattedValue
                                    currentReminderTime = updated
                                } else {
                                    // Normal case - insertion या कोई change नहीं
                                    val formattedValue = formatTimeInput(newValue)
                                    val updated = currentReminderTime.toMutableList()
                                    updated[index] = formattedValue
                                    currentReminderTime = updated
                                }
                            }
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        placeholder = {
                            Text("00:00:00", color = Color(0xFF697383))
                        },
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_appointed_gray_icon),
                                contentDescription = "clock",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        enabled = index == 0,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )*/
                    /*OutlinedTextField(
                        value = reminderValue,
                        onValueChange = { newValue ->
                            if (index == 0) {
                                val formattedValue = formatTimeInput(newValue)
                                val updated = currentReminderTime.toMutableList()
                                updated[index] = formattedValue
                                currentReminderTime = updated
                            }
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight.Normal
                        ),
                        placeholder = {
                           // Text(stringResource(R.string.time_format_placeholder))
                            Text("00:00:00", color = Color(0xFF697383))
                        },
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_appointed_gray_icon),
                                contentDescription = "clock",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        enabled = index == 0,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, Color(0xFF697383), RoundedCornerShape(30.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )*/

                    if (index == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add_icon),
                            contentDescription = "Add",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (currentReminderTime[0].isNotBlank()) {
                                        val updated = currentReminderTime.toMutableList()
                                        updated.add(updated[0])
                                        updated[0] = ""
                                        currentReminderTime = updated
                                    }
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_remove_icon),
                            contentDescription = "Remove",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val updatedList = currentReminderTime.toMutableList()
                                    updatedList.removeAt(index)
                                    currentReminderTime = updatedList
                                }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(modifier = Modifier.padding( vertical = 24.dp)) {
                UniversalInputField(
                    wholePadding = 0.dp,
                    textStartPadding = 0.dp,
                    title = stringResource(R.string.start_date_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder),
                    value = startDate,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon,
                ) {
                    showDialog = true
                }
                Spacer(Modifier.width(5.dp))
                UniversalInputField(
                    wholePadding = 0.dp,
                    textStartPadding = 0.dp,
                    title = stringResource(R.string.end_date_optional_label),
                    isImportant = false,
                    placeholder = stringResource(R.string.date_format_placeholder),
                    value = endDate,
                    modifier = Modifier.weight(1f),
                    rightIcon = R.drawable.ic_calender_icon
                ) {
                    showDialog1 = true
                }
            }

            ProfilePhotoPicker(
                label = stringResource(R.string.upload_prescription_optional_label),
                fileName = uploadedFiles?.let { getFileName(it) } ?: stringResource(R.string.no_file_chosen),
                onChooseClick = {
                    filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "application/dicom"))
                }
            )

            Text(
                stringResource(R.string.file_formats_supported),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "application/dicom"))
                    }
            )

            Spacer(Modifier.height(24.dp))

            ProfileInputMultipleLineField2(
                label = stringResource(R.string.notes_label),
                isImportant = false,
                placeholder = stringResource(R.string.notes_placeholder),
                value = description,
                onValueChange = { description = it },
                heightOfEditText = 135.dp,
                paddingHorizontal = 0.dp,
                borderColor = Color(0xFF697383),
                textColor = Color(0xFF697383),
                textStartPadding = 0.dp
            )

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { checked = !checked },
                verticalAlignment = Alignment.Top
            ) {
                RoundedCustomCheckbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.enable_reminder_label),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CancelButton(title = stringResource(R.string.cancel_button)) {
                    navController.popBackStack()
                }

                ContinueButton(text = stringResource(R.string.update_medication_button)) {
                    // In real app, this would save to database
                    if (validateFields()) {
                        showDialogSuccessFully = true
                    }
                }
            }

            Spacer(Modifier.height(37.dp))
        }
    }

    if (showDialog) {
        CalendarDialog(
            onDismiss = { showDialog = false },
            onDateApplied = {
                showDialog = false
                startDate = it.toString()
            }
        )
    }

    if (showDialog1) {
        CalendarDialog(
            onDismiss = { showDialog1 = false },
            onDateApplied = {
                showDialog1 = false
                endDate = it.toString()
            }
        )
    }

    if (showDialogSuccessFully) {
        SuccessfulDialog(
            title = stringResource(R.string.medication_update_success_title),
            description = stringResource(R.string.medication_update_success_description),
            onDismiss = {
                showDialogSuccessFully = false
                navController.popBackStack()
            },
            onOkClick = {
                showDialogSuccessFully = false
                navController.popBackStack()
            }
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EditMedicationScreenPreview() {
    val navController = rememberNavController()
    EditMedicationScreen(navController = navController)
}
