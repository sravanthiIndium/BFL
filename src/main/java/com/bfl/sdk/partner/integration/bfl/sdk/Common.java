package com.bfl.sdk.partner.integration.bfl.sdk;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class Common {
    public List<PartnerInput> setData = new ArrayList<>();
    public BFLConfigData bFLConfigData = new BFLConfigData();
    public String currentApiName;
    public String currentApiUrl;
    public String supplierId;
    public String sealValue;
    public List<Specs> specs;
    public void setValueHelper(String jsonTag, String setval) {
                PartnerInput pI = new PartnerInput();
                pI.setJsonTag(jsonTag);
                pI.setValue(setval);
                var optionalPartnerInput = setData.stream()
                        .filter(m -> m.getJsonTag().equals(pI.getJsonTag()))
                        .findFirst();
                if (optionalPartnerInput.isPresent()) //Add only latest value for matching
                    optionalPartnerInput.get().setValue(pI.getValue());
                else
                    setData.add(pI);
    }

    public void loadSpecs() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = "";
        ClassPathResource classPathResource = new ClassPathResource(currentApiName + "Request.json");
        try {
            byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            jsonData = new String(binaryData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            SpecsMaster specsMaster = objectMapper.readValue(jsonData, SpecsMaster.class);
            specs = specsMaster.getRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Read the specification file
        //Load it into cSpecs if is empty
    }

    public List<Specs> getCSpecs() {
        if (specs == null)
            loadSpecs();
        return specs;

    }

    public String getRequestBody() {
        StringBuilder requestBuild = new StringBuilder();
        String requestBody = "";
        int totCount = setData.size();
        int counter = 0;
        for (var jsonData : setData) {
            counter++;
            requestBuild.append("\"").append(jsonData.getJsonTag()).append("\":\"").append(jsonData.getValue()).append("\"");
            if (counter < totCount) {
                requestBuild.append(",");
            }
            requestBody = "{" + requestBuild.toString() + "}";
        }
            return requestBody;
    }
    public BFLConfigData loadBFLConfigData()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = "";
        ClassPathResource classPathResource = new ClassPathResource("BFLAppConfig.json");
        try {
            byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            jsonData = new String(binaryData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            bFLConfigData = objectMapper.readValue(jsonData, BFLConfigData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bFLConfigData;
        //Read the specification file
        //Load it into cSpecs if is empty
    }
    public String apiRequest(String parameter) {

        String content = null;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });

            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSF).build();
            HttpPost httpPost = new HttpPost(currentApiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("SealValue", sealValue);
            httpPost.setHeader("SupplierID", supplierId);
            StringEntity entity = new StringEntity(
                    parameter,
                    ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // EntityUtils to get the response content
                content = EntityUtils.toString(respEntity);
            }
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e){
            // writing exception to log
            e.printStackTrace();
        }
        return content;
    }

}
