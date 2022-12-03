package org.clevercastle.kotwarden.web.service

import org.clevercastle.kotwarden.model.TwoFactor
import org.clevercastle.kotwarden.util.TWO_FACTOR_PREFIX
import org.clevercastle.kotwarden.util.convert
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

interface ITwoFactorRepository {
    fun listByUserId(userId: String): List<TwoFactor>
}


class TwoFactorRepository(private val client: DynamoDbEnhancedClient) : ITwoFactorRepository {
    private val schema = TableSchema.fromBean(TwoFactor::class.java)
    private val table = client.table(TwoFactor.TABLE_NAME, schema)

    override fun listByUserId(userId: String): List<TwoFactor> {
        val query =
            QueryConditional.sortBeginsWith(Key.builder().partitionValue(userId).sortValue(TWO_FACTOR_PREFIX).build())
        return convert(table.query(query))
    }
}


class TwoFactorService(private val twoFactorRepository: ITwoFactorRepository) {
    fun getByUser(userId: String): List<TwoFactor> {
        return twoFactorRepository.listByUserId(userId)
    }
}
