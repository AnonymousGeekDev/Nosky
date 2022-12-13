package kt.nostr.nosky_compose.common_components.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.home.ui.CustomDivider
import kt.nostr.nosky_compose.profile.model.Profile

//TODO: Move list to being a parameter, move list state out.

@Composable
fun ProfileListView(numberOfProfiles: Int = 1, goBack: () -> Unit) {

    BackHandler {
        goBack()
    }

    val userList by remember {
        mutableStateOf(List(numberOfProfiles){
            Profile(
                userName = "Satoshi Nakamoto", pubKey =  "satoshi",
                bio = "A pseudonymous dev working on iubigsubtieybgeuygbeiygbtgyibei",
                following = 10, followers =  1_000)
        })
    }
    Scaffold(topBar = {
        AppTopBar(label = "Related Users", goBack = goBack)
    }) { contentPadding ->
        ListOfProfiles(modifier = Modifier.padding(contentPadding), userList = userList)
    }
}


@Composable
fun ListOfProfiles(modifier: Modifier = Modifier, userList: List<Profile>) {
    val names by remember {
        derivedStateOf { userList.map {  user ->
            user.userName }
        }
    }
    val profileBios by remember {
        derivedStateOf { userList.map {  user ->
            user.bio }
        }
    }
    LazyColumn(modifier = Modifier
        .padding(bottom = 50.dp)
        .then(modifier)){
        items(userList.size){ profilePosition ->
            UserProfile(
                userName = names[profilePosition],
                userBio = profileBios[profilePosition],
                isUserVerified = profilePosition % 2 == 0
            )
            CustomDivider()
        }
    }
}

@Composable
fun UserProfile(userName: String,
                userBio: String,
                isUserVerified: Boolean = true) {

    val color = remember {
        Color(0.4392157F, 0.5019608F, 0.72156864F, 1.0F, ColorSpaces.Srgb)
    }

    val targetColor by animateColorAsState(targetValue = color)

    Row(
        Modifier
            .padding(start = 8.dp, top = 5.dp, bottom = 8.dp)
            .fillMaxWidth()

    ) {
        Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center){
            Image(painter = painterResource(id = R.drawable.nosky_logo),
                contentDescription = "App Logo",
                Modifier
                    .clip(CircleShape)
                    .size(65.dp)
                    .background(Color.Cyan)
                    .border(
                        border = BorderStroke(width = 3.dp, color = targetColor),
                        shape = CircleShape
                    )
                    .aspectRatio(1f))
        }
        Spacer(modifier = Modifier.width(5.dp))
        Column(Modifier.weight(3f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThemedText(
                    text = userName,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 1
                )
                if (isUserVerified)
                    VerifiedUserIcon(Modifier.padding(top = 1.dp, start = 3.dp))
            }
            ThemedText(
                text = userBio,
                style = TextStyle(fontSize = 14.sp),
                maxLines = 2
            )
        }


        Button(
            modifier = Modifier
                .align(CenterVertically)
                .padding(start = 5.dp, end = 10.dp)
                .weight(2f),
            onClick = {
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(text = "Follow", color = MaterialTheme.colors.primary)
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun UserProfilePreview() {
    NoskycomposeTheme() {
        Surface {
            UserProfile(userName = "Satoshi Nakamoto (Gone)",
                userBio = "A pseudonymous dev working on iubigsubtieybgeuygbeiygbtgyibei")
        }
    }
}

@Preview
@Composable
fun ProfileListPreview() {
    NoskycomposeTheme(darkTheme = true) {
        ProfileListView(goBack = {})
    }
}