package com.andre.calculadoradeimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andre.calculadoradeimc.model.AppDatabase
import com.andre.calculadoradeimc.model.IMCRepository
import com.andre.calculadoradeimc.view.GraphScreen
import com.andre.calculadoradeimc.view.HistoryScreen
import com.andre.calculadoradeimc.view.Home
import com.andre.calculadoradeimc.view.TMBScreen
import com.andre.calculadoradeimc.viewmodel.IMCViewModel
import com.andre.calculadoradeimc.viewmodel.IMCViewModelFactory
import com.andre.calculadoradeimc.ui.theme.CalculadoraDeIMCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = IMCRepository(db.imcDao())
        val viewModel: IMCViewModel by viewModels { IMCViewModelFactory(repository) }

        setContent {
            CalculadoraDeIMCTheme { 
                 val navController = rememberNavController()
                 NavHost(navController = navController, startDestination = "home") {
                     composable("home") {
                         Home(
                             viewModel = viewModel,
                             onNavigateToTmb = { peso, altura, idade, isHomem ->
                                 navController.navigate("tmb/$peso/$altura/$idade/$isHomem")
                             },
                             onNavigateToHistory = {
                                 navController.navigate("history")
                             },
                             onNavigateToGraphs = {
                                 navController.navigate("graphs")
                             }
                         )
                     }
                     composable(
                         route = "tmb/{peso}/{altura}/{idade}/{isHomem}",
                         arguments = listOf(
                             navArgument("peso") { type = NavType.StringType },
                             navArgument("altura") { type = NavType.StringType },
                             navArgument("idade") { type = NavType.StringType },
                             navArgument("isHomem") { type = NavType.BoolType }
                         )
                     ) { backStackEntry ->
                         val peso = backStackEntry.arguments?.getString("peso") ?: ""
                         val altura = backStackEntry.arguments?.getString("altura") ?: ""
                         val idade = backStackEntry.arguments?.getString("idade") ?: ""
                         val isHomem = backStackEntry.arguments?.getBoolean("isHomem") ?: true
                         
                         TMBScreen(
                             viewModel = viewModel,
                             initialPeso = peso,
                             initialAltura = altura,
                             initialIdade = idade,
                             initialIsHomem = isHomem,
                             onBack = { navController.popBackStack() }
                         )
                     }
                     composable("history") {
                         HistoryScreen(
                             viewModel = viewModel,
                             onBack = { navController.popBackStack() }
                         )
                     }
                     composable("graphs") {
                         GraphScreen(
                             viewModel = viewModel,
                             onBack = { navController.popBackStack() }
                         )
                     }
                 }
            }
        }
    }
}