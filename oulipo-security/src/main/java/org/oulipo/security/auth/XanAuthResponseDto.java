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
package org.oulipo.security.auth;

import java.security.SignatureException;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.oulipo.net.TumblerAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class XanAuthResponseDto {

	public String publicKey;

	public String masterToken;

	public long expiresIn;

	public String message;

	public int code;

	public TumblerAddress address;

	@JsonIgnore
	public boolean isAuthorized;

	public XanAuthResponseDto() {
	}

	public XanAuthResponseDto(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public XanAuthResponseDto(int code, String message, TumblerAddress address) {
		this.code = code;
		this.message = message;
		this.address = address;
	}

	@Override
	public String toString() {
		return "XanAuthResponseDto [publicKey=" + publicKey + ", masterToken="
				+ masterToken + ", expiresIn=" + expiresIn + ", message="
				+ message + ", code=" + code + "]";
	}

	public static XanAuthResponseDto createUsed() {
		return new XanAuthResponseDto(XanAuthResponseCodes.INVALID_TOKEN,
				"JIT has been used");
	}

	public static XanAuthResponseDto create(String publicKey, String message,
			String signature) {
		try {
			if (!publicKey.equals(ECKey.signedMessageToKey(message, signature)
					.toAddress(MainNetParams.get()).toString())) {
				return new XanAuthResponseDto(
						XanAuthResponseCodes.INVALID_ADDRESS,
						"Signature is incorrect");
			}
		} catch (SignatureException e) {
			e.printStackTrace();
			return new XanAuthResponseDto(
					XanAuthResponseCodes.INVALID_SIGNATURE, e.getMessage());
		}
		XanAuthResponseDto response = new XanAuthResponseDto();
		response.expiresIn = -1;
		response.isAuthorized = true;
		response.publicKey = publicKey;
		return response;
	}

}
