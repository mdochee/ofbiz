 <#if (shoppingCartSize > 0)>
  <div class="showCartItems">
  <#assign offerPriceVisible= "N"/>
  <#list shoppingCart.items() as cartLine>
    <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
    <#if (cartItemAdjustment < 0) >
      <#assign offerPriceVisible= "Y"/>
      <#break>
    </#if>
  </#list>
  <div id="cart_wrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    <table cellspacing="0" cellpadding="0" id="cart_display" summary="CurrentOrder_TABLE_SUMMARY">
        <thead>
            <tr class="cart_headers">
                <th class="product firstCol" scope="col" colspan="2">${uiLabelMap.Product}</th>
                <th class="quantity" scope="col">${uiLabelMap.QuantityLabel}</th>
                <th class="priceCol numberCol" scope="col">${uiLabelMap.PriceLabel}</th>
                <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
                    <th class="priceCol numberCol" scope="col">${uiLabelMap.OfferPriceLabel}</th>
                </#if>
                <th class="total numberCol" scope="col">${uiLabelMap.TotalLabel}</th>
                <th class="actions lastCol" scope="col">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
        <#assign itemsFromList = false>
        <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
        <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          <#assign product = cartLine.getProduct()>
          <#assign urlProductId = cartLine.getProductId()>
          <#assign productCategoryId = product.primaryProductCategoryId!""/>
          <#assign productCategoryId = cartLine.getProductCategoryId()?if_exists>
          <#if !productCategoryId?has_content>
             <#assign productCategoryMemberList = product.getRelatedCache("ProductCategoryMember") />
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
              <#if productCategoryMemberList?has_content>
    	          <#assign productCategoryMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
              </#if>
          </#if>
          <#if product.isVariant?if_exists?upper_case == "Y">
             <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(cartLine.getProductId(), delegator)?if_exists>
             <#assign urlProductId=virtualProduct.productId>
             <#if !productCategoryId?has_content>
	             <#assign productCategoryMemberList = virtualProduct.getRelatedCache("ProductCategoryMember") />
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
	              <#if productCategoryMemberList?has_content>
	    	          <#assign productCategoryMember =Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
	                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
	              </#if>
              </#if>
          </#if>

          <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
          <#if !productImageUrl?has_content && virtualProduct?has_content>
               <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
          </#if>

          <#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
          <#if (productImageUrl?string?has_content && (productImageUrl == "null"))>
               <#assign productImageUrl = "">
          </#if>
          <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "DESCRIPTION", locale, dispatcher)?if_exists>
          <#if !prodDescription?has_content && virtualProduct?has_content>
               <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "DESCRIPTION", locale, dispatcher)?if_exists>
          </#if>
          <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "PRODUCT_NAME", locale, dispatcher)?if_exists>
          <#if !productName?has_content && virtualProduct?has_content>
               <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
          </#if>

          <#assign price = cartLine.getBasePrice()>
          <#assign displayPrice = cartLine.getDisplayPrice()>
          <#assign offerPrice = "">
          <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
          <#if (cartItemAdjustment < 0) >
              <#assign offerPrice = cartLine.getDisplayPrice() + (cartItemAdjustment/cartLine.getQuantity())>
          </#if>
          <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
              <#assign price= cartLine.getDisplayPrice()>
          <#else>
            <#if (cartLine.getSelectedAmount() > 0) >
                <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
            <#else>
                <#assign price = cartLine.getBasePrice()>
            </#if>
         </#if>

         <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}')/>

            <tr class="cart_contents">
                <td class="image firstCol <#if !cartLine_has_next>lastRow</#if>" scope="row">

                    <a href="${productFriendlyUrl}" id="image_${urlProductId}">
                      <#assign IMG_SIZE_CART_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_H")!""/>
                      <#assign IMG_SIZE_CART_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_W")!""/>
                        <img alt="${StringUtil.wrapString(productName)}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}">
                    </a>
                </td>
                <td class="description <#if !cartLine_has_next>lastRow</#if>">
                    <dl>
                        <dt>${uiLabelMap.ProductDescriptionAttributesInfo}</dt>
                        <dd class="description">
                          <a href="${productFriendlyUrl}">${StringUtil.wrapString(productName!)}</a>
                        </dd>
		                 <#assign productFeatureAndAppls = product.getRelatedCache("ProductFeatureAndAppl") />
		                 <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureAndAppls,true)/>
		                 <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
                        <#if productFeatureAndAppls?has_content>
                          <#list productFeatureAndAppls as productFeatureAndAppl>
                            <#assign productFeature = productFeatureAndAppl.getRelatedOneCache("ProductFeatureCategory")?if_exists />
                            <dd>${productFeature.description!}:${productFeatureAndAppl.description!}</dd>
                          </#list>
                        </#if>
                    </dl>
                </td>
                <td class="quantity <#if !cartLine_has_next>lastRow</#if>">
                    <#if cartLine.getIsPromo()>
                        <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" maxlength="5" readonly="readonly"/>
                    <#else>
                        <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" maxlength="5"/><span class="action"><a class="standardBtn action" href="javascript:submitCheckoutForm(document.${formName!}, 'UC', '');">Update</a></span>
                    </#if>
                </td>
                <td class="priceCol numberCol <#if !cartLine_has_next>lastRow</#if>">
                    <ul title="Price Information">
                        <li>
                        <div id="priceelement">
                            <ul>
                                <li>
                                    <span class="price"><@ofbizCurrency amount=displayPrice rounding=2 isoCode=currencyUom/></span>
                                </li>
                            </ul>
                        </div>

                       </li>
                    </ul>
                </td>
                <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
                    <td class="priceCol numberCol <#if !cartLine_has_next>lastRow</#if>">
                        <ul title="Price Information">
                            <li>
                            <div id="priceelement">
                                <ul>
                                    <li>
                                        <span class="price">
                                        <#if offerPrice?exists && offerPrice?has_content>
                                            <@ofbizCurrency amount=offerPrice rounding=2 isoCode=currencyUom/>
                                        </#if>
                                        </span>
                                    </li>
                                </ul>
                            </div>
    
                           </li>
                        </ul>
                    </td>
                </#if>
                <td class="total numberCol <#if !cartLine_has_next>lastRow</#if>">
                    <ul>
                        <li>
                            <span class="price"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() rounding=2 isoCode=currencyUom/></span>
                        </li>
                    </ul>
                </td>
                <td class="actions lastCol <#if !cartLine_has_next>lastRow</#if>">
                    <ul>
                        <li class="remove">
                            <span class="action">
                                <#if !cartLine.getIsPromo()>
                                    <a class="standardBtn action" href="<@ofbizUrl>deleteFromCart?delete_${cartLineIndex}=${cartLineIndex}</@ofbizUrl>" title="Remove Item">
                                    <span>Remove Item</span>
                                    </a>
                                </#if>
                            </span>
                        </li>

                    </ul>
                </td>
            </tr>
        </#list>
      </tbody>
        <tfoot class="cart_totals summary">
          <tr class=" discount_subtotal">
            <td  class="caption" <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >colspan="7"<#else>colspan="6"</#if>>
              <ul class="footContainer">
                  <li>
                    <div class="labelText">
                      <label>${uiLabelMap.SubTotalLabel}</label>
                    </div>
                    <div class="labelValue">
                      <span class="amount"><@ofbizCurrency amount=shoppingCart.getSubTotal() rounding=2 isoCode=currencyUom/></span>
                    </div>
                   </li>
                   <#if (shoppingCart.getShipmentMethodTypeId()?has_content)>
                     <#assign selectedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION")?if_exists />
                     <#if !selectedStoreId?has_content && shoppingCart.getShipmentMethodTypeId()?has_content && shoppingCart.getCarrierPartyId()?has_content>
                         <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shoppingCart.getCarrierPartyId()))?if_exists />
                         <#assign chosenShippingMethodDescription = carrier.groupName?default(carrier.partyId) + " " + shoppingCart.getShipmentMethodType(0).description />
                     </#if>
                   <li>
                     <div class="labelText">
                       <label>${uiLabelMap.CartShippingMethodLabel}</label>
                     </div>
                     <div class="labelValue">
                       <span class="shippingMethod">${chosenShippingMethodDescription!}</span>
                     </div>
                   </li>
                   <li>
                      <div class="labelText">
                        <label>${uiLabelMap.CartShippingAndHandlingLabel}</label>
                      </div>
                      <div class="labelValue">
                        <span class="amount"><@ofbizCurrency amount=orderShippingTotal rounding=2 isoCode=currencyUom/></span>
                      </div>
                   </li>
                  </#if>
                  <#if userLogin?has_content && (!Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO") || (orderTaxTotal?has_content && (orderTaxTotal &gt; 0)))>
                   <li>
                        <div class="labelText">
                            <label>${uiLabelMap.CartTaxLabel}</label>
                        </div>
                        <div class="labelValue">
                            <span class="amount"><@ofbizCurrency amount=orderTaxTotal rounding=2 isoCode=currencyUom/></span>
                        </div>
                   </li>
                  </#if>
                <li>
                  <div class="labelText">
                    <label>${uiLabelMap.CartTotalLabel}</label>
                  </div>
                  <div class="labelValue">
                    <span class="amount"><@ofbizCurrency amount=orderGrandTotal rounding=2 isoCode=currencyUom/></span>
                  </div>
                </li>
                <#if shoppingCart.getAdjustments()?has_content>
                  <#list shoppingCart.getAdjustments() as cartAdjustment>
                    <#assign promoCodeText = ""/>
                    <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                    <#assign productPromo = cartAdjustment.getRelatedOneCache("ProductPromo")!"">
                    <#if productPromo?has_content>
                      <#assign promoText = productPromo.promoText?if_exists/>
                      <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
                      <#if productPromoCode?has_content>
                        <#assign promoCodesEntered = shoppingCart.getProductPromoCodesEntered()!"">
                         <#if promoCodesEntered?has_content>
                            <#list promoCodesEntered as promoCodeEntered>
                              <#if productPromoCode?has_content>
                                <#list productPromoCode as promoCode>
                                  <#assign promoCodeEnteredId = promoCodeEntered/>
                                  <#assign promoCodeId = promoCode.productPromoCodeId!""/>
                                  <#if promoCodeEnteredId?has_content>
                                      <#if promoCodeId == promoCodeEnteredId>
                                         <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
                                      </#if>
                                  </#if>
                                </#list>
                              </#if>
                             </#list>
                         </#if>
                      </#if>
                    </#if>
                  <li>
                      <div class="labelText">
                        <label><#if promoText?has_content>(<#if promoCodeText?has_content>${promoCodeText} </#if>${promoText})<#else>${adjustmentType.get("description",locale)?if_exists}</#if></label>
                      </div>
                      <div class="labelValue">
                        <span class="amount"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) rounding=2 isoCode=currencyUom/></span>
                      </div>
                    </li>
                  </#list>
                  <#-- show adjusted total if a promo is entered -->
                  <#if promoText?has_content>
                     <li>
                        <div class="labelText">
                          <div class="adjustedTotalLabel"><label>${uiLabelMap.CartAdjustedTotalLabel}</label></div>
                        </div>
                        <div class="labelValue">
                          <div class="adjustedTotalValue"><span class="amount"><@ofbizCurrency amount=shoppingCart.getGrandTotal() rounding=2 isoCode=currencyUom/></span></div>
                        </div>
                     </li>
                  </#if>
                </#if>
              </ul>
            </td>
          </tr>
          <tr>
            <td colspan="6">
              <span>${uiLabelMap.CartPostageInfo}</span>
            </td>
          </tr>
      </tfoot>
    </table>

  </div>
</div>
<#else>
  <div class="showCartItems">
    <div class="displayBox">
      <p class="instructions">${uiLabelMap.YourShoppingCartIsEmptyInfo}</p>
    </div>
  </div>
 </#if>

