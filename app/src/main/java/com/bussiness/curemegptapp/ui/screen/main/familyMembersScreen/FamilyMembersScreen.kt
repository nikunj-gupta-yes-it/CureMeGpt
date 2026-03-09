package com.bussiness.curemegptapp.ui.screen.main.familyMembersScreen

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialogProperties
import com.bussiness.curemegptapp.ui.sheet.FilterFamilyMembersTypeSheet

data class FamilyMember(
    val name: String,
    val age: Int,
    val relationship: String,
    val appointments: Int,
    val medications: String,
    val imageRes: Int? = null
)

@Composable
fun FamilyMembersScreen(navController: NavHostController) {
    // Original list of all members
    val allMembers = remember {
        listOf(
            FamilyMember("James Logan", 40, "Self", 2, "2/2", R.drawable.ic_profile_image),
            FamilyMember("Rose Logan", 30, "Spouse", 2, "2/4", R.drawable.ic_profile_image),
            FamilyMember("Peter Logan", 17, "Son", 2, "2/3", null),
            FamilyMember("Sarah Logan", 65, "Mother", 1, "1/2", null),
            FamilyMember("John Logan", 70, "Father", 1, "1/1", null)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Members") }

    // Filter options
    val filterOptions = remember {
        listOf(
            "All Members",
            "Children",
            "Adults",
            "Seniors",
            "Friends",
            "Relatives"
        )
    }


    // Filtered members based on search query and selected filter
    val filteredMembers = remember(allMembers, searchQuery, selectedFilter) {
        allMembers.filter { member ->
            // Search filter
            val matchesSearch = searchQuery.isEmpty() ||
                    member.name.contains(searchQuery, ignoreCase = true) ||
                    member.relationship.contains(searchQuery, ignoreCase = true)

            // Category filter
            val matchesCategory = when (selectedFilter) {
                "All Members" -> true
                "Children" -> member.age < 18
                "Adults" -> member.age in 18..64
                "Seniors" -> member.age >= 65
                "Friends" -> member.relationship == "Friend"
                "Relatives" -> member.relationship in listOf("Son", "Daughter", "Mother", "Father", "Spouse", "Brother", "Sister")
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .statusBarsPadding()
//            .padding(horizontal = 20.dp)
//    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Header
            item {
                Text(
                    text = stringResource(R.string.family_members_title),
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            // Family Profiles Card
            item {
                Spacer(Modifier.height(15.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_family_person_blue_icons),
                            contentDescription = stringResource(R.string.family_profiles_icon_description),
                            modifier = Modifier
                                .height(63.dp)
                                .width(48.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.family_profiles_title),
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = stringResource(
                                    R.string.members_total_format,
                                    allMembers.size
                                ),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF909090)
                            )
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_family_person_add_icon),
                        contentDescription = stringResource(R.string.add_member_description),
                        modifier = Modifier.size(67.dp).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate("addFamilyMember?from=main")
                        }
                    )
                }
            }
            item {
                Spacer(Modifier.height(20.dp))
            }
            item {
                // Stats Cards Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icons = R.drawable.ic_calender_health_icon,
                        number = "02",
                        title = stringResource(R.string.appointments_title),
                        description = stringResource(R.string.appointments_description),
                        backgroundColor = Color(0xFFD9D7F4),
                        numberColor = Color(0xFF4338CA)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icons = R.drawable.ic_medication_icon_2,
                        number = "04",
                        title = stringResource(R.string.medications_title),
                        description = stringResource(R.string.medications_description),
                        backgroundColor = Color(0xFFD1F2EB),
                        numberColor = Color(0xFF1ABC9C)
                    )
                }
            }
            item {
                Spacer(Modifier.height(15.dp))
            }

            // Search and Filter Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Search Field
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFF4F4F4)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            // 🔥 single line fix
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
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = stringResource(R.string.search_icon_description),
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

                    // Filter Button with indicator
                    Box {
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

//                // Show indicator if filter is applied (not "All Members")
//                if (selectedFilter != stringResource(R.string.all_members_filter)) {
//                    Box(
//                        modifier = Modifier
//                            .size(8.dp)
//                            .clip(CircleShape)
//                            .background(Color.Red)
//                            .align(Alignment.TopEnd)
//                    )
//                }
                    }
                }
            }

            // Family Members List
         /*   item {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 30.dp)
                ) {
                    if (filteredMembers.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = stringResource(R.string.no_results_icon_description),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.no_family_members_found),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    color = Color(0xFF909090)
                                )
                                Text(
                                    text = stringResource(R.string.try_different_search_filter),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color(0xFF909090)
                                )
                            }
                        }
                    } else {
                        items(filteredMembers) { member ->
                            FamilyMemberCard(
                                member,
                                onViewProfileClick = {
                                    navController.navigate(AppDestination.FamilyPersonProfile)
                                },
                                onDeleteProfileClick = {
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }*/
            if (filteredMembers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search_icon),
                            contentDescription = stringResource(R.string.no_results_icon_description),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_family_members_found),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color(0xFF909090)
                        )
                        Text(
                            text = stringResource(R.string.try_different_search_filter),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color(0xFF909090)
                        )
                    }
                }
            } else {
                // Family Members Title
                item {
                    Text(
                        text = "Family Members",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Family Members Cards
                items(filteredMembers) { member ->
                    FamilyMemberCard(
                        member,
                        onViewProfileClick = {
                            navController.navigate(AppDestination.FamilyPersonProfile)
                        },
                        onDeleteProfileClick = {
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

        }
    }
    // Filter Bottom Sheet
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
            FilterFamilyMembersTypeSheet(
                members = filterOptions,
                selectedMember = selectedFilter,
                onMemberSelected = { newFilter ->
                    selectedFilter = newFilter
                    showSheet = false
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = stringResource(R.string.delete_member_dialog_title),
            message = stringResource(R.string.delete_member_dialog_message, stringResource(R.string.peter_logan_son_user).split(" (")[0]),
            confirmText = stringResource(R.string.delete_button),
            cancelText = stringResource(R.string.cancel_button),
            onDismiss = { showDeleteDialog = false },
            onConfirm = { showDeleteDialog = false }
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icons: Int = R.drawable.ic_calender_health_icon,
    number: String,
    title: String,
    description: String,
    backgroundColor: Color,
    numberColor: Color
) {
    Card(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = icons),
                    contentDescription = stringResource(R.string.stat_icon_description),
                    modifier = Modifier.size(44.dp)
                )

                Text(
                    text = number,
                    fontSize = 56.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    fontWeight = FontWeight.SemiBold,
                    color = numberColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Text(
                text = description,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_italic)),
                color = Color(0xFF181818),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
    onViewProfileClick: () -> Unit,
    onDeleteProfileClick: () -> Unit
) {
    var checkedState by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = member.imageRes ?: R.drawable.ic_profile_image),
                contentDescription = stringResource(R.string.family_member_photo_description, member.name),
                modifier = Modifier
                    .size(70.dp, 84.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Member Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Row {
                            Text(
                                text = member.name,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.widthIn(max = 130.dp)
                            )
                            Spacer(Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(30.dp),
                                color = Color(0xFFCAC7F0),
                            ) {
                                Text(
                                    text = stringResource(R.string.age_format, member.age),
                                    fontSize = 10.sp,
                                    color = Color(0xFF4338CA),
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 9.dp)
                                )
                            }
                        }
                        Text(
                            text = member.relationship,
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    FamilyContentMenu(
                        modifier = Modifier,
                        checked = checkedState,
                        onCheckedChange = { checkedState = it },
                        onViewProfileClick = { onViewProfileClick() },
                        onDeleteClick = { onDeleteProfileClick() }
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calender_health_icon),
                            contentDescription = stringResource(R.string.appointments_icon_description),
                            modifier = Modifier.size(29.dp),
                        )

                        Text(
                            text = member.appointments.toString(),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_medication_icon_2),
                            contentDescription = stringResource(R.string.medications_icon_description),
                            modifier = Modifier.size(29.dp),
                        )
                        Text(
                            text = member.medications,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FamilyMembersScreenPreview() {
    val navController = rememberNavController()
    FamilyMembersScreen(navController = navController)
}

/*
@Composable
fun FamilyMembersScreen(navController: NavHostController) {
    // Original list of all members
    val allMembers = remember {
        listOf(
            FamilyMember("James Logan", 40, "Self", 2, "2/2", R.drawable.ic_profile_image),
            FamilyMember("Rose Logan", 30, "Spouse", 2, "2/4", R.drawable.ic_profile_image),
            FamilyMember("Peter Logan", 17, "Son", 2, "2/3", null),
            FamilyMember("Sarah Logan", 65, "Mother", 1, "1/2", null),
            FamilyMember("John Logan", 70, "Father", 1, "1/1", null)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Members") }

    // Filter options
    val filterOptions = remember {
        listOf(
            "All Members",
            "Children",
            "Adults",
            "Seniors",
            "Friends",
            "Relatives"
        )
    }

    // Filtered members based on search query and selected filter
    val filteredMembers = remember(allMembers, searchQuery, selectedFilter) {
        allMembers.filter { member ->
            // Search filter
            val matchesSearch = searchQuery.isEmpty() ||
                    member.name.contains(searchQuery, ignoreCase = true) ||
                    member.relationship.contains(searchQuery, ignoreCase = true)

            // Category filter
            val matchesCategory = when (selectedFilter) {
                "All Members" -> true
                "Children" -> member.age < 18
                "Adults" -> member.age in 18..64
                "Seniors" -> member.age >= 65
                "Friends" -> member.relationship == "Friend"
                "Relatives" -> member.relationship in listOf("Son", "Daughter", "Mother", "Father", "Spouse", "Brother", "Sister")
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // Header
        Text(
            text = "Family Members",
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        // Family Profiles Card
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_family_person_blue_icons),
                    contentDescription = null,
                    modifier = Modifier
                        .height(63.dp)
                        .width(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Family Profiles",
                        fontSize = 26.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${allMembers.size} Members Total",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF909090)
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_family_person_add_icon),
                contentDescription = "Add member",
                modifier = Modifier.size(67.dp).clickable( interactionSource = remember { MutableInteractionSource() },
                    indication = null){
                   // navController.navigate(AppDestination.AddFamilyMemberScreen)
                    navController.navigate("addFamilyMember?from=main")
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        // Stats Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icons = R.drawable.ic_calender_health_icon,
                number = "02",
                title = "Appointments",
                description = "Track and manage your doctor visits.",
                backgroundColor = Color(0xFFD9D7F4),
                numberColor = Color(0xFF4338CA)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icons = R.drawable.ic_medication_icon_2,
                number = "04",
                title = "Medications",
                description = "Stay on top of your prescriptions.",
                backgroundColor = Color(0xFFD1F2EB),
                numberColor = Color(0xFF1ABC9C)
            )
        }

        Spacer(Modifier.height(15.dp))

        // Search and Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Search Field
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFFF4F4F4)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search",
                            color = Color(0xFFBCBCBC),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search_icon),
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp)
                        )
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

            // Filter Button with indicator
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_filter_icon),
                    contentDescription = "Filter",
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable(  interactionSource = remember { MutableInteractionSource() },
                            indication = null){
                            showSheet = true
                        }

                )

                // Show indicator if filter is applied (not "All Members")
                if (selectedFilter != "All Members") {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        // Show filter applied info
//        if (selectedFilter != "All Members") {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Filter: $selectedFilter",
//                    fontSize = 14.sp,
//                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
//                    color = Color(0xFF4338CA)
//                )
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Text(
//                    text = "${filteredMembers.size} members",
//                    fontSize = 12.sp,
//                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                    color = Color(0xFF909090)
//                )
//            }
//        }

        // Family Members List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 30.dp)
        ) {
            if (filteredMembers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search_icon),
                            contentDescription = "No results",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No family members found",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color(0xFF909090)
                        )
                        Text(
                            text = "Try a different search or filter",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color(0xFF909090)
                        )
                    }
                }
            } else {
                items(filteredMembers) { member ->
                    FamilyMemberCard(member, onViewProfileClick = {
                        navController.navigate(AppDestination.FamilyPersonProfile)
                    },
                        onDeleteProfileClick = {
                            showDeleteDialog = true
                        })
                }
            }
        }
    }

    // Filter Bottom Sheet
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
            FilterFamilyMembersTypeSheet(
                members = filterOptions,
                selectedMember = selectedFilter,
                onMemberSelected = { newFilter ->
                    selectedFilter = newFilter
                    showSheet = false
                }
            )
        }
    }
    if (showDeleteDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = "Delete Member?",
            message = "Are you sure you want to delete Peter’s profile? This action cannot be undone.",
            confirmText = "Delete",
            cancelText = "Cancel",
            onDismiss = { showDeleteDialog = false},
            onConfirm = {  showDeleteDialog = false
            }
        )

    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icons: Int = R.drawable.ic_calender_health_icon,
    number: String,
    title: String,
    description: String,
    backgroundColor: Color,
    numberColor: Color
) {
    Card(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Image(
                    painter = painterResource(id = icons),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )

                Text(
                    text = number,
                    fontSize = 56.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    fontWeight = FontWeight.SemiBold,
                    color = numberColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Text(
                text = description,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_italic)),
                color = Color(0xFF181818),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun FamilyMemberCard(member: FamilyMember,
                     onViewProfileClick: () -> Unit,
                     onDeleteProfileClick: () -> Unit
) {
    var checkedState by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = member.imageRes?:R.drawable.ic_profile_image),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp, 84.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Member Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Column(Modifier.weight(1f)) {
                        Row {

                            Text(
                                text = member.name,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.widthIn(max = 130.dp)
                            )
                            Spacer(Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(30.dp),
                                color = Color(0xFFCAC7F0),
                            ) {
                                Text(
                                    text = "${member.age} yrs",
                                    fontSize = 10.sp,
                                    color = Color(0xFF211C64),
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 9.dp)
                                )
                            }
                        }
                        Text(
                            text = member.relationship,
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    FamilyContentMenu(
                        modifier = Modifier,
                        checked = checkedState,
                        onCheckedChange = { checkedState = it },
                        onViewProfileClick = { onViewProfileClick()},
                        onDeleteClick = { onDeleteProfileClick()})
                }

                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calender_health_icon),
                            contentDescription = null,
                            modifier = Modifier.size(29.dp),
                        )

                        Text(
                            text = member.appointments.toString(),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_medication_icon_2),
                            contentDescription = null,
                            modifier = Modifier.size(29.dp),
                        )
                        Text(
                            text = member.medications,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun FamilyMembersScreenPreview() {
    val navController = rememberNavController()
    FamilyMembersScreen(navController = navController)
}*/
