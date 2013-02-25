package common;

import org.ofbiz.base.util.UtilValidate;
import java.text.NumberFormat;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import javolution.util.FastList;
import com.osafe.util.Util;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.InventoryServices;
import org.ofbiz.party.content.PartyContentWrapper;
import com.osafe.services.CatalogUrlServlet;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.Debug;
import java.util.LinkedHashMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;


productStore = ProductStoreWorker.getProductStore(request);
productStoreId = productStore.get("productStoreId");
String productId = parameters.productId;
String productCategoryId = parameters.productCategoryId;
inlineCounter = request.getAttribute("inlineCounter");
contentPathPrefix = CatalogWorker.getContentPathPrefix(request);
catalogName = CatalogWorker.getCatalogName(request);
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
cart = ShoppingCartEvents.getCartObject(request);
webSiteId = CatalogWorker.getWebSiteId(request);
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
currencyUomId = Util.getProductStoreParm(request, "CURRENCY_UOM_DEFAULT");

imagePlaceHolder="/osafe_theme/images/user_content/images/NotFoundImage.jpg";
imageLargePlaceHolder="/osafe_theme/images/user_content/images/NotFoundImagePDPLarge.jpg";

if(UtilValidate.isEmpty(currencyUomId))
{
	currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD"); 
}
selectOne = UtilProperties.getMessage("OSafeUiLabels", "SelectOneLabel", locale);

//BUILD JS TO CREATE DROPDOWN FOR ALL SELECTABLE FEATURE EXCEPT FIRST ONE
String buildNext(Map map, List order, String current, String prefix) {
    def ct = 0;
    def featureType = null;
    def featureIndex = 0;
    def buf = new StringBuffer();
    buf.append("function listFT" + current + prefix + "() { ");
    buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options.length = 1;");
    
    buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
    map.each { key, value ->
        def optValue = null;

        if (order.indexOf(current) == (order.size()-1)) {
            optValue = value.iterator().next();
        } else {
            optValue = prefix + "_" + ct;
        }
        String selectedFeature = current+":"+key;
        if(parameters.productFeatureType && parameters.productFeatureType == selectedFeature) {
            featureType = current;
            featureIndex = ct;
        }
        buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options[" + (ct + 1) + "] = new Option(\"" + key + "\",\"" + optValue + "\");");
        ct++;
    }
    buf.append(" }");
    if(map.size()==1)
    {   
        //jsBufDefault.append("getList(\"FT" + current + "\", \""+featureIndex+"\", 1);");
    }
        
    if (order.indexOf(current) < (order.size()-1)) {
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNext(value, order, nextOrder, newPrefix));
            ct++;
        }
    }
    return buf.toString();
}

//BUILD JS TO CREATE 'LI' FOR ALL SELECTABLE FEATURE EXCEPT FIRST ONE
String buildNextLi(Map map, List order, String current, String prefix, Map productVariantInventoryMap) 
{
    def ct = 0;
    def featureType = null;
    def featureIndex = null;
    def productFeatureId = null;
    def productFeatureTypeId = null;
    def buf = new StringBuffer();
    buf.append("var VARSTOCK = new Object();");
    buf.append("function listLiFT" + current + prefix + "() { ");
    buf.append("document.getElementById(\"LiFT" + current + "\").innerHTML = \"\";");
    map.each { key, value ->
        def optValue = null;
        def stockClass = "";
        if (order.indexOf(current) == (order.size()-1)) 
        {
            optValue = value.iterator().next();
            
            inventoryLevelMap = productVariantInventoryMap.get(optValue);
            
            inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
            inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
            inventoryLevel = inventoryLevelMap.get("inventoryLevel");
            
	        if(inventoryLevel <= inventoryOutOfStockTo)
	        {
	            stockClass = "outOfStock";
	        }
	        else
	        {
	            if(inventoryLevel >= inventoryInStockFrom)
	            {
	                stockClass = "inStock";
	            }
	            else
	            {
	                stockClass = "lowStock";
	            }
	        }
            buf.append("VARSTOCK['" + optValue + "'] = \"" + stockClass + "\";");
        } 
        else 
        {
            optValue = prefix + "_" + ct;
        }
        
        String selectedFeature = current+":"+key;
        def selectedClass = false;
        
        if(parameters.productFeatureType)
        {
            productFeatureTypeIdArr = parameters.productFeatureType.split(":");
            productFeatureTypeId = productFeatureTypeIdArr[0];
            if(parameters.productFeatureType == selectedFeature) 
            {
                featureType = current;
                featureIndex = ct;
                selectedClass = true;
            }
        }
        
        if(!parameters.productFeatureType || (UtilValidate.isNotEmpty(productFeatureTypeId) && current != productFeatureTypeId))
        {
            if(map.size()==1)
            {
                selectedClass = true;
            }
        }
        buf.append("var li = document.createElement('li');");
        if(selectedClass == true)
        {
            buf.append("li.setAttribute(\"class\",\"selected "+stockClass+"\");");
        } 
        else 
        {
            buf.append("li.setAttribute(\"class\",\""+stockClass+"\");");
        }
        
        liText = "<a href=javascript:void(0); onclick=getList('FT" + current + "','" + ct + "',1);>" + key + "</a>";
        buf.append("document.getElementById(\"LiFT" + current + "\").appendChild(li);");
        buf.append("li.innerHTML = \"" + liText + "\";");
        ct++;
    }
    
    buf.append(" }");
    if (order.indexOf(current) < (order.size()-1)) {
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNextLi(value, order, nextOrder, newPrefix,productVariantInventoryMap));
            ct++;
        }
    }
    return buf.toString();
}

//BUILD JS TO CREATE DROPDOWN FOR FIRST SELECTABLE FEATURE AND CALL THE FUNCTION TO CREATE DROPDOWN FOR REST FEATURES
String buildFeatureJS(List featureOrder, Map variantTree, Map productVariantInventoryMap)
{
	def nextFeatureJsBuf = new StringBuffer();
	def buf = new StringBuffer();
	topLevelName = featureOrder[0];
	buf.append("var VARSTOCK = new Object();");
	buf.append("var OPT = new Array(" + featureOrder.size() + ");");
    featureOrder.eachWithIndex { feature, i ->
        buf.append("OPT[" + i + "] = \"FT" + feature + "\";");
    }
    
	buf.append("function list" + topLevelName + "() {");
	buf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options.length = 1;");
	buf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");

    //COMMENTING OUT THIS CODE FOR NOW, WILL REMOVE WHEN MAKE SURE THAT IT'S NOT AFFECTING ANY FUNCTIONALITY.
    /* featureOrder.each { featureKey ->
            jsBuf.append("document.forms[\"addform\"].elements[\"FT" + featureKey + "\"].options.length = 1;");
    } */
                 
    def counter = 0;
    featureCnt = 0;

    variantTree.each { key, value ->
        opt = null;
        if (featureOrder.size() == 1) 
        {
            opt = value.iterator().next();
            inventoryLevelMap = productVariantInventoryMap.get(opt);

            inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
            inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
            inventoryLevel = inventoryLevelMap.get("inventoryLevel");

            if(inventoryLevel <= inventoryOutOfStockTo)
            {
                stockClass = "outOfStock";
            }
            else
            {
                if(inventoryLevel >= inventoryInStockFrom)
                {
                    stockClass = "inStock";
                }
                else
                {
                    stockClass = "lowStock";
                }
            }
            //ADD STOCK LEVEL CLASSES TO THE MAP IF THERE IS ONLY ONE SELECTABLE FEATURE FOR THAT PRODUCT.
            buf.append("VARSTOCK['" + opt + "'] = \"" + stockClass + "\";");
        } 
        else 
        {
            opt = counter as String;
        }
        
        buf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options[" + (counter+1) + "] = new Option(\"" + key + "\",\"" + opt + "\");");
        
        productFeatureType = topLevelName+":"+key;
        if(parameters.productFeatureType)
        {
            
            if(parameters.productFeatureType == productFeatureType)
            {
                featureCnt = counter;
            }
        }
        
        //CALLS THE FUNCTION TO CREATE DROPDOWN AND LI FOR REST OF THE SELECTABLE FEATURES.
        varTree = value;
        cnt = "" + counter;
        if (value instanceof Map) 
        {
        	nextFeatureJsBuf.append(buildNext(varTree, featureOrder, featureOrder[1], cnt));
        	nextFeatureJsBuf.append(buildNextLi(varTree, featureOrder, featureOrder[1], cnt, productVariantInventoryMap));
        }
        counter++;
    }
        
    buf.append("}");

    buf.append(nextFeatureJsBuf);
    
    return buf.toString();
}

