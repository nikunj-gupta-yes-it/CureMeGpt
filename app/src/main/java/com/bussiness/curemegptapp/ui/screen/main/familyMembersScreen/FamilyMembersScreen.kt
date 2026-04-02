package com.bussiness.curemegptapp.ui.screen.main.familyMembersScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMemberUi
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.dialog.AlertCardDialog
import com.bussiness.curemegptapp.ui.sheet.BottomSheetDialog
import com.bussiness.curemegptapp.ui.sheet.FilterFamilyMembersTypeSheet
import com.bussiness.curemegptapp.viewmodel.FamilyMember.FamilyMemberProfileViewModel

data class FamilyMember(
    val name: String,
    val age: Int,
    val relationship: String,
    val appointments: Int,
    val medications: String,
    val imageRes: Int? = null
)

@Composable
fun FamilyMembersScreen(navController: NavHostController,
    viewModel: FamilyMemberProfileViewModel = hiltViewModel()
    ) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchFamilyMembers()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val members by viewModel.filteredMembers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
  var selectedMemberId by remember { mutableStateOf<Int>(0) }
    val filterOptions = listOf(
        "All Members", "Children", "Adults", "Seniors", "Friends", "Relatives"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 🔥 Loader
        if (isLoading) {
        //    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

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

            item { Spacer(Modifier.height(15.dp)) }

            // Family Profiles Card
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(R.string.family_profiles_title),
                                fontSize = 24.sp
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = stringResource(
                                    R.string.members_total_format,
                                    uiState.totalFamilyMembers
                                ),
                                fontSize = 15.sp,
                                color = Color(0xFF909090)
                            )
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_family_person_add_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(67.dp)
                            .clickable {
                                navController.navigate("addFamilyMember?from=main")
                            }
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icons = R.drawable.ic_calender_health_icon,
                        number = uiState.totalFamilyAppointmentCount.toString(),
                        title = stringResource(R.string.appointments_title),
                        description = stringResource(R.string.appointments_description),
                        backgroundColor = Color(0xFFD9D7F4),
                        numberColor = Color(0xFF4338CA)
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icons = R.drawable.ic_medication_icon_2,
                        number = uiState.totalFamilyMedicationCount.toString(),
                        title = stringResource(R.string.medications_title),
                        description = stringResource(R.string.medications_description),
                        backgroundColor = Color(0xFFD1F2EB),
                        numberColor = Color(0xFF1ABC9C)
                    )
                }
            }

            item { Spacer(Modifier.height(15.dp)) }

            // 🔍 Search + Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            onValueChange = { viewModel.updateSearch(it) }, // ✅ VM call
                            singleLine = true,
                            maxLines = 1,
                            placeholder = {
                                Text(stringResource(R.string.search_placeholder))
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = null
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable { showSheet = true }
                    )
                }
            }

            // ❌ Empty State
            if (members.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No family members found")
                    }
                }
            }

            // ✅ Members List
            else {
                item {
                    Text(
                        text = "Family Members",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                items(members) { member ->
                    FamilyMemberCard(
                        member = member,
                        onViewProfileClick = {
                            val id = member.id

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("id", member.id)
                            navController.navigate(AppDestination.FamilyPersonProfile)
                        },
                        onDeleteProfileClick = {
                            selectedMemberId = member.id
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // 🔽 Filter Bottom Sheet
    if (showSheet) {
        BottomSheetDialog(onDismissRequest = { showSheet = false }) {
            FilterFamilyMembersTypeSheet(
                members = filterOptions,
                selectedMember = selectedFilter,
                onMemberSelected = {
                    viewModel.updateFilter(it) // ✅ VM call
                    showSheet = false
                }
            )
        }
    }

    // ⚠️ Delete Dialog
    if (showDeleteDialog) {
        AlertCardDialog(
            icon = R.drawable.ic_delete_icon_new,
            title = "Delete Member",
            message = "Are you sure you want to delete this member?",
            confirmText = "Delete",
            cancelText = "Cancel",
            onDismiss = { showDeleteDialog = false },
            onConfirm = { showDeleteDialog = false
                viewModel.deleteProfile(selectedMemberId,{

                },{
                    msg->
                    Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
                })
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
    member: FamilyMemberUi,
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
//            Image(
//                painter = painterResource(id = member.imageRes ?: R.drawable.ic_profile_image),
//                contentDescription = stringResource(R.string.family_member_photo_description, member.name),
//                modifier = Modifier
//                    .size(70.dp, 84.dp)
//                    .clip(RoundedCornerShape(20.dp)),
//                contentScale = ContentScale.Crop
//            )
            AsyncImage(
                model = member.imageUrl,
                contentDescription = stringResource(
                    R.string.family_member_photo_description,
                    member.name
                ),
                placeholder = painterResource(R.drawable.user_not_found),
                error = painterResource(R.drawable.user_not_found),
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
                                    text = stringResource(R.string.age_format, member.age?:0),
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
