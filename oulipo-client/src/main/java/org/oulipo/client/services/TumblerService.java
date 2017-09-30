/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.client.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;
import org.oulipo.resources.model.VSpan;
import org.oulipo.resources.model.Virtual;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.services.responses.Network;
import org.oulipo.streams.VirtualContent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service that maps between tumbler addresses and a DocuverseService.
 */
public class TumblerService {

	private static class TumblerCallback<T> implements Callback<T> {

		private TumblerSuccess<T> success;

		public TumblerCallback(TumblerSuccess<T> success) {
			this.success = success;
		}

		@Override
		public void onFailure(Call<T> arg0, Throwable arg1) {
			arg1.printStackTrace();
		}

		@Override
		public void onResponse(Call<T> arg0, Response<T> arg1) {
			success.onSuccess(arg1);
		}

	}

	public interface TumblerFailure {

		void onError(Throwable arg1);
	}

	public interface TumblerSuccess<T> {

		void onSuccess(Response<T> thing);
	}

	private final DocuverseService service;

	public TumblerService(DocuverseService service) {
		this.service = service;
	}

	public void copy(Document document, long position, List<VSpan> spans, Callback<VirtualContent> callback)
			throws IOException {
		service.copy(document.networkId(), document.nodeId(), document.userId(), document.documentId(), "1." + position,
				spans).enqueue(callback);
	}

