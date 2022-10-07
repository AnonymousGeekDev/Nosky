package kt.nostr.nosky_compose.reusable_components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.compose.rememberNavController
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.home.ui.CustomDivider
import kt.nostr.nosky_compose.profile.ProfileTweets
import kt.nostr.nosky_compose.reusable_components.theme.NoskycomposeTheme


//TODO : Fix BottomNavigation controller issue.

//TODO: Find a way to separate the state of the main post from the replies state.

@Composable
fun PostView(goBack: () -> Unit) {
    BackHandler {
        goBack()
    }

//    val postColor by animateColorAsState(
//        targetValue = if (isSystemInDarkTheme())
//            Color(27, 27, 27) else
//                Color.White.copy(alpha = 0.5f)
//                //Color(240, 240, 240)
//    )
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (topBar, mainPost, replies, bottomBar) = createRefs()


        TopAppBar(Modifier.constrainAs(topBar) {
            //height = Dimension.value(56.dp)
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Box(modifier = Modifier
                .fillMaxWidth()) {
                Icon(modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { goBack() },
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null)

                ThemedText(modifier = Modifier.align(Alignment.Center),
                    text = "Post",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 1, textColor = Color.White)

            }
        }


        Column(
            Modifier
                .constrainAs(mainPost) {
                    height = Dimension.fillToConstraints
                    top.linkTo(topBar.bottom, margin = 2.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
                )) {
            Post(containsImage = false,
                post = "One of the user's very very long messages. from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                onPostClick = {})
            Column() {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Replies", style = TextStyle(
                    fontSize = 18.sp,
                    color = if (isSystemInDarkTheme()) Color(0xFF666666) else Color.Gray
                ), maxLines = 1)
                CustomDivider(Modifier.padding(start = 15.dp, end = 15.dp))
            }
        }


        Replies(Modifier.constrainAs(replies){
            height = Dimension.fillToConstraints

            top.linkTo(mainPost.bottom)
            bottom.linkTo(bottomBar.top, margin = 10.dp)
        })

        BottomNavigationBar(modifier = Modifier.constrainAs(bottomBar){
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }, navController = rememberNavController())
    }


}

@Composable
fun Replies(modifier: Modifier = Modifier) {
    ProfileTweets(modifier = modifier, onPostClick = {})
}

@Preview
@Composable
fun ParticularPostPreview() {
    NoskycomposeTheme(darkTheme = true) {
        Surface {
            PostView({})
        }
    }
}