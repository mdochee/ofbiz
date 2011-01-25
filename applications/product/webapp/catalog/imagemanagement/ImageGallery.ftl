<#--
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
-->

<table cellspacing="0" cellpadding="0" border="0" align="center" width="100%">
    <#assign imageIndex = 0>
    <#if productContentAndInfos?has_content>
        <#if product?has_content>
            <h1><b>${product.productId}</b></h1>
        </#if>
        <#assign productName = productTextData >
        <#assign seoUrl = productName.replaceAll(" ", "-") >
        <#assign alt_row = false>
        <#list productContentAndInfos as productContentAndInfo>
            <#assign contentAssocs  = delegator.findByAnd("ContentAssoc",Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", productContentAndInfo.contentId?if_exists, "contentAssocTypeId", "IMAGE_THUMBNAIL"))/>
            <#if contentAssocs?has_content>
                <#list contentAssocs as contentAssoc>
                    <#assign ImageContent = delegator.findByPrimaryKey("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", contentAssoc.contentIdTo))?if_exists>
                    <#assign contentDataResourceView = delegator.findByPrimaryKey("ContentDataResourceView", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", contentAssoc.contentIdTo, "drDataResourceId", ImageContent.dataResourceId))?if_exists>
                    <#assign contentName  = productContentAndInfo.contentName>
                    <#if imageIndex < 7>
                        <td style="vertical-align:bottom">
                            <table>
                                <tbody>
                                    <tr valign="middle">
                                        <td align="center"><a href="/catalog/images/${seoUrl}-${product.productId}/${seoUrl}-${contentName}" target="_blank"><img src="<@ofbizContentUrl>${(contentDataResourceView.drObjectInfo)?if_exists}</@ofbizContentUrl>" vspace="5" hspace="5" alt=""/></a></td>
                                    </tr>
                                    <tr valign="middle">
                                        <td align="center"><a href="javascript:lookup_popup2('ImageShare?contentId=${productContentAndInfo.contentId}&amp;dataResourceId=${productContentAndInfo.dataResourceId}&amp;seoUrl=/catalog/images/${seoUrl}-${product.productId}/${seoUrl}-${contentName}','' ,500,500);" class="buttontext">Share</a></td>
                                    </tr>
                                    <br/>
                                </tbody>
                            </table>
                        </td>
                        <#assign imageIndex = imageIndex+1>
                    <#else>
                        <#assign imageIndex = 0>
                        <tr></tr>
                        <td style="vertical-align:bottom">
                            <table>
                                <tbody>
                                    <tr valign="middle">
                                        <td align="center"><a href="/catalog/images/${seoUrl}-${product.productId}/${seoUrl}-${contentName}" target="_blank"><img src="<@ofbizContentUrl>${(contentDataResourceView.drObjectInfo)?if_exists}</@ofbizContentUrl>" vspace="5" hspace="5" alt=""/></a></td>
                                    </tr>
                                    <tr valign="middle">
                                        <td align="center"><a href="javascript:lookup_popup2('ImageShare?contentId=${productContentAndInfo.contentId}&amp;dataResourceId=${productContentAndInfo.dataResourceId}&amp;seoUrl=/catalog/images/${seoUrl}-${product.productId}/${seoUrl}-${contentName}','' ,500,500);" class="buttontext">Share</a></td>
                                    </tr>
                                    <br/>
                                </tbody>
                            </table>
                        </td>
                        <#assign imageIndex = imageIndex+1>
                    </#if>
                </#list>    
            </#if>
        </#list>
    </#if>
</table>
<br/>