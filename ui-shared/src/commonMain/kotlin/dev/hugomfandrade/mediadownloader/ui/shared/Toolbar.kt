package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge, // maps to TitleText
                    // color = Color.White
                    modifier = Modifier.padding(start = 16.dp)
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall, // maps to SubTitleText
                        // color = Color.White
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .statusBarsPadding()          // paint status bar area
            .windowInsetsPadding(         // add REAL status bar size
                WindowInsets.statusBars
            )
            .requiredHeight(56.dp),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary, // maps ActionBarStyleOverlay
            titleContentColor = Color.White
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ToolbarBackButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = Color.White,
            contentDescription = "Back"
        )
    }
}

@Composable
fun ToolbarMenuIcon() {
    Icon(
        Icons.Default.Menu,
        tint = Color.White,
        contentDescription = "Menu"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchFocused: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var searching by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .statusBarsPadding()          // paint status bar area
            .windowInsetsPadding(         // add REAL status bar size
                WindowInsets.statusBars
            )
            .requiredHeight(56.dp),
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary, // maps ActionBarStyleOverlay
            titleContentColor = Color.White
        ),
        scrollBehavior = scrollBehavior,


        title = {
            if (!searching) {
                Text(title)
            }
        },
        navigationIcon = {
            if (searching) {
                IconButton(onClick = {
                    searching = false
                    onQueryChange("")
                    onSearchFocused(false)
                    onBackPressed()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            if (!searching) {
                IconButton(onClick = {
                    searching = true
                    onSearchFocused(true)
                }) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                }
            }
        },
        // modifier = modifier
    )

    if (searching) {
        SearchBarOverlay(
            query = query,
            onQueryChange = onQueryChange,
            onClose = {
                searching = false
                onQueryChange("")
                onSearchFocused(false)
            }
        )
    }
}

@Composable
fun SearchBarOverlay(
    query: String,
    onQueryChange: (String) -> Unit = {},
    onClose: () -> Unit,
    onSearch: (String) -> Unit = {}
) {
    Surface(
        tonalElevation = 4.dp,
        color = Color.Transparent,
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(start = 16.dp)
        ) {
            val searchTextColor = Color.White
            val searchHintColor = Color(0x90FFFFFF)
            var queryState by remember { mutableStateOf(query) }

            val focusRequester = remember { FocusRequester() }
            var active by remember { mutableStateOf(true) }

            val focusManager = LocalFocusManager.current
            val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

            // detect when keyboard is dismissed
            LaunchedEffect(imeVisible) {
                if (active) return@LaunchedEffect
                if (!imeVisible) {
                    // Keyboard dismissed manually
                    onClose()
                    focusManager.clearFocus()
                }
            }

            // Automatically request focus (and open keyboard)
            LaunchedEffect(active) {
                if (active) {
                    focusRequester.requestFocus()
                    active = false
                }
            }

            Spacer(Modifier.width(60.dp))
            TextField(
                value = queryState,
                onValueChange = {
                    queryState = it
                    onQueryChange(queryState)
                },
                singleLine = true,
                modifier = Modifier.weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .focusRequester(focusRequester),
                placeholder = { Text("Url (eg. RTP Play, SIC, Sapo)", color = searchHintColor) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = searchTextColor,
                    unfocusedTextColor = searchTextColor,
                    disabledTextColor = searchTextColor,

                    focusedPlaceholderColor = searchHintColor,
                    unfocusedPlaceholderColor = searchHintColor,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,

                    cursorColor = MaterialTheme.colorScheme.tertiary,

                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearch(queryState)
                        active = false

                    }
                )
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            } else {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}
