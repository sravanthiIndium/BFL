package com.bfl.sdk.partner.integration.bfl.sdk;

import java.util.ArrayList;
import java.util.List;

public class BFLControllerBase {
    private final List<Validity> validityList = new ArrayList<>();
    public BFLConfigData bflConfigData = new BFLConfigData();
    public Common commonOTP = new Common();

    public BFLControllerBase(String apiName) {
        commonOTP.currentApiName = apiName;
        commonOTP.loadSpecs();
        bflConfigData = commonOTP.loadBFLConfigData();
        commonOTP.currentApiUrl= bflConfigData.getBaseUrl()+apiName;
    }
    public String sendRequest()
    {
        boolean IsValid = true;
        Validation validation = new Validation();

        for (var d : commonOTP.setData) {
            Specs specs;
            specs = commonOTP.getCSpecs().stream()
                    .filter(m -> m.getJsonTag().equals(d.getJsonTag()))
                    .findFirst()
                    .orElseThrow();
            FieldValue fieldValue = new FieldValue();
            fieldValue.setSpecs(specs);
            fieldValue.setValue(d.getValue());
            boolean isFieldValid = validation.ValidateField(fieldValue);
            Validity validity = new Validity();
            validity.setJsonTag(fieldValue.getSpecs().getJsonTag());
            validity.setIsValid(isFieldValid);
            validityList.add(validity);
            IsValid &= isFieldValid;
        }
        if (IsValid) {
            AESService AESService = new AESService(bflConfigData.getIV(),bflConfigData.getKEY());
            String encryptData = AESService.encrypt(commonOTP.getRequestBody());
            System.out.println("encryptData: " + encryptData);
            commonOTP.sealValue = AESService.hash(encryptData+bflConfigData.getKEY());
            var supplierId = commonOTP.setData.stream()
                    .filter(m -> m.getJsonTag().equals(bflConfigData.getSupplierIDLabel()))
                    .findFirst();
            supplierId.ifPresent(partnerInput -> commonOTP.supplierId = partnerInput.getValue());
            if(supplierId.isEmpty()){
                commonOTP.supplierId = bflConfigData.getPODDealerId();
            }
            String apiRequest= commonOTP.apiRequest("\""+encryptData+"\"");
            apiRequest = apiRequest.replace(commonOTP.sealValue, "").replace("\"", "").replace("\\","");
            int index = apiRequest.indexOf("|");
            if (index >= 0)
                apiRequest = apiRequest.substring(0, index);
            return AESService.decrypt(apiRequest);
        }
        return null;
    }
    public void setValues(List<PartnerInput> listPartnerInput) {
        for (var li : listPartnerInput) {
            commonOTP.setValueHelper(li.getJsonTag(), li.getValue());

        }
    }
}
