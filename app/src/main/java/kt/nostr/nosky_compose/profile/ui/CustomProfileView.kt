package kt.nostr.nosky_compose.profile.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kt.nostr.nosky_compose.BottomNavigationBar
import kt.nostr.nosky_compose.R
import kt.nostr.nosky_compose.common_components.theme.NoskycomposeTheme
import kt.nostr.nosky_compose.common_components.ui.DotsFlashing
import kt.nostr.nosky_compose.common_components.ui.ProfileListView
import kt.nostr.nosky_compose.common_components.ui.UserInfo
import kt.nostr.nosky_compose.home.backend.opsList
import kt.nostr.nosky_compose.navigation.structure.Destination
import kt.nostr.nosky_compose.profile.model.PostsResult
import kt.nostr.nosky_compose.profile.model.Profile
import kotlin.math.min


@Composable
fun ProfileView(modifier: Modifier = Modifier,
                user: Profile = Profile(userName = "Satoshi Nakamoto",
                    pubKey = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                    bio = "A pseudonymous dev", following = 10, followers = 1_001),
                userPostsState: PostsResult = PostsResult.Loading,
                isProfileMine: Boolean = true,
                navController: BackStack<Destination>,
                goBack: () -> Unit) {



    val internalUser: Profile by remember {
        derivedStateOf {
            user
        }
    }
    BackHandler() {
        if (!isProfileMine){
            goBack()
        } else {
            navController.run {
                elements.value.first().key.navTarget.let {
                    singleTop(it)
                }
            }
        }

    }

    var showFollowersProfiles by remember {
        mutableStateOf(false)
    }

    var showFollowingProfiles by remember {
        mutableStateOf(false)
    }

    if (showFollowersProfiles){
        ProfileListView(user.followers) {
            showFollowersProfiles = !showFollowersProfiles
        }
    }
    else if (showFollowingProfiles){
        ProfileListView(user.following) {
            showFollowingProfiles = !showFollowingProfiles
        }
    }

    else {

        val nestedScrollState = rememberScrollState(0)
        val delta = with(LocalDensity.current){ 160.dp.toPx() - 30.dp.toPx() }

        val scrollState = rememberLazyListState(
            initialFirstVisibleItemIndex = nestedScrollState.value,
            initialFirstVisibleItemScrollOffset = nestedScrollState.value
        )

        val scrollOffset: Float by remember {
            derivedStateOf {
                if (isProfileMine){
                    0f
                } else {
                    min(1f, scrollState.firstVisibleItemIndex*50/delta)
                }
               // min(nestedScrollState.value/delta, 1f)

            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(backStackNavigator = navController)
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(paddingValues = padding)
            ) {
                Surface(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
                ) {
                    ProfileDetails(
                        user = internalUser,
                        profileSelected = !isProfileMine,
                        isProfileMine = isProfileMine,
                        showFollowing = {
                            showFollowingProfiles = !showFollowingProfiles
                        },
                        showFollowers = {
                            showFollowersProfiles = !showFollowersProfiles
                        },
                        goBack = goBack, offsetProvider = { scrollOffset })
                }

                Crossfade(targetState = userPostsState) { state ->
                    when(state) {
                        PostsResult.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                //CircularProgressIndicator(Modifier.align(Alignment.Center))
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(text = "Loading feed")
                                    DotsFlashing(
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }
                        is PostsResult.Success -> {
                            ProfilePosts(
                                listOfPosts = state.postList
                                    .map {
                                         it.copy(
                                             user = it.user.copy(
                                                 username = user.userName,
                                                 image = user.profileImage
                                             )
                                         )
                                    },
                                listState = scrollState,
                                onPostClick = {
                                    navController.push(Destination.ViewingPost(clickedPost = it))
                                }
                            )
                        }
                        is PostsResult.Error -> {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                //CircularProgressIndicator(Modifier.align(Alignment.Center))
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(text = "Error loading posts: ${state.obtainedError.message}")
                                }
                            }
                        }
                    }
                }
            }
        }

    }



}

/**
 * TODO: Replace current Profile View layout
 *  with this layout below.
  */
