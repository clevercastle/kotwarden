package org.clevercastle.kotless

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.BaseApplicationEngine

/**
 * Kotless implementation of Ktor engine.
 * Optimized for serverless use-case.
 */
class KotlessEngine(environment: ApplicationEngineEnvironment) : BaseApplicationEngine(environment) {
    override fun start(wait: Boolean): ApplicationEngine {
        environment.start()
        return this
    }

    override fun stop(gracePeriodMillis: Long, timeoutMillis: Long) {
        environment.stop()
    }
}
