package com.bfl.sdk.partner.integration.bfl.sdk;

public class Validation {

    public boolean isNumeric(String param) {
        return param.matches("^[0-9]+([,.][0-9]{1,2})?");
    }

    public boolean isAlphaNum(String param) {
        return param.matches("^[a-zA-Z0-9.:_ ]*$");
    }

    public boolean isChar(String param) {
        return param.matches("^[a-zA-Z]+$");
    }

    public boolean isMandatory(FieldValue fieldValue) {
        boolean isMandate = fieldValue.getSpecs().getIsMandatory().equals("Yes");
        if (!isMandate)
            return true;
        else
            return fieldValue.getValue().length() != 0;
    }

    public boolean ValidateField(FieldValue fieldValue) {
        boolean isValid = false;
        boolean isMandatory = isMandatory(fieldValue);
        boolean isValidateDatatype = validateDataType(fieldValue);
        boolean isValidateMinLen = validateMinLen(fieldValue);
        boolean isValidateMaxLen = validateMaxLen(fieldValue);
        if (isMandatory && isValidateDatatype && isValidateMinLen && isValidateMaxLen) {
            isValid = true;
        }
        return isValid;
    }

    public boolean validateDataType(FieldValue fieldValue) {
        if (fieldValue.getSpecs().getIsMandatory().equals("Yes"))
        {
        switch (fieldValue.getSpecs().getDataType()) {
            case "N":
                return (isNumeric(fieldValue.getValue()));
            case "AN":
                return (isAlphaNum(fieldValue.getValue()));
            case "C":
                return (isChar(fieldValue.getValue()));
            default:
                return false;
        }
    }
        return true;
    }

    public boolean validateMinLen(FieldValue fieldValue) {
        return (fieldValue.getValue().length() >= fieldValue.getSpecs().getMin_len());

    }

    public boolean validateMaxLen(FieldValue fieldValue) {
        return (fieldValue.getValue().length() <= fieldValue.getSpecs().getMax_len());

    }

    public boolean validateSpecialChar(FieldValue fieldValue) {
        return fieldValue.getValue().matches("^[a-zA-Z0-9#,&*()-_< >./\\[ ]:;]*$");
    }
}
