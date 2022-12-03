package org.clevercastle.kotwarden.web.service

import org.clevercastle.kotwarden.model.CollectionCipher
import org.clevercastle.kotwarden.model.VaultCollection
import org.clevercastle.kotwarden.util.COLLECTION_PREFIX
import org.clevercastle.kotwarden.util.convert
import org.clevercastle.kotwarden.web.kError
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactDeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import java.time.OffsetDateTime

interface IVaultCollectionRepository {
    fun save(vaultCollection: VaultCollection)
    fun listByOrganization(organizationId: String): List<VaultCollection>
    fun listByCollectionIds(collectionIds: List<String>): List<VaultCollection>
}


interface ICollectionCipherRepository {
    fun save(collectionCipher: CollectionCipher)

    fun updateCipherCollections(cipherId: String, collectionIds: List<String>)

    fun listByCipherId(cipherId: String): List<CollectionCipher>

    fun listByCollectionId(collectionId: String): List<CollectionCipher>

    fun listByCollectionIds(collectionIds: List<String>): List<CollectionCipher>

}

class VaultCollectionRepository(private val client: DynamoDbEnhancedClient) : IVaultCollectionRepository {
    private val schema = TableSchema.fromBean(VaultCollection::class.java)
    private val table = client.table(VaultCollection.TABLE_NAME, schema)
    private val idx = table.index(VaultCollection.SK_INDEX)

    override fun save(vaultCollection: VaultCollection) {
        table.putItem(vaultCollection)
    }

    override fun listByOrganization(organizationId: String): List<VaultCollection> {
        val queryConditional =
            QueryConditional.sortBeginsWith(
                Key.builder().partitionValue(organizationId).sortValue(COLLECTION_PREFIX).build()
            );
        val iter = table.query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build())
        return convert(iter)
    }

    override fun listByCollectionIds(collectionIds: List<String>): List<VaultCollection> {
        val list = mutableListOf<VaultCollection>()

        collectionIds.forEach {
            // TODO: 2022/7/23 sort value should start with cipher- or organization-
            val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(it).build())
            val resp = idx.query(
                QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
            )
            val tmpList = convert(resp)
            if (tmpList.size > 1) {
                kError("Duplicate collection with same collection id")
            }
            if (tmpList.isEmpty()) {
                kError("Fail to find collection by id: $it")
            }
            list.add(tmpList[0])
        }
        return list
    }
}

class CollectionCipherRepository(private val client: DynamoDbEnhancedClient) : ICollectionCipherRepository {
    private val schema = TableSchema.fromBean(CollectionCipher::class.java)
    private val table = client.table(CollectionCipher.TABLE_NAME, schema)
    private val index = table.index(CollectionCipher.REVERSE_INDEX)

    override fun save(collectionCipher: CollectionCipher) {
        table.putItem(collectionCipher)
    }

    override fun updateCipherCollections(cipherId: String, collectionIds: List<String>) {
        // transaction size limit ??
        val list = listByCipherId(cipherId)
        if (list.isEmpty() && collectionIds.isEmpty()) {
            return
        }
        val intersectionList = list.map { it.collectionId }.intersect(collectionIds.toSet())
        val transactionBuilder = TransactWriteItemsEnhancedRequest.builder()
        if (intersectionList.size == list.size && intersectionList.size == collectionIds.size) {
            return
        }
        list.map { it.collectionId }.minus(intersectionList).forEach {
            transactionBuilder.addDeleteItem(
                table, TransactDeleteItemEnhancedRequest.builder()
                    .key(Key.builder().partitionValue(it).sortValue(cipherId).build()).build()
            )
        }
        collectionIds.minus(intersectionList).forEach {
            val item = CollectionCipher()
            item.cipherId = cipherId
            item.collectionId = it
            item.createdAt = OffsetDateTime.now()
            item.updatedAt = OffsetDateTime.now()
            transactionBuilder.addPutItem(
                table, TransactPutItemEnhancedRequest.builder(CollectionCipher::class.java)
                    .item(item).build()
            );
        }
        client.transactWriteItems(transactionBuilder.build())
    }

    override fun listByCipherId(cipherId: String): List<CollectionCipher> {
        val queryConditional = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(cipherId).build())
        val iter = index.query(queryConditional)
        return convert(iter)
    }

    override fun listByCollectionId(collectionId: String): List<CollectionCipher> {
        val queryConditional = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(collectionId).build())
        val iter = table.query(queryConditional)
        return convert(iter)
    }

    override fun listByCollectionIds(collectionIds: List<String>): List<CollectionCipher> {
        val res = mutableListOf<CollectionCipher>()
        collectionIds.forEach {
            res.addAll(listByCollectionId(it))
        }
        return res
    }
}

class CollectionService(
    private val collectionCipherRepository: ICollectionCipherRepository,
    private val userCollectionRepository: IUserCollectionRepository,
    private val collectionRepository: IVaultCollectionRepository
) {

    fun listCollectionIdsByCipher(cipherId: String): List<CollectionCipher> {
        return collectionCipherRepository.listByCipherId(cipherId)
    }
}