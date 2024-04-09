class TypeProjection {
}

open class Entity
{}

open class Animal : Entity(){

}
class Human : Animal(){

}
class Dog: Animal(){

}

interface MyComp<T>
{
    fun comp(a: T, b: T) : T
    {
        return a
    }
}

interface I<in T>{
    operator fun invoke(a: T) : @UnsafeVariance T
    {
        return a
    }
}

abstract class I1<out T>{
    abstract operator fun invoke(): T
}

fun<T> a(arr: Array<T>) where T: Animal
{

}
fun<T : Animal> a1(arr: Array<T>)
{

}
fun a2(arr: Array<out Animal>)
{
    arr.indexOf(Animal())
}
fun a3(lst: I1<Animal>)
{
}

fun b(arr: Array<Human>, comp: MyComp<in Human>)
{
    var res = comp.comp(arr[0], arr[1])
    arr.forEachIndexed{ ind, e -> comp.comp(arr[ind], arr[ind + 1]) }
}

fun c(i: I<Human>)
{
    var res = i.invoke(Human())
    var i1: I<Human> = i
    //var i2: I<Entity> = i
}
fun c1(i: I1<Animal>)
{
    var i1: I1<Entity> = i
    var res = i.invoke()
}

fun test()
{
    a(arrayOf<Human>(Human(), Human()))
    a1(arrayOf<Human>(Human(), Human()))
    a2(arrayOf<Human>(Human(), Human()))

    /*b(arrayOf<Human>(Human()), object : MyComp<Animal>{
        override fun comp(a: Animal, b: Animal) {
            super.comp(a, b)
        }
    })*/

    c(object : I<Animal>{})
    c1(object : I1<Human>(){
        override fun invoke(): Human {
            TODO("Not yet implemented")
        }
    })
}

