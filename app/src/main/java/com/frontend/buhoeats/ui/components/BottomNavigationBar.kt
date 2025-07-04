package com.frontend.buhoeats.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.frontend.buhoeats.R
import com.frontend.buhoeats.navigation.Screens
import com.frontend.buhoeats.ui.theme.AppColors
import com.frontend.buhoeats.utils.Translations
@Composable
fun BottomNavigationBar(navController: NavController) {
    // 👇 Esto obliga a recomponer cuando cambia el idioma
    val lang = Translations.currentLanguage

    NavigationBar(containerColor = AppColors.primary) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screens.Home.route) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.hogar),
                    contentDescription = Translations.t("home"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(Translations.t("home")) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B1D2A),
                selectedTextColor = Color(0xFF1B1D2A),
                indicatorColor = Color(0xFF2F3245),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screens.Promocion.route) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.promociones),
                    contentDescription = Translations.t("promotions"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(Translations.t("promotions")) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B1D2A),
                selectedTextColor = Color(0xFF1B1D2A),
                indicatorColor = Color(0xFF2F3245),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screens.Map.route) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.mapa),
                    contentDescription = Translations.t("map"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(Translations.t("map")) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B1D2A),
                selectedTextColor = Color(0xFF1B1D2A),
                indicatorColor = Color(0xFF2F3245),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screens.Search.route) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.lupa),
                    contentDescription = Translations.t("search"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(Translations.t("search")) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B1D2A),
                selectedTextColor = Color(0xFF1B1D2A),
                indicatorColor = Color(0xFF2F3245),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )
    }
}
