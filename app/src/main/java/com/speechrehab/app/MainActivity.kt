package com.speechrehab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { SpeechRehabApp() } }
    }
}

private enum class AppTab(val label: String) {
    HOME("Home"), CLIENTS("Clients"), PLAN("Sessions"), GOALS("Goals"), PROGRESS("Progress")
}

private data class Client(val id: Int, val name: String, val age: String, val concern: String)
private data class GoalPlan(
    val name: String,
    val target: String,
    val techniques: String,
    val activities: List<String>
)
private data class TherapyTask(val title: String, val duration: String, val goal: String)
private data class ResponseRecord(val activity: String, val response: String, val prompt: String, val notes: String, val trials: String)

private val goalPlans = listOf(
    GoalPlan("Expressive vocabulary", "The client will name selected familiar objects or pictures with clinician-defined support.", "Modeling, focused stimulation, expectant pause, graduated prompting", listOf("Picture naming", "Category sorting", "Requesting during play", "Object identification")),
    GoalPlan("Receptive language", "The client will identify named items or follow selected instructions with agreed support.", "Visual supports, gesture cues, repetition, graduated prompting", listOf("Identify named objects", "Follow one-step directions", "Find the picture", "Place objects by instruction")),
    GoalPlan("Phrase expansion", "The client will practise longer functional phrases during structured or play-based activities.", "Language expansion, recasting, modeling, parallel talk", listOf("Expand one-word requests", "Describe pictures", "Pretend play", "Complete familiar phrases")),
    GoalPlan("Speech sound production", "The client will practise the selected target sound at a clinician-defined level of complexity.", "Auditory model, appropriate placement cues, imitation, structured practice", listOf("Sound imitation", "Syllable practice", "Target-word practice", "Picture naming")),
    GoalPlan("Following instructions", "The client will follow selected instructions with the agreed level of prompting.", "Gesture cues, visual supports, simplified language, graduated prompting", listOf("One-step directions", "Action games", "Object placement", "Routine-based instructions")),
    GoalPlan("Social communication", "The client will practise a selected social communication skill in a structured or natural interaction.", "Modeling, visual scripts, prompting, naturalistic teaching", listOf("Turn-taking games", "Greeting routines", "Requesting help", "Conversational play"))
)