//@Composable
//fun ModifiedProfile(modifier: Modifier = Modifier,
//                    user: User,
//                    profileSelected: Boolean,
//                    isProfileMine: Boolean,
//                    showRelatedFollowers: () -> Unit,
//                    goBack: () -> Unit, offsetProvider: () -> Float) {
//    val startContraints = ConstraintSet {
//
//        val banner = createRefFor("banner")
//        val backButton = createRefFor("back")
//        val moreButton = createRefFor("more")
//        val avatar = createRefFor("avatar")
//        val content = createRefFor("content")
//        val followButton = createRefFor("follow")
//
//
//        constrain(avatar){
//
//            top.linkTo(banner.bottom, margin = (-40).dp)
//            //bottom.linkTo(banner.bottom, margin = (-40).dp)
//            start.linkTo(parent.start, margin = 16.dp)
//
//        }
//
//        constrain(followButton){
//            top.linkTo(banner.bottom, margin = 16.dp)
//            end.linkTo(parent.end, margin = 16.dp)
//
//        }
//
//        constrain(backButton){
//            top.linkTo(parent.top)
//            start.linkTo(parent.start)
//        }
//
//        constrain(moreButton){
//            top.linkTo(parent.top)
//            end.linkTo(parent.end)
//        }
//        constrain(content){
//            top.linkTo(parent.top, margin = 80.dp)
//            //bottom.linkTo(banner.bottom)
//            start.linkTo(banner.start, margin = 0.dp)
//
//        }
//    }
//
//    val endConstraintSet = ConstraintSet {
//        val banner = createRefFor("banner")
//        val backButton = createRefFor("back")
//        val moreButton = createRefFor("more")
//        val avatar = createRefFor("avatar")
//        val content = createRefFor("content")
//        val followButton = createRefFor("follow")
//
//
//        constrain(avatar){
//
//            top.linkTo(banner.bottom, margin = 3.dp)
//            //bottom.linkTo(banner.bottom, margin = (-40).dp)
//            start.linkTo(parent.start, margin = 40.dp)
//
//        }
//
//        constrain(followButton){
//            top.linkTo(banner.bottom, margin = 5.dp)
//            end.linkTo(parent.end, margin = 2.dp)
//
//        }
//
//        constrain(backButton){
//            top.linkTo(parent.top)
//            start.linkTo(parent.start)
//        }
//
//        constrain(moreButton){
//            top.linkTo(parent.top)
//            end.linkTo(parent.end)
//        }
//        constrain(content){
//            top.linkTo(parent.top, margin = 0.dp)
//            //bottom.linkTo(banner.bottom)
//            start.linkTo(banner.start, margin = 75.dp)
//
//        }
//    }
//    MotionLayout(start = startContraints,
//        end = endConstraintSet,
//        progress = offsetProvider()
//    ) {
//
//    }
//}



@Composable
fun ProfileDetails(
    modifier: Modifier = Modifier,
    user: Profile,
    profileSelected: Boolean,
    isProfileMine: Boolean,
    showFollowing: () -> Unit,
    showFollowers: () -> Unit,
    goBack: () -> Unit,
    offsetProvider: () -> Float
) {
    //List of animations
    //--Common for all components--
    val alphaProvider by animateFloatAsState(targetValue = 1 - offsetProvider())

    //---For Avatar(or profile picture, refer to constraint ref below)---
    val imageSize by animateDpAsState(
        targetValue = lerp(start = 80.dp, stop = 50.dp , fraction = offsetProvider())
    )
    val profilePictureLeftMargin by animateDpAsState(
        targetValue = lerp(start = 16.dp, stop = 40.dp, fraction = offsetProvider())
    )

    val profilePictureTopMargin by animateDpAsState(
        targetValue = lerp(start = (-40).dp, stop = 3.dp, fraction = offsetProvider())
    )

    //---For banner(or background image, refer to constraint ref below)---
    val bannerHeight by animateDpAsState(
        targetValue = lerp(start = 70.dp, stop = 0.dp, fraction = offsetProvider())
    )
    //---For the follow or Edit profile button---
    val followButtonMargin by animateDpAsState(
        targetValue = lerp(start = 16.dp, stop = 1.dp, fraction = offsetProvider())
    )

    //---For the profile description(refer to constraint ref below)---
    val profileDescHeight by animateDpAsState(
        targetValue = lerp(start = 160.dp, stop = 30.dp, fraction = offsetProvider())
    )
    val profileNameLeftMargin by animateDpAsState(
        targetValue = lerp(start = 0.dp, stop = 80.dp, fraction = offsetProvider())
    )

    val profileNameTopMargin by animateDpAsState(
        targetValue = lerp(start = 124.dp, stop = 0.dp, fraction = offsetProvider())
    )

    ConstraintLayout(constraintSet = ConstraintSet {

        val banner = createRefFor("banner")
        val backButton = createRefFor("back")
        val moreButton = createRefFor("more")
        val avatar = createRefFor("avatar")
        val content = createRefFor("content")
        val followButton = createRefFor("follow")

        constrain(banner){
            height = Dimension.value(if (isProfileMine) 70.dp else bannerHeight)
        }


        constrain(avatar){
            height = Dimension.value(if (isProfileMine) 80.dp else imageSize)
            width = Dimension.value(if (isProfileMine) 80.dp else imageSize)
            top.linkTo(banner.bottom, margin = if (isProfileMine) (-40).dp else profilePictureTopMargin)
            start.linkTo(parent.start, margin = if (isProfileMine) 16.dp else profilePictureLeftMargin)

        }

        constrain(followButton){
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            top.linkTo(banner.bottom, margin = if (isProfileMine) 16.dp else followButtonMargin)
            end.linkTo(parent.end, margin = if (isProfileMine) 16.dp else followButtonMargin)

        }

        constrain(backButton){
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }

        constrain(moreButton){
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }

        constrain(content){
            height = Dimension.preferredValue(if (isProfileMine) 160.dp else profileDescHeight)
            width = Dimension.preferredWrapContent
            top.linkTo(parent.top, margin = if (isProfileMine) 124.dp else profileNameTopMargin)
            start.linkTo(parent.start, margin = if (isProfileMine) 0.dp else profileNameLeftMargin)
            if (offsetProvider() >= 0.6f && !isProfileMine) end.linkTo(followButton.start)


        }
    },
        modifier = Modifier.then(modifier)
    ) {
        Banner()
        FollowButton(isProfileMine = isProfileMine)
        if (profileSelected)
            TopBar(
                modifier = Modifier
                    .graphicsLayer {
                          alpha = if (isProfileMine) 1f else alphaProvider
                    },
                goBack = goBack)

        Avatar(profileImageUrl = { user.profileImage })
        ProfileDescription(
            transparency = if (isProfileMine) 1f else alphaProvider,
            user = user,
            showFollowing = showFollowing,
            showFollowers = showFollowers
        )
    }
}

