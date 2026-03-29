package com.example.mobilealgorithms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu

import com.example.mobilealgorithms.screens.*

val screenLabels = listOf("Home", "AStar", "Clusterization", "Decision Tree",
                          "Ant algorithm", "Genetic algorithm", "Neural Network")
sealed class NavRoutes(val route: String) {
    object Home : NavRoutes(screenLabels[0])
    object AStar : NavRoutes(screenLabels[1])
    object Clusterization : NavRoutes(screenLabels[2])
    object DecisionTree : NavRoutes(screenLabels[3])
    object AntAlgo : NavRoutes(screenLabels[4])
    object GeneticAlgo : NavRoutes(screenLabels[5])
    object NeuralNetwork : NavRoutes(screenLabels[6])
}

@Composable
fun AppNavigationMenu(screenLabel: String, screenContent: @Composable () -> Unit,
                      navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Row(Modifier.fillMaxSize().statusBarsPadding()) {
                Column(Modifier.weight(0.4f).fillMaxHeight()
                    .background(Color.White).alpha(1f)
                    .padding(16.dp))
                {
                    for (label in screenLabels) {
                        Button(onClick = {
                            navController.navigate(label)
                            scope.launch { drawerState.close() }
                        },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape=RoundedCornerShape(0)
                        )
                        {
                            Text(label)
                        }
                    }
                }
                Column(Modifier.weight(0.6f).fillMaxHeight().alpha(0f)) {}
            }
        },
        scrimColor = Color(0f, 0f, 0f, 0f),
        content = {
            Column(Modifier.statusBarsPadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        scope.launch {drawerState.open()} }) {
                        Icon(Icons.Filled.Menu, "Меню")
                    }
                    Text(screenLabel, fontSize = 24.sp,
                        modifier = Modifier.weight(1f))
                }
                screenContent()
            }
        }
    )
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = NavRoutes.Home.route) {
                composable(NavRoutes.Home.route) {
                    AppNavigationMenu(NavRoutes.Home.route,
                        ::Home, navController)
                }
                composable(NavRoutes.AStar.route) {
                    AppNavigationMenu(NavRoutes.AStar.route,
                        ::AStar, navController)
                }
                composable(NavRoutes.Clusterization.route) {
                    AppNavigationMenu(NavRoutes.Clusterization.route,
                        ::Clusterization, navController)
                }
                composable(NavRoutes.DecisionTree.route) {
                    AppNavigationMenu(NavRoutes.DecisionTree.route,
                        ::DecisionTree, navController)
                }
                composable(NavRoutes.AntAlgo.route) {
                    AppNavigationMenu(NavRoutes.AntAlgo.route,
                        ::AntAlgo, navController)
                }
                composable(NavRoutes.GeneticAlgo.route) {
                    AppNavigationMenu(NavRoutes.GeneticAlgo.route,
                        ::GeneticAlgo, navController)
                }
                composable(NavRoutes.NeuralNetwork.route) {
                    AppNavigationMenu(NavRoutes.NeuralNetwork.route,
                        ::NeuralNetwork, navController)
                }
            }
        }
    }
}
