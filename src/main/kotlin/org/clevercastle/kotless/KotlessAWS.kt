package org.clevercastle.kotless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


/**
 * Entrypoint of Kotless application written with Ktor DSL.
 *
 * Override [prepare] method and setup your application
 */
@Suppress("unused")
abstract class KotlessAWS : RequestStreamHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(KotlessAWS::class.java)

        private var prepared = false
        private val applicationEnvironment = commandLineEnvironment(arrayOf())
        val engine = KotlessEngine(applicationEnvironment)
    }

    abstract fun prepare(app: Application)

    override fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context?) {
        logger.info("start handle request")
        val startTime = System.currentTimeMillis()
        if (!prepared) {
            engine.start(true)
            prepared = true
            logger.info("not prepared, prepare costs {}", System.currentTimeMillis() - startTime)
        } else {
            logger.info("prepared, start costs {}", System.currentTimeMillis() - startTime)
        }

        val response = try {
            runBlocking {
                val json = input.bufferedReader().use { it.readText() }

                logger.info("Started handling request")
                logger.debug("Request is {}", json)

                if (json.contains("Scheduled Event")) {
                    val event = JSON.parse(CloudWatch.serializer(), json)
                    if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                        logger.info("Request is Scheduled Event")
                        try {
                            engine.environment.monitor.raise(LambdaWarming, engine.application)
                        } catch (e: Throwable) {
                            logger.error("One or more of the LambdaWarming handlers thrown an exception", e)
                        }
                        return@runBlocking null
                    }
                }
                val request = JSON.parse(AwsHttpApiRequest.serializer(), json)
                val call = KotlessCall(engine.application, request.toRequest())

                engine.pipeline.execute(call)

                call.response.toHttp()
            }
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            null
        }

        if (response != null) {
            output.write(JSON.bytes(HttpResponse.serializer(), response))
        } else {
            logger.info("Got null response")
        }

        logger.info("Ended handling request")
    }
}