@Composable
private fun ProfileDescription(modifier: Modifier = Modifier,
                               transparency: Float = 1f,
                               user: Profile,
                               showFollowing: () -> Unit,
                               showFollowers: () -> Unit) {
    Column(modifier = Modifier
        .layoutId("content")
        .padding(start = 16.dp, end = 16.dp, top = 6.dp)
        .then(modifier)) {
        UserInfo(
            Modifier.graphicsLayer { alpha = transparency },
            username = user.userName,
            userPubKey = user.pubKey,
            userBio = user.bio,
            following = user.following,
            followers = user.followers,
            isUserVerified = false,
            showFollowing = showFollowing,
            showFollowers = showFollowers
        )
        Divider(color = MaterialTheme.colors.onSurface)
    }
}


@Composable
internal fun Avatar(modifier: Modifier = Modifier, profileImageUrl: () -> String = { ""}) {


    val profileImage: Any = remember {
        profileImageUrl().ifBlank { R.drawable.nosky_logo }
    }

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .components {
            if (Build.VERSION.SDK_INT >= 28){
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(SvgDecoder.Factory())
        }
        .build()

//    ResourcesCompat.getDrawable(
//        context.resources, R.drawable.nosky_logo, context.theme)


    CoilImage(
        imageModel = { profileImage },
        imageLoader = { imageLoader },
        modifier = Modifier
            .clip(shape = RoundedCornerShape(40.dp))
            .layoutId("avatar")
            .border(
                border = BorderStroke(width = 1.dp, color = Color.Gray),
                shape = CircleShape
            )
            .then(modifier),
        imageOptions = ImageOptions(
            contentScale = ContentScale.Fit,
            contentDescription = "Profile Image"
        )

    )

}

@Composable
private fun TopBar(modifier: Modifier = Modifier, goBack:() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .layoutId("back"),
            onClick = goBack) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
        }

//        IconButton(modifier = Modifier
//            .layoutId("more")
//            .then(modifier), onClick = {}) {
//            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
//        }
    }
}

@Composable
private fun FollowButton(modifier: Modifier = Modifier, isProfileMine: Boolean) {
    val buttonLabel by remember {
        derivedStateOf { if (isProfileMine) "Edit Profile" else "Follow" }
    }
    val localContext = LocalContext.current
//    val icon by remember {
//        derivedStateOf {
//            if (isProfileMine) Icons.Default.MoreVert else FontAwesomeIcons.Solid.Envelope
//        }
//    }

    Row(
        modifier = Modifier.layoutId("follow"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Image(
//            imageVector = icon,
//            contentDescription = "Send a direct message.",
//            modifier = Modifier
//                .size(35.dp)
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colors.primary,
//                    shape = RoundedCornerShape(80.dp)
//                )
//                .clip(CircleShape)
//                .scale(0.5f),
//            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//        )

        Button(
            modifier = Modifier
                //.layoutId("follow")
                .then(modifier),
            onClick = {
                Toast.makeText(
                    localContext,
                    "Not yet implemented :|",
                    Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(text = buttonLabel, color = MaterialTheme.colors.primary)
        }
    }

}

@Composable
private fun Banner(modifier: Modifier = Modifier) {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .layoutId("banner")
            .then(modifier)
    )
}



//@ExperimentalFoundationApi
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CustomProfileViewPreview() {
    NoskycomposeTheme {
        ProfileView(
            userPostsState = PostsResult.Success(opsList),
            //profileSelected = true, isProfileMine = false,
            navController = BackStack(
                initialElement = Destination.Home,
                savedStateMap = null
            ), goBack = {})
    }
}