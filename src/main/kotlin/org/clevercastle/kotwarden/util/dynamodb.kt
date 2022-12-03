@file:JvmName(name = "DynamodbUtil")

package org.clevercastle.kotwarden.util

import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import java.util.stream.Collectors

fun <T> convert(iter: SdkIterable<Page<T>>): List<T> {
    return iter.flatMap { it.items() }.toList()
}

fun <T> convert(iter: BatchGetResultPageIterable, table: MappedTableResource<T>): List<T> {
    val list = mutableListOf<T>()
    for (p in iter) {
        list.addAll(p.resultsForTable(table))
    }
    return list
}

fun <T> convertWithoutPage(iter: SdkIterable<T>): List<T> {
    return iter.stream().collect(Collectors.toList())
}

private val MAX_DYNAMODB_BATCH_SIZE = 25;  // AWS blows chunks if you try to include more than 25

// items in a batch or sub-batch
fun <T> batchRead(
    itemType: Class<T>,
    pks: List<Key>,
    client: DynamoDbEnhancedClient, table: DynamoDbTable<T>
): List<T> {
    if (pks.isEmpty()) {
        return listOf()
    }
    val chunksOfPks: List<List<Key>> = pks.chunked(MAX_DYNAMODB_BATCH_SIZE)
    val res: MutableList<T> = ArrayList()
    for (chunksOfPk in chunksOfPks) {
        val chunkResults: List<T> = batchReadImpl(itemType, chunksOfPk, client, table)
        res.addAll(chunkResults)
    }
    return res
}

private fun <T> batchReadImpl(
    itemType: Class<T>,
    keys: List<Key>,
    client: DynamoDbEnhancedClient,
    table: DynamoDbTable<T>
): List<T> {
    val subBatchBuilder = ReadBatch.builder(itemType).mappedTableResource(table)
    for (key in keys) {
        subBatchBuilder.addGetItem(
            GetItemEnhancedRequest.builder()
                .key(key)
                .build()
        )
    }
    val overallBatchBuilder = BatchGetItemEnhancedRequest.builder()
    overallBatchBuilder.addReadBatch(subBatchBuilder.build())
    return convertWithoutPage(client.batchGetItem(overallBatchBuilder.build()).resultsForTable(table))
}