package com.pdf.semantic.data.datasource

import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

fun <T> Box<T>.observeById(id: Long): Flow<T?> =
    callbackFlow {
        val observer =
            DataObserver<Class<T>> { _ ->
                trySendBlocking(get(id))
            }

        val subscription: DataSubscription =
            store
                .subscribe(entityClass)
                .observer(observer)

        trySendBlocking(get(id))

        awaitClose {
            subscription.cancel()
        }
    }.conflate()

fun <T> Query<T>.asFlow(box: Box<T>): Flow<List<T>> =
    callbackFlow {
        val observer =
            DataObserver<Class<T>> { _ ->
                trySendBlocking(find())
            }

        val subscription: DataSubscription =
            box.store
                .subscribe(box.entityClass)
                .observer(observer)

        trySendBlocking(find())

        awaitClose {
            subscription.cancel()
        }
    }.conflate()
