# Программа на JetpackCompose kotlin, выполняющая задания из Java
### Выполнены задания:
1. Динамическое заполнение данных и реализация своих интерфейсов
2. Работа с потоками ввода-вывода
    - `InputStream`, `OutputStream`
    - `Reader`, `Writer`
    - `BufferedReader`
    - `Scanner`, `PrintWriter`
    - `ObjectOutputStream`, `ObjectInputStream`

### Реализованы фичи:
1. Название методов в Java так, как называются операторы в Kotlin
    - свойства, например: `public Sting getResult(){/*...*/}` -> **в котлине можно написать** `myObject.result`
    - операторы, например: `public void plusAssing(elem)` -> **в котлине можно написать** `myObject += ...`
    - или вот так: `public void set(Integer index, Pair<String, Number> data){/*...*/}` -> **в котлине можно написать** `myObject[5] = "Book1" to 16`
2. Использование TypeProjection(wildcards) `MyClass(Pair<String, out Number>... elements)`
   Обусловлено тем, что в java в самом *generic* интерфейсе/абстрактном классе не работает type projection, нужно добавлять в методы ? extends
   
   В котлине у класса `Pair` есть projection: `class Pair<out A, out B>`, и нет нужды повторно прописывать out

## Основные скриншоты
* Просмотр элементов
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/ee0aa38f-619e-49c0-a064-4e0e72b72595"></img>
</p>

* Добавление Сборника
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/6642f506-7b41-4445-ac3a-8eabc851829c"></img>
</p>

* Добавление книги
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/0534c811-7809-4ced-a741-8dcd943db7ae"></img>
</p>

* Просмотр результатов
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/79d65db6-159d-49a0-9139-bdc78b0d1094"></img>
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/6924a4ce-4435-4dab-9337-313b9a72a003"></img>
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/d4ac0c46-475f-43f8-828a-a0d307703b46"></img>
</p>

* Тест сериализации базы и десериализации
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/a3d3ec8c-1216-4bc2-9baf-7286f6d26085"></img>
</p>

* Тест многопоточности
<p align = "center">
  <img src = "https://github.com/Zeredan/JetpackCompose-ui-for-Java-tasks/assets/165821992/e08db51d-0f0d-4612-87e4-d9cb299d43df"></img>
</p>

