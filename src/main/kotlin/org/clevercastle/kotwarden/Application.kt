package org.clevercastle

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.cio.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.Json
import org.clevercastle.kotwarden.Config
import org.clevercastle.kotwarden.ModuleConfig
import org.clevercastle.kotwarden.loadConfig
import org.clevercastle.kotwarden.plugins.account
import org.clevercastle.kotwarden.plugins.cipher
import org.clevercastle.kotwarden.plugins.folder
import org.clevercastle.kotwarden.plugins.health
import org.clevercastle.kotwarden.plugins.icon
import org.clevercastle.kotwarden.plugins.identity
import org.clevercastle.kotwarden.plugins.organization
import org.clevercastle.kotwarden.plugins.sync
import org.clevercastle.kotwarden.plugins.twofa
import org.clevercastle.kotwarden.web.KotwardenException
import org.clevercastle.kotwarden.web.controller.AccountController
import org.clevercastle.kotwarden.web.controller.CipherController
import org.clevercastle.kotwarden.web.controller.FolderController
import org.clevercastle.kotwarden.web.controller.IdentityController
import org.clevercastle.kotwarden.web.controller.OrganizationController
import org.clevercastle.kotwarden.web.controller.SyncController
import org.clevercastle.kotwarden.web.controller.TwoFactorController
import org.kodein.di.instance

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.main() {
    val configType = environment.config.property("kotwarden.config.type")
    this@main.log.info("config type is {}", configType.getString())
    Config.config = loadConfig(environment.developmentMode, configType.getString().toInt(), environment.config)

    val accountController by ModuleConfig.kodein.instance<AccountController>()
    val identityController by ModuleConfig.kodein.instance<IdentityController>()
    val syncController by ModuleConfig.kodein.instance<SyncController>()
    val cipherController by ModuleConfig.kodein.instance<CipherController>()
    val folderController by ModuleConfig.kodein.instance<FolderController>()
    val organizationController by ModuleConfig.kodein.instance<OrganizationController>()
    val twoFactorController by ModuleConfig.kodein.instance<TwoFactorController>()
    install(CORS) {
        val corsHost = Config.config.corsHost
        corsHost.split(";").forEach {
            val splits = it.split("://")
            if (it == "*") {
                anyHost()
            }
            if (splits.isNotEmpty()) {
                when (splits.size) {
                    1 -> {
                        allowHost(splits[0], schemes = listOf("https"))
                    }

                    2 -> {
                        allowHost(splits[1], schemes = listOf(splits[0]))
                    }

                    else -> {
                        error("Fail to add cors host: $it")
                    }
                }
            }
        }
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        listOf("auth-email", "bitwarden-client-name", "bitwarden-client-version", "device-type", "pragma").forEach {
            allowHeader(it)
        }
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.CacheControl)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is KotwardenException) {
                this@main.log.warn("kotwarden exception", cause)
                call.respondText(text = cause.message ?: "", status = cause.httpCode)
            } else {
                this@main.log.error("inner error", cause)
                call.respondText(text = cause.message ?: "", status = HttpStatusCode.InternalServerError)
                throw cause
            }
        }
    }
    install(Routing) {
        health()
        icon()
        account(accountController)
        identity(identityController)
        sync(syncController)
        cipher(cipherController, organizationController)
        folder(folderController)
        organization(organizationController)
        twofa(twoFactorController)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        })
    }
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.RSA256(Config.getPublicKey(), Config.getPrivateKey()))
                    .withAudience(Config.config.jwtAudience).withIssuer(Config.config.jwtIssuer).build()
            )
            validate { credentials ->
                if (credentials.payload.getClaim("id").asString() != "") {
                    JWTPrincipal(credentials.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

    }
}