//BUILD JS TO CREATE FIRST VARIANT PRODUCT ID MAP
String buildVariantMapJS(Map firstVariantIdMap, Map variantStandardFeatureMap)
{
	def buf = new StringBuffer();
	def featureExist = [];
	buf.append("var VARMAP = new Object();");
	firstVariantIdMap.each { key, value ->
	    def productVariantStandardFeatures = variantStandardFeatureMap.get(value);
	    if(UtilValidate.isNotEmpty(productVariantStandardFeatures))
	    {
	    	for(GenericValue productVariantStandardFeatureAppl : productVariantStandardFeatures)
		    {
				def mapKey = "FT"+productVariantStandardFeatureAppl.productFeatureTypeId+"_"+productVariantStandardFeatureAppl.description;
		        if(!featureExist.contains(mapKey))
		        {
		        	buf.append("VARMAP['" + mapKey + "'] = \"" + productVariantStandardFeatureAppl.productId + "\";");
		            featureExist.add(mapKey);
		        }
		    }
	    }
    }
	
	return buf.toString();
}

//BUILD JS TO CREATE VARIANT GROUP MAP
String buildVariantGroupMapJS(Map variantDisFeatureMap, String pdpFacetGroupVariantMatch) 
{
	def buf = new StringBuffer();
	buf.append("var VARGROUPMAP = new Object();");
	if(UtilValidate.isNotEmpty(pdpFacetGroupVariantMatch))
	{
		variantDisFeatureMap.each { key, value ->
			if(UtilValidate.isNotEmpty(value))
	        {
				def distinguishFeatureByTypeMap = value.get("productFeaturesByType");
				if(UtilValidate.isNotEmpty(distinguishFeatureByTypeMap))
				{
				    def disFeatureAndApplList = distinguishFeatureByTypeMap.get(pdpFacetGroupVariantMatch);
					if(UtilValidate.isNotEmpty(disFeatureAndApplList))
					{
					    def distinguishFeatureMap = disFeatureAndApplList.get(0);
		      	        buf.append("VARGROUPMAP['" + distinguishFeatureMap.productId + "'] = \"" + distinguishFeatureMap.description + "\";");
						
					}
				}
	        }
		}
	}
	
    return buf.toString();
}

// BUILDS A PRODUCT FEATURE TREE
Map makeGroup(Delegator delegator, Map<String, List<String>> featureList, List<String> items, List<String> order, int index, Map standardFeatureMap) throws IllegalArgumentException, IllegalStateException
{
        
        Map<String, List<String>> tempGroup = FastMap.newInstance();
        Map<String, Object> group = new LinkedHashMap<String, Object>();
        String orderKey = (String) order.get(index);

        if (featureList == null) 
        {
            throw new IllegalArgumentException("Cannot build feature tree: featureList is null");
        }

        if (index < 0) 
        {
            throw new IllegalArgumentException("Invalid index '" + index + "' min index '0'");
        }
        if (index + 1 > order.size()) 
        {
            throw new IllegalArgumentException("Invalid index '" + index + "' max index '" + (order.size() - 1) + "'");
        }

        // loop through items and make the lists
        for (String thisItem: items) 
        {
            List<GenericValue> features = null;
            features = standardFeatureMap.get(thisItem);
            
            for (GenericValue item: features) 
            {
                String itemKey = item.getString("description");

                if (tempGroup.containsKey(itemKey)) 
                {
                    List<String> itemList = tempGroup.get(itemKey);

                    if (!itemList.contains(thisItem))
                    {
                        itemList.add(thisItem);
                    }
                } 
                else 
                {
                    List<String> itemList = UtilMisc.toList(thisItem);
                    tempGroup.put(itemKey, itemList);
                }
            }
        }

        // Loop through the feature list and order the keys in the tempGroup
        List<String> orderFeatureList = featureList.get(orderKey);

        if (orderFeatureList == null) 
        {
            throw new IllegalArgumentException("Cannot build feature tree: orderFeatureList is null for orderKey=" + orderKey);
        }

        for (GenericValue featureStr: orderFeatureList) 
        {
            if (tempGroup.containsKey(featureStr.getString("description")))
                group.put(featureStr.getString("description"), tempGroup.get(featureStr.getString("description")));
        }

        // no groups; no tree
        if (group.size() == 0) 
        {
            return group;
            throw new IllegalStateException("Cannot create tree from group list; error on '" + orderKey + "'");
        }

        if (index + 1 == order.size()) 
        {
            return group;
        }

        // loop through the keysets and get the sub-groups
        for (String key: group.keySet()) 
        {
            List<String> itemList = UtilGenerics.checkList(group.get(key));

            if (UtilValidate.isNotEmpty(itemList)) 
            {
                Map<String, Object> subGroup = makeGroup(delegator, featureList, itemList, order, index + 1, standardFeatureMap);
                group.put(key, subGroup);
            } 
            else 
            {
                // do nothing, ie put nothing in the Map
                throw new IllegalStateException("Cannot create tree from an empty list; error on '" + key + "'");
            }
        }
        
        return group;
}

