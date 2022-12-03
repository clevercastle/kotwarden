@file:JvmName(name = "CommonUtil")

package org.clevercastle.kotwarden.util

const val EMPTY_STRING = ""

fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}

fun isNotEmpty(str: String?): Boolean {
    return str != null && str.isNotEmpty()
}

fun isEmpty(data: Collection<*>?): Boolean {
    return data == null || data.isEmpty()
}

fun <S, T, V> combine(
    list1: Collection<S>,
    id1: (data: S) -> V,
    list2: Collection<T>,
    id2: (data: T) -> V
): List<Pair<S, T>> {
    val res = mutableListOf<Pair<S, T>>()
    val map = mutableMapOf<V, T>()
    list2.forEach {
        map[id2.invoke(it)] = it
    }
    list1.forEach {
        res.add(it to map[id1.invoke(it)]!!)
    }
    return res
}