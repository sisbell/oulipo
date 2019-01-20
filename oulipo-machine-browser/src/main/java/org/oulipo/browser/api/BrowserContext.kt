/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */
package org.oulipo.browser.api

import org.oulipo.browser.api.bookmark.BookmarkManager
import org.oulipo.browser.api.history.HistoryManager
import org.oulipo.browser.api.history.HistoryRepository
import org.oulipo.browser.api.people.Account
import org.oulipo.browser.api.people.AccountManager
import org.oulipo.browser.api.people.CurrentUser
import org.oulipo.browser.api.storage.RemoteStorage
import org.oulipo.browser.api.storage.SessionStorage
import org.oulipo.browser.api.tabs.TabManager
import org.oulipo.browser.framework.StorageContext
import org.oulipo.browser.framework.impl.*
import org.oulipo.browser.framework.toolbar.ToolbarController
import com.google.common.base.Strings
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.RadioMenuItem
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.oulipo.client.services.DocuverseService
import org.oulipo.client.services.ServiceBuilder
import org.oulipo.security.keystore.FileStorage
import org.oulipo.storage.StorageException

import java.io.IOException
import java.util.ResourceBundle

/**
 * The context attached to each instance (or window) of the org.oulipo.browser. Context
 * will be different per instance.
 */
class BrowserContext
/**
 * Constructs a org.oulipo.browser context
 *
 * @param loader
 * @param storageContext
 * @param menuContext
 * @throws IOException
 * @throws StorageException
 */
@Throws(IOException::class, StorageException::class)
constructor(
    val applicationContext: ApplicationContext, private val loader: FXMLLoader, val contentArea: StackPane,
    private val storageContext: StorageContext, val menuContext: MenuContext, private val userName: Label
) {

    val accountManager: AccountManager

    private val bookmarkManager: BookmarkManagerImpl

    val currentUser: CurrentUser?

    val historyManager = HistoryManager()

    var historyRepository: HistoryRepository? = null
        private set

    private val keyStorage: FileStorage

    private val remoteStorage: IpfsRemoteStorage

    val sessionStorage: SessionStorage

    val tabManager: TabManager

    val docuverseService: DocuverseService
        @Throws(StorageException::class)
        get() {
            val account = accountManager.activeAccount ?: throw StorageException("Active account not set")
            val token = accountManager.getTokenFor(account)
            return ServiceBuilder("http://localhost:4567/docuverse/").publicKey(account.publicKey).sessionToken(token)
                .build(DocuverseService::class.java)

        }

    init {
        this.remoteStorage = IpfsRemoteStorage()
        this.sessionStorage = SessionStorage(storageContext.sessionStorage)
        this.keyStorage = FileStorage(storageContext.keystoreStorage)
        this.historyRepository = HistoryRepositoryImpl(
            menuContext.historyMenu,
            storageContext.historyStorage
        )
        this.tabManager = TabManagerImpl(this, storageContext.tabStorage, menuContext.tabs)
        this.bookmarkManager = BookmarkManagerImpl(
            menuContext.bookmarkMenu,
            storageContext.bookmarkStorage, tabManager
        )
        this.accountManager = AccountManagerImpl(
            menuContext.peopleMenu, this, sessionStorage,
            storageContext.accountsStorage, keyStorage, remoteStorage
        )
        currentUser = accountManager.currentUserAddress
        if (currentUser != null) {
            val name = if (!Strings.isNullOrEmpty(currentUser.xandle)) currentUser.xandle else currentUser.address
            userName.text = name
        }
    }

    /**
     * Closes the context and cleans up resources
     */
    fun closeContext() {
        storageContext.close()
    }

    fun getBookmarkManager(): BookmarkManager {
        return bookmarkManager
    }

    fun getLoader(): FXMLLoader {
        loader.setController(null)
        loader.location = null
        loader.setRoot(null)
        return loader
    }

    fun getRemoteStorage(): RemoteStorage {
        return remoteStorage
    }

    /**
     * Launches a new toolbar in a different window.
     *
     * @param isIncognito
     * @throws IOException
     * @throws StorageException
     */
    @Throws(IOException::class, StorageException::class)
    fun launchNewToolbar(isIncognito: Boolean, publicKey: String) {
        val stage = Stage()
        applicationContext.putStage(publicKey, stage)

        val item = RadioMenuItem()
        item.text = publicKey
        item.userData = stage
        item.isSelected = true
        menuContext.windowMenu.items.add(item)

        val loader = getLoader()
        loader.location = javaClass.getResource("/org/oulipo/browser/framework/toolbar/ToolbarView.fxml")
        loader.resources = ResourceBundle.getBundle("bundles.org.oulipo.browser")

        val browser = loader.load<Parent>()
        val scene = Scene(browser)
        val controller = loader.getController<ToolbarController>()

        if (isIncognito) {
            scene.stylesheets.clear()
            scene.userAgentStylesheet = null
            scene.stylesheets.add(javaClass.getResource("material.css").toExternalForm())
            historyRepository = DummyHistoryRepository()
            controller.setIncognitoMode()
        }

        stage.scene = scene
        stage.show()
    }

    fun ownsResource(publicKey: String): Boolean {
        return accountManager.activeAccount != null && accountManager.activeAccount.publicKey == publicKey
    }

    fun setUserName(address: String, xandle: String) {
        userName.text = xandle
        val currentUser = CurrentUser()
        currentUser.address = address
        currentUser.xandle = xandle
        try {
            accountManager.currentUserAddress = currentUser
        } catch (e: StorageException) {
            e.printStackTrace()
        }
    }

    /**
     * Shows the specified message as a snackbar (or toast)
     *
     * @param message
     * the message to show
     */
    fun showMessage(message: String) {
        Platform.runLater {
            val bar = JFXSnackbar(contentArea)
            bar.enqueue(SnackbarEvent(message))
        }
    }

    fun showToolbar(id: String) {
        val stage = applicationContext.getStage(id)
        stage?.show()
    }
}
