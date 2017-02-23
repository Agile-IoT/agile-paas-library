/**
 * Copyright 2016 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.paas;

/**
 * Exception that encapsulates exceptions thrown by provider clients (e.g., CF client) and 
 * indicates an unexpected error from the provider.
 */
public class PaasProviderException extends PaasException {
    private static final long serialVersionUID = 1L;

    public PaasProviderException() {
    }

    public PaasProviderException(String msg) {
        super(msg);
    }

    public PaasProviderException(String msg, Throwable cause) {
        super(msg, cause);
    }

}