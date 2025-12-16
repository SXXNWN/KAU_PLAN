package com.example.kau_plan.ui.theme.navigation

<<<<<<< HEAD
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
=======
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
>>>>>>> origin/sangwon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kau_plan.ReservationScreen // ReservationScreen import
import com.example.kau_plan.StatusScreen
import com.example.kau_plan.data.Expense
import com.example.kau_plan.ui.theme.expense.AddExpenseScreen
import com.example.kau_plan.ui.theme.expense.ExpenseListScreen
<<<<<<< HEAD
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kau_plan.ui.theme.board.BoardScreen
import com.example.kau_plan.ui.theme.board.PostDetailScreen
=======
>>>>>>> origin/sangwon
import com.example.kau_plan.ui.theme.expense.ExpenseViewModel
import com.example.kau_plan.ui.theme.home.HomeScreen
import com.example.kau_plan.ui.theme.profile.ProfileScreen
import com.example.kau_plan.ui.theme.board.WritePostScreen

// 각 화면의 정보를 담는 Sealed Class 추가
//sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
//    object Home : Screen("home", "홈", Icons.Default.Home)
//    object UsageStatus : Screen("usage_status", "이용현황", Icons.Default.Search) // 예시 경로
//    object Expense : Screen("expense", "소비내역", Icons.Default.Lightbulb)
//    object Board : Screen("board", "게시판", Icons.Default.BarChart)
//    object Profile : Screen("profile", "프로필", Icons.Default.Person)
//}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: ExpenseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        // 🔹 홈 화면
        composable(BottomNavItem.Home.route) {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 현황 화면
        composable(BottomNavItem.Status.route) {
            StatusScreen(
                onNavigateToReservation = {
                    navController.navigate("reservation_screen")
                }
            )
        }

        // 🔹 가계부 화면
        composable(BottomNavItem.Expense.route) {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 게시판 화면 (임시)
        composable(BottomNavItem.Board.route) {
            Text("게시판 화면")
        }

        // 🔹 상점 화면 (임시)
        composable(BottomNavItem.Shop.route) {
            Text("상점 화면")
        }

        // 🔹 프로필(내정보) 화면
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                monthlyTotal = viewModel.monthlyTotal,
                expenses = viewModel.expenses
            )
        }

        // 🔹 소비내역 '추가' 화면
        composable("add_expense") {
            AddExpenseScreen(
                onSaveClick = { expense: Expense ->
                    viewModel.addExpense(expense)
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

<<<<<<< HEAD
        composable(route = "expense") {
            ExpenseListScreen(
                monthlyTotal = viewModel.monthlyTotal,
                monthlyGoal = viewModel.monthlyGoal,
                expenses = viewModel.expenses,
                onAddClick = {
                    navController.navigate("add_expense")
                }
            )
        }

        // 🔹 홈 화면 (기존 'home' 라우트를 새로운 HomeScreen으로 연결)
        composable(route = "home") {
            HomeScreen(
                navController = navController
                // 필요하다면 ViewModel의 데이터를 HomeScreen에 전달할 수 있습니다.
                // 예: monthlyTotal = viewModel.monthlyTotal,
                // 예: onNavigateToBoard = { navController.navigate("board") }
            )
        }

        // 🔹 게시판 화면 (새로 추가)
        composable(route = "board") {
            BoardScreen(
                navController = navController
            )
        }

        composable("postDetail") {
            PostDetailScreen(navController = navController)
        }

        composable("writePost") {
            WritePostScreen(navController = navController)
        }

        // 🔹 프로필 화면
        composable(route = "profile") {
            ProfileScreen(
                monthlyTotal = viewModel.monthlyTotal,
                expenses = viewModel.expenses
            )
        }
=======
        // 🔹 예약 화면 (수정된 부분)
        composable("reservation_screen") {
            // ReservationScreen을 호출할 때 onNavigateBack 파라미터에 값을 전달합니다.
            ReservationScreen(
                onNavigateBack = {
                    // 이 함수가 호출되면, 현재 화면(ReservationScreen)을 닫고
                    // 이전 화면(StatusScreen)으로 돌아갑니다.
                    navController.popBackStack()
                }
            )
        }
>>>>>>> origin/sangwon
    }
}