package com.nenolink.huskeseddel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nenolink.huskeseddel.data.CustomProductEntity
import com.nenolink.huskeseddel.data.Product
import com.nenolink.huskeseddel.data.ProductCategory
import com.nenolink.huskeseddel.data.ShoppingItemEntity
import com.nenolink.huskeseddel.data.formatShoppingListText
import com.nenolink.huskeseddel.ui.HuskeseddelUiState
import com.nenolink.huskeseddel.ui.HuskeseddelViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HuskeseddelTheme { HuskeseddelApp() } }
    }
}

private enum class Destination(val title: String, val symbol: String) {
    Products("Varer", "☷"),
    Shopping("Huskeseddel", "✓"),
    Settings("Indstillinger", "⚙"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuskeseddelApp(viewModel: HuskeseddelViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var destinationName by rememberSaveable { mutableStateOf(Destination.Products.name) }
    val destination = Destination.valueOf(destinationName)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(destination.title) },
                actions = {
                    if (destination == Destination.Shopping) {
                        TextButton(onClick = { shareShoppingList(context, state) }) { Text("Del") }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = item == destination,
                        onClick = { destinationName = item.name },
                        icon = { Text(item.symbol) },
                        label = { Text(item.title) },
                    )
                }
            }
        },
    ) { padding ->
        when (destination) {
            Destination.Products -> ProductsScreen(state, viewModel, Modifier.padding(padding))
            Destination.Shopping -> ShoppingScreen(state, viewModel, Modifier.padding(padding))
            Destination.Settings -> SettingsScreen(Modifier.padding(padding))
        }
    }
}

private fun shareShoppingList(context: Context, state: HuskeseddelUiState) {
    val text = formatShoppingListText(
        items = state.shoppingItems,
        categoryNames = state.allCategories.associate { it.id to it.name },
        note = state.note,
    )
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, "Del huskeseddel"))
}

@Composable
private fun ProductsScreen(state: HuskeseddelUiState, viewModel: HuskeseddelViewModel, modifier: Modifier = Modifier) {
    val selectedKeys = state.shoppingItems.mapTo(mutableSetOf()) { it.productKey }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }
    var editing by remember { mutableStateOf<CustomProductEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::setQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Søg i varer") },
            singleLine = true,
            keyboardActions = KeyboardActions.Default,
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.categories, key = { it.id }) { category ->
                val isExpanded = expanded[category.id] ?: true
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth().clickable { expanded[category.id] = !isExpanded }.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(category.name, style = MaterialTheme.typography.titleMedium)
                            Text(if (isExpanded) "▲" else "▼")
                        }
                        if (isExpanded) category.products.forEach { product ->
                            ProductRow(
                                product = product,
                                checked = product.key in selectedKeys,
                                onChecked = { viewModel.setSelected(product, it) },
                                onEdit = product.customId?.let { id ->
                                    { editing = state.customProducts.firstOrNull { it.id == id }; showEditor = true }
                                },
                                onDelete = product.customId?.let { id ->
                                    { state.customProducts.firstOrNull { it.id == id }?.let(viewModel::deleteCustom) }
                                },
                            )
                        }
                    }
                }
            }
            if (state.categories.isEmpty()) item { Text("Ingen varer matcher søgningen.", modifier = Modifier.padding(16.dp)) }
        }
        Button(
            onClick = { editing = null; showEditor = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) { Text("Tilføj vare") }
    }

    if (showEditor) ProductEditorDialog(
        existing = editing,
        categories = state.allCategories,
        onDismiss = { showEditor = false },
        onSave = { name, categoryId -> viewModel.saveCustom(editing, name, categoryId); showEditor = false },
    )
}

@Composable
private fun ProductRow(
    product: Product,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onChecked(!checked) }.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(product.name, modifier = Modifier.weight(1f))
        if (onEdit != null) TextButton(onClick = onEdit) { Text("Rediger") }
        if (onDelete != null) TextButton(onClick = onDelete) { Text("Slet") }
    }
}

