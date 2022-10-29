@file:OptIn(ExperimentalMaterialApi::class)

package kt.nostr.nosky_compose

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.navigation.AppNavigation
import kt.nostr.nosky_compose.navigation.NavigationItem
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.settings.backend.AppThemeState
import kt.nostr.nosky_compose.settings.backend.ThemeStateSaver

const val PROFILE_DATA = "profile_data"

class MainActivity : ComponentActivity() {
    private val preferenceManager: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!preferenceManager.contains("profile_present")
            || !preferenceManager.getBoolean("profile_present", false)){
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        //val obtainedProfile = intent.getParcelableExtra<Profile>(PROFILE_DATA)

        setContent {

            val themePreference = preferenceManager.getBoolean("dark_theme", isSystemInDarkTheme())
//            val themePreference = rememberPreferenceBooleanSettingState(
//                key = "dark_theme",
//                defaultValue = isSystemInDarkTheme()
//            )
            val appThemeState = rememberSaveable(saver = ThemeStateSaver) {
                AppThemeState(themePreference)
            }

            Screen(appThemeState, { value ->
                appThemeState.switchTheme()
                this.preferenceManager.edit(commit = true) {
                    putBoolean("dark_theme", appThemeState.isDark())
                }
                //themePreference.value = appThemeState.isDark()
            })
        }
    }


}



@Composable
fun Screen(theme: AppThemeState, onThemeChange: (Boolean) -> Unit){

    val rootNavController = rememberNavController()

    NoskycomposeTheme(theme.themeState.value) {

        Surface {
            AppNavigation(navController = rootNavController, appThemeState = theme, onThemeChange = onThemeChange)
            //BottomNavigationBar(navController = navController)

        }
    }


}


@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier,
                        navController: NavController,
                        isNewNotification: Boolean = false){


    val navItems = remember {
        listOf(
            NavigationItem.Home,
            NavigationItem.Profile,
            NavigationItem.Notifications,
            NavigationItem.Messages,
            NavigationItem.Settings
        )
    }

    BottomNavigation(modifier = modifier) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        navItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                     BadgedBox(badge = {
                         this@BottomNavigation.AnimatedVisibility(
                             visible = item == NavigationItem.Notifications && isNewNotification,
                             enter = fadeIn(), exit = fadeOut()){
                             Badge()
                         }
                     }) {
                         Icon(item.icon, contentDescription = item.title)
                     }
                },
                //label = { Text(text = item.title, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.route){

                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route){
                                saveState = false

                            }

                            restoreState = true
                        }

                        launchSingleTop = true
                        restoreState = true

                    }
                }
            )

        }

    }

}



@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    NoskycomposeTheme {
        Screen(AppThemeState(darkModeEnabled = true), { })
    }
}