@Composable
private fun SpeechRehabApp() {
    var tab by remember { mutableStateOf(AppTab.HOME) }
    var addingClient by remember { mutableStateOf(false) }
    var nextId by remember { mutableIntStateOf(3) }
    var selectedGoal by remember { mutableStateOf(goalPlans.first()) }
    var selectedClient by remember { mutableStateOf("Demo Client A") }
    var notice by remember { mutableStateOf<String?>(null) }
    val clients = remember {
        mutableStateListOf(
            Client(1, "Demo Client A", "4 years", "Language development"),
            Client(2, "Demo Client B", "6 years", "Speech sound production")
        )
    }
    val tasks = remember {
        mutableStateListOf(
            TherapyTask("Picture naming", "10 min", "Expressive vocabulary"),
            TherapyTask("Follow one-step instructions", "10 min", "Receptive language"),
            TherapyTask("Build 2–3 word phrases", "10 min", "Phrase expansion"),
            TherapyTask("Parent-child shared story", "10 min", "Interaction and language")
        )
    }
    val completed = remember { mutableStateListOf(false, false, false, false) }
    val records = remember { mutableStateListOf<ResponseRecord>() }

    if (addingClient) {
        AddClientScreen(
            onCancel = { addingClient = false },
            onSave = { name, age, concern ->
                clients.add(Client(nextId++, name.trim(), age.trim(), concern.trim()))
                selectedClient = name.trim()
                addingClient = false
                tab = AppTab.CLIENTS
                notice = "Client added on this device."
            }
        )
        return
    }

    Scaffold(bottomBar = {
        NavigationBar {
            AppTab.entries.forEach { destination ->
                NavigationBarItem(
                    selected = tab == destination,
                    onClick = { tab = destination },
                    icon = { Text(when (destination) {
                        AppTab.HOME -> "⌂"
                        AppTab.CLIENTS -> "♙"
                        AppTab.PLAN -> "✓"
                        AppTab.GOALS -> "✦"
                        AppTab.PROGRESS -> "↗"
                    }) },
                    label = { Text(destination.label) }
                )
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp)) {
            notice?.let { Text(it, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(4.dp)) }
            when (tab) {
                AppTab.HOME -> HomeScreen(
                    clients = clients.size, done = completed.count { it }, total = tasks.size,
                    onClients = { tab = AppTab.CLIENTS }, onPlan = { tab = AppTab.PLAN },
                    onGoals = { tab = AppTab.GOALS }, onProgress = { tab = AppTab.PROGRESS }
                )
                AppTab.CLIENTS -> ClientsScreen(clients, onAdd = { addingClient = true })
                AppTab.PLAN -> PlanScreen(
                    clients = clients, selectedClient = selectedClient,
                    onClientSelected = { selectedClient = it }, tasks = tasks, completed = completed,
                    onRecord = { task ->
                        selectedGoal = goalPlans.firstOrNull { it.name == task.goal } ?: goalPlans.first()
                        tab = AppTab.GOALS
                    }
                )
                AppTab.GOALS -> GoalScreen(
                    selectedGoal = selectedGoal,
                    onGoalSelected = { selectedGoal = it },
                    records = records,
                    onSave = { record ->
                        records.add(record)
                        notice = "Response recorded for ${record.activity}."
                    }
                )
                AppTab.PROGRESS -> ProgressScreen(completed.count { it }, tasks.size, records.size)
            }
        }
    }
}

@Composable
private fun HomeScreen(clients: Int, done: Int, total: Int, onClients: () -> Unit, onPlan: () -> Unit, onGoals: () -> Unit, onProgress: () -> Unit) {
    Text("Speech Rehab", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Manage • Plan • Practise • Progress")
    Spacer(Modifier.height(18.dp))
    Text("Today's overview", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard("Clients", clients.toString(), Modifier.weight(1f))
        MetricCard("Tasks done", "$done/$total", Modifier.weight(1f))
    }
    Spacer(Modifier.height(18.dp))
    Button(onClick = onClients, modifier = Modifier.fillMaxWidth()) { Text("Client management") }
    OutlinedButton(onClick = onPlan, modifier = Modifier.fillMaxWidth()) { Text("Session and therapy plan") }
    OutlinedButton(onClick = onGoals, modifier = Modifier.fillMaxWidth()) { Text("Suggest goals, techniques and activities") }
    OutlinedButton(onClick = onProgress, modifier = Modifier.fillMaxWidth()) { Text("Progress and response records") }
    Spacer(Modifier.height(8.dp))
    Text("Prototype only: use fictional information. Secure clinical storage is not implemented.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun ClientsScreen(clients: List<Client>, onAdd: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text("Clients", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("${clients.size} profiles on this device")
        }
        Button(onClick = onAdd) { Text("+ Add") }
    }
    Spacer(Modifier.height(10.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(clients, key = { it.id }) { client ->
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(client.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Age: ${client.age}")
                    Text("Primary concern: ${client.concern}")
                }
            }
        }
    }
}

@Composable
private fun AddClientScreen(onCancel: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var concern by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Add client", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Use fictional test details only.")
        OutlinedTextField(name, { name = it; error = null }, label = { Text("Client name or code") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(age, { age = it; error = null }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(concern, { concern = it; error = null }, label = { Text("Primary communication concern") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            if (name.isBlank() || age.isBlank() || concern.isBlank()) error = "Please complete all fields."
            else onSave(name, age, concern)
        }, modifier = Modifier.fillMaxWidth()) { Text("Save client") }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
    }
}

@Composable
private fun PlanScreen(clients: List<Client>, selectedClient: String, onClientSelected: (String) -> Unit, tasks: List<TherapyTask>, completed: MutableList<Boolean>, onRecord: (TherapyTask) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Text("Session plan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Client: $selectedClient  ▾") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            clients.forEach { client ->
                DropdownMenuItem(text = { Text(client.name) }, onClick = { onClientSelected(client.name); expanded = false })
            }
        }
    }
    Text("Sample activities — clinician review required", style = MaterialTheme.typography.bodySmall)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tasks.size) { index ->
            val task = tasks[index]
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = completed[index], onCheckedChange = { completed[index] = it })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.SemiBold)
                        Text("${task.duration} • ${task.goal}", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { onRecord(task) }) { Text("Record") }
                }
            }
        }
    }
}

