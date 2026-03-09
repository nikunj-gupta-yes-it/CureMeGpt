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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.BottomMessageBar1
import com.bussiness.curemegptapp.ui.component.GradientRedButton
import com.bussiness.curemegptapp.ui.component.input.AIChatHeader
import com.bussiness.curemegptapp.ui.component.input.RightSideDrawer
import com.bussiness.curemegptapp.ui.dialog.CaseDialog
import com.bussiness.curemegptapp.ui.theme.gradientBrush
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding


@Composable
fun OpenChatScreen(navController: NavHostController,from: String ?= "",) {
    var showMenu by remember { mutableStateOf(false) }
   // val uiState by viewModel.uiState.collectAsState()
    var selectedUser by remember { mutableStateOf("James (Myself)") }
    var showUserDropdown by remember { mutableStateOf(false) }
    var showCaseDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val users = listOf(
        "James (Myself)",
        "Rose Logan (Spouse)",
        "Peter Logan (Son)"
    )
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatInputState by chatViewModel.uiState.collectAsState()
    val messages by chatViewModel.messages.collectAsState() // ये add करें
    // Navigation trigger state
    var shouldNavigate by remember { mutableStateOf(false) }

    BackHandler {
        if (from=="auth") {
            navController.navigate(AppDestination.MainScreen) {
                popUpTo(0)
                launchSingleTop = true
            }
        }else{
            navController.navigate(AppDestination.Home) {
                popUpTo(0)
                launchSingleTop = true
            }
        }

    }

    // Track when a new message is sent
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && shouldNavigate) {
            // Navigate to chat detail screen
            navController.navigate(AppDestination.ChatDataScreen) {
                popUpTo("openChatScreen") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            shouldNavigate = false
        }
    }



    // Define questions for each user
    val userQuestionsMap = mapOf(
        "James (Myself)" to Pair(
            // Suggested Questions for James
            listOf(
                "Why do my teeth hurt when I drink something cold?",
                "What's the best way to treat bleeding gums at home?",
                "Do I need to remove my wisdom tooth if it hurts?",
                "Why do I keep getting frequent headaches?",
                "How can I tell if my stomach pain is serious?",
                "What should I do if I feel dizzy often?"
            ),
            // Fitness Questions for James
            listOf(
                "Get fit question 1?",
                "Get fit question 2?",
                "Get fit question 3?"
            )
        ),
        "Rose Logan (Spouse)" to Pair(
            // Suggested Questions for Rose
            listOf(
                "What are common symptoms of migraines in women?",
                "How to manage back pain during pregnancy?",
                "Best exercises for postpartum recovery?",
                "Diet tips for improving skin health?",
                "How to improve sleep quality naturally?",
                "Managing stress and anxiety effectively?"
            ),
            // Fitness Questions for Rose
            listOf(
                "Postpartum fitness routine?",
                "Yoga for stress relief?",
                "Healthy meal planning?"
            )
        ),
        "Peter Logan (Son)" to Pair(
            // Suggested Questions for Peter
            listOf(
                "Common childhood allergies and symptoms?",
                "Best nutrition for growing children?",
                "How to boost immune system naturally?",
                "Managing childhood asthma?",
                "Healthy screen time limits for kids?",
                "When to consult doctor for fever in children?"
            ),
            // Fitness Questions for Peter
            listOf(
                "Fun exercises for kids?",
                "Building healthy habits early?",
                "Outdoor activities for children?"
            )
        )
    )

    // Get questions based on selected user
    val (suggestedQuestions, fitnessQuestions) = userQuestionsMap[selectedUser] ?: Pair(
        listOf("No questions available"),
        listOf("No fitness questions available")
    )


    var showDrawer by remember { mutableStateOf(false) }


    RightSideDrawer(
        drawerState = showDrawer,
        onClose = { showDrawer = false },
        drawerWidth = 320.dp,
        drawerContent = {
            MenuDrawer(
                onDismiss = { showDrawer = false },
                selectedUser = selectedUser,
                onUserChange = {
                    selectedUser = it
                    showDrawer = false
                },
                onClickNewCaseChat = {
                    showCaseDialog = true
                }
            )
        }
    ) {

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().statusBarsPadding()
                        .padding(horizontal = 20.dp)
                ) {


                    AIChatHeader(
                        logoRes = R.drawable.ic_logo,
                        sideArrow = R.drawable.left_ic,
                        filterIcon = R.drawable.filter_ic,
                        onLeftIconClick = {      if (from=="auth") {
                            navController.navigate(AppDestination.MainScreen) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }else{
                            navController.navigate(AppDestination.Home) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                        },
                        onFilterClick = { showDrawer = true },
                    )

                    Column(
                        modifier = Modifier.fillMaxSize().imePadding()
                    ) {



                    // Main Content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))

                            // Logo
                            Image(
                                painter = painterResource(R.drawable.main_ic),
                                contentDescription = null,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Greeting
                            Row {
                                Text(
                                    text = "Good afternoon, ",
                                    fontSize = 21.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "James",
                                    fontSize = 21.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4338CA)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // User Selector
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth().padding(horizontal = 10.dp)
                                    .clickable(interactionSource = remember { MutableInteractionSource() },
                                        indication = null) { showUserDropdown = !showUserDropdown },
                                shape = RoundedCornerShape(30.dp),
                                color = Color(0xFFF0EDFF),

                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_chat_circle_person_icon),
                                            contentDescription = null,
                                            modifier = Modifier.wrapContentSize()
                                        )
                                        Text("Ask for:", fontWeight = FontWeight.Medium, fontSize = 14.sp,)

                                    }

                                    Text(
                                        selectedUser,
                                        color = Color(0xFF5B47DB),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Image(

                                        painter = painterResource(
                                            if (showUserDropdown) R.drawable.ic_dropdown_show
                                            else R.drawable.ic_dropdown_icon
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                }
                            }


                            if (showUserDropdown) {
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

                                                        selectedUser = user
                                                        showUserDropdown = false
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
                                                            contentDescription = "Selected",
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            GradientRedButton(
                                text = "New Case Chat",
                                icon = R.drawable.page_img,
                                modifier = Modifier.fillMaxWidth(),
                                height = 56.dp,
                                fontSize = 14.sp,
                                imageSize = 16.dp,
                                gradientColors = listOf(
                                    Color(0xFF4338CA),
                                    Color(0xFF211C64)
                                ),
                                onClick = {

                                    showCaseDialog = true
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Suggested Questions
                        items(suggestedQuestions) { question ->
                            QuestionCard(question = question, isHealthQuestion = true, onClick = {
                                navController.navigate(AppDestination.ChatDataScreen) {
                                    popUpTo("openChatScreen") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Fitness Questions
                        items(fitnessQuestions) { question ->
                            QuestionCard(question = question, isHealthQuestion = false, onClick = {
                                navController.navigate(AppDestination.ChatDataScreen) {
                                    popUpTo("openChatScreen") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }


                    }


                    BottomMessageBar1(
                        modifier = Modifier
                            .fillMaxWidth().background(color = Color.Transparent)
                            .padding(horizontal = 0.dp).padding(bottom = 10.dp),
                        state = chatInputState,
                        viewModel = chatViewModel,
                        onSendClicked = {
                            // When send button is clicked, trigger navigation
                            shouldNavigate = true
                        }
                    )

                    }
                }



                // Case Dialog
                if (showCaseDialog) {
                    CaseDialog(
                        onDismiss = { showCaseDialog = false },
                        onConfirm = {
                            navController.navigate(AppDestination.ChatDataScreen) {
                                popUpTo("openChatScreen") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedUser = "Rose Logan (Spouse)"
                            showCaseDialog = false
                        }
                    )
                }
            }
        }

}


@Composable
fun QuestionCard(question: String, isHealthQuestion: Boolean,onClick:()->Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(){
            onClick()
        },
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (isHealthQuestion) {
                Image(
                    painter = painterResource(R.drawable.ic_circle_page_image),
                    contentDescription = null,

                    modifier = Modifier.size(51.dp).align(alignment = Alignment.CenterVertically)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_g_icon),
                    contentDescription = null,

                    modifier = Modifier.size(51.dp).align(alignment = Alignment.CenterVertically)
                )
            }
            Text(question, fontSize = 16.sp, modifier = Modifier.weight(1f).align(alignment = Alignment.CenterVertically), fontFamily = FontFamily(Font(R.font.urbanist_regular)))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OpenChatScreenPreview() {
    val navController = rememberNavController()
    OpenChatScreen(navController = navController)
}

