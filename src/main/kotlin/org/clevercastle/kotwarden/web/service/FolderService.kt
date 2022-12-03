package org.clevercastle.kotwarden.web.service

import org.clevercastle.kotwarden.model.Folder
import org.clevercastle.kotwarden.util.FOLDER_PREFIX
import org.clevercastle.kotwarden.util.convert
import org.clevercastle.kotwarden.web.model.FolderRequestModel
import org.clevercastle.kotwarden.web.model.FolderResponseModel
import org.clevercastle.kotwarden.web.model.KotwardenPrincipal
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.time.OffsetDateTime
import java.util.UUID

interface IFolderRepository {
    fun findById(id: String, userId: String): Folder?
    fun listByUser(userId: String): List<Folder>
    fun save(folder: Folder)
    fun updateFolder(id: String, userId: String, folder: Folder): Folder?
    fun deleteById(id: String, userId: String): Folder?
}


class FolderRepository(private val client: DynamoDbEnhancedClient) : IFolderRepository {
    private val schema = TableSchema.fromBean(Folder::class.java)
    private val table = client.table(Folder.TABLE_NAME, schema)

    override fun findById(id: String, userId: String): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        val folder = table.getItem(key)
        if (folder == null) {
            return null
        } else {
            if (folder.userId != userId) {
                return null
            }
            return folder
        }
    }

    override fun listByUser(userId: String): List<Folder> {
        val queryConditional =
            QueryConditional.sortBeginsWith(Key.builder().partitionValue(userId).sortValue(FOLDER_PREFIX).build());
        val iter = table.query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build())
        return convert(iter)
    }

    override fun deleteById(id: String, userId: String): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        return table.deleteItem(key)
    }

    override fun updateFolder(id: String, userId: String, folder: Folder): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        val data: Folder = table.getItem { r -> r.key(key) } ?: return null
        data.name = folder.name
        return table.updateItem { r ->
            r.item(data)
        }
    }

    override fun save(folder: Folder) {
        return table.putItem(folder)
    }
}

class FolderService(
    private val folderRepository: IFolderRepository,
    private val cipherRepository: ICipherRepository
) {
    fun createFolder(principal: KotwardenPrincipal, request: FolderRequestModel): Folder {
        val folder = Folder()
        folder.name = request.name
        folder.id = FOLDER_PREFIX + UUID.randomUUID().toString()
        folder.userId = principal.id
        folder.createdAt = OffsetDateTime.now()
        folder.updatedAt = OffsetDateTime.now()
        folder.userId = principal.id
        folderRepository.save(folder)
        return folder
    }

    fun deleteFolder(principal: KotwardenPrincipal, id: String) {
        cipherRepository.updateFolderCiphersToNull(id)
        folderRepository.deleteById(id, principal.id) ?: error("Fail to find the folder")
    }

    fun updateFolder(principal: KotwardenPrincipal, id: String, request: FolderRequestModel): FolderResponseModel {
        val folder = Folder()
        folder.name = request.name
        val res = folderRepository.updateFolder(id, principal.id, folder) ?: error("Fail to find the folder")
        return Folder.converter.toFolderResponse(res)
    }

    fun findById(id: String, userId: String): Folder? {
        return folderRepository.findById(id, userId)
    }

    fun listByUser(userId: String): List<Folder> {
        return folderRepository.listByUser(userId)
    }
}