@Composable
private fun ProductEditorDialog(
    existing: CustomProductEntity?,
    categories: List<ProductCategory>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var name by remember(existing) { mutableStateOf(existing?.name.orEmpty()) }
    val initialCategory = existing?.categoryId ?: categories.firstOrNull()?.id.orEmpty()
    var categoryId by remember(existing, categories) { mutableStateOf(initialCategory) }
    var expanded by remember { mutableStateOf(false) }
    val categoryName = categories.firstOrNull { it.id == categoryId }?.name ?: "Vælg kategori"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Tilføj vare" else "Rediger vare") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Varenavn") }, singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(categoryName) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = { categoryId = category.id; expanded = false },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, categoryId) }, enabled = name.isNotBlank() && categoryId.isNotBlank()) { Text("Gem") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuller") } },
    )
}

@Composable
private fun ShoppingScreen(state: HuskeseddelUiState, viewModel: HuskeseddelViewModel, modifier: Modifier = Modifier) {
    val categoryNames = state.allCategories.associate { it.id to it.name }
    var note by rememberSaveable { mutableStateOf(state.note) }
    LaunchedEffect(state.note) { if (state.note != note) note = state.note }
    val speakItemName = rememberItemTts()

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = note,
            onValueChange = { note = it; viewModel.saveNote(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note") },
            minLines = 2,
            maxLines = 4,
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            state.shoppingItems.groupBy { it.categoryId }.forEach { (categoryId, entries) ->
                item(key = "heading:$categoryId") {
                    Text(
                        categoryNames[categoryId] ?: "Egne varer",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                    )
                }
                items(entries, key = { it.productKey }) { item -> ShoppingRow(item, viewModel, speakItemName) }
            }
            if (state.shoppingItems.isEmpty()) item { Text("Din huskeseddel er tom.", modifier = Modifier.padding(vertical = 24.dp)) }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::clearPurchased, modifier = Modifier.weight(1f)) { Text("Ryd købte") }
            Button(onClick = viewModel::newShopping, modifier = Modifier.weight(1f)) { Text("Nyt indkøb") }
        }
    }
}

@Composable
private fun rememberItemTts(): (String) -> Unit {
    val context = LocalContext.current
    val tts = remember { ItemTts(context.applicationContext) }
    DisposableEffect(tts) {
        onDispose { tts.shutdown() }
    }
    return remember(tts) { { name -> tts.speak(name) } }
}

@Composable
private fun ShoppingRow(item: ShoppingItemEntity, viewModel: HuskeseddelViewModel, onSpeakName: (String) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.purchased, onCheckedChange = { viewModel.setPurchased(item, it) })
            Text(
                item.displayName,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(item.productKey, item.displayName) {
                        detectTapGestures(onDoubleTap = { onSpeakName(item.displayName) })
                    },
                color = if (item.purchased) Color.Red else Color.Unspecified,
            )
            TextButton(onClick = { viewModel.changeQuantity(item, -1) }) { Text("−") }
            Text(item.quantity.toString())
            TextButton(onClick = { viewModel.changeQuantity(item, 1) }) { Text("+") }
        }
        HorizontalDivider()
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Om Huskeseddel", style = MaterialTheme.typography.headlineSmall)
        Text("En enkel indkøbsliste fra Nenolink.")
        Text("Version 1.0.0-dev")
        Spacer(Modifier.height(8.dp))
        Text("Privatliv og data", style = MaterialTheme.typography.titleMedium)
        Text("Alle data gemmes lokalt på enheden. Appen bruger ikke login, cloud, analytics eller netværk.")
        Text("© Nenolink")
    }
}

@Composable
private fun HuskeseddelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF386A20),
            secondary = Color(0xFF55624C),
            background = Color(0xFFFFF8F0),
        ),
        content = content,
    )
}

private class ItemTts(context: Context) {
    @Volatile private var ready = false
    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) return@TextToSpeech
            try {
                if (::tts.isInitialized) {
                    tts.setLanguage(Locale.forLanguageTag("da-DK"))
                }
            } catch (_: Exception) {
                // Continue without Danish TTS rather than crashing.
            }
            ready = true
        }
    }

    fun speak(text: String) {
        if (!ready || text.isBlank() || !::tts.isInitialized) return
        try {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "huskeseddel-item")
        } catch (_: Exception) {
            // Missing engine or language must not crash the app.
        }
    }

    fun shutdown() {
        ready = false
        if (!::tts.isInitialized) return
        try {
            tts.stop()
            tts.shutdown()
        } catch (_: Exception) {
            // Ignore release errors from a missing or already shut-down engine.
        }
    }
}
