package com.frontend.buhoeats.ui.screens


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.frontend.buhoeats.R
import com.frontend.buhoeats.navigation.Screens
import com.frontend.buhoeats.utils.ValidatorUtils.isValidEmail
import com.frontend.buhoeats.ui.components.ValidationMessage
import com.frontend.buhoeats.viewmodel.UserSessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.frontend.buhoeats.auth.getGoogleSignInClient
import com.frontend.buhoeats.models.User
import com.frontend.buhoeats.ui.theme.AppColors
import com.frontend.buhoeats.utils.Translations
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.frontend.buhoeats.MainActivity

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Login(
    navControl: NavHostController,
    userSessionViewModel: UserSessionViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val email = account.email ?: ""
            val user = userSessionViewModel.getUserByEmail(email)
            if (user != null) {
                userSessionViewModel.login(user)
                Toast.makeText(context, Translations.t("login_google_success"), Toast.LENGTH_SHORT).show()
                navControl.navigate(Screens.Home.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
            } else {
                navControl.navigate(Screens.SignUp.route)
                Toast.makeText(context, Translations.t("user_not_found"), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, Translations.t("google_signin_error"), Toast.LENGTH_SHORT).show()
        }
    }

    val containerColor = Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BÚHO EATS",
                style = TextStyle(
                    fontFamily = montserratFontFamily,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(
                        color = Color.White,
                        offset = Offset(4f, 4f),
                        blurRadius = 12f
                    )
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Image(
                painter = painterResource(id = R.drawable.buho),
                contentDescription = "Logo Búho Eats",
                modifier = Modifier.size(190.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Translations.t("email_label"),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                )
            }

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError.isNotEmpty()) emailError = ""
                },
                placeholder = {
                    Text(
                        Translations.t("email_placeholder"),
                        color = Color.Gray,
                        fontSize = 16.sp,
                        style = TextStyle(fontFamily = montserratFontFamily)
                    )
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = montserratFontFamily
                ),
                isError = emailError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    errorContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            if (emailError.isNotEmpty()) {
                ValidationMessage(message = emailError)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Translations.t("password_label"),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                )
            }

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError.isNotEmpty()) passwordError = ""
                },
                placeholder = {
                    Text(
                        Translations.t("password_placeholder"),
                        color = Color.Gray,
                        fontSize = 16.sp,
                        style = TextStyle(fontFamily = montserratFontFamily)
                    )
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = montserratFontFamily
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    errorContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            if (passwordError.isNotEmpty()) {
                ValidationMessage(message = passwordError)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var hasError = false

                    if (email.isBlank()) {
                        emailError = Translations.t("error_email_required")
                        hasError = true
                    } else if (!isValidEmail(email)) {
                        emailError = Translations.t("error_email_invalid")
                        hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = Translations.t("error_password_required")
                        hasError = true
                    }

                    if (!hasError) {
                        isLoading = true

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)

                            // Asegurarse de que los usuarios estén cargados
                            if (userSessionViewModel.users.value.isEmpty()) {
                                userSessionViewModel.loadUsers()
                            }
                            val user = userSessionViewModel.users.value.find {
                                it.email == email && it.password == password
                            }
                            isLoading = false

                            if (user != null) {
                                userSessionViewModel.login(user)
                                Toast.makeText(context, Translations.t("login_success"), Toast.LENGTH_SHORT).show()
                                // Llamar a la función de descarga/instalación de APK
                                (context as? MainActivity)?.downloadAndInstallApkFromGithub()
                                navControl.navigate(Screens.Home.route) {
                                    popUpTo(Screens.Login.route) { inclusive = true }
                                }
                            } else {
                                passwordError = Translations.t("error_credentials")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06BB0C)),
            ) {
                Text(
                    text = Translations.t("login_button"),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    val signInClient = getGoogleSignInClient(context)
                    val signInIntent = signInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_image),
                        contentDescription = "Google logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                    )
                    Text(
                        text = Translations.t("login_with_google"),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = Translations.t("no_account_question"),
                style = TextStyle(
                    fontFamily = montserratFontFamily,
                    color = Color.White,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            val isEnglish = Translations.currentLanguage == Translations.Language.EN

            Button(
                onClick = { navControl.navigate(Screens.SignUp.route) },
                modifier = Modifier
                    .width(if (isEnglish) 280.dp else 200.dp)
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06BB0C))
            ) {
                Text(
                    text = Translations.t("create_account"),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontSize = 18.sp,
                        color = Color.White
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }

        }
    }
}