	public void createOrUpdateDocument(Document document, Callback<Document> callback) throws IOException {
		service.createOrUpdateDocument(document.networkId(), document.nodeId(), document.userId(),
				document.documentId(), document).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createOrUpdateDocument(Document document, TumblerSuccess callback) throws IOException {
		createOrUpdateDocument(document, new TumblerCallback<Document>(callback));
	}

	public void createOrUpdateLink(Link link, Callback<Link> callback) throws IOException {
		service.createOrUpdateLink(link.networkId(), link.nodeId(), link.userId(), link.documentId(), link.elementId(),
				link).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createOrUpdateLink(Link link, TumblerSuccess callback) throws IOException {
		createOrUpdateLink(link, new TumblerCallback<Link>(callback));
	}

	public void createOrUpdateNode(Node node, Callback<Node> callback) throws IOException {
		service.createOrUpdateNode(node.networkId(), node.nodeId(), node).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createOrUpdateNode(Node node, TumblerSuccess callback) throws IOException {
		createOrUpdateNode(node, new TumblerCallback<Node>(callback));
	}

	public void createOrUpdateUser(User user, Callback<User> callback) throws IOException {
		service.createOrUpdateUser(user.networkId(), user.nodeId(), user.userId(), user).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createOrUpdateUser(User user, TumblerSuccess callback) throws IOException {
		createOrUpdateUser(user, new TumblerCallback<User>(callback));
	}

	public void delete(Document document, VSpan range, Callback<VirtualContent> callback) throws IOException {
		String spans = "1." + range.start + "~" + range.width;
		service.delete(document.networkId(), document.nodeId(), document.userId(), document.documentId(), spans)
				.enqueue(callback);
	}

	public void getDocument(String address, Callback<Document> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getDocument(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDocument(String address, final TumblerSuccess callback) throws IOException {
		getDocument(address, new TumblerCallback<Document>(callback));
	}

	public void getDocumentLink(TumblerAddress ta, Callback<Link> callback) throws IOException {
		service.getDocumentLink(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal(),
				ta.getElement().asString()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDocumentLink(TumblerAddress address, TumblerSuccess callback) throws IOException {
		getDocumentLink(address, new TumblerCallback<Link>(callback));
	}

	public void getDocumentLinks(String address, Map<String, String> options, Callback<List<Link>> callback)
			throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getDocumentLinks(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal(), options)
				.enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDocumentLinks(String address, Map<String, String> options, TumblerSuccess callback)
			throws IOException {
		getDocumentLinks(address, options, new TumblerCallback<List<Link>>(callback));
	}

	public void getDocuments(String address, Map<String, String> options, Callback<List<Document>> callback)
			throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getDocuments(ta.networkVal(), ta.nodeVal(), ta.userVal(), options).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDocuments(String address, Map<String, String> options, TumblerSuccess callback) throws IOException {
		getDocuments(address, options, new TumblerCallback<List<Document>>(callback));
	}

	public void getEndsets(String address, Callback<EndsetByType> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getEndsets(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getEndsets(String address, final TumblerSuccess callback) throws IOException {
		getEndsets(address, new TumblerCallback<EndsetByType>(callback));
	}

	public void getNetwork(String address, Callback<Network> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getNetwork(ta.networkVal()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getNetwork(String address, final TumblerSuccess callback) throws IOException {
		getNetwork(address, new TumblerCallback<Network>(callback));
	}

	public void getNode(String address, Callback<Node> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getNode(ta.networkVal(), ta.nodeVal()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getNode(String address, final TumblerSuccess callback) throws IOException {
		getNode(address, new TumblerCallback<Node>(callback));
	}

	public void getNodes(String address, Map<String, String> options, Callback<List<Node>> callback)
			throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getNodes(ta.networkVal(), options).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getNodes(String address, Map<String, String> options, TumblerSuccess callback) throws IOException {
		getNodes(address, options, new TumblerCallback<List<Node>>(callback));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSystemDocuments(Map<String, String> options, TumblerSuccess callback) throws IOException {
		service.getSystemDocuments(options).enqueue(new TumblerCallback<List<Document>>(callback));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSystemLinks(Map<String, String> options, TumblerSuccess callback) throws IOException {
		service.getSystemLinks(options).enqueue(new TumblerCallback<List<Link>>(callback));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSystemNodes(Map<String, String> options, TumblerSuccess callback) throws IOException {
		service.getSystemNodes(options).enqueue(new TumblerCallback<List<Node>>(callback));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSystemUsers(Map<String, String> options, TumblerSuccess callback) throws IOException {
		service.getSystemUsers(options).enqueue(new TumblerCallback<List<User>>(callback));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSystemVSpans(Map<String, String> options, TumblerSuccess callback) throws IOException {
		service.getSystemVSpans(options).enqueue(new TumblerCallback<List<VSpan>>(callback));
	}

	public void getUser(String address, Callback<User> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getUser(ta.networkVal(), ta.nodeVal(), ta.userVal()).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getUser(String address, final TumblerSuccess callback) throws IOException {
		getUser(address, new TumblerCallback<User>(callback));
	}

	public void getUsers(String address, Map<String, String> options, Callback<List<User>> callback)
			throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.getUsers(ta.networkVal(), ta.nodeVal(), options).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getUsers(String address, Map<String, String> options, TumblerSuccess callback) throws IOException {
		getUsers(address, options, new TumblerCallback<List<User>>(callback));
	}

	public void getVirtual(String address, Map<String, String> options, Callback<Virtual> callback) throws IOException {
		if (options == null) {
			options = new HashMap<>();
		}
		TumblerAddress ta = TumblerAddress.create(address);
		service.getVirtual(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal(), options).enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getVirtual(String address, Map<String, String> options, TumblerSuccess callback) throws IOException {
		getVirtual(address, options, new TumblerCallback<Virtual>(callback));
	}

	public void getVSpan(TumblerAddress ta, Callback<VSpan> callback) throws IOException {
		service.getVSpan(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal(), ta.getElement().asString())
				.enqueue(callback);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getVSpan(TumblerAddress address, TumblerSuccess callback) throws IOException {
		getVSpan(address, new TumblerCallback<VSpan>(callback));
	}

	public void insert(Document document, long position, String text, Callback<VirtualContent> callback)
			throws IOException {
		service.insert(document.networkId(), document.nodeId(), document.userId(), document.documentId(),
				"1." + position, text).enqueue(callback);
	}

	public void loadOperations(String address, String operations, Callback<String> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.loadOperations(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal(), operations)
				.enqueue(callback);
	}

	public void newDocument(String address, Callback<Document> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.newDocument(ta.networkVal(), ta.nodeVal(), ta.userVal()).enqueue(callback);
	}

	public void newLink(String address, Callback<Link> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.newLink(ta.networkVal(), ta.nodeVal(), ta.userVal(), ta.documentVal()).enqueue(callback);
	}

	public void newUser(String address, Callback<User> callback) throws IOException {
		TumblerAddress ta = TumblerAddress.create(address);
		service.newUser(ta.networkVal(), ta.nodeVal()).enqueue(callback);
	}

	public void swap(Document document, VSpan from, VSpan to, Callback<VirtualContent> callback) throws IOException {
		String spans = "1." + from.start + "~" + from.width + ", " + "1." + to.start + "~" + to.width;
		service.swap(document.networkId(), document.nodeId(), document.userId(), document.documentId(), spans)
				.enqueue(callback);
	}

}
