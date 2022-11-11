package kt.nostr.nosky_compose.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kt.nostr.nosky_compose.home.backend.Post
import kt.nostr.nosky_compose.home.backend.opsList
import kt.nostr.nosky_compose.notifications.ui.PostsList
import kt.nostr.nosky_compose.reusable_ui_components.PostView

@Composable
fun ProfilePosts(modifier: Modifier = Modifier,
                 listOfPosts: List<Post> = opsList,
                 listState: LazyListState = rememberLazyListState(),
                 onPostClick: (Post) -> Unit) {

    val list by remember() {
        derivedStateOf {
            PostsList(listOfPosts)
        }
    }

    LazyColumn(state = listState, modifier = Modifier.padding(top = 10.dp).then(modifier)) {
        itemsIndexed(items = list.items, key = { index: Int, _: Post -> index }) { index, post ->
            PostView(
                viewingPost = post.copy(
                    textContent = "One of the user's very very long messages. " +
                            "from 8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
                ),
                isUserVerified = index.mod(2) != 0,
                containsImage = index.mod(2) == 0,
                onPostClick = onPostClick
            )
            //CustomDivider()
            //Spacer(modifier = Modifier.height(3.dp))
        }
    }
}