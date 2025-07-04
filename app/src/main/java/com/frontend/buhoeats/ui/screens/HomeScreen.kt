package com.frontend.buhoeats.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.frontend.buhoeats.R
import com.frontend.buhoeats.models.Restaurant
import com.frontend.buhoeats.navigation.Screens
import com.frontend.buhoeats.ui.components.RestaurantCard
import com.frontend.buhoeats.ui.components.BottomNavigationBar
import com.frontend.buhoeats.ui.components.ConfirmationDialog
import com.frontend.buhoeats.ui.components.DeleteButton
import com.frontend.buhoeats.ui.components.DeleteFloatingButton
import com.frontend.buhoeats.ui.components.EditFloatingButton
import com.frontend.buhoeats.ui.components.TopBar
import com.frontend.buhoeats.viewmodel.RestaurantViewModel
import com.frontend.buhoeats.viewmodel.UserSessionViewModel
import kotlinx.coroutines.launch
import kotlin.text.equals
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.frontend.buhoeats.ui.theme.AppColors
import com.frontend.buhoeats.ui.theme.ThemeManager
import com.frontend.buhoeats.utils.Translations
import com.frontend.buhoeats.utils.ImageConverter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue
import com.frontend.buhoeats.utils.Translations.t
@Composable
fun HomeScreen(
    onRestaurantClick: (String) -> Unit,
    navController: NavController,
    userSessionViewModel: UserSessionViewModel,
    restaurantViewModel: RestaurantViewModel
) {
    val restaurantList = restaurantViewModel.restaurantList
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    val currentUser by userSessionViewModel.currentUser

    val isAdmin = currentUser?.rol == "admin"
    val isSuperAdmin = currentUser?.rol == "superadmin"
    val myRestaurant = restaurantList.find { it.admin == currentUser?.id }
    var isEditing by remember { mutableStateOf(false) }
    var isCreatingNewLocal by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var restaurantToDelete by remember { mutableStateOf<Restaurant?>(null) }

    LaunchedEffect(Unit) {
        userSessionViewModel.currentUser
    }

    val filteredRestaurants = restaurantList.filter { restaurant ->
        val isAdminFilter = if (isAdmin) {
            restaurant.admin != currentUser?.id
        } else {
            true
        }

        val typeFilter = selectedFilter?.let { filter ->
            restaurant.category.equals(filter, ignoreCase = true)
        } ?: true

        isAdminFilter && typeFilter
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val adminRestaurant = restaurantList.find { it.admin == currentUser?.id }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            currentUser?.let { user ->
                Scaffold(
                    topBar = { TopBar(showBackIcon = true) { scope.launch { drawerState.close() } } },
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        SettingSlider(
                            navController = navController,
                            currentUser = user,
                            restaurant = adminRestaurant,
                            onNavigateToProfile = { navController.navigate(Screens.Profile.route) }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    showMenuIcon = true,
                    onNavClick = {
                        scope.launch {
                            drawerState.open()
                            Log.d("HomeScreen", "Slider opened by user: ${currentUser?.email}")
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            },
            floatingActionButton = {
                if (isSuperAdmin) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (!isDeleting) {
                            EditFloatingButton(onClick = { isEditing = !isEditing })
                        }
                        if (!isEditing) {
                            DeleteFloatingButton(onClick = { isDeleting = !isDeleting })
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val backgroundImage = if (ThemeManager.isDarkTheme)
                    painterResource(id = R.drawable.backgrounddark)
                else
                    painterResource(id = R.drawable.backgroundlighttheme)

                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (showDialog && restaurantToDelete != null) {
                    ConfirmationDialog(
                        message = t("confirm_delete_restaurant"),
                        onConfirm = {
                            restaurantViewModel.deleteRestaurant(restaurantToDelete!!.id)
                            showDialog = false
                            isDeleting = false
                        },
                        onDismiss = {
                            showDialog = false
                            restaurantToDelete = null
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        GreetingSection(
                            userName = currentUser!!.name,
                            restaurants = restaurantList
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilterSection(
                            onFilterSelected = { selectedFilter = it },
                            onReset = { selectedFilter = null },
                            resultCount = filteredRestaurants.size
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (isAdmin && myRestaurant != null) {
                            Text(
                                text = t("your_location"),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.texto
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            RestaurantCard(restaurant = myRestaurant) {
                                onRestaurantClick(myRestaurant.id)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = t("restaurants_title"),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.texto
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (isEditing) {
                        item {
                            AddRestaurantCard(onClick = {
                                isCreatingNewLocal = true
                                navController.navigate(Screens.EditLocal.createRoute("-1", true))
                            })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    items(filteredRestaurants) { restaurant ->
                        Box {
                            RestaurantCard(restaurant = restaurant) {
                                if (isEditing) {
                                    isCreatingNewLocal = false
                                    navController.navigate(Screens.EditLocal.createRoute(restaurant.id, false))
                                } else if (!isDeleting) {
                                    onRestaurantClick(restaurant.id)
                                }
                            }
                            if (isDeleting) {
                                DeleteButton(
                                    onClick = {
                                        restaurantToDelete = restaurant
                                        showDialog = true
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String, restaurants: List<Restaurant>) {
    Column {
        Text(
            text = "${t("welcome_user")} $userName",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp, start = 12.dp),
            color = AppColors.texto
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (restaurants.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { restaurants.size })
            val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

            LaunchedEffect(key1 = pagerState.pageCount, key2 = isDragged) {
                if (!isDragged && pagerState.pageCount > 0) {
                    while (true) {
                        delay(5000L)

                        if (!isDragged && pagerState.pageCount > 0) {
                            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                            try {
                                pagerState.animateScrollToPage(nextPage)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                break
                            }
                        } else if (isDragged) {
                            break
                        }
                        yield()
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentPadding = PaddingValues(horizontal = 15.dp),
                pageSpacing = 12.dp
            ) { pageIndex ->
                val restaurant = restaurants[pageIndex]
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - pageIndex) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = lerp(
                                start = 0.8f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(
                                start = 0.8f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .fillMaxSize()
                ) {
                    // CAMBIO MÍNIMO: Detectar tipo de imagen automáticamente
                    when {
                        ImageConverter.isBase64(restaurant.imageUrl) -> {
                            // Es Base64 - convertir a Bitmap
                            val bitmap = ImageConverter.base64ToBitmap(restaurant.imageUrl)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = restaurant.name,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Fallback si falla la conversión
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text("Error loading image")
                                }
                            }
                        }
                        restaurant.imageUrl.isNotBlank() -> {
                            // Es URL - usar AsyncImage como antes
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(restaurant.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = restaurant.name,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            // Sin imagen
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("No image")
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(t("no_restaurant_images"))
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    onFilterSelected: (String) -> Unit,
    onReset: () -> Unit,
    resultCount: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = AppColors.secondary
            )

            Button(onClick = { onFilterSelected("Desayuno") }, colors = buttonColors) {
                Text(t("breakfast"), color = AppColors.text)
            }
            Button(onClick = { onFilterSelected("Almuerzo") }, colors = buttonColors) {
                Text(t("lunch"), color = AppColors.text)
            }
            Button(onClick = { onFilterSelected("Cena") }, colors = buttonColors) {
                Text(t("dinner"), color = AppColors.text)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${t("results")} $resultCount", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onReset() },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.accent)
            ) {
                Text(t("reset"), color = Color.White)
            }
        }
    }
}
@Composable
fun AddRestaurantCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = AppColors.accent),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = Translations.t("add_restaurant"),
                    tint = AppColors.texto,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Translations.t("add_restaurant"),
                    color = AppColors.texto,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}