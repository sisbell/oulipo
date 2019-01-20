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

import javafx.fxml.Initializable
import javafx.scene.Node

import java.io.IOException

/**
 * A [Page] has a [Page.Controller] that will show the page URL in the address bar.
 */
class Page @JvmOverloads constructor(var controller: Controller, var view: View? = null) {

    /**
     *
     */
    interface Controller : Initializable {

        @Throws(IOException::class)
        fun show(addressBarController: AddressBarController)
    }

    class View(var location: String?)

    @Throws(IOException::class)
    fun present(addressBarController: AddressBarController) {
        if (view != null) {
            val loader = addressBarController.context.getLoader()
            loader.setController(controller)
            loader.location = javaClass.getResource(view!!.location)
            val node = loader.load<Node>()
            addressBarController.addContent(node)
        }
        controller.show(addressBarController)
    }
}
