package com.bussiness.curemegptapp.ui.screen.main.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.screen.main.schedule.EditDeleteMenu
import com.bussiness.curemegptapp.ui.theme.gradientBrush

@Composable
fun MenuDrawer(onDismiss: () -> Unit, selectedUser: String, onUserChange: (String) -> Unit,onClickNewCaseChat:()-> Unit) {
    var showChatHistory by remember { mutableStateOf(false) }
    var showCaseHistory by remember { mutableStateOf(false) }
    var selectedChatIndex by remember { mutableStateOf<Int?>(null) }



    // Define case history for each user
    val userCaseHistoryMap = mapOf(
        stringResource(R.string.james_myself_user) to listOf(
            stringResource(R.string.toothache_treatment_case),
            stringResource(R.string.annual_checkup_case),
            stringResource(R.string.headache_consultation_case),
            stringResource(R.string.stomach_issue_case),
            stringResource(R.string.dental_cleaning_case),
            stringResource(R.string.general_health_review_case)
        ),
        stringResource(R.string.rose_logan_spouse_user) to listOf(
            stringResource(R.string.pregnancy_checkup_case),
            stringResource(R.string.migraine_treatment_case),
            stringResource(R.string.postpartum_recovery_case),
            stringResource(R.string.skin_consultation_case),
            stringResource(R.string.sleep_disorder_case),
            stringResource(R.string.stress_management_case)
        ),
        stringResource(R.string.peter_logan_son_user) to listOf(
            stringResource(R.string.childhood_allergy_test_case),
            stringResource(R.string.growth_monitoring_case),
            stringResource(R.string.immunization_update_case),
            stringResource(R.string.asthma_management_case),
            stringResource(R.string.pediatric_checkup_case),
            stringResource(R.string.nutrition_counseling_case)
        )
    )

    // Get case history based on selected user
    val caseHistory = userCaseHistoryMap[selectedUser] ?: listOf(stringResource(R.string.no_case_history_available))

    var searchQuery by remember { mutableStateOf("") }
    var showUserDropdown1 by remember { mutableStateOf(false) }
    val users = listOf(
        stringResource(R.string.james_myself_user),
        stringResource(R.string.rose_logan_spouse_user),
        stringResource(R.string.peter_logan_son_user)
    )
    Box(
        modifier = Modifier
            .fillMaxSize().clip(shape = RoundedCornerShape(topStart = 40.dp,bottomStart = 40.dp))
            .background(Color.White)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight().padding(vertical = 10.dp)
                .width(340.dp)
                .align(Alignment.CenterEnd)
                .clickable(enabled = false, interactionSource = remember { MutableInteractionSource() },
                    indication = null) { },
            color = Color.White,
            shape = RoundedCornerShape(topStart = 40.dp,bottomStart = 40.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = stringResource(R.string.logo_icon_description),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(30.dp) // Adjust size according to your logo
                            )
                        }

                    }


                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFF4F4F4),
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


                    Spacer(modifier = Modifier.height(16.dp))



                    GradientRedButton(
                        text = stringResource(R.string.new_chat_button),
                        icon = R.drawable.ic_add_chat_icon,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        height = 56.dp,
                        fontSize = 16.sp,
                        imageSize = 24.dp,
                        gradientColors = listOf(
                            Color(0xFF4338CA),
                            Color(0xFF211C64)
                        ),
                        onClick = { /* Your action */ }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientRedButton(
                        text = stringResource(R.string.new_case_chat_button),
                        icon = R.drawable.page_img,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        height = 56.dp,
                        fontSize = 16.sp,
                        imageSize = 24.dp,
                        gradientColors = listOf(
                            Color(0xFF4338CA),
                            Color(0xFF211C64)
                        ),
                        onClick = { onClickNewCaseChat()  }
                    )

                    Spacer(modifier = Modifier.height(12.dp))



                    Spacer(modifier = Modifier.height(24.dp))

                    // Chat History Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null) { showChatHistory = !showChatHistory }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.chat_history_title), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(if (showChatHistory) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon),
                            contentDescription = null
                        )
                    }

                    if (showChatHistory) {
                        // User Selector in Chat History

                        Box(

                            modifier = Modifier
                                .wrapContentHeight().fillMaxWidth()
                                .padding(horizontal = 16.dp).clip(RoundedCornerShape(24.dp)).background(gradientBrush)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null){

                                }.padding(start = 10.dp),

                            ) {
                            Text(stringResource(R.string.case_chat_history_title),color = Color.White, fontWeight = FontWeight.Medium)
                        }

                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null){
                                showUserDropdown1 = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, Color(0xFFE7E9EC)),
                        color = Color.Unspecified
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedUser, color = Color(0xFF4338CA), fontWeight = FontWeight.Medium)
                            Image(painter = painterResource( if(showUserDropdown1) R.drawable.ic_dropdown_show else R.drawable.ic_dropdown_icon), contentDescription = null)
                        }
                    }

                    if (showUserDropdown1) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth().padding(horizontal = 5.dp)
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                users.forEachIndexed { index, user ->

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {

                                                onUserChange(user)
                                                showUserDropdown1 = false
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // User name with proper styling
                                            Text(
                                                text = user,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                fontWeight = if (user == selectedUser) FontWeight.Medium else FontWeight.Normal,
                                                color = if (user == selectedUser) Color(0xFF4338CA) else Color(0xFF374151)
                                            )

                                            // Tick icon only for selected user
                                            if (user == selectedUser) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_tick_icon),
                                                    contentDescription = stringResource(R.string.selected_icon_description),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }



                }



                // Chat History Items
                if (showChatHistory) {
                    item {

                        Spacer(modifier = Modifier.height(16.dp))

                        // Case Chat History Button


                        if (showCaseHistory) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // User Selector in Case History
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedUser, color = Color(0xFF5B47DB), fontWeight = FontWeight.Medium)
                                    Icon(painter = painterResource( R.drawable.ic_show_drop_down_icon), contentDescription = null, tint = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                }




                items(caseHistory.size) { index ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(30.dp),
                        color = Color(0xFFF5F0FF)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null) { }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource( R.drawable.ic_clock_new_icon),
                                    contentDescription = stringResource(R.string.clock_icon_description),
                                    modifier = Modifier.size(31.dp)
                                )
                                Text(caseHistory[index], fontSize = 14.sp)
                            }
                       /*     IconButton(
                                onClick = { },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    painter = painterResource( R.drawable.ic_horizontal_menu_icon),
                                    contentDescription = stringResource(R.string.menu_icon_description),
                                    tint = Color.Unspecified,

                                    )
                            }*/
                            RenameDeleteShareMenu(
                                modifier = Modifier,
                                onEditClick = { /*onEditClick()*/ },
                                onShareClick = { /*onEditClick()*/ },
                                onDeleteClick = {  /*onDeleteClick()*/ })
                        }
                    }
                }


                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}




