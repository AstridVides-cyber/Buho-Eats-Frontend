package com.frontend.buhoeats.ui.screens

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.frontend.buhoeats.R
import com.frontend.buhoeats.data.InMemoryUserDataSource
import com.frontend.buhoeats.models.Dish
import com.frontend.buhoeats.ui.components.*
import com.frontend.buhoeats.ui.theme.AppColors
import com.frontend.buhoeats.ui.theme.ThemeManager
import com.frontend.buhoeats.utils.ImageConverter
import com.frontend.buhoeats.utils.Translations
import com.frontend.buhoeats.utils.ValidatorUtils.isValidPrice
import com.frontend.buhoeats.viewmodel.RestaurantViewModel
import com.frontend.buhoeats.viewmodel.UserSessionViewModel
import androidx.compose.ui.graphics.asImageBitmap
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditMenuScreen(
    navController: NavController,
    userSessionViewModel: UserSessionViewModel,
    restaurantViewModel: RestaurantViewModel
) {
    val currentUser = userSessionViewModel.currentUser.value
    val restaurant = restaurantViewModel.restaurantList.find { it.admin == currentUser?.id }
    val context = LocalContext.current

    if (currentUser?.rol != "admin" || restaurant == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var priceFormatError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(showBackIcon = true) { navController.popBackStack() } },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    Translations.t("edit_daily_menu"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = AppColors.texto
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.accent)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when (imageUri) {
                        null -> {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Agregar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        else -> {
                            // CAMBIO: Detectar tipo de imagen automáticamente y convertir a Base64
                            val imageToShow = imageUri?.toString() ?: ""
                            when {
                                ImageConverter.isBase64(imageToShow) -> {
                                    // Es Base64 - convertir a Bitmap
                                    val bitmap = ImageConverter.base64ToBitmap(imageToShow)
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Imagen seleccionada",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // Fallback si falla la conversión
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Error cargando imagen",
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }
                                imageToShow.isNotBlank() -> {
                                    // Es URI/URL - usar AsyncImage como antes
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = "Imagen seleccionada",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    // Sin imagen
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Agregar imagen",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = Translations.t("dish_name"),
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    isError = nameError,
                    placeholderText = Translations.t("dish_name_placeholder")
                )
                if (nameError) ValidationMessage(Translations.t("error_name_required"))

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = Translations.t("description"),
                    value = description,
                    onValueChange = { description = it; descriptionError = false },
                    isError = descriptionError,
                    isMultiline = true,
                    placeholderText = Translations.t("dish_description_placeholder")
                )
                if (descriptionError) ValidationMessage(Translations.t("error_description_required"))

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = Translations.t("price"),
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = false
                        priceFormatError = !isValidPrice(it)
                    },
                    isError = priceError || priceFormatError,
                    placeholderText = Translations.t("price_placeholder")
                )
                if (priceError) ValidationMessage(Translations.t("error_price_required"))
                else if (priceFormatError) ValidationMessage(Translations.t("error_price_format"))

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC11D0C)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(50.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(Translations.t("cancel"), color = Color.White, fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            nameError = name.isBlank()
                            descriptionError = description.isBlank()
                            priceError = price.isBlank()
                            priceFormatError = !isValidPrice(price)

                            if (!nameError && !descriptionError && !priceError && !priceFormatError) {
                                val formattedPrice = "%.2f".format(price.toDoubleOrNull() ?: 0.0)

                                // CAMBIO: Convertir imagen URI a Base64 si se seleccionó una
                                val imageToSave = imageUri?.let { uri ->
                                    ImageConverter.uriToBase64(context, uri)
                                } ?: "https://plus.unsplash.com/premium_photo-1673108852141-e8c3c22a4a22"

                                val newDish = Dish(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    description = description,
                                    imageUrl = imageToSave,
                                    price = formattedPrice,
                                    category = "General"
                                )
                                val updatedRestaurant = restaurant.copy(
                                    menu = restaurant.menu + newDish
                                )
                                restaurantViewModel.updateRestaurant(updatedRestaurant)
                                Toast.makeText(context, Translations.t("dish_added_success"), Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06BB0C)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(50.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(Translations.t("confirm"), color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
