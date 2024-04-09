import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.print.Book

import java.io.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock


class Data{
    companion object{
        var informationList = mutableStateListOf<Businessable<String>>()
    }
}

/**
 * Creates linear gradient from start offset(percentX, percentY) to end offset(percentX, percentY)
 * (Note: percentX, percentY are from 0f to 1f)
 */
fun Modifier.linearGradient(startOffset: Offset, endOffset: Offset, colors: List<Color>) : Modifier
{
    return this.background(object: ShaderBrush(){
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(Offset(startOffset.x * size.width, startOffset.y * size.height), Offset(endOffset.x * size.width, endOffset.y * size.height), colors)
            }
        })
}


fun Modifier.questRadialGradient(centerColor: Color = Color.Yellow, sideColor: Color = Color.Yellow): Modifier
{
    return this.background(
                object : ShaderBrush(){
                    override fun createShader(size: Size): Shader {
                        return RadialGradientShader(Offset(size.width / 2, size.height / 2), size.width / 3, listOf(
                            Color.White, centerColor, sideColor, Color.Black
                        ), listOf(0.2f, 0.5f, 0.8f, 1f))
                    }
                }
            )
}


//HelpComposables
@Composable
fun ArrayElementRow(infoObj: Businessable<String>)
{
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .linearGradient(
                    Offset(1f, 0f),
                    Offset(0f, 1f),
                    listOf(Color.hsl(150f, 1f, 0.5f), Color.hsl(150f, 0.3f, 0.5f))
                ),
            contentAlignment = Alignment.Center
        )
        {
            Text(infoObj.businessInfo, color = Color.White)
        }
        Divider(modifier = Modifier.fillMaxHeight().width(2.dp), color = Color.Blue)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .linearGradient(
                    Offset(1f, 0f),
                    Offset(0f, 1f),
                    listOf(Color.hsl(130f, 1f, 0.5f), Color.hsl(130f, 0.3f, 0.5f))
                ),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                try {
                    infoObj.business()
                } catch (e: Exception) {
                    e.message!!
                }, color = Color.White
            )
        }
        Divider(modifier = Modifier.fillMaxHeight().width(2.dp), color = Color.Blue)
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        )
        {
            infoObj.forEach { obj ->
                Column(
                    modifier = Modifier.fillMaxHeight().width(200.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(obj.first, color = Color.White)
                    Divider(thickness = 1.dp, color = Color.Red)
                    Text(obj.second.toString(), color = Color.White)
                }
                Divider(modifier = Modifier.fillMaxHeight().width(2.dp), color = Color.Blue)
            }
        }
    }
}

