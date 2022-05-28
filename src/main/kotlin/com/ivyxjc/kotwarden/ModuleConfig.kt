package com.ivyxjc.kotwarden

import com.ivyxjc.kotwarden.web.controller.AccountController
import com.ivyxjc.kotwarden.web.service.AccountService
import com.ivyxjc.kotwarden.web.service.UserRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

object ModuleConfig {
    private val userModule = DI.Module("userModule") {
        bindSingleton { UserRepository(instance()) }
        bindSingleton { AccountService(instance()) }
        bindSingleton { AccountController(instance()) }
    }

    private val dynamodbModule = DI.Module("dynamodb") {
        val c = DynamoDbClient.builder().region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsAccessKey,
                        awsAccessSecret
                    )
                )
            )
            .build()
        bindSingleton { DynamoDbEnhancedClient.builder().dynamoDbClient(c).build() }
    }

    internal val kodein = DI {
        import(userModule)
        import(dynamodbModule)
    }
}