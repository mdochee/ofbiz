<#if productSelectableFeatureAndAppl?has_content>
 <#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
 <#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
 <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpSwatchImage">
  <div class="swatch">
    <#list productSelectableFeatureAndAppl as productFeatureAppls>
      <#assign productFeatureId=productFeatureAppls.productFeatureId/>
      <#assign productFeatureTypeId=productFeatureAppls.productFeatureTypeId/>
      <#assign productFeatureDescription=productFeatureAppls.description!""/>

      <#assign productFeatureVariantId=""/>
      <#assign productVariantFeatureMap = plpProductFeatureFirstVariantIdMap.get(productFeatureId)!"">
      <#if productVariantFeatureMap?has_content>
          <#assign productFeatureVariantId=productVariantFeatureMap.get("productVariantId")!""/>
          <#assign productFeatureVariantProduct=productVariantFeatureMap.get("productVariant")!""/>
          <#assign descriptiveFeatureGroupDesc = productVariantFeatureMap.get("descriptiveFeatureGroupDesc")!"" />
          <#assign variantListPrice = productVariantFeatureMap.get("listPrice")!""/>
          <#assign variantOnlinePrice = productVariantFeatureMap.get("basePrice")!""/>
      </#if>
      <#if productFeatureVariantId?has_content>

        <#assign variantProductUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request, StringUtil.wrapString(pdpUrl) + "&productFeatureType=${productFeatureTypeId!}:${productFeatureDescription!}") />
        <input type = "hidden" id="${productId}${productFeatureTypeId!}:${productFeatureDescription!}" value="${variantProductUrl!}"/>
        <input type = "hidden" class="featureGroup" value="${descriptiveFeatureGroupDesc!}"/>
        
        <#assign productVariantContentWrapper = plpProductVariantContentWrapperMap.get('${productFeatureVariantId!}')/>
        <#assign variantContentIdMap = plpProductVariantProductContentIdMap.get('${productFeatureVariantId}')!""/>
        <#assign productVariantSmallURL = "">
        <#assign productVariantSmallAltURL = "">
        <#assign productVariantPlpSwatchURL = "">
 	    <#if variantContentIdMap?has_content>
	    	<#assign variantContentId = variantContentIdMap.get("SMALL_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
                <#assign productVariantSmallURL = productVariantContentWrapper.get("SMALL_IMAGE_URL")!"">
            <#else>
                <#assign productVariantSmallURL = productContentWrapper.get("SMALL_IMAGE_URL")!"">
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("SMALL_IMAGE_ALT_URL")!""/>
	        <#if variantContentId?has_content>
               <#assign productVariantSmallAltURL = productVariantContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
            <#else>
                <#assign productVariantSmallAltURL = productContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("PLP_SWATCH_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
                <#assign productVariantPlpSwatchURL = productVariantContentWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
	        </#if>
	    </#if>
        <#if productVariantPlpSwatchURL?string?has_content>
          <img src="<@ofbizContentUrl>${productVariantPlpSwatchURL}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight?has_content && plpSwatchImageHeight != '0'>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth?has_content && plpSwatchImageWidth != '0'>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
        <#else>
          <#assign productFeatureUrl = ""/>
          <#if productFeatureDataResourceMap?has_content>
           <#assign productFeatureResourceUrl = productFeatureDataResourceMap.get(productFeatureId)!""/>
           <#if productFeatureResourceUrl?has_content>
             <#assign productFeatureUrl=productFeatureResourceUrl/>
           </#if>
          </#if>
          <#if productFeatureUrl?has_content>
            <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight?has_content && plpSwatchImageHeight != '0'>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth?has_content && plpSwatchImageWidth != '0'>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
          </#if>
        </#if>
        <div class="swatchVariant" style="display:none">
          <a class="pdpUrl" title="${productName}" href="${productFriendlyUrl}">
            <img alt="${productName}" title="${productName}" src="${productVariantSmallURL!}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if productVariantSmallAltURL?string?has_content>onmouseover="src='${productVariantSmallAltURL}'"</#if> onmouseout="src='${productVariantSmallURL}'" onerror="onImgError(this, 'PLP-Thumb');"/>
          </a>
        </div>

        <div class="swatchVariantOnlinePrice" style="display:none">
          <p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=variantOnlinePrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
        </div>
        
        <div class="swatchVariantListPrice" style="display:none">
          <#if variantListPrice?has_content && variantListPrice gt variantOnlinePrice>
            <p class="price">${uiLabelMap.PlpListPriceLabel} <@ofbizCurrency amount=variantListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
          </#if>
        </div>
        
        <div class="swatchVariantSaveMoney" style="display:none">
          <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
          <#if variantListPrice?has_content && variantOnlinePrice?has_content>
            <#assign youSaveMoney = (variantListPrice - variantOnlinePrice)/>
            <#if (youSaveMoney?has_content) && (youSaveMoney gt showSavingMoneyAbove?number)>  
              <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
            </#if>
          </#if>
        </div>
        
        <div class="swatchVariantSavingPercent" style="display:none">
          <#if variantListPrice?has_content && variantListPrice != 0>
            <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
            <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
            <#assign youSavePercent = ((variantListPrice - variantOnlinePrice)/variantListPrice) />
            <#if youSavePercent gt showSavingPercentAbove?number>  
              <p class="price">${uiLabelMap.YouSaveCaption}${youSavePercent?string("#0%")}</p>
            </#if>
          </#if>
        </div>
      </#if>
    </#list>
  </div>
</div>
</#if>