if (UtilValidate.isNotEmpty(productId)) 
{
    
    GenericValue gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
 // Setting variables required in the Manufacturer Info section 
    if (UtilValidate.isNotEmpty(gvProduct)) 
    {
        context.currentProduct = gvProduct;
    	if(UtilValidate.isEmpty(productCategoryId))
        {
	        productCategoryMemberList = gvProduct.getRelatedCache("ProductCategoryMember");
            productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
    	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
	        if(UtilValidate.isNotEmpty(productCategoryMemberList))
	        {
	            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
	            productCategoryId = productCategoryMember.productCategoryId; 
	        }    
        }
        if (UtilValidate.isNotEmpty(productCategoryId)) 
        {
            productCategoryId = StringUtil.replaceString(productCategoryId,"/","");
            context.productCategoryId = productCategoryId;
            gvProductCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), true);
            if (UtilValidate.isNotEmpty(gvProductCategory))
            {
    	        CategoryContentWrapper currentProductCategoryContentWrapper = new CategoryContentWrapper(gvProductCategory, request);
    	        context.currentProductCategory = gvProductCategory;
    	        context.currentProductCategoryContentWrapper = currentProductCategoryContentWrapper;


                productCategoryContentList = gvProductCategory.getRelatedCache("ProductCategoryContent");
                prodCategoryContentList = EntityUtil.filterByDate(productCategoryContentList,true);
                if (productCategoryContentList) 
                {
                  pdpAddCategoryContentList = EntityUtil.filterByAnd(productCategoryContentList, UtilMisc.toMap("prodCatContentTypeId" , "PDP_ADDITIONAL"));
                  if (UtilValidate.isNotEmpty(pdpAddCategoryContentList)) 
                  {
                     pdpAddCategoryContent = EntityUtil.getFirst(pdpAddCategoryContentList);
                     context.pdpEspotContent = pdpAddCategoryContent.getRelatedOneCache("Content");
                  }
                }
            }
        }
    	
        productId = gvProduct.productId;
        partyManufacturer=gvProduct.getRelatedOneCache("ManufacturerParty");
        if (UtilValidate.isNotEmpty(partyManufacturer))
        {
          context.manufacturerPartyId = partyManufacturer.partyId;
          PartyContentWrapper partyContentWrapper = new PartyContentWrapper(partyManufacturer, request);
          context.partyContentWrapper = partyContentWrapper;
          context.pdpManufacturerDescription = partyContentWrapper.get("DESCRIPTION");
          context.pdpManufacturerProfileName = partyContentWrapper.get("PROFILE_NAME");
          context.pdpManufacturerProfileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL");
        }
        
     // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
        virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
        if (UtilValidate.isNotEmpty(virtualProductId))
        {
            productId = virtualProductId;
            gvProduct = delegator.findByPrimaryKeyCache("Product", [productId : productId]);
        }

        context.title = "";
    }


    if (UtilValidate.isNotEmpty(gvProduct)) 
    {

        //CREATE PRODUCT CONTENT WRAPPER
        ProductContentWrapper productContentWrapper = new ProductContentWrapper(gvProduct, request);
        context.productContentWrapper = productContentWrapper;

        //GET PRODUCT CONTENT LIST AND SET INTO CONTEXT
        Map productContentIdMap = FastMap.newInstance();
		productContentList = gvProduct.getRelatedCache("ProductContent");
		productContentList = EntityUtil.filterByDate(productContentList,true);
		if (UtilValidate.isNotEmpty(productContentList))
		{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
    		   context.put(productContent.productContentTypeId,productContent.contentId);
               productContentIdMap.put(productContent.productContentTypeId,productContent.contentId);
            }
		}

        context.product = gvProduct;
        context.product_id = gvProduct.productId;
        context.productId = gvProduct.productId;
	    context.internalName = gvProduct.internalName;
	    context.manufacturerPartyId = gvProduct.manufacturerPartyId;

        //SET PRODUCT NAME
        productName = gvProduct.productName;
	    productContentId = productContentIdMap.get("PRODUCT_NAME");
        if (UtilValidate.isNotEmpty(productContentId))
        {
    	    productName = productContentWrapper.get("PRODUCT_NAME");
  	        productName = StringEscapeUtils.unescapeHtml(productName.toString());
     	    context.pdpProductName = productName;
	        productName = productName;
        }
        context.productName = productName;
        
        //SET PRODUCT LONG DESCRIPTION
        productLongDesc = "";
	    productContentId = productContentIdMap.get("LONG_DESCRIPTION");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productLongDesc = productContentWrapper.get("LONG_DESCRIPTION");
        	productLongDesc = StringEscapeUtils.unescapeHtml(productLongDesc.toString());
     	    productLongDesc = productLongDesc;
        }
 	    context.pdpLongDescription = productLongDesc;
        
        //GET PRODUCT ATTRIBUTE LIST AND SET INTO LOCAL MAP(productAttrMap)
        productAttr = gvProduct.getRelatedCache("ProductAttribute");
        productAttrMap = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(productAttr))
        {
            attrlIter = productAttr.iterator();
            while (attrlIter.hasNext()) {
                attr = (GenericValue) attrlIter.next();
                productAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
            }
        }

        //SET META INFORMATION WITH PRODUCT DATA
        if (UtilValidate.isEmpty(productName)) 
        {
            productName = gvProduct.productName;
        }
        if(UtilValidate.isNotEmpty(productName)) 
        {
            context.metaTitle = productName;
            context.pageTitle = productName;
        }
        
	    productContentId = productContentIdMap.get("DESCRIPTION");
        if (UtilValidate.isNotEmpty(productContentId))
        {
            context.metaKeywords = productContentWrapper.get("DESCRIPTION");
        }

        if(UtilValidate.isNotEmpty(productLongDesc)) 
        {
            context.metaDescription = productLongDesc;
        }
        
        //OVERRIDE META INFORMATION WITH SPECIFIC PRODUCT ATTRIBUTES, IF DEFINED
	    productContentId = productContentIdMap.get("HTML_PAGE_TITLE");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
            context.metaTitle =productContentWrapper.get("HTML_PAGE_TITLE");
        } 
        else  
        {
        	if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_TITLE")))
        	{
        		context.metaTitle = productAttrMap.get("SEO_TITLE");
        	}
        }
        
	    productContentId = productContentIdMap.get("HTML_PAGE_META_KEY");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
            context.metaKeywords = productContentWrapper.get("HTML_PAGE_META_KEY");
        } 
        else 
        {
        	if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_KEYWORDS")))
        	{
        		context.metaKeywords = productAttrMap.get("SEO_KEYWORDS");
        	}
        }
        
	    productContentId = productContentIdMap.get("HTML_PAGE_META_DESC");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
            context.metaDescription = productContentWrapper.get("HTML_PAGE_META_DESC");
        } 
        else 
        {
        	if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_DESCRIPTION")))
        	{
        			context.metaKeywords = productAttrMap.get("SEO_DESCRIPTION");
        	}
        }

        
        //SET PRODUCT CONTENT IMAGE URLS
	    productContentId = productContentIdMap.get("ADDTOCART_IMAGE");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
    	  imageUrl = productContentWrapper.get("ADDTOCART_IMAGE");
	      context.addToCartImageUrl = contentPathPrefix + imageUrl;
        }
        else
        {
	      context.addToCartImageUrl = imagePlaceHolder;
        }

	    productContentId = productContentIdMap.get("LARGE_IMAGE_URL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
     	  imageUrl = productContentWrapper.get("LARGE_IMAGE_URL");
	      context.productLargeImageUrl = contentPathPrefix + imageUrl;
	      request.setAttribute("largeImageUrl",contentPathPrefix + imageUrl);
        }
        else
        {
	      context.productLargeImageUrl = imageLargePlaceHolder;
        }

	    productContentId = productContentIdMap.get("DETAIL_IMAGE_URL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
     	  imageUrl = productContentWrapper.get("DETAIL_IMAGE_URL");
	      context.productDetailImageUrl = contentPathPrefix + imageUrl;
	      request.setAttribute("detailImageUrl",contentPathPrefix + imageUrl);
        }
	
	    productContentId = productContentIdMap.get("THUMBNAIL_IMAGE_URL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
     	  imageUrl = productContentWrapper.get("THUMBNAIL_IMAGE_URL");
	      context.productThumbImageUrl = contentPathPrefix + imageUrl;
        }
        else
        {
	      context.productThumbImageUrl = "";
        }
        try {
            maxAltImages = Integer.parseInt(UtilProperties.getPropertyValue("osafe", "pdp-alternate-images"));
        }
        catch(NumberFormatException nfe) 
         {
            Debug.logError("PDP Properties pdp-alternate-images:" + nfe.getMessage(), "eCommerceProductDetail.groovy");
    	    maxAltImages = 10;
         }
    
        context.maxAltImages = maxAltImages;
        
        for (int i = 1; i <= (maxAltImages +1); i++)
        {
    	    productContentId = productContentIdMap.get("XTRA_IMG_" + i + "_LARGE");
            if(UtilValidate.isNotEmpty(productContentId)) 
	        {
	          
    		   imageUrl = productContentWrapper.get("ADDITIONAL_IMAGE_" + i);
		       context.put("productAddImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productAddImageUrl" + i,"");
	        }
	        
        }
	
        for (int i = 1; i <= (maxAltImages +1); i++)
        {
    	    productContentId = productContentIdMap.get("XTRA_IMG_" + i + "_LARGE");
            if(UtilValidate.isNotEmpty(productContentId)) 
	        {
    		   imageUrl = productContentWrapper.get("XTRA_IMG_" + i + "_LARGE");
		       context.put("productXtraAddLargeImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productXtraAddLargeImageUrl" + i,"");
	        }
	        
        }

        for (int i = 1; i <= (maxAltImages +1); i++)
        {
    	    productContentId = productContentIdMap.get("XTRA_IMG_" + i + "_DETAIL");
            if(UtilValidate.isNotEmpty(productContentId)) 
	        {
	          
    		   imageUrl = productContentWrapper.get("XTRA_IMG_" + i + "_DETAIL");
		       context.put("productXtraAddImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productXtraAddImageUrl" + i,"");
	        }
        }

	    productContentId = productContentIdMap.get("PDP_VIDEO_URL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
     	  imageUrl = productContentWrapper.get("PDP_VIDEO_URL");
	      context.pdpVideoUrl = imageUrl;
        }
        else
        {
	      context.pdpVideoUrl = "";
	    }
        
	    productContentId = productContentIdMap.get("PDP_VIDEO_360_URL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
     	  imageUrl = productContentWrapper.get("PDP_VIDEO_360_URL");
	      context.pdpVideo360Url = imageUrl;
        }
        else
        {
	      context.pdpVideo360Url = "";
	    }
        
	    productContentId = productContentIdMap.get("PDP_LABEL");
        if(UtilValidate.isNotEmpty(productContentId)) 
        {
            context.tertiaryInformation =  productContentWrapper.get("PDP_LABEL");
        }
        else
        {
            internalNameLabel = UtilProperties.getMessage("OSafeUiLabels", "InternalNameLabel", locale);
            context.tertiaryInformation =  internalNameLabel + " " + gvProduct.internalName ;
        }
        
        pdpFacetGroupVariantSwatch = Util.getProductStoreParm(request, "PDP_FACET_GROUP_VARIANT_SWATCH_IMG");
        if (UtilValidate.isNotEmpty(pdpFacetGroupVariantSwatch))
        {
     	    pdpFacetGroupVariantSwatch = pdpFacetGroupVariantSwatch.toUpperCase();
        }
   	    context.PDP_FACET_GROUP_VARIANT_SWATCH = pdpFacetGroupVariantSwatch;

   	    pdpFacetGroupVariantMatch = Util.getProductStoreParm(request, "PDP_FACET_GROUP_VARIANT_MATCH");
        if (UtilValidate.isNotEmpty(pdpFacetGroupVariantMatch))
        {
        	pdpFacetGroupVariantMatch = pdpFacetGroupVariantMatch.toUpperCase();
        }
	    context.PDP_FACET_GROUP_VARIANT_MATCH = pdpFacetGroupVariantMatch;

        //BUILD RECENTLY VIELED LIST
        pdpRecentViewedaMaxStr = Util.getProductStoreParm(request, "PDP_RECENT_VIEW_MAX");
        try 
        {
            pdpRecentViewedMax = Integer.parseInt(pdpRecentViewedaMaxStr);
        } 
        catch(NumberFormatException) 
        {
            pdpRecentViewedMax = 0;
        }
        
        if(pdpRecentViewedMax > 0)
        {
        	lastViewedProducts = FastList.newInstance();
            try 
            {
                lastViewedProducts = UtilGenerics.checkList(session.getAttribute("lastViewedProducts"));
            } 
            catch(Exception e) 
            {
                Debug.logError("PDP Last Viewed Product:" + e.getMessage(), "eCommerceProductDetail.groovy");
            }
            lastViewedProductsClone = FastList.newInstance();
            if (UtilValidate.isNotEmpty(lastViewedProducts)) 
            {
            	lastViewedProductsClone.addAll(lastViewedProducts);
            }
            
            if (UtilValidate.isNotEmpty(lastViewedProductsClone)) 
            {
                lastViewedProductsClone.remove(productId);
            }
            lastViewedProductsClone.add(0, productId);
            session.setAttribute("lastViewedProducts", lastViewedProductsClone);
            
        } 
        else 
        {
            session.removeAttribute("lastViewedProducts");
        }
        context.pdpRecentViewedMax = pdpRecentViewedMax;
        
        if (cart.isSalesOrder()) 
        {
            // sales order: run the "calculateProductPrice" service
            priceContext = [product : gvProduct, prodCatalogId : currentCatalogId,
                        currencyUomId : cart.getCurrency(), autoUserLogin : autoUserLogin];
            priceContext.webSiteId = webSiteId;
            priceContext.productStoreId = productStoreId;
            priceContext.checkIncludeVat = "Y";
            priceContext.agreementId = cart.getAgreementId();
            priceContext.partyId = cart.getPartyId();  // IMPORTANT: must put this in, or price will be calculated for the CSR instead of the customer
            //priceContext.findAllQuantityPrices="Y";
            pdpPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
            context.pdpPriceMap = pdpPriceMap;
        } 

        //GET QUANTITY PRICE BREAK RULES TO SHOW
        volumePricingRule = [];
        volumePricingRuleMap = FastMap.newInstance();
        productIdConds = delegator.findByAndCache("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: gvProduct.productId],["productPriceRuleId"]);
        if (UtilValidate.isNotEmpty(productIdConds))
        {
            for (GenericValue priceCond: productIdConds) 
            {
                priceRule = priceCond.getRelatedOneCache("ProductPriceRule");
                if (EntityUtil.isValueActive(priceRule,UtilDateTime.nowTimestamp()))
                {
	                qtyBreakIdConds = priceRule.getRelatedCache("ProductPriceCond");
	                qtyBreakIdConds = EntityUtil.filterByAnd(qtyBreakIdConds,UtilMisc.toMap("inputParamEnumId","PRIP_QUANTITY"));
	                if (UtilValidate.isNotEmpty(qtyBreakIdConds)) 
	                {
		                priceIdActions = priceRule.getRelatedCache("ProductPriceAction");
		                priceIdActions = EntityUtil.filterByAnd(priceIdActions,UtilMisc.toMap("productPriceActionTypeId","PRICE_FLAT"));
                        priceIdAction = EntityUtil.getFirst(priceIdActions);
                        volumePricingRule.add(priceRule);
                        volumePricingRuleMap.put(priceRule.productPriceRuleId,priceIdAction.amount);
	                }
                }
            }
        }
        context.volumePricingRule = volumePricingRule;
        context.volumePricingRuleMap = volumePricingRuleMap;

        // ADD ONE TO CALCULATED INFO TO COUNT TOTAL TIMES PRODUCT VIEWED; (it is done async)
        dispatcher.runAsync("countProductView", [productId : gvProduct.productId, weight : new Long(1)], false);

        //GET PRODUCT RATINGS AND REVIEWS
	    decimals=Integer.parseInt("1");
	    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	    context.put("decimals",decimals);
	    context.put("rounding",rounding);
	    // get the average rating
 	    productCalculatedInfo = gvProduct.getRelatedOneCache("ProductCalculatedInfo");
        if (UtilValidate.isNotEmpty(productCalculatedInfo))
        {
   	        averageRating= productCalculatedInfo.getBigDecimal("averageCustomerRating");
            if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
            {
     	       averageCustomerRating= averageRating.setScale(1,rounding);
     	       context.put("averageStarRating", averageCustomerRating);
            }
        }
	   
        reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
		context.productReviews = FastList.newInstance();
		if(UtilValidate.isNotEmpty(reviewMethod))
		{
			if(reviewMethod.equalsIgnoreCase("BIGFISH"))
	        {
	            reviews = gvProduct.getRelatedCache("ProductReview");
	            if (UtilValidate.isNotEmpty(reviews))
	            {
	                reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
		     	    context.put("reviewSize",reviews.size());
				    sortReviewCol2="-postedDateTime";
				    sortReviewCol1="-productRating";
				    sortReviewBy = requestParameters.get("sortReviewBy");
			        if (UtilValidate.isNotEmpty(sortReviewBy))
			        {
			    	    if ("-productRating".equals(sortReviewBy) || "productRating".equals(sortReviewBy))
			    	    {
			               sortReviewCol1=sortReviewBy;
			    	       sortReviewCol2="-postedDateTime";
			    	    }
			    	    else
			    	    {
			    	       sortReviewCol1=sortReviewBy;
			    	       sortReviewCol2="-productRating";
			    	    }
			        }
			        else
			        {
			          sortReviewBy="-productRating";
			        }
			        reviews = EntityUtil.orderBy(reviews,UtilMisc.toList(sortReviewCol1,sortReviewCol2));
					context.put("sortReviewBy",sortReviewBy);
			  		listSize=reviews.size();
					// set the page parameters
							try 
							{
							    viewIndex = Integer.valueOf((String) request.getParameter("viewIndex")).intValue();
							} 
							catch (Exception e) 
							{
							    viewIndex = 1;
							}
							try 
							{
							    viewSize = Integer.valueOf((String) request.getParameter("viewSize")).intValue();
							} 
							catch (Exception e) 
							{
							    viewSize = 5;
					 	    }
							try 
							{
							     if(viewIndex == 0)
							            viewIndex = viewIndex + 1;
							     lowIndex = (viewIndex -1) * viewSize + 1;
							     if(lowIndex > listSize)
							          lowIndex = listSize;
							            
							} 
							catch (Exception e) 
							{
							    lowIndex = 0;
							}
							try 
							{
							      if(viewIndex == 0)
							         viewIndex = viewIndex + 1;
							        
							       highIndex = viewIndex * viewSize;
							       if (highIndex > listSize)
							            highIndex = listSize;
							        
							} 
							catch (Exception e) 
							{
							    highIndex = 0;
							}
							
					if (listSize > 0)
					{
						subList = reviews.subList(lowIndex-1, highIndex);            
						context.put("productReviews", subList);
					}
			        viewPages= (listSize / viewSize).intValue();
					if (listSize % viewSize != 0)
					{
					  viewPages = viewPages +1;
					}
					context.put("viewPages", viewPages);
					context.put("listSize", listSize);
					context.put("viewIndex", viewIndex);
					context.put("viewSize", viewSize);
					context.put("lowIndex", lowIndex);
					context.put("highIndex", highIndex);				    
		     	    
	            }
	        }
		}


        // GET PRODUCT FEATURE AND APPLS: DISTINGUISHING FEATURES
        // Using findByAndCache Call since the ProductService(Service getProductVariantTree call) will make the same findByAndCache Call.
        productDistinguishingFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", gvProduct.productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"), UtilMisc.toList("sequenceNum"));
        productDistinguishingFeatures = EntityUtil.filterByDate(productDistinguishingFeatures, true);

        // GET PRODUCT FEATURE AND APPLS : DISTINGUISHING FEATURES BY FEATURE TYPE
        productFeatureTypes = FastList.newInstance();
        productFeaturesByType = new LinkedHashMap();
        for (GenericValue feature: productDistinguishingFeatures) 
        {
            featureType = feature.getString("productFeatureTypeId");
            if (!productFeatureTypes.contains(featureType)) 
            {
                productFeatureTypes.add(featureType);
            }
            features = productFeaturesByType.get(featureType);
            if (UtilValidate.isEmpty(features)) 
            {
                features = FastList.newInstance();
                productFeaturesByType.put(featureType, features);
            }
            features.add(feature);
        }
        context.disFeatureTypesList = productFeatureTypes;
        context.disFeatureByTypeMap = productFeaturesByType;

        //GET PRODUCT INVENTORY MAP
        Map productInventoryMap = FastMap.newInstance();
        inventoryLevelMap = InventoryServices.getProductInventoryLevel(gvProduct.productId, request);
        productInventoryMap.put(gvProduct.productId, inventoryLevelMap);
        context.productInventoryMap = productInventoryMap;

        //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPES (productFeatureTypeId, description)
        Map productFeatureTypesMap = FastMap.newInstance();
        productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);
        if(UtilValidate.isNotEmpty(productFeatureTypesList))
        {
            for (GenericValue productFeatureType : productFeatureTypesList)
            {
            	productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,productFeatureType.description);
            }
        	
        }
        context.productFeatureTypesMap = productFeatureTypesMap;
        
        
        
        context.variantTree = null;
        context.variantTreeSize = null;
        context.variantSample = null;
        context.variantSampleKeys = null;
        context.variantSampleSize = null;
        featureOrder = [];
        // Special Variant Code
        if ("Y".equals(gvProduct.isVirtual)) 
        {
            if ("VV_FEATURETREE".equals(gvProduct.getString("virtualVariantMethodEnum"))) 
            {
                context.featureLists = ProductWorker.getSelectableProductFeaturesByTypesAndSeq(gvProduct);
            } 
            else 
            {
                // GET PRODUCT FEATURE AND APPLS: SELECTABLE FEATURES
                // Using findByAndCache Call since the ProductService(Service getProductVariantTree call) will make the same findByAndCache Call.
                productSelectableFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", gvProduct.productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), UtilMisc.toList("sequenceNum"));
                productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures, true);
            
                //BUILD PRODUCT FEATURE SET (SELECTABLE FEATURES BY FEATURE TYPE)
                featureSet = new LinkedHashSet();
                if (UtilValidate.isNotEmpty(productSelectableFeatures))
                {
                    for (GenericValue productSelectableFeatureAndAppl : productSelectableFeatures)
                    {
                        featureSet.add(productSelectableFeatureAndAppl.productFeatureTypeId);
                    }
                }
                orderBy = ["sequenceNum"];
                
                if (UtilValidate.isNotEmpty(featureSet)) 
                {
                    context.featureSet = featureSet;
                    String lastProductFeatureTypeId="";
                    List productFeatureAndApplSelectList = FastList.newInstance();
                    Map productFeatureAndApplSelectMap = FastMap.newInstance();
                    Map productVariantDisFeatureMap = FastMap.newInstance();
                    Map productVariantStandardFeatureMap = FastMap.newInstance();
                    Map productVariantContentWrapperMap = FastMap.newInstance();
                    Map productVariantProductMap = FastMap.newInstance();
                    Map productVariantPriceMap = FastMap.newInstance();
                    Map variantVolumePricingRuleMap = FastMap.newInstance();
                    Map variantVolumePricingRuleMapMap = FastMap.newInstance();
                    Map productVariantInventoryMap = FastMap.newInstance();
                    Map productVariantProductContentIdMap = FastMap.newInstance();
                    Map productFeatureFirstVariantIdMap = FastMap.newInstance();
                    

	                //IF PRODUCT CATEGORY BUILD THE FEATURE SET BASED ON THE PRODUCT_FEATURE_CAT_GRP_APPL (SEQUENCE)
	                if(UtilValidate.isNotEmpty(gvProductCategory))
	                {
					    productFeatureCatGroupAppls = gvProductCategory.getRelatedCache("ProductFeatureCatGrpAppl");
					    //Commenting out since the thru date might be set to hide from the facet group but we do NOT
					    //want to remove from PDP
					    //productFeatureCatGroupAppls = EntityUtil.filterByDate(productFeatureCatGroupAppls,true);
						productFeatureCatGroupAppls = EntityUtil.orderBy(productFeatureCatGroupAppls,UtilMisc.toList("sequenceNum"));
	
		                if(UtilValidate.isNotEmpty(productFeatureCatGroupAppls))
		                {
	                        featureCategorySet = new LinkedHashSet();
		                    for (GenericValue productFeatureCatGroupAppl : productFeatureCatGroupAppls)
			                {
			                    if (featureSet.contains(productFeatureCatGroupAppl.productFeatureGroupId))
			                    {
	     		                    featureCategorySet.add(productFeatureCatGroupAppl.productFeatureGroupId);
			                    }
			                }
			                if (UtilValidate.isNotEmpty(featureCategorySet))
			                {
			                    featureSet.clear();
			                    featureSet.addAll(featureCategorySet);
			                }
		                }
		                
	                }

	                //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_DATA_RESOURCE (productFeatureId, objectInfo)
	                if (UtilValidate.isNotEmpty(pdpFacetGroupVariantSwatch))
	                {
		                Map productFeatureDataResourceMap = FastMap.newInstance();
		                productFeatureDataResourceList = delegator.findList("ProductFeatureDataResource", EntityCondition.makeCondition(["featureDataResourceTypeId" : "PDP_SWATCH_IMAGE_URL"]), null, ["productFeatureId"], null, true);
		                if (UtilValidate.isNotEmpty(productFeatureDataResourceList))
		                {
		                    for (GenericValue productFeatureDataResource : productFeatureDataResourceList)
		                    {
		                    	dataResource = productFeatureDataResource.getRelatedOneCache("DataResource");
		                        if(UtilValidate.isNotEmpty(dataResource.objectInfo))
		                        {
		                        	productFeatureDataResourceMap.put(productFeatureDataResource.productFeatureId,dataResource.objectInfo);
		                        }
		                    }
		                	
		                }
		                context.productFeatureDataResourceMap = productFeatureDataResourceMap;
	                }

	                //CREATE A MAP FOR CONTEXT OF PRODUCT FEATURE AND APPL: SELECTABLE FEATURES
                    for(GenericValue productFeatureAndAppl : productSelectableFeatures) 
                    {
                       String productFeatureTypeId = productFeatureAndAppl.productFeatureTypeId;
                       if (!productFeatureAndApplSelectMap.containsKey(productFeatureTypeId))
                       {
                           productFeatureAndApplSelectList = FastList.newInstance();
                       }
                       else
                       {
                           productFeatureAndApplSelectList = productFeatureAndApplSelectMap.get(productFeatureTypeId);
                       }
                       productFeatureAndApplSelectList.add(productFeatureAndAppl);
                       productFeatureAndApplSelectMap.put(productFeatureTypeId,productFeatureAndApplSelectList);
                    }

                    
                    //GET PRODUCT ASSOCIATE PRODUCT
			        productAssoc = gvProduct.getRelatedCache("MainProductAssoc");
			        productAssoc = EntityUtil.filterByDate(productAssoc,true);
			        productAssoc = EntityUtil.orderBy(productAssoc,UtilMisc.toList("sequenceNum"));

                    //GET PRODUCT ASSOCIATE PRODUCT: PRODUCT_VARIANT
			        productAssocVariant = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_VARIANT"));
                
                    //GET PRODUCT ASSOCIATE PRODUCT: PRODUCT_COMPLEMENT
            	    complementProducts = FastList.newInstance();
            	    productAssocComplement = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));

            		for (GenericValue compProduct: productAssocComplement)
            		{
            		    if (ProductWorker.isSellable(delegator, compProduct.productIdTo))
            		    {
            			    complementProducts.add(compProduct);
            		    }
            		}
            	    if (UtilValidate.isNotEmpty(complementProducts))
            	    {
            	        context.complementProducts = complementProducts;
            	    }
            	    
            		
                    //GET PRODUCT ASSOCIATE PRODUCT: PRODUCT_ACCESSORY
            	    accessoryProducts = FastList.newInstance();
            	    productAssocAccessory = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId", "PRODUCT_ACCESSORY"));
            		for (GenericValue accessProduct: productAssocAccessory)
            		{
            		    if (ProductWorker.isSellable(delegator, accessProduct.productIdTo))
            		    {
            			    accessoryProducts.add(accessProduct);
            		    }
            		}
            	    if (UtilValidate.isNotEmpty(accessoryProducts))
            	    {
            	        context.accessoryProducts = accessoryProducts;
            	    }
            	    
                    
                    List<String> items = FastList.newInstance();
                    variantFeatureIdExist = [];
                    
                    if (UtilValidate.isNotEmpty(productAssocVariant)) 
                    {
                	    //BUILD PRODUCT VARIANT MAPS FOR CONTENT
                        for(GenericValue pAssoc : productAssocVariant) 
                        {
                            //GET ASSOCIATED PRODUCT (VARIANT) 
                            assocVariantProduct = pAssoc.getRelatedOneCache("AssocProduct");
                            
                            if(ProductWorker.isSellable(assocVariantProduct))
                            {
	                            //BULLD PRODUCT MAP FOR EACH VARIANT TO PUT INTO CONTEXT
	                            productVariantProductMap.put(assocVariantProduct.productId, assocVariantProduct);
	
	                            //BULLD PRODUCT CONTENT WRAPPER FOR EACH VARIANT TO PUT INTO CONTEXT
	                            varProductContentWrapper = new ProductContentWrapper(assocVariantProduct, request);
	                            productVariantContentWrapperMap.put(assocVariantProduct.productId, varProductContentWrapper);
	
	                            //CALCULATE PRODCUT PRICE FOR THE VARIANT.
	                            //VARIANT PRICE MAP IS PUT INTO CONTEXT.
	                            //REUSE priceContext DEFINED FOR VIRTUAL PRODUCT REPLACING THE PRODUCT
	                            if (cart.isSalesOrder()) 
	                            {
	                                priceContext.product = assocVariantProduct;
	                                variantPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
	                                productVariantPriceMap.put(assocVariantProduct.productId,variantPriceMap);
	                            }
	                            
	                            //GET PRODUCT VARIANT CONTENT LIST AND SET INTO CONTEXT
	                    		productVariantContentList = assocVariantProduct.getRelatedCache("ProductContent");
	                    		productVariantContentList = EntityUtil.filterByDate(productVariantContentList,true);
	                    		if (UtilValidate.isNotEmpty(productVariantContentList))
	                    		{
	                                Map variantProductContentMap = FastMap.newInstance();
	                                for (GenericValue productContent: productVariantContentList) 
	                                {
	                        		   productContentTypeId = productContent.productContentTypeId;
	                        		   variantProductContentMap.put(productContent.productContentTypeId,productContent.contentId);
	                                }
	                                productVariantProductContentIdMap.put(assocVariantProduct.productId,variantProductContentMap);
	                                
	                    		}
	
	                            //CHECK IF THERE IS ANY VOLUME PRICING FOR EACH VARIANT
	                            //variantVolumePricingRuleMapMap IS PUT INTO CONTEXT
	                            volumePricingRule = [];
	                            volumePricingRuleMap = FastMap.newInstance();
	                            productIdConds = delegator.findByAndCache("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: assocVariantProduct.productId],["productPriceRuleId"]);
	                            if (UtilValidate.isNotEmpty(productIdConds))
	                            {
						            for (GenericValue priceCond: productIdConds) 
						            {
						                priceRule = priceCond.getRelatedOneCache("ProductPriceRule");
						                if (EntityUtil.isValueActive(priceRule,UtilDateTime.nowTimestamp()))
						                {
							                qtyBreakIdConds = priceRule.getRelatedCache("ProductPriceCond");
							                qtyBreakIdConds = EntityUtil.filterByAnd(qtyBreakIdConds,UtilMisc.toMap("inputParamEnumId","PRIP_QUANTITY"));
							                if (UtilValidate.isNotEmpty(qtyBreakIdConds)) 
							                {
								                priceIdActions = priceRule.getRelatedCache("ProductPriceAction");
								                priceIdActions = EntityUtil.filterByAnd(priceIdActions,UtilMisc.toMap("productPriceActionTypeId","PRICE_FLAT"));
						                        priceIdAction = EntityUtil.getFirst(priceIdActions);
						                        volumePricingRule.add(priceRule);
						                        volumePricingRuleMap.put(priceRule.productPriceRuleId,priceIdAction.amount);
							                }
						                }
						            }
	                            }
	                            variantVolumePricingRuleMap.put(assocVariantProduct.productId, volumePricingRule);
	                            variantVolumePricingRuleMapMap.put(assocVariantProduct.productId, volumePricingRuleMap);                            
	                        
	                            //GET VARIANT INVENTORY MAP
	                            inventoryLevelMap = InventoryServices.getProductInventoryLevel(assocVariantProduct.productId, request);
	                            productVariantInventoryMap.put(assocVariantProduct.productId, inventoryLevelMap);
	                            
	                            variantProductFeatureAndAppls = assocVariantProduct.getRelatedCache("ProductFeatureAndAppl");
	                            variantProductFeatureAndAppls = EntityUtil.filterByDate(variantProductFeatureAndAppls,true);
	              	            variantProductFeatureAndAppls = EntityUtil.orderBy(variantProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
	                            
						        // GET VARIANT PRODUCT FEATURE AND APPLS: DISTINGUISHING FEATURES
						        productVariantDistinguishingFeatures = EntityUtil.filterByAnd(variantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "DISTINGUISHING_FEAT"));
						        // GET VARIANT PRODUCT FEATURE AND APPLS: STANDARD FEATURES
						        productVariantStandardFeatures = EntityUtil.filterByAnd(variantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "STANDARD_FEATURE"));
	                            productVariantStandardFeatureMap.put(assocVariantProduct.productId, productVariantStandardFeatures);
	
						        // GET VARIANT PRODUCT FEATURE AND APPLS : DISTINGUISHING FEATURES BY FEATURE TYPE
	                            //CREATE A MAP FOR CONTEXT OF VARIANT PRODUCT FEATURE AND APPL: DISTINGUISHING FEATURES
						        productVariantFeatureTypes = FastList.newInstance();
						        productVariantFeaturesByType = new LinkedHashMap();
	                            Map variantDistinguishingFeatureMap = FastMap.newInstance();
						        for (GenericValue disFeature: productVariantDistinguishingFeatures) 
						        {
						           featureType = disFeature.getString("productFeatureTypeId");
						           if (!productVariantFeatureTypes.contains(featureType)) 
						           {
						              productVariantFeatureTypes.add(featureType);
						           }
						           features = productVariantFeaturesByType.get(featureType);
						           
						           if (UtilValidate.isEmpty(features)) 
						           {
						              features = FastList.newInstance();
						              productVariantFeaturesByType.put(featureType, features);
						           }
						           features.add(disFeature);
						        }
						        variantDistinguishingFeatureMap.put("productFeatureTypes",productVariantFeatureTypes);
						        variantDistinguishingFeatureMap.put("productFeaturesByType",productVariantFeaturesByType);
						        productVariantDisFeatureMap.put(assocVariantProduct.productId,variantDistinguishingFeatureMap);
						        
						        //CREATE A MAP FOR PRODUCT FEATURE ID AND FIRST SELECTED VARIANT PRODUCT ID
						        if (UtilValidate.isNotEmpty(productVariantStandardFeatures))
                                {
						        	for(GenericValue productVariantStandardFeatureAppl : productVariantStandardFeatures)
							        {
						        		if(!variantFeatureIdExist.contains(productVariantStandardFeatureAppl.productFeatureId))
	                                    {
	                                        productFeatureFirstVariantIdMap.put(productVariantStandardFeatureAppl.productFeatureId, productVariantStandardFeatureAppl.productId); 
	                                        variantFeatureIdExist.add(productVariantStandardFeatureAppl.productFeatureId);
	                                    }
							        }	
                                }
						        
						        //ADD VARIANT PRODUCTS ID TO ITEMS LIST THAT IS USED TO PREPARE THE VARIANT TREE.
						        items.add(pAssoc.getString("productIdTo"));
						        
					        }
                        
                        }
                        //PREPARING THE VARIANT TREE
                        featureOrder = UtilMisc.makeListWritable(UtilGenerics.checkCollection(featureSet));
                        if (featureOrder) 
                        {
                            context.featureOrderFirst = featureOrder[0];
                            context.featureOrder = featureOrder;
                        }
                        variantTree = null;
                        try
                        {
                            variantTree = makeGroup(delegator, productFeatureAndApplSelectMap, items, featureOrder, 0, productVariantStandardFeatureMap);
                        }
                        catch(Exception e) 
                        {
                            Debug.logError("Exception in Creating Product Variant Tree" + e.getMessage(), "eCommerceProductDetail.groovy");
                        }
                        
	                    if (UtilValidate.isNotEmpty(variantTree)) 
	                    {
	                        context.variantTree = variantTree;
	                    }
                        
	                    //START - CRAETING VIRTUAL JS
                        jsBuf = new StringBuffer();
                        jsBuf.append("<script language=\"JavaScript\" type=\"text/javascript\">");
                        
                        //CALL THE FUNCTION TO CREATE JS FOR PRODUCT FEATURE VARIANT MAP
                        if(UtilValidate.isNotEmpty(productFeatureFirstVariantIdMap))
                        {
                        	jsBuf.append(buildVariantMapJS(productFeatureFirstVariantIdMap, productVariantStandardFeatureMap));
                        }
                        //CALL THE FUNCTION TO CREATE JS FOR PRODUCT FEATURE VARIANT GROUP MAP
                        if(UtilValidate.isNotEmpty(productVariantDisFeatureMap))
                        {
                        	jsBuf.append(buildVariantGroupMapJS(productVariantDisFeatureMap, pdpFacetGroupVariantMatch));
                        }
                        
                        // CALL THE FUNCTION TO CREATE DROPDOWN FOR ALL SELECTABLE FEATURE, LI FOR ALL SELECTABLE FEATURE EXCEPT FIRST.
                        if(UtilValidate.isNotEmpty(variantTree))
                        {
                            jsBuf.append(buildFeatureJS(featureOrder, variantTree, productVariantInventoryMap));
                        }
                        
                        jsBuf.append("</script>");

                        // ADD getList() FUNCTION CALL TO DEFAULT JS BUFFER SO THAT WHEN THE PAGE IS LOAD THE FIRST VALUE IS DEFAULT SELECTED FOR THE FIRST FEATURE.
                        jsBufDefault = new StringBuffer();
                        jsBufDefault.append("<script language=\"JavaScript\" type=\"text/javascript\">jQuery(document).ready(function(){");
                        jsBufDefault.append("getList(\"FT" + topLevelName + "\", \""+featureCnt+"\", 1);");
                        jsBufDefault.append("  });</script>");
                        //END - CRAETING VIRTUAL JS
                        
                        // PUT VARIANT MAPS INTO CONTEXT
                        context.virtualJavaScript = jsBuf;
                        context.virtualDefaultJavaScript = jsBufDefault;
                        context.productVariantProductMap = productVariantProductMap;
                        context.productVariantMapKeys = productVariantProductMap.keySet();
                        context.productVariantContentWrapperMap = productVariantContentWrapperMap;
                        context.productVariantPriceMap = productVariantPriceMap;
                        context.productVariantInventoryMap = productVariantInventoryMap;
                        context.productFeatureAndApplSelectMap = productFeatureAndApplSelectMap;
                        context.productVariantDisFeatureTypeMap = productVariantDisFeatureMap;
                        context.productVariantStandardFeatureMap = productVariantStandardFeatureMap;
                        context.productVariantProductContentIdMap = productVariantProductContentIdMap;
                        context.variantVolumePricingRuleMap = variantVolumePricingRuleMap;
                        context.variantVolumePricingRuleMapMap = variantVolumePricingRuleMapMap;
                        context.productFeatureFirstVariantIdMap = productFeatureFirstVariantIdMap;
                    }
                }
            }
        }
        
        
       //Get Sequence for PDP Div Containers 
       XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "ecommerce-UiSequence-xml-file"), context);
       searchRestrictionMap = FastMap.newInstance();
       searchRestrictionMap.put("screen", "Y");
       uiSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, uiSequenceScreen,true, false,true);
       
       for(Map uiSequenceScreenMap : uiSequenceSearchList) 
       {
            if ((uiSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiSequenceScreenMap.value))) 
            {
                if (UtilValidate.isNotEmpty(uiSequenceScreenMap.value)) 
                {
                    uiSequenceScreenMap.value = Integer.parseInt(uiSequenceScreenMap.value);
                } 
                else 
                {
                    uiSequenceScreenMap.value = 0;
                }
            }
        }
       uiSequenceSearchList = UtilMisc.sortMaps(uiSequenceSearchList, UtilMisc.toList("value"));
       context.uiSequenceSearchList = uiSequenceSearchList;

       uiPdpTabSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, "PDPTabs",true, false,true);
       for(Map uiPdpTabSequenceScreenMap : uiPdpTabSequenceSearchList) 
       {
            if ((uiPdpTabSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiPdpTabSequenceScreenMap.value))) 
            {
                if (UtilValidate.isNotEmpty(uiPdpTabSequenceScreenMap.value)) 
                {
                    uiPdpTabSequenceScreenMap.value = Integer.parseInt(uiPdpTabSequenceScreenMap.value);
                } 
                else 
                {
                    uiPdpTabSequenceScreenMap.value = 0;
                }
            }
        }

        uiPdpTabSequenceGroupMaps = [:] as TreeMap;
        for(Map uiPdpTabSequenceScreenMap : uiPdpTabSequenceSearchList) 
        {
            if ((UtilValidate.isNotEmpty(uiPdpTabSequenceScreenMap.group)) && (UtilValidate.isInteger(uiPdpTabSequenceScreenMap.group)) && (uiPdpTabSequenceScreenMap.group != "0")) 
            {
                groupNum = Integer.parseInt(uiPdpTabSequenceScreenMap.group)
                if (!uiPdpTabSequenceGroupMaps.containsKey(groupNum)) 
                {
                    searchGroupMapList =  OsafeManageXml.getSearchListFromListMaps(uiPdpTabSequenceSearchList, UtilMisc.toMap("group", "Y"), uiPdpTabSequenceScreenMap.group, true, false);
                    if (UtilValidate.isNotEmpty(searchGroupMapList)) 
                    {
                        uiPdpTabSequenceGroupMaps.put(groupNum, UtilMisc.sortMaps(searchGroupMapList, UtilMisc.toList("value")))
                    }
                }
            }
        }
        context.uiPdpTabSequenceGroupMaps = uiPdpTabSequenceGroupMaps;

       //USER PDP VARIABLES
       context.PRODUCT_ID=context.productId;
       context.PRODUCT_NAME=context.productName;
       context.PRODUCT_IMAGE_URL=context.productLargeImageUrl;   
       context.CATEGORY_ID=context.productCategoryId;
       context.REQUEST_URL=CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+productCategoryId);
        
    }
    
}