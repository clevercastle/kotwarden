package org.clevercastle.kotwarden.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import io.ktor.util.hex
import org.clevercastle.kotwarden.Config
import org.clevercastle.kotwarden.web.controller.AccountController
import org.clevercastle.kotwarden.web.controller.CipherController
import org.clevercastle.kotwarden.web.controller.FolderController
import org.clevercastle.kotwarden.web.controller.IdentityController
import org.clevercastle.kotwarden.web.controller.OrganizationController
import org.clevercastle.kotwarden.web.controller.SyncController
import org.clevercastle.kotwarden.web.controller.TwoFactorController

fun Routing.health() {
    route("api") {
        get("health") {
            this.context.respond(HttpStatusCode.OK, "OK")
        }
        get("info") {
            this.context.respond(
                HttpStatusCode.OK, mapOf(
                    "publicKey" to hex(Config.getPublicKey().encoded),
                    "privateKey" to hex(Config.getPrivateKey().encoded),
                    "corsHost" to Config.config.corsHost,
                    "signUpAllowed" to Config.isSignupAllowed("").toString(),
                    "kdfIterations" to Config.config.kdfIterations.toString(),
                    "defaultValidity" to Config.config.defaultValidityHours.toString(),
                )
            )
        }
    }
}

fun Routing.icon() {
    get("icons/{domain}/icon.png") {
        val domain = this.context.parameters.getOrFail<String>("domain")
        call.respondRedirect("https://icons.bitwarden.net/${domain}/icon.png")
    }
}

fun Routing.account(accountController: AccountController) {
    route("api/accounts") {
        post("register") {
            accountController.register(this.context)
        }
        post("prelogin") {
            accountController.preLogin(this.context)
        }

    }
    authenticate("auth-jwt") {
        route("api/accounts") {
            get("profile") {
                accountController.profile(this.context)
            }
            post("kdf") {
                accountController.updateKdf(this.context)
            }
        }
    }
}

fun Routing.twofa(twoFactorController: TwoFactorController) {
    authenticate("auth-jwt") {
        route("api") {
            get("/two-factor") {
                twoFactorController.twoFactor(this.context)
            }
        }
    }
}

fun Routing.identity(
    identityController: IdentityController,
    accountController: AccountController
) {
    route("identity") {
        post("connect/token") {
            identityController.login(this.context)
        }
        post("accounts/prelogin") {
            accountController.preLogin(this.context)
        }
    }
}

fun Routing.sync(syncController: SyncController) {
    authenticate("auth-jwt") {
        get("api/sync") {
            syncController.sync(this.context)
        }
    }
}

fun Routing.cipher(cipherController: CipherController, organizationController: OrganizationController) {

    authenticate("auth-jwt") {
        route("api/ciphers") {
            // Called when creating a new user-owned cipher.
            post("") {
                cipherController.createCipher(this.context)
            }

            post("create") {
                // Called when creating a new org-owned cipher, or cloning a cipher (whether
                // user- or org-owned). When cloning a cipher to a user-owned cipher,
                // `organizationId` is null.
                cipherController.createCipherRequest(this.context)
            }
            post("/admin") {
                cipherController.createCipherRequest(this.context)
            }
            post("{id}/share") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.shareCipher(id, this.context)
            }
            put("{id}/share") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.shareCipher(id, this.context)
            }
            get("{id}/admin") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.getCipher(id, this.context)
            }
            put("{id}/collections-admin") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateCipherCollections(id, this.context)
            }
            put("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateCipherCollections(id, this.context)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.updateCipher(this.context, id)
            }
            post("import") {
                cipherController.importCiphers(this.context)
            }
            put("{id}/delete") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.deleteCipher(this.context, id)
            }
            put("delete") {
                cipherController.deleteCiphers(this.context)
            }
            get("organization-details") {
                val parameters = this.context.request.queryParameters
                val organizationId = parameters.getOrFail<String>("organizationId")
                organizationController.listOrganizationDetail(organizationId, this.context)
            }
            post("purge") {
                cipherController.purge(this.context)
            }
        }
    }
}

fun Routing.folder(folderController: FolderController) {
    authenticate("auth-jwt") {
        route("api/folders") {
            post("") {
                folderController.createFolder(this.context)
            }
            delete("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                folderController.deleteFolder(this.context, id)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                folderController.updateFolder(this.context, id)
            }
            get("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                TODO()
            }
        }
    }
}

fun Routing.organization(organizationController: OrganizationController) {
    authenticate("auth-jwt") {
        route("api/collections") {
            get("") {
                organizationController.listCollectionsByUser(this.context)
            }
        }
        route("api/plans") {
            get("") {
                organizationController.getPlans(this.context)
            }
            get("/") {
                organizationController.getPlans(this.context)
            }

        }
        route("api/organizations") {
            post("") {
                organizationController.createOrganization(this.context)
            }
            get("") {
                organizationController.listOrganizations(this.context)
            }
            get("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.getOrganization(id, this.context)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateOrganization(id, this.context)
            }

            get("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.listCollectionsByOrganization(id, this.context)
            }
            post("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.createCollection(id, this.context)
            }
            get("{id}/users") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.listUsers(id, this.context)
            }
        }
    }
}