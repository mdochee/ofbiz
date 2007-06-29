package org.ofbiz.oagis;

/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
**/
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OagisServices {
    
    public static final String module = OagisServices.class.getName();
    
    public static Map sendConfirmBod(DispatchContext ctx, Map context) {
        
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        
        GenericValue userLogin = null;
        try {
            userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting userLogin", module);
        }
        
        String logicalId = UtilProperties.getPropertyValue("oagis.properties", "CNTROLAREA.SENDER.LOGICALID");
        String authId = UtilProperties.getPropertyValue("oagis.properties", "CNTROLAREA.SENDER.AUTHID");
        
        MapStack bodyParameters =  MapStack.create();
        bodyParameters.put("logicalId", logicalId);
        bodyParameters.put("authId", authId);

        String referenceId = delegator.getNextSeqId("OagisMessageInfo");
        bodyParameters.put("referenceId", referenceId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'Z");
        Timestamp timestamp = UtilDateTime.nowTimestamp();
        String sentDate = dateFormat.format(timestamp);
        bodyParameters.put("sentDate", sentDate);
        
        bodyParameters.put("errorLogicalId", context.get("logicalId"));
        bodyParameters.put("errorComponent", context.get("component"));
        bodyParameters.put("errorTask", context.get("task"));
        bodyParameters.put("errorReferenceId", context.get("referenceId"));
        bodyParameters.put("errorDescription", context.get("description"));
        bodyParameters.put("errorReasonCode", context.get("reasonCode"));
        bodyParameters.put("origRef", context.get("origRefId"));
        OutputStream out = (OutputStream) context.get("outputStream");
        String bodyScreenUri = UtilProperties.getPropertyValue("oagis.properties", "Oagis.Template.ConfirmBod");
        Writer writer = new OutputStreamWriter(out);
        ScreenRenderer screens = new ScreenRenderer(writer, bodyParameters, new HtmlScreenRenderer());
        try {
            screens.render(bodyScreenUri);
        } catch (Exception e) {
            Debug.logError(e, "Error rendering [text/xml]: ", module);
        }
        Map oagisMsgInfoContext = new HashMap();
        oagisMsgInfoContext.put("logicalId", logicalId);
        oagisMsgInfoContext.put("component", "EXCEPTION");
        oagisMsgInfoContext.put("task", "RECIEPT");
        oagisMsgInfoContext.put("referenceId", referenceId);
        oagisMsgInfoContext.put("authId", authId);
        oagisMsgInfoContext.put("sentDate", timestamp);
        oagisMsgInfoContext.put("confirmation", "0");
        oagisMsgInfoContext.put("bsrVerb", "CONFIRM");
        oagisMsgInfoContext.put("bsrNoun", "BOD");
        oagisMsgInfoContext.put("bsrRevision", "004");
        oagisMsgInfoContext.put("userLogin", userLogin);
        try
        {
            Map oagisMsgInfoResult = dispatcher.runSync("createOagisMessageInfo", oagisMsgInfoContext);
            if (ServiceUtil.isError(oagisMsgInfoResult)) return ServiceUtil.returnError("Error creating OagisMessageInfo");
            
            
        } catch (GenericServiceException e) {
            Debug.logError(e, "Saving message to database failed", module);
        }
        
        return ServiceUtil.returnSuccess("Service Completed Successfully");
    }

    public static Map receiveConfirmBod(DispatchContext ctx, Map context) {
        
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        InputStream in = (InputStream) context.get("inputStream");
        
        try{
            GenericValue userLogin = delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId","admin"));
            Document doc = UtilXml.readXmlDocument(in, true, "RecieveConfirmBod");
            Element confirmBodElement = doc.getDocumentElement();
            confirmBodElement.normalize();
            Element docCtrlAreaElement = UtilXml.firstChildElement(confirmBodElement, "N1:CNTROLAREA");
            Element bsrElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:BSR");
            String bsrVerb = UtilXml.childElementValue(bsrElement, "N2:VERB");
            String bsrNoun = UtilXml.childElementValue(bsrElement, "N2:NOUN");
            String bsrRevision = UtilXml.childElementValue(bsrElement, "N2:REVISION");
            
            Element docSenderElement = UtilXml.firstChildElement(docCtrlAreaElement, "N1:SENDER");
            String logicalId = UtilXml.childElementValue(docSenderElement, "N2:LOGICALID");
            String component = UtilXml.childElementValue(docSenderElement, "N2:COMPONENT");
            String task = UtilXml.childElementValue(docSenderElement, "N2:TASK");
            String referenceId = UtilXml.childElementValue(docSenderElement, "N2:REFERENCEID");
            String confirmation = UtilXml.childElementValue(docSenderElement, "N2:CONFIRMATION");
            String language = UtilXml.childElementValue(docSenderElement, "N2:LANGUAGE");
            String codepage = UtilXml.childElementValue(docSenderElement, "N2:CODEPAGE");
            String authId = UtilXml.childElementValue(docSenderElement, "N2:AUTHID");
            String sentDate = UtilXml.childElementValue(docCtrlAreaElement, "N1:DATETIMEANY");
            
            Element dataAreaElement = UtilXml.firstChildElement(confirmBodElement, "n:DATAAREA");
            Element dataAreaConfirmBodElement = UtilXml.firstChildElement(dataAreaElement, "n:CONFIRM_BOD");
            Element dataAreaConfirmElement = UtilXml.firstChildElement(dataAreaConfirmBodElement, "n:CONFIRM");
            Element dataAreaCtrlElement = UtilXml.firstChildElement(dataAreaConfirmElement, "N1:CNTROLAREA");
            Element dataAreaSenderElement = UtilXml.firstChildElement(dataAreaCtrlElement, "N1:SENDER");
            String dataAreaLogicalId = UtilXml.childElementValue(dataAreaSenderElement, "N2:LOGICALID");
            String dataAreaComponent = UtilXml.childElementValue(dataAreaSenderElement, "N2:COMPONENT");
            String dataAreaTask = UtilXml.childElementValue(dataAreaSenderElement, "N2:TASK");
            String dataAreaReferenceId = UtilXml.childElementValue(dataAreaSenderElement, "N2:REFERENCEID");
            String dataAreaDate = UtilXml.childElementValue(dataAreaCtrlElement, "N1:DATETIMEANY");
            String origRef = UtilXml.childElementValue(dataAreaConfirmElement, "N2:ORIGREF");
            
            Element dataAreaConfirmMsgElement = UtilXml.firstChildElement(dataAreaConfirmElement, "n:CONFIRMMSG");
            String description = UtilXml.childElementValue(dataAreaConfirmMsgElement, "N2:DESCRIPTN");
            String reasonCode = UtilXml.childElementValue(dataAreaConfirmMsgElement, "N2:REASONCODE");
            
            Timestamp timestamp = UtilDateTime.nowTimestamp();
            Map oagisMsgInfoContext = new HashMap();
            oagisMsgInfoContext.put("logicalId", logicalId);
            oagisMsgInfoContext.put("component", component);
            oagisMsgInfoContext.put("task", task);
            oagisMsgInfoContext.put("referenceId", referenceId);
            oagisMsgInfoContext.put("authId", authId);
            oagisMsgInfoContext.put("receivedDate", timestamp);
            oagisMsgInfoContext.put("confirmation", confirmation);
            oagisMsgInfoContext.put("bsrVerb", bsrVerb);
            oagisMsgInfoContext.put("bsrNoun", bsrNoun);
            oagisMsgInfoContext.put("bsrRevision", bsrRevision);
            oagisMsgInfoContext.put("userLogin", userLogin);
            
            Map oagisMsgInfoResult = dispatcher.runSync("createOagisMessageInfo", oagisMsgInfoContext);
            if (ServiceUtil.isError(oagisMsgInfoResult)) return ServiceUtil.returnError("Error creating OagisMessageInfo");
            
            Map oagisMsgErrorContext = new HashMap();
            oagisMsgErrorContext.put("logicalId", dataAreaLogicalId);
            oagisMsgErrorContext.put("component", dataAreaComponent);
            oagisMsgErrorContext.put("task", dataAreaTask);
            oagisMsgErrorContext.put("referenceId", dataAreaReferenceId);
            
            GenericValue oagisMsgInfo = delegator.findByPrimaryKey("OagisMessageInfo", oagisMsgErrorContext);
            if (oagisMsgInfo != null){
                oagisMsgErrorContext.put("reasonCode", reasonCode);
                oagisMsgErrorContext.put("description", description);
                oagisMsgErrorContext.put("userLogin", userLogin);
                Map oagisMsgErrorInfoResult = dispatcher.runSync("createOagisMessageErrorInfo", oagisMsgErrorContext);
                if (ServiceUtil.isError(oagisMsgInfoResult)) return ServiceUtil.returnError("Error creating OagisMessageErrorInfo");
            } else{
                Debug.logWarning("No such message with an error was found in OagisMessageInfoEntity ; Not creating OagisMessageErrorInfo", module);
            }
        }catch (Exception e){
            String errMsg = "Error running method receiveConfirmBod";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        Map result = ServiceUtil.returnSuccess("Service Completed Successfully");
        result.put("contentType", "text/plain");
        return result;
    }
}
