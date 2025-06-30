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

    private static K8sPack getK8sPack(String configYamlText) throws IOException {
    	K8sPack k8s = k8sClients.getOrDefault(configYamlText, null);
    	if(k8s == null) {
    		ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new StringReader(configYamlText))).build();
    		CoreV1Api api = new CoreV1Api(client);
    		k8s = new K8sPack(client, api);
    		k8sClients.put(configYamlText, k8s);
    	}
    	return k8s;
    }
    
    public static ApiClient getK8sApiClient(String configYamlText) throws IOException {
    	return getK8sPack(configYamlText).client;
    }
    
    public static CoreV1Api getK8sCoreApi(String configYamlText) throws IOException {
    	return getK8sPack(configYamlText).api;
    }

    /**
     * create namespace
     * @throws ApiException 
     * */
    public static void createNamespace(String configYamlText, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configYamlText);
        if (checkNamespace(k8s.api, namespace)) {
        	return ;
        }
        V1Namespace n = new V1Namespace();
        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setName(namespace);
        meta.setNamespace(namespace);
        n.setMetadata(meta);
        k8s.api.createNamespace(n);
    }
    
    /**
     * delete namespace
     * @throws ApiException 
     * */
    public static void deleteNamespace(String configYamlText, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configYamlText);
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
    public V1NodeList listAllNode(String configYamlText) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configYamlText);
        return k8s.api.listNode().execute();
    }
    
    /**
     * list all services
     *
     * @return ServiceList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1ServiceList listAllService(String configYamlText, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configYamlText);
        return k8s.api.listNamespacedService(namespace).execute();
    }
    
    /**
     * get service
     *
     * @return ServiceList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1Service getService(String configYamlText, String namespace, String serviceName) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configYamlText);
        return k8s.api.readNamespacedService(serviceName, namespace).execute();
    }
    /**
     * list all pods
     *
     * @return PodList
     * @throws ApiException 
     * @throws IOException 
     */
    public V1PodList listAllPods(String configYamlText, String namespace) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configYamlText);
        return k8s.api.listNamespacedPod(namespace).execute();
    }
    /**
     * list all pods
     *
     * @return PodList
     * @throws ApiException 
     * @throws IOException 
     */
    public Object listAllObject(String configYamlText, String namespace, String group, String version, String plural) throws IOException, ApiException {
    	K8sPack k8s = getK8sPack(configYamlText);
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
    public static V1SecretList listAllSecrets(String configYamlText, String namespace) throws ApiException, IOException {
    	K8sPack k8s = getK8sPack(configYamlText);
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
