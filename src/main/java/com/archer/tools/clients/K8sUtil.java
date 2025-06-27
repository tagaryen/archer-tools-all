package com.archer.tools.clients;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class K8sUtil {
    
    private static ConcurrentHashMap<String, K8sPack> k8sClients = new ConcurrentHashMap<>();

    private static K8sPack getK8sPack(String configStr) throws IOException {
    	K8sPack k8s = k8sClients.getOrDefault(configStr, null);
    	if(k8s == null) {
    		ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new StringReader(configStr))).build();
    		CoreV1Api api = new CoreV1Api(client);
    		k8s = new K8sPack(client, api);
    		k8sClients.put(configStr, k8s);
    	}
    	return k8s;
    }
    
    public static ApiClient getK8sApiClient(String configStr) throws IOException {
    	return getK8sPack(configStr).client;
    }
    
    public static CoreV1Api getK8sCoreApi(String configStr) throws IOException {
    	return getK8sPack(configStr).api;
    }

//    /**
//     * delete namespace
//     * @throws ApiException 
//     * */
//    public static void createNamespace(String configStr, String namespace) throws ApiException, IOException {
//    	K8sPack k8s = getK8sPack(configStr);
//        if (checkNamespace(k8s.api, namespace)) {
//        	return ;
//        }
//        V1Namespace n = new V1Namespace();
//        k8s.api.createNamespace(n);
//    }
    
    /**
     * delete namespace
     * @throws ApiException 
     * */
    public static void deleteNamespace(String configStr, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configStr);
        if (checkNamespace(k8s.api, namespace)) {
        	k8s.api.deleteNamespace(namespace).execute();
        }
    }

    /**
     * list all nodes
     *
     * @return nodeList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1NodeList listAllNode(String configStr) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configStr);
        return k8s.api.listNode().execute();
    }
    
    /**
     * list all services
     *
     * @return ServiceList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1ServiceList listAllService(String configStr, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configStr);
        return k8s.api.listNamespacedService(namespace).execute();
    }
    
    /**
     * get service
     *
     * @return ServiceList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1Service getService(String configStr, String namespace, String serviceName) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configStr);
        return k8s.api.readNamespacedService(serviceName, namespace).execute();
    }
    /**
     * list all pods
     *
     * @return PodList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1PodList listAllPods(String configStr, String namespace) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configStr);
        return k8s.api.listNamespacedPod(namespace).execute();
    }
    /**
     * list all pods
     *
     * @return PodList
     * @throws ApiException 
     * @throws IOException 
     */
    public Object listAllObject(String configStr, String namespace, String group, String version, String plural) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configStr);
        CustomObjectsApi api = new CustomObjectsApi(k8s.client);
        return api.listNamespacedCustomObject(group, version, namespace, plural).execute();
    }
    /**
     * list all secrets
     *
     * @return SecretList
     * @throws ApiException 
     * @throws IOException 
     */
    public static V1SecretList listAllSecrets(String configStr, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configStr);
        return k8s.api.listNamespacedSecret(namespace).execute();
    }
    
    private static boolean checkNamespace(CoreV1Api api, String namespace) throws ApiException {
        V1NamespaceList namespaceList = api.listNamespace().execute();
        return namespaceList.getItems().stream().anyMatch(v -> Objects.requireNonNull(Objects.requireNonNull(v.getMetadata())
                .getName()).equalsIgnoreCase(namespace));
    }
    
    private static class K8sPack {
    	ApiClient client;
    	CoreV1Api api;
		public K8sPack(ApiClient client, CoreV1Api api) {
			this.client = client;
			this.api = api;
		}
    }
}