@Composable
private fun GoalScreen(selectedGoal: GoalPlan, onGoalSelected: (GoalPlan) -> Unit, records: List<ResponseRecord>, onSave: (ResponseRecord) -> Unit) {
    var goalExpanded by remember { mutableStateOf(false) }
    var activity by remember(selectedGoal) { mutableStateOf(selectedGoal.activities.first()) }
    var response by remember { mutableStateOf("Correct") }
    var prompt by remember { mutableStateOf("Independent") }
    var notes by remember { mutableStateOf("") }
    var trials by remember { mutableStateOf("") }
    var responseExpanded by remember { mutableStateOf(false) }
    var promptExpanded by remember { mutableStateOf(false) }

    Text("Goal suggestions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Box {
        OutlinedButton(onClick = { goalExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Goal: ${selectedGoal.name}  ▾") }
        DropdownMenu(expanded = goalExpanded, onDismissRequest = { goalExpanded = false }) {
            goalPlans.forEach { goal -> DropdownMenuItem(text = { Text(goal.name) }, onClick = { onGoalSelected(goal); goalExpanded = false }) }
        }
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
        item {
            Text("Suggested goal", fontWeight = FontWeight.Bold)
            Text(selectedGoal.target)
            Spacer(Modifier.height(6.dp))
            Text("Suggested techniques", fontWeight = FontWeight.Bold)
            Text(selectedGoal.techniques)
            Spacer(Modifier.height(6.dp))
            Text("Choose activity", fontWeight = FontWeight.Bold)
        }
        items(selectedGoal.activities) { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = activity == option, onClick = { activity = option })
                Text(option)
            }
        }
        item {
            Divider(Modifier.padding(vertical = 6.dp))
            Text("Record response", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Activity: $activity")
            Box {
                OutlinedButton(onClick = { responseExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Response: $response  ▾") }
                DropdownMenu(expanded = responseExpanded, onDismissRequest = { responseExpanded = false }) {
                    listOf("Correct", "Incorrect", "Prompted", "Not attempted").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { response = it; responseExpanded = false })
                    }
                }
            }
            Box {
                OutlinedButton(onClick = { promptExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Prompt level: $prompt  ▾") }
                DropdownMenu(expanded = promptExpanded, onDismissRequest = { promptExpanded = false }) {
                    listOf("Independent", "Verbal", "Gesture", "Visual", "Model", "Physical").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { prompt = it; promptExpanded = false })
                    }
                }
            }
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(trials, { trials = it }, label = { Text("Trial count (optional, e.g. 4/5)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = {
                onSave(ResponseRecord(activity, response, prompt, notes, trials))
                notes = ""; trials = ""
            }, modifier = Modifier.fillMaxWidth()) { Text("Save response") }
            if (records.isNotEmpty()) {
                Text("Saved records in this session: ${records.size}", fontWeight = FontWeight.SemiBold)
                records.takeLast(3).reversed().forEach { Text("• ${it.activity}: ${it.response}, ${it.prompt}") }
            }
        }
    }
}

@Composable
private fun ProgressScreen(done: Int, total: Int, responseCount: Int) {
    val ratio = if (total == 0) 0f else done.toFloat() / total
    Text("Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Activity completion", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(progress = { ratio }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("$done of $total activities marked complete")
            Spacer(Modifier.height(10.dp))
            Text("Response records saved in this session: $responseCount")
        }
    }
    Spacer(Modifier.height(12.dp))
    Text("Completion and recorded responses are management indicators, not a diagnosis or independent measure of clinical improvement.")
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(title)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
