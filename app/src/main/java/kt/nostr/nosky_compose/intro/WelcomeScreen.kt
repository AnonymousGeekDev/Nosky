package kt.nostr.nosky_compose.intro

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.theme.Purple500
import kt.nostr.nosky_compose.common_components.theme.Purple700
import kt.nostr.nosky_compose.common_components.ui.ThemedText
import kt.nostr.nosky_compose.settings.backend.AppThemeState


@Composable
fun WelcomeScreen(appThemeState: AppThemeState,
                  privKey: () -> String,
                  updatePrivKey: (String) -> Unit,
                  pubKey: () -> String,
                  updatePubKey: (String) -> Unit,
                  onLoginClick:() -> Unit,
                  onCreateProfileClick:() -> Unit) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val clearFocus = remember {
        { focusManager.clearFocus(force = true)}
    }

    val privKeyFieldActions = remember {
        KeyboardActions {
            this.defaultKeyboardAction(ImeAction.Next)
        }
    }
    val pubKeyFieldActions = remember {
        KeyboardActions {
            clearFocus()
        }
    }
    val backgroundColor: @Composable () -> Color = remember {
        { if (appThemeState.isDark()) MaterialTheme.colors.surface else Purple500 }
    }

    var isVisible by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(modifier = Modifier
        .background(backgroundColor())
        .fillMaxSize()
        .verticalScroll(scrollState)
        .offset { IntOffset(x = 0, y = (-scrollState.value).times(1 / 5)) }
    ) {
        val (topContent, formContent, bottomContent) = createRefs()

        //Top Content
        Column(modifier = Modifier.constrainAs(topContent){
            top.linkTo(parent.top, margin = 90.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AppLogo()
            ThemedText(text = "Welcome to Nosky!",
                textColor = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
        }

        //Form Content
        Column(modifier = Modifier
            .constrainAs(formContent) {
                top.linkTo(topContent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .imePadding(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.height(40.dp))
            KeyEntryField(fieldName = "Private key",
                data = privKey(),
                isFieldContentWrong = privKey().startsWith("npub1"),
                fieldKeyboardOptions = keyboardOptions,
                fieldKeyboardActions = privKeyFieldActions,
                visualTransformation = if (isVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                trailingIconImage = if (isVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                onTrailingIconTapped = {
                      isVisible = !isVisible
                },
                onDataUpdate = { updatePrivKey(it) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            KeyEntryField(fieldName = "Public key",
                data = pubKey(),
                isFieldContentWrong = pubKey().startsWith("nsec1"),
                fieldKeyboardActions = pubKeyFieldActions,
                onDataUpdate = { updatePubKey(it) }
            )
        }

        //Bottom content
        Row(modifier = Modifier.constrainAs(bottomContent){
            top.linkTo(formContent.bottom, margin = 60.dp)
            //bottom.linkTo(parent.bottom, margin = 50.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround) {
            OutlinedButton(onClick = onLoginClick,
                enabled = privKey().isNotBlank() && pubKey().isNotBlank(),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = backgroundColor(),
                    contentColor = Color.White,
                    //disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.3f))
            Column {
                Text(
                    text = "First time joining?",
                    color = Color.White.copy(alpha = ContentAlpha.medium)
                )
                TextButton(onClick = onCreateProfileClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = backgroundColor())) {
                    Text(text = "New Profile", color = Color.White, maxLines = 2)
                }
            }
        }
    }


}

@Composable
private fun AppLogo() {
    val color by remember {
        derivedStateOf {
            Color(0.4392157F, 0.5019608F, 0.72156864F, 1.0F, ColorSpaces.Srgb)
        }
    }

    val targetColor by animateColorAsState(targetValue = color)

    Image(
        painterResource(id = R.drawable.nosky_logo),
        contentDescription = "App Logo",
        modifier = Modifier
            .size(100.dp)
            .border(
                border = BorderStroke(width = 3.dp, color = targetColor),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Purple500,
                        Purple700
                    )
                )
            ),
        contentScale = ContentScale.Inside
    )
}

@Composable
private fun KeyEntryField(
    fieldName: String,
    data: String,
    isFieldContentWrong: Boolean = false,
    fieldKeyboardOptions: KeyboardOptions = remember {
        KeyboardOptions() },
    fieldKeyboardActions: KeyboardActions = remember {
        KeyboardActions() },
    visualTransformation: VisualTransformation = remember {
        VisualTransformation.None
    },
    trailingIconImage: ImageVector = remember {
        Icons.Default.ContentPaste
    },
    onTrailingIconTapped: () -> Unit = {},
    onDataUpdate: (String) -> Unit
    ) {

    val fieldLabel = remember {
        if (isFieldContentWrong)
            "Sorry, this is not a $fieldName"
        else "Enter the $fieldName here..."
    }

    Column {
        Text(text = fieldName, color = Color.White)
        OutlinedTextField(value = data,
            onValueChange = { value -> onDataUpdate(value) },
            modifier = Modifier
                .requiredSize(TextFieldDefaults.MinWidth, height = 63.dp)
                .selectable(true, onClick = {}),
            label = {
                Text(
                    text = fieldLabel,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            trailingIcon = { Icon(
                imageVector = trailingIconImage,
                contentDescription = "Copy the $fieldName",
                modifier = Modifier.clickable { onTrailingIconTapped() },
                tint = Color.White
            ) },
            isError = isFieldContentWrong,
            visualTransformation = visualTransformation,
            keyboardOptions = fieldKeyboardOptions,
            keyboardActions = fieldKeyboardActions,
            singleLine = true,
            colors = TextFieldDefaults
                .outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium),
                    unfocusedBorderColor = Color.White.copy(alpha = ContentAlpha.medium)
                )
        )
    }
}

private val keyboardOptions = KeyboardOptions(
    autoCorrect = false,
    keyboardType = KeyboardType.Ascii,
    imeAction = ImeAction.Next
)

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun WelcomeScreenPreview() {
    val appThemeState = AppThemeState(false)
    var privKey by remember {
        mutableStateOf("")
    }
    var pubKey by remember {
        mutableStateOf("")
    }
    NoskycomposeTheme(darkTheme = appThemeState.isDark()) {
        Surface {
            WelcomeScreen(appThemeState = appThemeState,
                privKey = { privKey },
                updatePrivKey = { privKey = it},
                pubKey = { pubKey },
                updatePubKey = { pubKey = it },
                onLoginClick = {},
                onCreateProfileClick = {})
        }
    }
}