@Composable
fun DataInput(informationList: SnapshotStateList<Businessable<String>>)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        var newElemShow = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        )
        {
            informationList.forEach { infoObj ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(3.dp, color = Color.White)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    var plusExpanded = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.weight(1f)
                    )
                    {
                        ArrayElementRow(infoObj)
                    }
                    Box(
                        modifier = Modifier
                            .clickable {
                                plusExpanded.value = true
                            }
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(modifier = Modifier.scale(3f).padding(5.dp), text = "+", color = Color.Yellow)
                    }

                    DropdownMenu(
                        modifier = Modifier.fillMaxSize(0.7f),
                        expanded = plusExpanded.value,
                        onDismissRequest = {
                            plusExpanded.value = false
                        }
                    )
                    {
                        when {
                            (infoObj is BookArray) -> {
                                var bookName = remember { mutableStateOf("") }
                                var pagesCountStr = remember { mutableStateOf("") }
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = bookName.value,
                                    label = { Text("Введите название книги") },
                                    onValueChange = { str ->
                                        bookName.value = str
                                    },
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = pagesCountStr.value,
                                    label = { Text("Введите количество страниц") },
                                    onValueChange = { str ->
                                        if (str.isEmpty()) pagesCountStr.value = ""
                                        str.toIntOrNull()?.takeIf { it >= 0 }?.let { pagesCountStr.value = str }
                                    },
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                DropdownMenuItem(
                                    modifier = Modifier.linearGradient(
                                        Offset(0f, 0.5f),
                                        Offset(1f, 0.5f),
                                        listOf(Color.Blue, Color.White, Color.Blue)
                                    ),
                                    onClick = {
                                        infoObj += (bookName.value to (pagesCountStr.value.toIntOrNull() ?: 0))
                                        plusExpanded.value = false
                                    }
                                )
                                {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    )
                                    {
                                        Text("ДОБАВИТЬ КНИГУ")
                                    }
                                }
                            }

                            (infoObj is ArticleArray) -> {
                                var articleName = remember { mutableStateOf("") }
                                var pagesCountStr = remember { mutableStateOf("") }
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = articleName.value,
                                    label = { Text("Введите название статьи") },
                                    onValueChange = { str ->
                                        articleName.value = str
                                    },
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = pagesCountStr.value,
                                    label = { Text("Введите количество страниц") },
                                    onValueChange = { str ->
                                        if (str.isEmpty()) pagesCountStr.value = ""
                                        str.toIntOrNull()?.takeIf { it >= 0 }?.let { pagesCountStr.value = str }
                                    },
                                )
                                Spacer(Modifier.weight(1f))
                                DropdownMenuItem(
                                    modifier = Modifier.linearGradient(
                                        Offset(0f, 0.5f),
                                        Offset(1f, 0.5f),
                                        listOf(Color.Blue, Color.White, Color.Blue)
                                    ),
                                    onClick = {
                                        infoObj += (articleName.value to (pagesCountStr.value.toIntOrNull() ?: 0))
                                        plusExpanded.value = false
                                    }
                                )
                                {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    )
                                    {
                                        Text("ДОБАВИТЬ СТАТЬЮ")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, color = Color.Green)
                .padding(10.dp)
                .clickable {
                    newElemShow.value = true
                },
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            )
            {
                Text(modifier = Modifier.scale(3f).padding(5.dp), text = "+", color = Color.Magenta)
            }
        }
        if (newElemShow.value)
        {
            var arrayName = remember { mutableStateOf("") }
            var limitCount = remember { mutableStateOf("") }
            AlertDialog(
                modifier = Modifier.fillMaxSize(1f),
                onDismissRequest = {newElemShow.value = false},
                dismissButton = {},
                confirmButton = {},
                text = {
                    var selectedContent = remember{ mutableStateOf("Книги") }
                    Column(
                        modifier = Modifier.fillMaxSize(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Spacer(modifier = Modifier.weight(1f))
                        when(selectedContent.value) {
                            ("Книги") -> {
                                Row(modifier = Modifier.fillMaxWidth())
                                {
                                    Spacer(modifier = Modifier.weight(1f))
                                    OutlinedTextField(
                                        modifier = Modifier.width(150.dp),
                                        value = arrayName.value,
                                        label = { Text("Введите название сборника") },
                                        onValueChange = { str ->
                                            arrayName.value = str
                                        }
                                    )
                                    Spacer(modifier = Modifier.weight(2f))
                                    OutlinedTextField(
                                        modifier = Modifier.width(150.dp),
                                        value = limitCount.value.toString(),
                                        label = { Text("Введите количество вводных страниц") },
                                        onValueChange = { str ->
                                            if (str.isEmpty()) limitCount.value = ""
                                            str.toIntOrNull()?.takeIf { it >= 0 }?.let { limitCount.value = str }
                                        }
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            ("Статьи") -> {
                                Row(modifier = Modifier.fillMaxWidth())
                                {
                                    Spacer(modifier = Modifier.weight(1f))
                                    OutlinedTextField(
                                        modifier = Modifier.width(150.dp),
                                        value = arrayName.value,
                                        label = { Text("Введите название статьи") },
                                        onValueChange = { str ->
                                            arrayName.value = str
                                        }
                                    )
                                    Spacer(modifier = Modifier.weight(2f))
                                    OutlinedTextField(
                                        modifier = Modifier.width(150.dp),
                                        value = limitCount.value.toString(),
                                        label = { Text("Введите количество аннотаций") },
                                        onValueChange = { str ->
                                            if (str.isEmpty()) limitCount.value = ""
                                            str.toIntOrNull()?.takeIf { it >= 0 }?.let { limitCount.value = str }
                                        }
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(2f))
                        Row(
                            modifier = Modifier.fillMaxWidth(1f),
                            horizontalArrangement = Arrangement.Center
                        )
                        {
                            OutlinedButton(
                                colors = ButtonDefaults.buttonColors(if (selectedContent.value == "Книги") Color.Magenta else Color.LightGray),
                                onClick = {
                                    selectedContent.value = "Книги"
                                }
                            )
                            {
                                Text("Сборник книг")
                            }
                            Spacer(Modifier.width(30.dp))
                            OutlinedButton(
                                colors = ButtonDefaults.buttonColors(if (selectedContent.value == "Статьи") Color.Magenta else Color.LightGray),
                                onClick = {
                                    selectedContent.value = "Статьи"
                                }
                            )
                            {
                                Text("Сборник статей")
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                when(selectedContent.value)
                                {
                                    "Книги" -> informationList += BookArray(arrayName.value, try{limitCount.value.toInt()}catch(e: Exception){0})
                                    "Статьи" -> informationList += ArticleArray(arrayName.value, try{limitCount.value.toInt()}catch(e: Exception){0})
                                }
                                newElemShow.value = false
                            }
                        )
                        {
                            Text("ДОБАВИТЬ")
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            )
        }
    }
}

@Composable
fun ShowResult(informationList: SnapshotStateList<Businessable<String>>)
{
    val books = remember { informationList.filter { it is BookArray } }
    val articles = remember { informationList.filter { it is ArticleArray } }
    val groupedByBusiness = remember { informationList.groupBy { busin ->
        { str: String ->
            (str.slice(str.indexOfLast { it == ':' } + 1 ..< str.length).toDoubleOrNull()?.toInt() ?: 0)
        }.run{
            try{this(busin.business())} catch(e: Exception){0.also{ }}
        }
    }.toSortedMap()
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.Bottom
        )
        {
            Text("СБОРНИКИ КНИГ", color = Color.Green)
        }
        Column(
            modifier = Modifier.border(4.dp, color = Color.Green).padding(10.dp)
        )
        {
            books.forEach { book ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp).border(3.dp, color = Color.White).padding(5.dp)
                ){
                    ArrayElementRow(book)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.Bottom
        )
        {
            Text("СБОРНИКИ СТАТЕЙ", color = Color.Yellow)
        }
        Column(
            modifier = Modifier.border(4.dp, color = Color.Yellow).padding(10.dp)
        )
        {
            articles.forEach { article ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp).border(3.dp, color = Color.White).padding(5.dp)
                ) { ArrayElementRow(article) }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.Bottom
        )
        {
            Text("СОРТИРОВКА ПО РЕЗУЛЬТАТУ БИЗНЕС-МЕТОДА", color = Color.hsl(240f, 1f, 0.7f))
        }
        Column(
            modifier = Modifier.border(4.dp, color = Color.hsl(240f, 1f, 0.5f)).padding(10.dp)
        )
        {
            groupedByBusiness.entries.forEachIndexed { ind, bookGroup ->
                Text("Результат: ${bookGroup.key}", color = Color.Cyan)
                bookGroup.value.forEach { book -> Row(modifier = Modifier.fillMaxWidth().height(100.dp).border(3.dp, color = Color.White).padding(5.dp)){ArrayElementRow(book)} }
                if (ind < groupedByBusiness.size - 1)
                {
                    Divider(thickness = 2.dp, color = Color.White)
                    Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
}
//EndHelpComposables
//MainTasks
@Composable
fun Lab3()
{
    val options = remember{mutableMapOf<String, @Composable () -> Unit>(
        "Ввод" to {DataInput(Data.informationList)},
        "Результаты" to {ShowResult(Data.informationList)}
    )}
    var workOption = remember{ mutableStateOf("Ввод") }


    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        TopAppBar(
            backgroundColor = Color.Blue
        )
        {
            options.keys.forEach { key ->
                var iSO = remember{ MutableInteractionSource() }
                var animatedColor = animateFloatAsState(
                    targetValue = if (key == workOption.value) 1f else 0f,
                    animationSpec = tween(1000, 0, FastOutSlowInEasing)
                )
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = iSO,
                            indication = null,
                            onClick = {}
                        )
                        .clip(CutCornerShape(5.dp))
                        .linearGradient(
                            Offset(0.5f, 0f),
                            Offset(0.5f, 1f),
                            (Color.hsl(0f + animatedColor.value * 120f, 1f, 0.5f)).run{listOf(this, Color.Yellow, this)}
                        )
                        .padding(15.dp),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(key, color = Color.Black)
                }
                if (iSO.collectIsPressedAsState().value) workOption.value = key
                Spacer(Modifier.width(40.dp))
            }
        }
        Divider(thickness = 3.dp, color = Color.White)
        options[workOption.value]?.invoke()
    }
}

@Composable
fun Lab4()
{
    val saveStream = { infoList : SnapshotStateList<Businessable<String>>, saveFileName: String ->
        FileOutputStream(saveFileName).use{ file ->
            file.write(StringBuilder("").also{sb -> infoList.forEachIndexed { ind, obj ->
                when(obj::class.java)
                {
                    (BookArray::class.java) -> sb.append("b" + if (ind < infoList.size - 1) " " else "")
                    (ArticleArray::class.java) -> sb.append("a" + if (ind < infoList.size - 1) " " else "")
                }
            }
                sb.append("\n")
            }.toString().toByteArray())
            infoList.forEachIndexed { ind, infoObj ->
                StreamStatic.output(infoObj, file)
                if (ind < infoList.size - 1) file.write("\n".byteInputStream().readAllBytes())
            }
        }
    }
    val loadStream = { infoList: SnapshotStateList<Businessable<String>>, loadFileName: String ->
        infoList.clear()
        FileInputStream(loadFileName).use{file ->
            var head = ""
            while(file.read().also{head += it.toChar()} != '\n'.code);
            head = head.trimEnd('\n')

            head.split(" ").forEach {
                when(it)
                {
                    "b" -> infoList += StreamStatic.inputBookArray(file)
                    "a" -> infoList += StreamStatic.inputArticleArray(file)
                }
            }
        }
    }


    val saveText = { infoList : SnapshotStateList<Businessable<String>>, saveFileName: String ->
        FileWriter(saveFileName).use{ file ->
            file.write(StringBuilder("").also{sb -> infoList.forEachIndexed { ind, obj ->
                when(obj::class.java)
                {
                    (BookArray::class.java) -> sb.append("b" + if (ind < infoList.size - 1) " " else "")
                    (ArticleArray::class.java) -> sb.append("a" + if (ind < infoList.size - 1) " " else "")
                }
            }
                sb.append("\n")
            }.toString())
            infoList.forEachIndexed { ind, infoObj ->
                StreamStatic.write(infoObj, file)
                if (ind < infoList.size - 1) file.write("\n")
            }
        }
    }
    val loadText = { infoList : SnapshotStateList<Businessable<String>>, loadFileName: String ->
        infoList.clear()
        BufferedReader(FileReader(loadFileName)).use{file ->
            file.readLine().split(" ").forEach {
                when(it)
                {
                    "b" -> infoList += StreamStatic.readBookArray(file)
                    "a" -> infoList += StreamStatic.readArticleArray(file)
                }
            }
        }
    }


    val saveSerializable = { infoList : SnapshotStateList<Businessable<String>>, saveFileName: String ->
         ObjectOutputStream(FileOutputStream(saveFileName)).use { file ->
            file.write(StringBuilder("").also{sb -> infoList.forEachIndexed { ind, obj ->
                when(obj::class.java)
                {
                    (BookArray::class.java) -> sb.append("b" + if (ind < infoList.size - 1) " " else "")
                    (ArticleArray::class.java) -> sb.append("a" + if (ind < infoList.size - 1) " " else "")
                }
            }
                sb.append("\n")
            }.toString().toByteArray())
            infoList.forEach {
                StreamStatic.serialize(it, file)
            }
        }

    }
    val loadSerializable = { infoList : SnapshotStateList<Businessable<String>>, loadFileName: String ->
        infoList.clear()
        ObjectInputStream(FileInputStream(loadFileName)).use{ file ->
            var head = ""
            while(file.read().also{head += it.toChar()} != '\n'.code);
            head = head.trimEnd('\n')
            head.split(" ").forEach {
                infoList += StreamStatic.deserialize(file)
            }
        }

    }

    val saveFormattable = { infoList : SnapshotStateList<Businessable<String>>, saveFileName: String ->
        PrintWriter(FileWriter(saveFileName)).use{ file ->
            file.print(StringBuilder("").also{sb -> infoList.forEachIndexed { ind, obj ->
                when(obj::class.java)
                {
                    (BookArray::class.java) -> sb.append("b" + if (ind < infoList.size - 1) " " else "")
                    (ArticleArray::class.java) -> sb.append("a" + if (ind < infoList.size - 1) " " else "")
                }
            }
                sb.append("\n")
            }.toString())
            infoList.forEachIndexed { ind, infoObj ->
                if (ind < infoList.size - 1) infoObj.println(file)
                else infoObj.print(file)
            }
        }
    }
    val loadFormattable = { infoList : SnapshotStateList<Businessable<String>>, loadFileName: String ->
        infoList.clear()
        Scanner(FileReader(loadFileName)).use{ file ->
            file.nextLine().split(" ").forEach {
                when(it)
                {
                    "b" -> infoList += StreamStatic.readFormatBookArray(file)
                    "a" -> infoList += StreamStatic.readFormatArticleArray(file)
                }
            }
        }
    }
    @Composable
    fun FileSaveLoad(informationList: SnapshotStateList<Businessable<String>>, onSave: (SnapshotStateList<Businessable<String>>, String) -> Unit, onLoad: (SnapshotStateList<Businessable<String>>, String) -> Unit)
    {
        Column(
            modifier = Modifier.fillMaxSize()
        )
        {
            Spacer(Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth())
            {
                Spacer(Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    var show = remember{ mutableStateOf(false) }
                    var path = remember { mutableStateOf("") }
                    Row()
                    {
                        TextField(
                            modifier = Modifier.linearGradient(Offset(1f, 1f), Offset(0.5f, 0f), listOf(Color.hsl(350f, 0.9f, 0.4f), Color.Magenta, Color.Blue)),
                            value = path.value,
                            label = { Text("Путь к файлу", color = Color.White) },
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.White),
                            onValueChange = { newStr ->
                                path.value = newStr
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        OutlinedButton(
                            onClick = {
                                show.value = true
                            }
                        )
                        {
                            Text("↑\nВыбрать", textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .clickable {
                                try {
                                    onLoad(informationList, path.value)
                                }
                                catch (e: Exception)
                                {

                                }
                            }
                            .clip(RoundedCornerShape(5.dp))
                            .linearGradient(Offset(0f, 0f), Offset(1f, 0.3f), listOf(Color.Red, Color.Yellow))
                            .padding(10.dp)
                    )
                    {
                        Text("Загрузить из файла", color = Color.hsl(300f, 1f, 0.3f))
                    }
                    FilePicker(
                        show = show.value,
                        fileExtension = "png,txt",
                        onFileSelected = {str ->
                            path.value = str ?: ""
                            show.value = false
                        }
                    )
                }
                Spacer(Modifier.weight(2f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    var show = remember{ mutableStateOf(false) }
                    var path = remember { mutableStateOf("") }
                    Row()
                    {
                        TextField(
                            modifier = Modifier.linearGradient(Offset(1f, 1f), Offset(0.5f, 0f), listOf(Color.hsl(350f, 0.9f, 0.4f), Color.Magenta, Color.Blue)),
                            value = path.value,
                            label = { Text("Путь к файлу", color = Color.White) },
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.White),
                            onValueChange = { newStr ->
                                path.value = newStr
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        OutlinedButton(
                            onClick = {
                                show.value = true
                            }
                        )
                        {
                            Text("↑\nВыбрать", textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .clickable {
                                try {
                                    onSave(informationList, path.value)
                                } catch (e: Exception) {

                                }
                            }
                            .clip(RoundedCornerShape(5.dp))
                            .linearGradient(Offset(0f, 0f), Offset(1f, 0.3f), listOf(Color.Red, Color.Yellow))
                            .padding(10.dp)
                    )
                    {
                        Text("Сохранить в файл", color = Color.hsl(300f, 1f, 0.3f))
                    }
                    FilePicker(
                        show = show.value,
                        fileExtension = "txt,png",
                        onFileSelected = {str ->
                            path.value = str ?: ""
                            show.value = false
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
        }
    }



    val options = remember{mutableMapOf<String, @Composable () -> Unit>(
        "Ввод" to {DataInput(Data.informationList)},
        "Байтовые потоки" to {FileSaveLoad(Data.informationList, saveStream, loadStream)},
        "Текстовые потоки" to {FileSaveLoad(Data.informationList, saveText, loadText)},
        "Сериализация" to {FileSaveLoad(Data.informationList, saveSerializable, loadSerializable)},
        "Форматные потоки" to {FileSaveLoad(Data.informationList, saveFormattable, loadFormattable)}
    )}
    var workOption = remember{ mutableStateOf("Ввод") }


    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        TopAppBar(
            backgroundColor = Color.Blue
        )
        {
            options.keys.forEach { key ->
                var iSO = remember{ MutableInteractionSource() }
                var animatedColor = animateFloatAsState(
                    targetValue = if (key == workOption.value) 1f else 0f,
                    animationSpec = tween(1000, 0, FastOutSlowInEasing)
                )
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = iSO,
                            indication = null,
                            onClick = {}
                        )
                        .clip(CutCornerShape(5.dp))
                        .linearGradient(
                            Offset(0.5f, 0f),
                            Offset(0.5f, 1f),
                            (Color.hsl(0f + animatedColor.value * 120f, 1f, 0.5f)).run{listOf(this, Color.Yellow, this)}
                        )
                        .padding(15.dp),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(key, color = Color.Black)
                }
                if (iSO.collectIsPressedAsState().value) workOption.value = key
                Spacer(Modifier.width(40.dp))
            }
        }
        Divider(thickness = 3.dp, color = Color.White)
        options[workOption.value]?.invoke()
    }
}

@Composable
fun Lab5()
{
    var testInfoObject = remember{ mutableStateOf(BookArray("e",0))}
    var dialogInfo = remember { mutableStateOf("") }

    var renewalState = remember { mutableStateOf(true) }
    var recomposeState = remember { mutableStateOf(true) }
    recomposeState.value

    LaunchedEffect(renewalState.value)
    {
        testInfoObject.value = BookArray("Books", 2, *Array(5){"emptyBook" to 2})
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (dialogInfo.value.isNotEmpty())
        {
            AlertDialog(
                //modifier = Modifier.fillMaxSize(0.6f),
                onDismissRequest = {
                    dialogInfo.value = ""
                },
                dismissButton = {
                },
                confirmButton = {

                },
                text = {
                    Text(dialogInfo.value, color = Color.Green)
                }
            )
        }
        Spacer(Modifier.height(40.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(3.dp, color = Color.White)
                .padding(10.dp)
        )
        {
            ArrayElementRow(testInfoObject.value)
        }
        Spacer(Modifier.height(40.dp))
        OutlinedButton(
            onClick = {
                renewalState.value = !renewalState.value
            }
        )
        {
            Text("Обнулить массив")
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth()
        )
        {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    thread {
                        runBlocking {
                            dialogInfo.value = ThreadTest.testPrioritised(testInfoObject.value, recomposeState, Thread.NORM_PRIORITY, Thread.NORM_PRIORITY)
                        }
                    }
                }
            )
            {
                Text(
                    text = "Несинхронизированная работа:\nБез приоритетов",
                    color = Color.hsl(30f, 0.8f, 0.4f),
                    textAlign = TextAlign.Center
                )
            }

            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    thread {
                        runBlocking {
                            dialogInfo.value = ThreadTest.testPrioritised(testInfoObject.value, recomposeState, Thread.MAX_PRIORITY, Thread.NORM_PRIORITY)
                        }
                    }
                }
            )
            {
                Text(
                    text = "Несинхронизированная работа:\nПриоритет заполнения",
                    color = Color.hsl(30f, 0.8f, 0.4f),
                    textAlign = TextAlign.Center
                )
            }

            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    thread {
                        runBlocking {
                            dialogInfo.value = ThreadTest.testPrioritised(testInfoObject.value, recomposeState, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY)
                        }
                    }
                }
            )
            {
                Text(
                    text = "Несинхронизированная работа:\nПриоритет считывания",
                    color = Color.hsl(30f, 0.8f, 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    thread {
                        runBlocking {
                            dialogInfo.value = ThreadTest.synchronisedBlockTest(testInfoObject.value, recomposeState, Thread.NORM_PRIORITY, Thread.NORM_PRIORITY)
                        }
                    }
                }
            )
            {
                Text(
                    text = "Синхронизированная работа:\nБлок",
                    color = Color.hsl(340f, 0.8f, 0.4f),
                    textAlign = TextAlign.Center
                )
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    thread {
                        runBlocking {
                            dialogInfo.value = ThreadTest.synchronisedMethodTest(testInfoObject.value, recomposeState, Thread.NORM_PRIORITY, Thread.NORM_PRIORITY)
                        }
                    }
                }
            )
            {
                Text(
                    text = "Синхронизированная работа:\nМетод",
                    color = Color.hsl(340f, 0.8f, 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}
//EndMainTasks

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App() {
    data class QuestInfo(val done: Boolean?, var func: @Composable () -> Unit)
    val questions = remember{ mapOf<String, QuestInfo>(
        "Лаб 1" to QuestInfo(true, {}),
        "Пусто" to QuestInfo(false, {}),
        "Лаб 3" to QuestInfo(true, {Lab3()}),
        "Лаб 4" to QuestInfo(true, {Lab4()}),
        "Лаб 5" to QuestInfo(true, {Lab5()}),

    ) }
    var selectedQuestion = remember{mutableStateOf("Лаб 3")}
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .linearGradient(Offset(0f, 0f), Offset(1f, 1f), listOf(Color.Black, Color.DarkGray)),
        )
        {
            try{questions[selectedQuestion.value]!!.func} catch(e: Exception){{}}
                .invoke() // fun move through ?.
        }
        Divider(thickness = 2.dp, color = Color.White)
        Row(
            modifier = Modifier
                .height(150.dp)
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
            //backgroundColor = Color.Black
        )
        {
            Spacer(Modifier.weight(1f))
            questions.entries.forEachIndexed { ind, it ->
                var animatedState = animateFloatAsState(if (it.key == selectedQuestion.value) 1f else 0f)
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedQuestion.value = it.key
                        }
                        .clip(CircleShape)
                        .size(100.dp, 100.dp)
                        .questRadialGradient(
                            when(it.value.done){
                                null -> Color.White
                                true -> Color.Green
                                else -> Color.Red
                            },
                            Color.hsl(180f, 1f, 0.1f + animatedState.value * 0.4f)
                        ),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(modifier = Modifier.scale(1.3f), text = it.key, color = if (it.key == selectedQuestion.value) Color.hsl(120f, 1f, 0.1f) else Color.hsl(60f, 1f, 0.1f))
                }
                if (ind < questions.size - 1)Spacer(Modifier.weight(1f).width(30.dp))
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

fun main() = application {
    Thread {
//        SingletonSync.getInstance().apply {
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread {
//                this.add()
//            }.start()
//            Thread.sleep(10000)
//            Thread {
//                this.remove()
//            }.start()
//            Thread {
//                this.remove()
//            }.start()
//            Thread {
//                this.remove()
//            }.start()
//            Thread {
//                this.remove()
//            }.start()
//        }
//    }.start()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Лабораторные по java"
    ) {
        App()
    }
}

