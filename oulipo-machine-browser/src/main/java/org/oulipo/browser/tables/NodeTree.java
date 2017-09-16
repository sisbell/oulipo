/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.browser.tables;

import org.oulipo.resources.model.Node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NodeTree extends ThingTreeObject<NodeTree> {

	public final BooleanProperty canCreateUsers;

	public final StringProperty name;

	public final StringProperty publicKey;

	public NodeTree(Node node) {
		super(node);
		this.publicKey = new SimpleStringProperty(node.publicKey);
		this.name = new SimpleStringProperty(node.nodeName);
		this.canCreateUsers = new SimpleBooleanProperty(node.allowUserToCreateAccount);
	}
}
