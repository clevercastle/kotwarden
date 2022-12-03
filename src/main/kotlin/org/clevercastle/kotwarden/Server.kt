@file:JvmName(name = "Server")

package org.clevercastle.kotwarden

import io.ktor.server.application.*
import org.clevercastle.kotless.KotlessAWS
import org.clevercastle.kotwarden.web.service.UserRepository
import org.clevercastle.main
import org.kodein.di.instance
import org.slf4j.LoggerFactory

@Suppress("unused")
class Server : KotlessAWS() {
    /**
     * lambda aws sdk client warm up
     * since aws sdk v2 dynamodb client first request is too slow
     */
    companion object {
        val log = LoggerFactory.getLogger(Server::class.java)
    }

    init {
        val t1 = System.currentTimeMillis()
        val repo by ModuleConfig.kodein.instance<UserRepository>()
        repo.findByEmail("sample@example.com")
        log.info("dynamodb first query costs {}", System.currentTimeMillis() - t1)
    }

    override fun prepare(app: Application) {
        app.main()
    }
}
