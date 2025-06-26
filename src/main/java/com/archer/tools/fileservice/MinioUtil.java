package com.archer.tools.fileservice;


import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class MinioUtil {


	/**
	 * @param endpoint minio server endpoint
	 * @param accessKey minio server accessKey
	 * @param secretKey minio server secretKey
	 * @param bucket file bucket name
	 * @param object file object name
	 * */
    public static InputStream downloadMinio(String endpoint, String accessKey, String secretKey, String bucket, String object) 
    		throws IOException {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient((new OkHttpClient()).newBuilder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build())
                .build();

        try {
            GetObjectResponse res = client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .build());
            return res;
        } catch (Exception e) {
        	if(e instanceof IOException) {
        		throw (IOException) e;
        	}
            throw new IOException(e);
        } finally {
            try {
				client.close();
			} catch (Exception e) {
	        	if(e instanceof IOException) {
	        		throw (IOException) e;
	        	}
	            throw new IOException(e);
			}
        }
    }
    
	/**
	 * @param endpoint minio server endpoint
	 * @param accessKey minio server accessKey
	 * @param secretKey minio server secretKey
	 * @param bucket file bucket name
	 * @param object file object name
	 * @param fileStream file content
	 * */
    public static void uploadMinio(String endpoint, String accessKey, String secretKey, String bucket, String object, InputStream fileStream) 
    		throws IOException {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient((new OkHttpClient()).newBuilder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build())
                .build();

        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .stream(fileStream, -1, 10485760)
                            .build()
            );
            client.close();
        } catch (Exception e) {
        	if(e instanceof IOException) {
        		throw (IOException) e;
        	}
            throw new IOException(e);
        } finally {
            try {
				client.close();
			} catch (Exception e) {
	        	if(e instanceof IOException) {
	        		throw (IOException) e;
	        	}
	            throw new IOException(e);
			}
        }
    }
}
